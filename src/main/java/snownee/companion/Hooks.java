package snownee.companion;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import org.jspecify.annotations.Nullable;

import com.google.common.collect.Lists;

import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.entity.animal.equine.AbstractHorse;
import net.minecraft.world.entity.animal.golem.IronGolem;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.NetherPortalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.pathfinder.PathfindingContext;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import snownee.companion.mixin.MobAccess;
import snownee.companion.mixin.TamableAnimalAccess;
import snownee.kiwi.loader.Platform;

public class Hooks {

	public static final Object2BooleanMap<Class<?>> FOLLOWABLE_CACHE = new Object2BooleanOpenHashMap<>();
	public static boolean traveling;
	public static boolean indyPets = Platform.isModLoaded("indypets");

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
			if (player.portalProcess != null) {
				portalPos = player.portalProcess.getEntryPosition();
			}

			if (portalPos == null) {
				return;
			}
		}

		for (Entity entity : getAllPets(from, to, player)) {
			if (nether) {
				entity.setPortalCooldown(0);
				entity.setAsInsidePortal((NetherPortalBlock) Blocks.NETHER_PORTAL, portalPos);
			}
			if (entity instanceof Mob mob) {
				entity.setPortalCooldown();
				Vec3 dest = Hooks.teleportWithRandomOffset(mob, to, player.blockPosition(), false, player).orElseGet(player::position);
				entity.teleport(new TeleportTransition(to, dest, Vec3.ZERO, 0.0F, 0.0F, TeleportTransition.DO_NOTHING));
			}
		}
	}

	public static List<Entity> getAllPets(ServerLevel level, ServerLevel to, ServerPlayer player) {
		int max = CompanionCommonConfig.portalMaxTeleportedPets;
		if (max == -1) {
			max = level.getGameRules().get(GameRules.MAX_ENTITY_CRAMMING);
		}
		traveling = true;
		List<Entity> entities = Lists.newArrayList();
		for (Entity entity : level.getAllEntities()) {
			if (entities.size() >= max) {
				break;
			}
			if (entity.isPassenger() || !entity.canTeleport(level, to)) {
				continue;
			}
			if (entity instanceof Mob mob) {
				if (mob.isLeashed()) {
					if (mob.getLeashHolder() == player) {
						entities.add(mob);
					}
					continue;
				}
				if (Objects.equals(player.getUUID(), getEntityOwnerUUID(mob)) && shouldFollowOwner(level, player, mob)) {
					entities.add(mob);
				}
			}
		}
		traveling = false;
		return entities;
	}

	public static Optional<Vec3> teleportWithRandomOffset(
			Mob pet,
			Level level,
			BlockPos blockPos,
			@Nullable Boolean canFly,
			@Nullable Entity owner) {
		boolean _canFly = canFly != null ?
				canFly :
				pet instanceof FlyingAnimal || pet instanceof TamableAnimalAccess tamable && tamable.callCanFlyToOwner();
		AABB box = owner == null ? null : owner.getBoundingBox();
		Vec3 ownerFacing = null;
		if (owner instanceof LivingEntity living) {
			float yaw = living.yBodyRot * ((float) Math.PI / 180F);
			ownerFacing = new Vec3(-Math.sin(yaw), 0, Math.cos(yaw));
		}
		Optional<Vec3> vec3 = teleportWithRandomOffsetInternal(pet, level, blockPos, _canFly, box, ownerFacing);
		if (vec3.isPresent() || _canFly) {
			return vec3;
		}
		BlockPos heightmapPos = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, blockPos);
		if (heightmapPos.getY() < blockPos.getY()) {
			return teleportWithRandomOffsetInternal(pet, level, heightmapPos, false, box, ownerFacing);
		}
		BlockPos.MutableBlockPos mutable = blockPos.mutable().move(Direction.DOWN);
		for (int i = 0; i < 25; ++i) {
			mutable.move(Direction.DOWN);
			BlockState blockState = level.getBlockState(mutable);
			if (Heightmap.Types.MOTION_BLOCKING.isOpaque().test(blockState)) {
				return teleportWithRandomOffsetInternal(pet, level, mutable, false, box, ownerFacing);
			}
		}
		return Optional.empty();
	}

	private static Optional<Vec3> teleportWithRandomOffsetInternal(
			Mob entity,
			Level level,
			BlockPos blockPos,
			boolean canFly,
			@Nullable AABB avoidColliding,
			@Nullable Vec3 ownerFacing) {
		if (entity.level() == level && blockPos.distToCenterSqr(entity.position()) < 16) {
			return Optional.empty();
		}
		RandomSource random = entity.getRandom();
		BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
		for (int i = 0; i < 25; ++i) {
			int j = randomIntInclusive(random, -3, 3);
			int l = randomIntInclusive(random, -3, 3);
			if (Math.abs(j) + Math.abs(l) < 2) {
				continue;
			}
			if (ownerFacing != null && isInFrontOfOwner(j, l, ownerFacing)) {
				continue;
			}
			int k = randomIntInclusive(random, -1, 1);
			pos.set(blockPos.getX() + j, blockPos.getY() + k, blockPos.getZ() + l);
			if (canTeleportTo(entity, level, pos, canFly, avoidColliding)) {
				return Optional.of(new Vec3(pos.getX() + .5, pos.getY(), pos.getZ() + .5));
			}
		}
		return Optional.empty();
	}

	private static int randomIntInclusive(RandomSource random, int i, int j) {
		return random.nextInt(j - i + 1) + i;
	}

	private static boolean isInFrontOfOwner(int offsetX, int offsetZ, Vec3 ownerFacing) {
		double dotProduct = offsetX * ownerFacing.x + offsetZ * ownerFacing.z;
		return dotProduct > 0;
	}

	private static boolean canTeleportTo(Mob entity, Level level, BlockPos blockPos, boolean canFly, @Nullable AABB avoidColliding) {
		BlockPos.MutableBlockPos mutable = blockPos.mutable();
		PathfindingContext pathfindingContext = new PathfindingContext(level, entity);
		PathType blockPathType = WalkNodeEvaluator.getPathTypeStatic(pathfindingContext, mutable);
		if (blockPathType == PathType.OPEN && !canFly) {
			return false;
		}
		if (entity instanceof PathfinderMob pathfinderMob) {
			float f = pathfinderMob.getWalkTargetValue(blockPos);
			if (blockPathType == PathType.WATER) {
				if (f > PathType.WATER.getMalus()) {
					return false;
				}
				mutable.move(Direction.UP);
				BlockState aboveState = level.getBlockState(mutable);
				if (!aboveState.isAir()) {
					return false;
				}
			} else if (blockPathType == PathType.LAVA || blockPathType == PathType.FIRE || blockPathType == PathType.FIRE_IN_NEIGHBOR) {
				if (!entity.fireImmune()) {
					return false;
				}
			} else if (blockPathType != PathType.WALKABLE && blockPathType != PathType.OPEN) {
				return false;
			}
		} else if (blockPathType != PathType.WALKABLE && blockPathType != PathType.OPEN) {
			return false;
		}
		BlockPos blockPos2 = blockPos.subtract(entity.blockPosition());
		AABB moved = entity.getBoundingBox().move(blockPos2.getX() + .5, blockPos2.getY(), blockPos2.getZ() + .5);
		return !moved.intersects(avoidColliding) && level.noCollision(entity, moved);
	}

	public static boolean wantsToAttack(ServerLevel level, TamableAnimal pet, @Nullable LivingEntity enemy) {
		if (!pet.isTame()) {
			return true;
		}
		if (isImmortalDying(level, pet)) {
			return false;
		}
		if (CompanionCommonConfig.petWontAttackWhenInjured && isInjured(pet)) {
			return enemy != null && !(enemy instanceof Enemy || enemy instanceof IronGolem);
		}
		return true;
	}

	public static boolean isImmortalDying(ServerLevel level, LivingEntity entity) {
		return !entity.isDeadOrDying() && entity.getHealth() <= 1
				&& level.getGameRules().get(Companion.IMMORTAL_PETS)
				&& !entity.is(Companion.IMMORTAL_BLACKLIST)
				&& Hooks.getEntityOwner(entity) != null;
	}

	public static boolean isInjured(LivingEntity entity) {
		return entity.getHealth() < entity.getMaxHealth() &&
				entity.getHealth() / entity.getMaxHealth() <= CompanionCommonConfig.petInjuredStatusHealthRatio;
	}

	public static void handleChunkPreUnload(List<net.minecraft.world.level.entity.EntityAccess> entities) {
		for (var entityAccess : entities) {
			if (entityAccess instanceof Mob entity) {
				Player owner = getEntityOwner(entity);
				if (owner != null && owner.level() instanceof ServerLevel level && shouldFollowOwner(level, owner, entity)) {
					if (level != entity.level()) {
						continue;
					}
					BlockPos pos = owner.blockPosition();
					teleportWithRandomOffset(entity, level, pos, null, owner).ifPresentOrElse(
							vec -> entity.teleportTo(vec.x, vec.y, vec.z), () -> {
								if (!entity.randomTeleport(pos.getX(), pos.getY(), pos.getZ(), false) &&
										CompanionCommonConfig.logIfTeleportingFailed) {
									Companion.LOGGER.warn(
											"Failed to teleport {}({}) from {} {} to {}",
											Objects.requireNonNull(entity.getDisplayName()).getString(),
											BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()),
											entity.level().dimension().identifier(),
											entity.blockPosition().toShortString(),
											pos.toShortString());
								}
							});
				}
			}
		}
	}

	public static boolean shouldFollowOwner(ServerLevel level, @Nullable LivingEntity owner, Mob pet) {
		if (owner == null || owner.isDeadOrDying() || owner.isSpectator() || pet.isLeashed() || pet.isPassenger()) {
			return false;
		}
		if (pet.hasHome() && !pet.isWithinHome(owner.blockPosition())) {
			return false;
		}
		if (pet instanceof TamableAnimal animal) {
			if (animal.isOrderedToSit()) {
				return false;
			}
			if (indyPets) {
//				if (IndyPetsUtil.isIndependent(animal)) {
//					return false;
//				}
			}
		}
		if (pet instanceof AbstractHorse) {
			return level.getGameRules().get(Companion.ALWAYS_TELEPORT_HORSES);
		}
		return FOLLOWABLE_CACHE.computeIfAbsent(
				pet.getClass(), _ -> {
					for (WrappedGoal goal : ((MobAccess) pet).getGoalSelector().getAvailableGoals()) {
						if (goal.getGoal() instanceof FollowOwnerGoal) {
							return true;
						}
					}
					return false;
				});
	}

	public static boolean isHoldingRangedWeapon(ServerPlayer player) {
		if (player.isHolding(CommonProxy::isRangedWeapon)) {
			ItemStack main = player.getMainHandItem();
			ItemStack off = player.getOffhandItem();
			ItemStack stack = CommonProxy.isRangedWeapon(main) ? main : off;
			if (stack.getItem() instanceof CrossbowItem) {
				if (CrossbowItem.isCharged(stack)) {
					return true;
				}
			} else {
				return true;
			}
		}
		if (player.isUsingItem() && player.getUseItemRemainingTicks() > 0 &&
				player.isHolding($ -> $.is(Companion.CHARGED_RANGED_WEAPONS))) {
			ItemStack stack = player.getUseItem();
			ItemUseAnimation anim = stack.getUseAnimation();
			return anim == ItemUseAnimation.BOW || anim == ItemUseAnimation.CROSSBOW || anim == ItemUseAnimation.SPEAR;
		}
		return false;
	}

	@Nullable
	public static Player getEntityOwner(Entity entity) {
		UUID ownerUUID = getEntityOwnerUUID(entity);
		if (ownerUUID == null) {
			return null;
		}
		MinecraftServer server = entity.level().getServer();
		if (server == null) {
			return entity.level().getPlayerByUUID(ownerUUID);
		}
		return server.getPlayerList().getPlayer(ownerUUID);
	}

	@Nullable
	public static UUID getEntityOwnerUUID(Entity entity) {
		if (entity instanceof OwnableEntity ownableEntity && ownableEntity.getOwnerReference() != null) {
			return ownableEntity.getOwnerReference().getUUID();
		}
		return null;
	}

	public static void stopAttacking(Mob mob) {
		mob.setTarget(null);
		for (WrappedGoal goal : mob.targetSelector.getAvailableGoals()) {
			goal.stop();
		}
		if (mob.getBrain().hasMemoryValue(MemoryModuleType.ATTACK_TARGET)) {
			mob.getBrain().eraseMemory(MemoryModuleType.ATTACK_TARGET);
		}
	}
}
