package snownee.companion.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import snownee.companion.CompanionCommonConfig;
import snownee.companion.CompanionLivingEntity;
import snownee.companion.CompanionTamableAnimal;
import snownee.companion.Hooks;

@Mixin(LivingEntity.class)
public class LivingEntityMixin implements CompanionLivingEntity {

	@Unique
	private double companion_lastX = Double.NaN;
	@Unique
	private double companion_lastZ = Double.NaN;
	@Unique
	private final double[] companion_movementHistory = new double[3];
	@Unique
	private int companion_movementIndex = 0;
	@Unique
	private int companion_movementCount = 0;

	@SuppressWarnings("ConstantValue")
	@Inject(at = @At("TAIL"), method = "hurt")
	private void companion_hurt(DamageSource damageSource, float f, CallbackInfoReturnable<Boolean> ci) {
		if (CompanionCommonConfig.petTeleportToOwnerWhenInjured
				&& !damageSource.is(DamageTypes.FELL_OUT_OF_WORLD)
				&& (Object) this instanceof TamableAnimal) {
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

	@Inject(method = "aiStep", at = @At("HEAD"))
	private void companion_trackPosition(CallbackInfo ci) {
		LivingEntity self = (LivingEntity) (Object) this;
		double currentX = self.getX();
		double currentZ = self.getZ();
		
		if (!Double.isNaN(companion_lastX) && !Double.isNaN(companion_lastZ)) {
			double dx = currentX - companion_lastX;
			double dz = currentZ - companion_lastZ;
			double movement = Math.sqrt(dx * dx + dz * dz);
			this.companion$updateMovement(movement);
		}
		
		this.companion$setLastPosition(currentX, currentZ);
	}

	@ModifyArg(
			method = "travel",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/entity/LivingEntity;moveRelative(FLnet/minecraft/world/phys/Vec3;)V",
					ordinal = 0),
			index = 0)
	private float companion_modifyWaterSpeed(float speed) {
		return companion_adjustTravelSpeed(speed);
	}

	@ModifyArg(
			method = "travel",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/entity/LivingEntity;moveRelative(FLnet/minecraft/world/phys/Vec3;)V",
					ordinal = 1),
			index = 0)
	private float companion_modifyLavaSpeed(float speed) {
		return companion_adjustTravelSpeed(speed);
	}

	@ModifyArg(
			method = "travel",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/entity/LivingEntity;handleRelativeFrictionAndCalculateMovement(Lnet/minecraft/world/phys/Vec3;F)Lnet/minecraft/world/phys/Vec3;"),
			index = 0)
	private Vec3 companion_modifyGroundMovement(Vec3 movement) {
		double speedRatio = companion_calculateSpeedRatio();
		return speedRatio > 1.0 ? movement.scale(speedRatio) : movement;
	}

	@Unique
	private float companion_adjustTravelSpeed(float originalSpeed) {
		double speedRatio = companion_calculateSpeedRatio();
		return speedRatio > 1.0 ? (float) (originalSpeed * speedRatio) : originalSpeed;
	}

	@Unique
	private double companion_calculateSpeedRatio() {
		LivingEntity self = (LivingEntity) (Object) this;
		if (!CompanionCommonConfig.petMatchSpeedToFollowOwner || !(self instanceof Mob mob)) {
			return 1.0;
		}

		Player owner = Hooks.getEntityOwner(mob);
		if (owner == null || !Hooks.isFollowingOwner(mob)) {
			return 1.0;
		}

		// Calculate owner's actual movement distance
		CompanionLivingEntity companionOwner = (CompanionLivingEntity) owner;
		double ownerMovement = companionOwner.companion$getAverageMovement();

		double petMovement = self.getDeltaMovement().horizontalDistance();

		if (ownerMovement > petMovement) {
			return Math.min(ownerMovement * 2 / petMovement, 100.0);
		}

		return 1.0;
	}

	@Override
	public double companion$getLastX() {
		return companion_lastX;
	}

	@Override
	public double companion$getLastZ() {
		return companion_lastZ;
	}

	@Override
	public void companion$setLastPosition(double x, double z) {
		this.companion_lastX = x;
		this.companion_lastZ = z;
	}

	@Override
	public double companion$getAverageMovement() {
		if (companion_movementCount == 0) {
			return 0.0;
		}
		double sum = 0.0;
		for (int i = 0; i < companion_movementCount; i++) {
			sum += companion_movementHistory[i];
		}
		return sum / companion_movementCount;
	}

	@Override
	public void companion$updateMovement(double movement) {
		companion_movementHistory[companion_movementIndex] = movement;
		companion_movementIndex = (companion_movementIndex + 1) % companion_movementHistory.length;
		if (companion_movementCount < companion_movementHistory.length) {
			companion_movementCount++;
		}
	}

}
