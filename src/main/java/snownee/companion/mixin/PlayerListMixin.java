package snownee.companion.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.llamalad7.mixinextras.sugar.Local;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.entity.Entity;
import snownee.companion.Hooks;

@Mixin(PlayerList.class)
public class PlayerListMixin {
	@Inject(method = "respawn", at = @At("TAIL"))
	private void companion_respawn(
			ServerPlayer serverPlayer,
			boolean keepAllPlayerData,
			Entity.RemovalReason removalReason,
			CallbackInfoReturnable<ServerPlayer> cir,
			@Local(name = "player") ServerPlayer player) {
		Hooks.onTeleport(player, player.level(), serverPlayer.level(), Hooks.TeleportType.Respawn);
	}
}
