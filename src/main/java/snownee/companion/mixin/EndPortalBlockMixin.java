package snownee.companion.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EndPortalBlock;
import net.minecraft.world.level.block.state.BlockState;
import snownee.companion.CompanionCommonConfig;
import snownee.companion.Hooks;

@Mixin(EndPortalBlock.class)
public class EndPortalBlockMixin {

	@Inject(method = "entityInside", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;showEndCredits()V"))
	private void companion_entityInside(BlockState blockState, Level level, BlockPos blockPos, Entity entity, CallbackInfo ci) {
		if (CompanionCommonConfig.portalTeleportingPets && level instanceof ServerLevel serverLevel &&
				entity instanceof ServerPlayer player) {
			Hooks.onTeleport(player, serverLevel.getServer().overworld(), serverLevel, Hooks.TeleportType.ReturnFromEnd);
		}
	}

}