package snownee.companion.mixin;

import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.llamalad7.mixinextras.sugar.Local;

import io.github.fabricators_of_create.porting_lib.entity.ITeleporter;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import snownee.companion.CompanionCommonConfig;
import snownee.companion.Hooks;

@Mixin(value = ServerPlayer.class, priority = 1010)
public abstract class ServerPlayerMixinPortingLib {

	@Shadow
	public abstract ServerLevel serverLevel();

	// We teleport all pets before level info being synced
	@Dynamic("io.github.fabricators_of_create.porting_lib.entity.mixin.common.ServerPlayerMixin")
	@Inject(
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/server/players/PlayerList;sendLevelInfo(Lnet/minecraft/server/level/ServerPlayer;Lnet/minecraft/server/level/ServerLevel;)V"
			),
			method = "changeDimension(Lnet/minecraft/server/level/ServerLevel;Lio/github/fabricators_of_create/porting_lib/entity/ITeleporter;)Lnet/minecraft/world/entity/Entity;",
			require = 0
	)
	private void companion_changeDimension(
			ServerLevel to,
			ITeleporter teleporter,
			CallbackInfoReturnable<Entity> cir,
			@Local(ordinal = 1) ServerLevel from) {
		if (CompanionCommonConfig.portalTeleportingPets) {
			Hooks.changeDimension((ServerPlayer) (Object) this, to, from, false);
		}
	}

	@Dynamic("io.github.fabricators_of_create.porting_lib.entity.mixin.common.ServerPlayerMixin")
	@Inject(
			at = @At(
					value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;unRide()V"
			),
			method = "changeDimension(Lnet/minecraft/server/level/ServerLevel;Lio/github/fabricators_of_create/porting_lib/entity/ITeleporter;)Lnet/minecraft/world/entity/Entity;",
			require = 0
	)
	private void companion_returnFromEnd(
			ServerLevel to,
			ITeleporter teleporter,
			CallbackInfoReturnable<Entity> cir,
			@Local(ordinal = 1) ServerLevel from) {
		if (CompanionCommonConfig.portalTeleportingPets) {
			Hooks.changeDimension((ServerPlayer) (Object) this, to, from, true);
		}
	}

}
