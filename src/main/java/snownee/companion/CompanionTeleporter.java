package snownee.companion;

import java.util.function.Function;

import org.jetbrains.annotations.Nullable;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.portal.PortalInfo;
import net.minecraft.world.phys.Vec3;

public class CompanionTeleporter {

	public static final CompanionTeleporter INSTANCE = new CompanionTeleporter();

	public @Nullable PortalInfo getPortalInfo(Entity entity, ServerLevel destWorld, Function<ServerLevel, PortalInfo> defaultPortalInfo) {
		Player owner = Hooks.getEntityOwner(entity);
		if (owner == null) {
			return null;
		}
		Vec3 dest = Hooks.teleportWithRandomOffset(owner, owner.blockPosition()).orElseGet(owner::position);
		return new PortalInfo(dest, Vec3.ZERO, entity.getYRot(), entity.getXRot());
	}

	public boolean playTeleportSound(ServerPlayer player, ServerLevel sourceWorld, ServerLevel destWorld) {
		return false;
	}
}
