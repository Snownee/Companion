package snownee.companion.mixin;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import snownee.companion.CompanionTamableAnimal;
import snownee.companion.Hooks;

@Mixin(TamableAnimal.class)
public class TamableAnimalMixin implements CompanionTamableAnimal {

	private long lastTeleportation = Long.MIN_VALUE;

	@Override
	public void tryTeleportToOwner(DamageSource damageSource) {
		TamableAnimal entity = (TamableAnimal) (Object) this;
		if (!Hooks.isInjured(entity)) {
			return;
		}
		LivingEntity owner = entity.getOwner();
		if (owner == damageSource.getEntity() || !Hooks.shouldFollowOwner(owner, entity)) {
			return;
		}
		long time = entity.level.getGameTime();
		long interval = time - lastTeleportation;
		if (interval > 0 && interval < 600) {
			return;
		}
		lastTeleportation = time;
		entity.setTarget(null);
		Hooks.teleportWithRandomOffset(entity, owner.blockPosition().relative(owner.getDirection().getOpposite(), 2));
	}

}
