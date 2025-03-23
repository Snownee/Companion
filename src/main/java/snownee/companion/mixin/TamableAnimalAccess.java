package snownee.companion.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.TamableAnimal;

@Mixin(TamableAnimal.class)
public interface TamableAnimalAccess {
	@Invoker
	boolean callCanFlyToOwner();

	@Invoker
	void callTeleportToAroundBlockPos(BlockPos pos);
}
