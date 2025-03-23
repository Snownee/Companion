package snownee.companion.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.llamalad7.mixinextras.sugar.Local;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.portal.DimensionTransition;
import snownee.companion.CompanionCommonConfig;
import snownee.companion.Hooks;

@Mixin(ServerPlayer.class)
public class ServerPlayerMixin {

	@Inject(
			method = "changeDimension", at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/server/players/PlayerList;sendLevelInfo(Lnet/minecraft/server/level/ServerPlayer;Lnet/minecraft/server/level/ServerLevel;)V"))
	private void companion_changeDimension(
			DimensionTransition dimensionTransition,
			CallbackInfoReturnable<Entity> cir,
			@Local(ordinal = 0) ServerLevel to,
			@Local(ordinal = 1) ServerLevel from) {
		if (CompanionCommonConfig.portalTeleportingPets) {
			Hooks.changeDimension((ServerPlayer) (Object) this, to, from, false);
		}
	}

}