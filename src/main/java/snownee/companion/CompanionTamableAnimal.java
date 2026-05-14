package snownee.companion;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;

public interface CompanionTamableAnimal {

	void companion$tryTeleportToOwner(ServerLevel level, DamageSource damageSource);

}
