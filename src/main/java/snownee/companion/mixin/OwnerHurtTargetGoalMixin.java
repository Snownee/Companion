package snownee.companion.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import snownee.companion.Hooks;

@Mixin(OwnerHurtTargetGoal.class)
public class OwnerHurtTargetGoalMixin {

	@Redirect(
			at = @At(
					value = "INVOKE", target = "Lnet/minecraft/world/entity/TamableAnimal;wantsToAttack(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/entity/LivingEntity;)Z"
			), method = "canUse"
	)
	public boolean companion_wantsToAttack(TamableAnimal pet, LivingEntity enemy, LivingEntity owner) {
		return Hooks.wantsToAttack(pet, enemy, owner);
	}

}
