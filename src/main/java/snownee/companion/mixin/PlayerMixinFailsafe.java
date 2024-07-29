package snownee.companion.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import net.minecraft.world.entity.player.Player;
import snownee.companion.CompanionPlayer;

@Mixin(value = Player.class, priority = 950)
public abstract class PlayerMixinFailsafe implements CompanionPlayer {

	@WrapOperation(
			at = @At(
					value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;removeEntitiesOnShoulder()V"
			), method = {"aiStep", "hurt"}, require = 0
	)
	private void nullifyDefaultRemoveEntitiesOnShoulder(Player player, Operation<Void> original) {
		// NOOP
	}

}
