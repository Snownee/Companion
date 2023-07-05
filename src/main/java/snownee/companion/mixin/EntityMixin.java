package snownee.companion.mixin;

import java.util.Objects;
import java.util.UUID;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import snownee.companion.Companion;
import snownee.companion.CompanionCommonConfig;
import snownee.companion.CompanionPlayer;
import snownee.companion.Hooks;

@Mixin(Entity.class)
public class EntityMixin {

	@Inject(at = @At("HEAD"), method = "checkFallDamage")
	private void companion_checkFallDamage(double d, boolean bl, BlockState blockState, BlockPos blockPos, CallbackInfo ci) {
		Entity entity = (Entity) (Object) this;
		if (bl && !entity.level().isClientSide && entity.fallDistance > 0 && entity instanceof Player) {
			CompanionPlayer player = (CompanionPlayer) this;
			Vec3 past = player.getJumpPos();
			if (past == null) {
				return;
			}
			Vec3 now = entity.position();
			player.setJumpPos(null);
			if (Mth.equal(past.x, now.x) && Mth.equal(past.y, now.y) && Mth.equal(past.z, now.z)) {
				player.removeShoulderEntities();
			}
		}
	}

	@Inject(at = @At("HEAD"), method = "canChangeDimensions", cancellable = true)
	private void companion_canChangeDimensions(CallbackInfoReturnable<Boolean> ci) {
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

	@Inject(at = @At("HEAD"), method = "isAlliedTo(Lnet/minecraft/world/entity/Entity;)Z", cancellable = true)
	private void companion_isAlliedTo(Entity entity, CallbackInfoReturnable<Boolean> ci) {
		if (CompanionCommonConfig.betterSweepingEdgeEffect && (Object) this instanceof Player) {
			Player owner = Hooks.getEntityOwner(entity);
			Player self = (Player) (Object) this;
			if (Objects.equals(self, owner)) {
				ci.setReturnValue(true);
			}
		}
	}

	@Inject(at = @At("HEAD"), method = "isInvulnerableTo", cancellable = true)
	private void companion_isInvulnerableTo(DamageSource damageSource, CallbackInfoReturnable<Boolean> ci) {
		Entity self = (Entity) (Object) this;
		if (!damageSource.is(DamageTypes.PLAYER_EXPLOSION) && damageSource.getEntity() != null && Hooks.getEntityOwner(self) == damageSource.getEntity()) {
			if (!self.level().getGameRules().getBoolean(Companion.PET_FRIENDLY_FIRE)) {
				ci.setReturnValue(true);
			}
		}
	}

}
