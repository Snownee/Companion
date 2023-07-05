package snownee.companion;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.world.level.GameRules;
import net.minecraftforge.fml.common.Mod;
import snownee.companion.mixin.BooleanValueAccess;

@Mod(Companion.ID)
public class Companion {
	public static final String ID = "companion";

	public static final Logger LOGGER = LogManager.getLogger(Companion.ID);

	public static final GameRules.Key<GameRules.BooleanValue> PET_FRIENDLY_FIRE = GameRules.register("petFriendlyFire", GameRules.Category.PLAYER, BooleanValueAccess.callCreate(true));
	public static final GameRules.Key<GameRules.BooleanValue> ALWAYS_TELEPORT_HORSES = GameRules.register("companion:alwaysTeleportHorses", GameRules.Category.PLAYER, BooleanValueAccess.callCreate(false));
}
