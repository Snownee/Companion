package snownee.companion.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import snownee.companion.CompanionCommonConfig;
import snownee.companion.CompanionTamableAnimal;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {

	@Inject(at = @At("TAIL"), method = "hurt")
	private void companion_hurt(DamageSource damageSource, float f, CallbackInfoReturnable<Boolean> ci) {
		if (CompanionCommonConfig.petTeleportToOwnerWhenInjured && !damageSource.is(DamageTypes.FELL_OUT_OF_WORLD) && (Object) this instanceof TamableAnimal) {
			((CompanionTamableAnimal) this).companion$tryTeleportToOwner(damageSource);
		}
	}

}
