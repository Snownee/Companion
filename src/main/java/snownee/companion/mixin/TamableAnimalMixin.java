package snownee.companion.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import snownee.companion.CompanionTamableAnimal;
import snownee.companion.Hooks;

@Mixin(TamableAnimal.class)
public class TamableAnimalMixin implements CompanionTamableAnimal {

	@Unique
	private long companion$lastTeleportation = Long.MIN_VALUE;

	@Override
	public void companion$tryTeleportToOwner(DamageSource damageSource) {
		TamableAnimal entity = (TamableAnimal) (Object) this;
		if (!Hooks.isInjured(entity)) {
			return;
		}
		LivingEntity owner = entity.getOwner();
		if (owner == damageSource.getEntity() || !Hooks.shouldFollowOwner(owner, entity)) {
			return;
		}
		long time = entity.level().getGameTime();
		long interval = time - companion$lastTeleportation;
		if (interval > 0 && interval < 600) {
			return;
		}
		companion$lastTeleportation = time;
		entity.setTarget(null);
		Hooks.teleportWithRandomOffset(entity, owner.blockPosition().relative(owner.getDirection().getOpposite(), 3), null).ifPresent(vec -> {
			entity.teleportTo(vec.x, vec.y, vec.z);
		});
	}

}
