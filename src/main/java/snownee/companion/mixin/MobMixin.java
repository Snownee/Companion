package snownee.companion.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

@Mixin(Mob.class)
public class MobMixin {

	@Inject(method = "checkAndHandleImportantInteractions", at = @At("HEAD"), cancellable = true)
	private void companion_interactLivingEntity(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
		if (!player.hasPermissions(2)) {
			return;
		}
		ItemStack stack = player.getItemInHand(hand);
		if (!stack.is(Items.STRUCTURE_VOID)) {
			return;
		}
		Mob entity = (Mob) (Object) this;
		if (entity instanceof TamableAnimal tamable) {
			tamable.tame(player);
			cir.setReturnValue(InteractionResult.sidedSuccess(player.level().isClientSide));
		} else if (entity instanceof AbstractHorse horse) {
			horse.tameWithName(player);
			horse.equipSaddle(null);
			cir.setReturnValue(InteractionResult.sidedSuccess(player.level().isClientSide));
		}
	}

}
