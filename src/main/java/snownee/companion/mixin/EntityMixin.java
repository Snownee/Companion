package snownee.companion.mixin;

import java.util.UUID;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import snownee.companion.CompanionCommonConfig;
import snownee.companion.CompanionPlayer;
import snownee.companion.Hooks;

@Mixin(Entity.class)
public class EntityMixin {

	@Inject(at = @At("HEAD"), method = "checkFallDamage")
	private void companion_checkFallDamage(double d, boolean bl, BlockState blockState, BlockPos blockPos, CallbackInfo ci) {
		Entity entity = (Entity) (Object) this;
		if (bl && !entity.level.isClientSide && entity.fallDistance > 0 && entity instanceof Player) {
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
		UUID ownerUUID;
		if (entity instanceof OwnableEntity) {
			ownerUUID = ((OwnableEntity) entity).getOwnerUUID();
		} else if (entity instanceof AbstractHorse) {
			ownerUUID = ((AbstractHorse) entity).getOwnerUUID();
		} else {
			return;
		}
		if (ownerUUID == null) {
			return;
		}
		Player owner = entity.level.getPlayerByUUID(ownerUUID);
		if (owner == null || owner.distanceToSqr(entity) > r * r) {
			ci.setReturnValue(false);
		}
	}

}
