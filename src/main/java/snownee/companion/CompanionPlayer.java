package snownee.companion;

import net.minecraft.world.phys.Vec3;

public interface CompanionPlayer {

	Vec3 getJumpPos();

	void setJumpPos(Vec3 pos);

	void removeShoulderEntities();

}
