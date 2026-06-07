package snownee.companion.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.portal.TeleportTransition;
import snownee.companion.CompanionCommonConfig;
import snownee.companion.CompanionPlayer;
import snownee.companion.Hooks;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin implements CompanionPlayer {

	@Inject(
			method = "teleport*", at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/server/players/PlayerList;sendLevelInfo(Lnet/minecraft/server/level/ServerPlayer;Lnet/minecraft/server/level/ServerLevel;)V"))
	private void companion_changeDimension(
			TeleportTransition transition,
			CallbackInfoReturnable<ServerPlayer> cir,
			@Local(name = "newLevel") ServerLevel newLevel,
			@Local(name = "oldLevel") ServerLevel oldLevel) {
		if (CompanionCommonConfig.portalTeleportingPets) {
			Hooks.changeDimension((ServerPlayer) (Object) this, newLevel, oldLevel, false);
		}
	}

	@WrapOperation(
			at = @At(
					value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;removeEntitiesOnShoulder()V"),
			method = "handleShoulderEntities")
	private void nullifyDefaultRemoveEntitiesOnShoulder(ServerPlayer instance, Operation<Void> original) {
		// NOOP
	}

	@Inject(at = @At("TAIL"), method = "handleShoulderEntities")
	private void companion_aiStep(CallbackInfo ci) {
		Player player = (Player) (Object) this;
		if (player.isSleeping() || player.isInPowderSnow) {
			companion$removeShoulderEntities();
			return;
		}
		if (CompanionCommonConfig.shoulderDismountInWater && player.isInWater()) {
			companion$removeShoulderEntities();
			return;
		}
		if (CompanionCommonConfig.shoulderDismountUnderWater && player.isUnderWater()) {
			companion$removeShoulderEntities();
			return;
		}
		if (player.fallDistance > CompanionCommonConfig.shoulderDismountFallDistance) {
			companion$removeShoulderEntities();
			return;
		}
		if (CompanionCommonConfig.shoulderDismountWhileFlying && player.getAbilities().flying) {
			companion$removeShoulderEntities();
			return;
		}
		//TODO under lava???
	}
}