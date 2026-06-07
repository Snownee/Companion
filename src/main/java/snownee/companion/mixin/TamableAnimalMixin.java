package snownee.companion.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.Level;
import snownee.companion.CompanionCommonConfig;
import snownee.companion.CompanionTamableAnimal;
import snownee.companion.Hooks;

@Mixin(TamableAnimal.class)
public abstract class TamableAnimalMixin extends Animal implements CompanionTamableAnimal, OwnableEntity {

	@Unique
	private long companion$lastTeleportation = Long.MIN_VALUE;

	protected TamableAnimalMixin(EntityType<? extends Animal> entityType, Level level) {
		super(entityType, level);
	}

	@Override
	public void companion$tryTeleportToOwner(ServerLevel level, DamageSource damageSource) {
		if (!Hooks.isInjured(this)) {
			return;
		}
		LivingEntity owner = getOwner();
		if (owner == null) {
			return;
		}
		Hooks.stopAttacking(this);
		if (damageSource.getEntity() instanceof Mob attacker) {
			Hooks.stopAttacking(attacker);
		}
		if (owner == damageSource.getEntity() || !Hooks.shouldFollowOwner(level, owner, this)) {
			return;
		}
		long time = level.getGameTime();
		long interval = time - companion$lastTeleportation;
		if (interval > 0 && interval < 600) {
			return;
		}
		companion$lastTeleportation = time;
		((TamableAnimalAccess) this).callTeleportToAroundBlockPos(owner.blockPosition().relative(owner.getDirection().getOpposite(), 3));
	}

	@WrapMethod(method = "teleportToAroundBlockPos")
	private void companion_teleportToAroundBlockPos(BlockPos targetPos, Operation<Void> original) {
		if (!CompanionCommonConfig.petForceTeleportingIfFollowFailed) {
			original.call(targetPos);
			return;
		}
		Hooks.teleportWithRandomOffset(this, level(), targetPos, null, getOwner())
				.ifPresent(vec -> teleportTo(vec.x, vec.y, vec.z));
	}

}