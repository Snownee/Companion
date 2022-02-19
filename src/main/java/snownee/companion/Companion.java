package snownee.companion;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.world.level.GameRules;
import net.minecraftforge.fml.common.Mod;
import snownee.companion.mixin.BooleanValueAccess;

@Mod(Companion.MODID)
public class Companion {
	public static final String MODID = "companion";

	public static final Logger LOGGER = LogManager.getLogger(Companion.MODID);

	public static final GameRules.Key<GameRules.BooleanValue> PET_FRIENDLY_FIRE = GameRules.register("petFriendlyFire", GameRules.Category.PLAYER, BooleanValueAccess.callCreate(true));
}
