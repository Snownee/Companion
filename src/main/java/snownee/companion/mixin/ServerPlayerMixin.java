package snownee.companion.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.portal.PortalInfo;
import net.minecraft.world.level.storage.LevelData;
import net.minecraftforge.common.util.ITeleporter;
import snownee.companion.CompanionCommonConfig;
import snownee.companion.Hooks;

@Mixin(ServerPlayer.class)
public class ServerPlayerMixin {

	// We teleport all pets before level info being synced
	@SuppressWarnings("rawtypes")
	@Inject(
			at = @At(
					value = "INVOKE", target = "Lnet/minecraft/server/players/PlayerList;sendLevelInfo(Lnet/minecraft/server/level/ServerPlayer;Lnet/minecraft/server/level/ServerLevel;)V", remap = true
			), method = "changeDimension", locals = LocalCapture.CAPTURE_FAILHARD, remap = false
	)
	private void companion_changeDimension(ServerLevel to, ITeleporter teleporter, CallbackInfoReturnable<Entity> cir, ServerLevel from, ResourceKey resourceKey, LevelData levelData, PlayerList playerList, PortalInfo portalInfo) {
		if (CompanionCommonConfig.portalTeleportingPets)
			Hooks.changeDimension((ServerPlayer) (Object) this, to, from, false);
	}

	@Inject(
			at = @At(
					value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;unRide()V", remap = true
			), method = "changeDimension", remap = false
	)
	private void companion_returnFromEnd(ServerLevel to, ITeleporter teleporter, CallbackInfoReturnable<Entity> cir) {
		if (CompanionCommonConfig.portalTeleportingPets) {
			ServerPlayer player = (ServerPlayer) (Object) this;
			Hooks.changeDimension(player, to, player.getLevel(), true);
		}
	}

}
