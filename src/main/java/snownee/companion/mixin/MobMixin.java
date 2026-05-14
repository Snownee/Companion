package snownee.companion.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.Permissions;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.chicken.Chicken;
import net.minecraft.world.entity.animal.equine.AbstractHorse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

@Mixin(Mob.class)
public class MobMixin {

	@Inject(method = "checkAndHandleImportantInteractions", at = @At("HEAD"), cancellable = true)
	private void companion_interactLivingEntity(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
		if (!player.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER)) {
			return;
		}
		ItemStack stack = player.getItemInHand(hand);
		if (!stack.is(Items.STRUCTURE_VOID)) {
			return;
		}
		Mob entity = (Mob) (Object) this;
		boolean handled = true;
		if (entity instanceof TamableAnimal tamable) {
			tamable.tame(player);
		} else if (entity instanceof AbstractHorse horse) {
			horse.tameWithName(player);
		} else if (entity instanceof Chicken && player instanceof ServerPlayer serverPlayer) {
			serverPlayer.seenCredits = false;
		} else {
			handled = false;
		}
		if (handled) {
			cir.setReturnValue(InteractionResult.SUCCESS_SERVER);
		}
	}

}
