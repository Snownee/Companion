package snownee.companion;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import org.jetbrains.annotations.Nullable;

import com.google.common.collect.Lists;
import com.lizin5ths.indypets.util.IndyPetsUtil;

import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import net.fabricmc.fabric.api.dimension.v1.FabricDimensions;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.minecraft.world.level.portal.PortalInfo;
import net.minecraft.world.phys.Vec3;
import snownee.companion.mixin.EntityAccess;
import snownee.companion.mixin.MobAccess;

public class Hooks {

	public static final TagKey<Item> RANGED_WEAPON = TagKey.create(Registries.ITEM, new ResourceLocation(Companion.ID, "ranged_weapon"));
	public static final TagKey<Item> CHARGED_RANGED_WEAPON = TagKey.create(Registries.ITEM, new ResourceLocation(Companion.ID, "charged_ranged_weapon"));
	public static final Object2BooleanMap<Class<?>> FOLLOWABLE_CACHE = new Object2BooleanOpenHashMap<>();
	public static boolean traveling;
	public static boolean indyPets = FabricLoader.getInstance().isModLoaded("indypets");

	// Here is a bug that tamed wolf reset their health when it travels through portal.
	// Good job mojang
	public static void changeDimension(ServerPlayer player, ServerLevel to, ServerLevel from, boolean returnFromEnd) {
		if (player.isSpectator() || player.isDeadOrDying()) {
			return;
		}
		if (returnFromEnd) {
			if (player.level() != from) {
				return;
			}
		} else {
			if (player.level() != to) {
				return;
			}
		}
		boolean nether = from.dimension() == Level.NETHER || to.dimension() == Level.NETHER;
		BlockPos portalPos = null;
		if (nether) {
			portalPos = ((EntityAccess) player).getPortalEntrancePos();
			if (portalPos == null) {
				return;
			}
		}
		for (Entity entity : getAllPets(from, player)) {
			if (nether) {
				((EntityAccess) entity).setPortalCooldown(0);
				((EntityAccess) entity).callHandleInsidePortal(portalPos);
			}
			entity.setPortalCooldown();
			PortalInfo portal = CompanionTeleporter.INSTANCE.getPortalInfo(entity, to, null);
			if (portal != null)
				FabricDimensions.teleport(entity, to, portal);

			// this is buggy... position change will not be sync to the client properly..
			// i guess an extra packet is needed
			//			if (entity == null)
			//				continue;
			//			teleportWithRandomOffset(entity, player.blockPosition());
		}
	}

	public static List<Entity> getAllPets(ServerLevel level, ServerPlayer player) {
		int max = CompanionCommonConfig.portalMaxTeleportedPets;
		if (max == -1) {
			max = level.getGameRules().getInt(GameRules.RULE_MAX_ENTITY_CRAMMING);
		}
		traveling = true;
		List<Entity> entities = Lists.newArrayList();
		for (Entity entity : level.getAllEntities()) {
			if (entities.size() >= max) {
				break;
			}
			if (entity.isPassenger() || !entity.canChangeDimensions()) {
				continue;
			}
			if (entity instanceof Mob) {
				Mob mob = (Mob) entity;
				if (mob.isLeashed()) {
					if (mob.getLeashHolder() == player) {
						entities.add(mob);
					}
					continue;
				}
				if (Objects.equals(player.getUUID(), getEntityOwnerUUID(mob)) && shouldFollowOwner(player, mob)) {
					entities.add(mob);
					continue;
				}
			}
		}
		traveling = false;
		return entities;
	}

	public static Optional<Vec3> teleportWithRandomOffset(LivingEntity entity, BlockPos blockPos) {
		RandomSource random = entity.getRandom();
		MutableBlockPos pos = new MutableBlockPos();
		for (int i = 0; i < 20; ++i) {
			int j = randomIntInclusive(random, -3, 3);
			int k = randomIntInclusive(random, -1, 1);
			int l = randomIntInclusive(random, -3, 3);
			pos.set(blockPos.getX() + j, blockPos.getY() + k, blockPos.getZ() + l);
			if (canTeleportTo(entity, pos)) {
				return Optional.of(new Vec3(pos.getX() + .5, pos.getY(), pos.getZ() + .5));
			}
		}
		return Optional.empty();
	}

	private static int randomIntInclusive(RandomSource random, int i, int j) {
		return random.nextInt(j - i + 1) + i;
	}

