package snownee.companion.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import io.github.fabricators_of_create.porting_lib.extensions.ITeleporter;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.portal.PortalInfo;
import net.minecraft.world.level.storage.LevelData;
import snownee.companion.CompanionCommonConfig;
import snownee.companion.Hooks;

@Mixin(value = ServerPlayer.class, priority = 1010)
public class ServerPlayerMixinPortingLib {

	// We teleport all pets before level info being synced
	@SuppressWarnings("rawtypes")
	@Inject(
			at = @At(
					value = "INVOKE", target = "Lnet/minecraft/server/players/PlayerList;sendLevelInfo(Lnet/minecraft/server/level/ServerPlayer;Lnet/minecraft/server/level/ServerLevel;)V", remap = true
			), method = "changeDimension(Lnet/minecraft/server/level/ServerLevel;Lio/github/fabricators_of_create/porting_lib/extensions/ITeleporter;)Lnet/minecraft/world/entity/Entity;", locals = LocalCapture.CAPTURE_FAILSOFT, remap = false, require = 0
	)
	private void companion_changeDimension(ServerLevel to, ITeleporter teleporter, CallbackInfoReturnable<Entity> cir, ServerLevel from, ResourceKey resourcekey, LevelData leveldata, PlayerList playerlist, PortalInfo portalinfo, Entity e) {
		if (CompanionCommonConfig.portalTeleportingPets)
			Hooks.changeDimension((ServerPlayer) (Object) this, to, from, false);
	}

	@Inject(
			at = @At(
					value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;unRide()V", remap = true
			), method = "changeDimension(Lnet/minecraft/server/level/ServerLevel;Lio/github/fabricators_of_create/porting_lib/extensions/ITeleporter;)Lnet/minecraft/world/entity/Entity;", remap = false, require = 0
	)
	private void companion_returnFromEnd(ServerLevel to, ITeleporter teleporter, CallbackInfoReturnable<Entity> cir) {
		if (CompanionCommonConfig.portalTeleportingPets) {
			ServerPlayer player = (ServerPlayer) (Object) this;
			Hooks.changeDimension(player, to, player.getLevel(), true);
		}
	}

}
