package snownee.companion.mixin;

import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import snownee.companion.Companion;
import snownee.companion.CompanionCommonConfig;
import snownee.companion.CompanionPlayer;
import snownee.companion.Hooks;

@Mixin(value = Player.class, priority = 1050)
public abstract class PlayerMixin implements CompanionPlayer {

	@Inject(at = @At("TAIL"), method = "hurtServer")
	private void companion_hurt(ServerLevel level, DamageSource source, float damage, CallbackInfoReturnable<Boolean> ci) {
		if (damage > CompanionCommonConfig.shoulderDismountDamageThreshold) {
			removeEntitiesOnShoulder();
		}
	}

	@Shadow
	protected abstract void removeEntitiesOnShoulder();

	@Unique
	private @Nullable Vec3 companion$jumpPos;

	@Override
	public @Nullable Vec3 companion$getJumpPos() {
		return companion$jumpPos;
	}

	@Override
	public void companion$setJumpPos(@Nullable Vec3 pos) {
		this.companion$jumpPos = pos;
	}

	@Override
	public void companion$removeShoulderEntities() {
		removeEntitiesOnShoulder();
	}

	@Inject(at = @At("HEAD"), method = "attack", cancellable = true)
	private void companion_attack(Entity entity, CallbackInfo ci) {
		if (Hooks.getEntityOwner(entity) == (Object) this) {
			Player self = (Player) (Object) this;
			if (self.level() instanceof ServerLevel level && !level.getGameRules().get(Companion.PET_FRIENDLY_FIRE)) {
				ci.cancel();
			}
		}
	}

}
