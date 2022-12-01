package snownee.companion.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

@Mixin(Item.class)
public class ItemMixin {

	@Inject(at = @At("HEAD"), method = "interactLivingEntity", cancellable = true)
	private void companion_interactLivingEntity(ItemStack stack, Player player, LivingEntity entity, InteractionHand hand, CallbackInfoReturnable<InteractionResult> ci) {
		if (stack.is(Items.STRUCTURE_VOID) && player.hasPermissions(2)) {
			if (entity instanceof TamableAnimal tamable) {
				tamable.tame(player);
				ci.setReturnValue(InteractionResult.sidedSuccess(player.level.isClientSide));
			} else if (entity instanceof AbstractHorse horse) {
				horse.tameWithName(player);
				ci.setReturnValue(InteractionResult.sidedSuccess(player.level.isClientSide));
			}
		}
	}

}
