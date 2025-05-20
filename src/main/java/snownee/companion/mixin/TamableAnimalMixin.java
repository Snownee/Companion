package snownee.companion.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.Level;
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
	public void companion$tryTeleportToOwner(DamageSource damageSource) {
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
		if (owner == damageSource.getEntity() || !Hooks.shouldFollowOwner(owner, this)) {
			return;
		}
		long time = level().getGameTime();
		long interval = time - companion$lastTeleportation;
		if (interval > 0 && interval < 600) {
			return;
		}
		companion$lastTeleportation = time;
		Hooks.teleportWithRandomOffset(
						(TamableAnimal) (Object) this,
						owner.level(),
						owner.blockPosition().relative(owner.getDirection().getOpposite(), 3),
						null,
						owner)
				.ifPresent(vec -> teleportTo(vec.x, vec.y, vec.z));
	}
}