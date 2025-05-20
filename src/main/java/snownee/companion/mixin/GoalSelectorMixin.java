package snownee.companion.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import snownee.companion.Hooks;

@Mixin(GoalSelector.class)
public class GoalSelectorMixin {
	@WrapOperation(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/goal/WrappedGoal;canUse()Z"))
	public boolean companion_canUse(WrappedGoal instance, Operation<Boolean> original) {
		if (instance.getFlags().contains(Goal.Flag.TARGET) && instance.getGoal() instanceof TargetGoalAccess goal &&
				goal.getMob() instanceof TamableAnimal pet && !Hooks.wantsToAttack(pet, null)) {
			return false;
		}
		return original.call(instance);
	}
}
