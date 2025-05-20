package snownee.companion.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.llamalad7.mixinextras.sugar.Local;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import snownee.companion.CompanionCommonConfig;
import snownee.companion.Hooks;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin {

	@Shadow
	public abstract ServerLevel serverLevel();

	// We teleport all pets before level info being synced
	@SuppressWarnings("rawtypes")
	@Inject(
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/server/players/PlayerList;sendLevelInfo(Lnet/minecraft/server/level/ServerPlayer;Lnet/minecraft/server/level/ServerLevel;)V"
			),
			method = "changeDimension"
	)
	private void companion_changeDimension(
			final ServerLevel to,
			final CallbackInfoReturnable<Entity> cir,
			@Local(ordinal = 1) ServerLevel from) {
		if (CompanionCommonConfig.portalTeleportingPets) {
			Hooks.changeDimension((ServerPlayer) (Object) this, to, from, false);
		}
	}

	@Inject(
			at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;unRide()V"),
			method = "changeDimension"
	)
	private void companion_returnFromEnd(
			ServerLevel to,
			CallbackInfoReturnable<Entity> cir,
			@Local(ordinal = 1) ServerLevel from) {
		if (CompanionCommonConfig.portalTeleportingPets) {
			Hooks.changeDimension((ServerPlayer) (Object) this, to, from, true);
		}
	}
}
