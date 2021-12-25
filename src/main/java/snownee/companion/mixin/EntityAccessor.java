package snownee.companion.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;

@Mixin(Entity.class)
public interface EntityAccessor {

	@Accessor
	BlockPos getPortalEntrancePos();

	@Accessor
	void setPortalCooldown(int cd);

	@Invoker
	void callHandleInsidePortal(BlockPos pos);

}
