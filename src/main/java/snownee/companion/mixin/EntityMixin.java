package snownee.companion.mixin;

import java.util.Objects;
import java.util.UUID;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.phys.Vec3;
import snownee.companion.CompanionCommonConfig;
import snownee.companion.CompanionPlayer;
import snownee.companion.Hooks;

@Mixin(Entity.class)
public class EntityMixin {

	@Inject(at = @At("HEAD"), method = "checkFallDamage")
	private void companion_checkFallDamage(double ya, boolean onGround, BlockState onState, BlockPos pos, CallbackInfo ci) {
		Entity entity = (Entity) (Object) this;
		if (onGround && CompanionCommonConfig.shoulderDismountSmartMode && !entity.level().isClientSide() && entity.fallDistance > 0 &&
				entity instanceof Player) {
			CompanionPlayer player = (CompanionPlayer) this;
			Vec3 past = player.companion$getJumpPos();
			if (past == null) {
				return;
			}
			Vec3 now = entity.position();
			player.companion$setJumpPos(null);
			if (Mth.equal(past.x, now.x) && Mth.equal(past.y, now.y) && Mth.equal(past.z, now.z)) {
				player.companion$removeShoulderEntities();
			}
		}
	}

	@Inject(at = @At("HEAD"), method = "canTeleport", cancellable = true)
	private void companion_canChangeDimensions(Level from, Level to, CallbackInfoReturnable<Boolean> ci) {
		if (Hooks.traveling) {
			return;
		}
		float r = CompanionCommonConfig.petWontChangeDimensionUnlessOwnerIsNearbyRadius;
		if (r < 0) {
			return;
		}
		Entity entity = (Entity) (Object) this;
		UUID ownerUUID = Hooks.getEntityOwnerUUID(entity);
		if (ownerUUID != null) {
			Player owner = entity.level().getPlayerByUUID(ownerUUID);
			if (owner == null || owner.distanceToSqr(entity) > r * r) {
				ci.setReturnValue(false);
			}
		}
	}

	@SuppressWarnings("ConstantValue")
	@Inject(at = @At("HEAD"), method = "isAlliedTo(Lnet/minecraft/world/entity/Entity;)Z", cancellable = true)
	private void companion_isAlliedTo(Entity other, CallbackInfoReturnable<Boolean> ci) {
		if (CompanionCommonConfig.betterSweepingEdgeEffect && (Object) this instanceof Player player) {
			Player owner = Hooks.getEntityOwner(other);
			if (Objects.equals(player, owner)) {
				ci.setReturnValue(true);
			}
		}
	}

	@SuppressWarnings("ConstantValue")
	@Inject(at = @At("HEAD"), method = "isInvulnerable", cancellable = true)
	private void companion_isInvulnerable(CallbackInfoReturnable<Boolean> cir) {
		if ((Object) this instanceof LivingEntity self && self.level() instanceof ServerLevel level && Hooks.isImmortalDying(level, self)) {
			cir.setReturnValue(true);
		}
	}

	@Inject(at = @At("HEAD"), method = "teleportCrossDimension")
	private void companion_teleportCrossDimension(
			ServerLevel oldLevel,
			ServerLevel newLevel,
			TeleportTransition transition,
			CallbackInfoReturnable<Entity> cir) {
		Hooks.teleportCrossDimension((Entity) (Object) this, oldLevel, newLevel, transition);
	}

}
