package snownee.companion.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.FollowOwnerGoal;
import snownee.companion.CompanionCommonConfig;

@Mixin(FollowOwnerGoal.class)
public class FollowOwnerGoalMixin {

	@Shadow
	private TamableAnimal tamable;
	@Shadow
	private LivingEntity owner;

	@Inject(at = @At("TAIL"), method = "teleportToOwner")
	private void companion_teleportToOwner(CallbackInfo ci) {
		if (CompanionCommonConfig.petForceTeleportingIfFollowFailed && owner != null) {
			tamable.randomTeleport(owner.getX(), owner.getY(), owner.getZ(), false);
		}
	}

}
