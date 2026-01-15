package snownee.companion;

import org.jspecify.annotations.Nullable;

import net.minecraft.world.phys.Vec3;

public interface CompanionPlayer {

	@Nullable Vec3 companion$getJumpPos();

	void companion$setJumpPos(@Nullable Vec3 pos);

	void companion$removeShoulderEntities();

}
