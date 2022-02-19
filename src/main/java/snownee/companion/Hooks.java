package snownee.companion;

import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;

import com.google.common.collect.Lists;

import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag.Named;
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
import snownee.companion.mixin.EntityAccess;

public class Hooks {

	public static boolean traveling;
	public static final Named<Item> RANGED_WEAPON = ItemTags.bind("companion:ranged_weapon");
	public static final Named<Item> CHARGED_RANGED_WEAPON = ItemTags.bind("companion:charged_ranged_weapon");
	public static final Object2BooleanMap<Class<?>> FOLLOWABLE_CACHE = new Object2BooleanOpenHashMap<>();

	// Here is a bug that tamed wolf reset their health when it travels through portal.
	// Good job mojang
	public static void changeDimension(ServerPlayer player, ServerLevel to, ServerLevel from, boolean returnFromEnd) {
		if (player.isSpectator() || player.isDeadOrDying()) {
			return;
		}
		if (returnFromEnd) {
			if (player.level != from) {
				return;
			}
		} else {
			if (player.level != to) {
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
		for (LivingEntity entity : getAllPets(from, player)) {
			if (nether) {
				((EntityAccess) entity).setPortalCooldown(0);
				((EntityAccess) entity).callHandleInsidePortal(portalPos);
			}
			entity.setPortalCooldown();
			entity = (LivingEntity) entity.changeDimension(to);

			// this is buggy... position change will not be sync to the client properly..
			// i guess an extra packet is needed
			//			if (entity == null)
			//				continue;
			//			teleportWithRandomOffset(entity, player.blockPosition());
		}
	}

	public static List<LivingEntity> getAllPets(ServerLevel level, ServerPlayer player) {
		int max = CompanionCommonConfig.portalMaxTeleportedPets;
		if (max == -1) {
			max = level.getGameRules().getInt(GameRules.RULE_MAX_ENTITY_CRAMMING);
		}
		traveling = true;
		List<LivingEntity> entities = Lists.newArrayList();
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
			}
			if (entity instanceof TamableAnimal) {
				TamableAnimal tamable = (TamableAnimal) entity;
				if (tamable.isOrderedToSit()) {
					continue;
				}
				if (Objects.equals(player.getUUID(), tamable.getOwnerUUID())) {
					entities.add(tamable);
					continue;
				}
			}
		}
		traveling = false;
		return entities;
	}

	public static boolean teleportWithRandomOffset(LivingEntity entity, BlockPos blockPos) {
		Random random = entity.getRandom();
		MutableBlockPos pos = new MutableBlockPos();
		for (int i = 0; i < 10; ++i) {
			int j = randomIntInclusive(random, -2, 2);
			int k = randomIntInclusive(random, -1, 1);
			int l = randomIntInclusive(random, -2, 2);
			pos.set(blockPos.getX() + j, blockPos.getY() + k, blockPos.getZ() + l);
			if (canTeleportTo(entity, pos)) {
				entity.teleportTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
				//				entity.level.setBlockAndUpdate(pos.above(), Blocks.DIAMOND_BLOCK.defaultBlockState());
				return true;
			}
		}
		return false;
	}

	private static int randomIntInclusive(Random random, int i, int j) {
		return random.nextInt(j - i + 1) + i;
	}

	private static boolean canTeleportTo(Entity entity, BlockPos blockPos) {
		BlockPathTypes blockPathTypes = WalkNodeEvaluator.getBlockPathTypeStatic(entity.level, blockPos.mutable());
		if (blockPathTypes != BlockPathTypes.WALKABLE) {
			return false;
		}
		BlockPos blockPos2 = blockPos.subtract(entity.blockPosition());
		return entity.level.noCollision(entity, entity.getBoundingBox().move(blockPos2));
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
		for (net.minecraft.world.level.entity.EntityAccess entity : entities) {
			if (entity instanceof TamableAnimal) {
				TamableAnimal pet = (TamableAnimal) entity;
				if (shouldFollowOwner(pet)) {
					BlockPos pos = pet.getOwner().blockPosition();
					if (!teleportWithRandomOffset(pet, pos)) {
						pet.randomTeleport(pos.getX(), pos.getY(), pos.getZ(), false);
					}
				}
			}
		}
	}

	public static boolean shouldFollowOwner(TamableAnimal pet) {
		if (pet.isLeashed() || pet.isOrderedToSit() || pet.isPassenger()) {
			return false;
		}
		LivingEntity owner = pet.getOwner();
		if (owner == null || owner.isDeadOrDying() || owner.isSpectator()) {
			return false;
		}
		return FOLLOWABLE_CACHE.computeIfAbsent(pet.getClass(), $ -> {
			for (WrappedGoal goal : pet.goalSelector.getAvailableGoals()) {
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

	public static Player getEntityOwner(Entity entity) {
		UUID ownerUUID = null;
		if (entity instanceof OwnableEntity) {
			ownerUUID = ((OwnableEntity) entity).getOwnerUUID();
		} else if (entity instanceof AbstractHorse) {
			ownerUUID = ((AbstractHorse) entity).getOwnerUUID();
		}
		if (ownerUUID == null) {
			return null;
		}
		return entity.level.getPlayerByUUID(ownerUUID);
	}

	public static boolean hasOwner(Entity entity) {
		if (entity instanceof OwnableEntity) {
			return ((OwnableEntity) entity).getOwnerUUID() != null;
		}
		if (entity instanceof AbstractHorse) {
			return ((AbstractHorse) entity).getOwnerUUID() != null;
		}
		return false;
	}

}
