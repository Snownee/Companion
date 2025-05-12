package snownee.companion.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;

@Mixin(TargetGoal.class)
public interface TargetGoalAccess {
	@Accessor
	Mob getMob();
}