	private static boolean canTeleportTo(Entity entity, BlockPos blockPos) {
		BlockPathTypes blockPathTypes = WalkNodeEvaluator.getBlockPathTypeStatic(entity.level(), blockPos.mutable());
		if (blockPathTypes != BlockPathTypes.WALKABLE) {
			return false;
		}
		BlockPos blockPos2 = blockPos.subtract(entity.blockPosition());
		return entity.level().noCollision(entity, entity.getBoundingBox().move(blockPos2.getX() + .5, blockPos2.getY(), blockPos2.getZ() + .5));
	}

	public static boolean wantsToAttack(TamableAnimal pet, LivingEntity enemy, LivingEntity owner) {
		return wantsToAttack0(pet, enemy) && pet.wantsToAttack(enemy, owner);
	}

	public static boolean wantsToAttack0(TamableAnimal pet, LivingEntity enemy) {
		if (CompanionCommonConfig.petWontAttackWhenInjured && isInjured(pet)) {
			return !(enemy instanceof Enemy || enemy instanceof IronGolem);
		}
		return true;
	}

	public static boolean isInjured(LivingEntity entity) {
		return entity.getHealth() / entity.getMaxHealth() <= CompanionCommonConfig.petInjuredStatusHealthRatio;
	}

	public static void handleChunkPreUnload(List<net.minecraft.world.level.entity.EntityAccess> entities) {
		for (var entityAccess : entities) {
			if (entityAccess instanceof Mob entity) {
				Player owner = getEntityOwner(entity);
				if (shouldFollowOwner(owner, entity)) {
					BlockPos pos = owner.blockPosition();
					if (owner.level() != entity.level()) {
						continue;
						//						newEntity = entity.changeDimension((ServerLevel) owner.level, CompanionTeleporter.INSTANCE);
					}
					teleportWithRandomOffset(entity, pos).ifPresentOrElse(vec -> {
						entity.teleportTo(vec.x, vec.y, vec.z);
					}, () -> {
						if (!entity.randomTeleport(pos.getX(), pos.getY(), pos.getZ(), false) && CompanionCommonConfig.logIfTeleportingFailed) {
							Companion.LOGGER.warn("Failed to teleport {} to {}", entity, pos);
						}
					});
				}
			}
		}
	}

	public static boolean shouldFollowOwner(LivingEntity owner, Mob pet) {
		if (owner == null || owner.isDeadOrDying() || owner.isSpectator() || pet.isLeashed() || pet.isPassenger()) {
			return false;
		}
		if (pet instanceof TamableAnimal animal) {
			if (animal.isOrderedToSit()) {
				return false;
			}
			if (indyPets) {
				if (IndyPetsUtil.isIndependent(animal)) {
					return false;
				}
			}
		}
		if (pet instanceof AbstractHorse) {
			return pet.level().getGameRules().getBoolean(Companion.ALWAYS_TELEPORT_HORSES);
		}
		return FOLLOWABLE_CACHE.computeIfAbsent(pet.getClass(), $ -> {
			for (WrappedGoal goal : ((MobAccess) pet).getGoalSelector().getAvailableGoals()) {
				if (goal.getGoal() instanceof FollowOwnerGoal) {
					return true;
				}
			}
			return false;
		});
	}

	public static boolean isHoldingRangedWeapon(ServerPlayer player) {
		if (player.isHolding($ -> $.is(RANGED_WEAPON))) {
			ItemStack main = player.getMainHandItem();
			ItemStack off = player.getOffhandItem();
			ItemStack stack = main.is(RANGED_WEAPON) ? main : off;
			if (stack.getItem() instanceof CrossbowItem) {
				if (CrossbowItem.isCharged(stack)) {
					return true;
				}
			} else {
				return true;
			}
		}
		if (player.isUsingItem() && player.getUseItemRemainingTicks() > 0 && player.isHolding($ -> $.is(CHARGED_RANGED_WEAPON))) {
			ItemStack stack = player.getUseItem();
			UseAnim anim = stack.getUseAnimation();
			if (anim == UseAnim.BOW || anim == UseAnim.CROSSBOW || anim == UseAnim.SPEAR) {
				return true;
			}
		}
		return false;
	}

	@Nullable
	public static Player getEntityOwner(Entity entity) {
		UUID ownerUUID = getEntityOwnerUUID(entity);
		if (ownerUUID == null) {
			return null;
		}
		if (entity.level().getServer() == null) {
			return entity.level().getPlayerByUUID(ownerUUID);
		}
		return entity.level().getServer().getPlayerList().getPlayer(ownerUUID);
	}

	@Nullable
	public static UUID getEntityOwnerUUID(Entity entity) {
		if (entity instanceof OwnableEntity) {
			return ((OwnableEntity) entity).getOwnerUUID();
		}
		return null;
	}

}
