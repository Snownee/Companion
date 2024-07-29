package snownee.companion;

import net.minecraft.world.phys.Vec3;

public interface CompanionPlayer {

	Vec3 companion$getJumpPos();

	void companion$setJumpPos(Vec3 pos);

	void companion$removeShoulderEntities();

}
