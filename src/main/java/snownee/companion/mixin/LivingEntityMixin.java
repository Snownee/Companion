package snownee.companion.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import snownee.companion.Companion;
import snownee.companion.CompanionCommonConfig;
import snownee.companion.CompanionPlayer;
import snownee.companion.CompanionTamableAnimal;
import snownee.companion.Hooks;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {

	@SuppressWarnings("ConstantValue")
	@Inject(at = @At("TAIL"), method = "hurtServer")
	private void companion_hurt(ServerLevel level, DamageSource source, float damage, CallbackInfoReturnable<Boolean> ci) {
		if (CompanionCommonConfig.petTeleportToOwnerWhenInjured && !source.is(DamageTypes.FELL_OUT_OF_WORLD) &&
				(Object) this instanceof TamableAnimal) {
			((CompanionTamableAnimal) this).companion$tryTeleportToOwner(level, source);
		}
	}

	@WrapOperation(method = "actuallyHurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;setHealth(F)V"))
	private void companion_setHealth(LivingEntity entity, float health, Operation<Void> original) {
		if (entity.level() instanceof ServerLevel level && Hooks.isImmortalDying(level, entity)) {
			health = 1;
		}
		original.call(entity, health);
	}

	@Inject(method = "baseTick", at = @At("HEAD"))
	private void companion_baseTick(CallbackInfo ci) {
		LivingEntity self = (LivingEntity) (Object) this;
		if (self.tickCount % 20 == 0 && self.level() instanceof ServerLevel level && Hooks.isImmortalDying(level, self)) {
			self.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 40, 1));
		}
	}

	@Inject(method = "jumpFromGround", at = @At("HEAD"))
	private void companion_jumpFromGround(CallbackInfo ci) {
		if (this instanceof CompanionPlayer player) {
			player.companion$setJumpPos(((LivingEntity) (Object) this).position());
		}
	}

	@Inject(at = @At("HEAD"), method = "isInvulnerableTo", cancellable = true)
	private void companion_isInvulnerableTo(ServerLevel level, DamageSource source, CallbackInfoReturnable<Boolean> ci) {
		LivingEntity self = (LivingEntity) (Object) this;
		if (!source.is(DamageTypes.PLAYER_EXPLOSION) && source.getEntity() != null &&
				Hooks.getEntityOwner(self) == source.getEntity() && !level.getGameRules().get(Companion.PET_FRIENDLY_FIRE)) {
			ci.setReturnValue(true);
		}
	}
}
