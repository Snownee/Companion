package snownee.companion.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import snownee.companion.Hooks;

@Mixin(OwnerHurtByTargetGoal.class)
public class OwnerHurtByTargetGoalMixin {
	@WrapOperation(
			method = "canUse", at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/entity/TamableAnimal;wantsToAttack(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/entity/LivingEntity;)Z"))
	public boolean companion_wantsToAttack(TamableAnimal pet, LivingEntity enemy, LivingEntity owner, Operation<Boolean> original) {
		return original.call(pet, enemy, owner) && Hooks.wantsToAttack(pet, enemy);
	}
}
