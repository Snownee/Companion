package snownee.companion.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.FollowOwnerGoal;
import snownee.companion.CompanionCommonConfig;
import snownee.companion.Hooks;

@Mixin(FollowOwnerGoal.class)
public class FollowOwnerGoalMixin {

	@Final
	@Shadow
	private TamableAnimal tamable;
	@Shadow
	private LivingEntity owner;
	@Shadow
	@Final
	private boolean canFly;

	@Inject(at = @At("TAIL"), method = "teleportToOwner")
	private void companion_teleportToOwner(CallbackInfo ci) {
		if (CompanionCommonConfig.petForceTeleportingIfFollowFailed && owner != null) {
			Hooks.teleportWithRandomOffset(tamable, owner.level(), owner.blockPosition(), canFly, owner).ifPresent(vec -> {
				tamable.teleportTo(vec.x, vec.y, vec.z);
			});
		}
	}

}
