package snownee.companion.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import snownee.companion.CompanionCommonConfig;
import snownee.companion.CompanionTamableAnimal;
import snownee.companion.Hooks;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {

	@SuppressWarnings("ConstantValue")
	@Inject(at = @At("TAIL"), method = "hurt")
	private void companion_hurt(DamageSource damageSource, float f, CallbackInfoReturnable<Boolean> ci) {
		if (CompanionCommonConfig.petTeleportToOwnerWhenInjured && !damageSource.is(DamageTypes.FELL_OUT_OF_WORLD) &&
				(Object) this instanceof TamableAnimal) {
			((CompanionTamableAnimal) this).companion$tryTeleportToOwner(damageSource);
		}
	}

	@WrapOperation(method = "actuallyHurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;setHealth(F)V"))
	private void companion_setHealth(LivingEntity entity, float health, Operation<Void> original) {
		if (Hooks.isImmortalDying(entity)) {
			health = 1;
		}
		original.call(entity, health);
	}

	@Inject(method = "baseTick", at = @At("HEAD"))
	private void companion_baseTick(CallbackInfo ci) {
		LivingEntity self = (LivingEntity) (Object) this;
		if (self.tickCount % 20 == 0 && Hooks.isImmortalDying(self)) {
			self.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 40, 1));
		}
	}

}
