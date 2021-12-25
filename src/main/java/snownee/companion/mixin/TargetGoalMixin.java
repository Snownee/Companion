package snownee.companion.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import snownee.companion.Hooks;

@Mixin(TargetGoal.class)
public class TargetGoalMixin {

	@Shadow
	protected Mob mob;
	@Shadow
	protected LivingEntity targetMob;

	@Inject(at = @At("HEAD"), method = "canContinueToUse", cancellable = true)
	private void companion_canContinueToUse(CallbackInfoReturnable<Boolean> ci) {
		if (targetMob != null && mob instanceof TamableAnimal && ((TamableAnimal) mob).isTame()) {
			if (Hooks.wantsToAttack0((TamableAnimal) mob, targetMob)) {
				ci.setReturnValue(false);
			}
		}
	}

}
