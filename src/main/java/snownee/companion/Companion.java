package snownee.companion;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.minecraft.world.level.GameRules;
import snownee.kiwi.Mod;

@Mod(Companion.MODID)
public class Companion implements ModInitializer {
	public static final String MODID = "companion";

	public static final Logger LOGGER = LogManager.getLogger(Companion.MODID);

	public static final GameRules.Key<GameRules.BooleanValue> PET_FRIENDLY_FIRE = GameRuleRegistry.register("petFriendlyFire", GameRules.Category.PLAYER, GameRuleFactory.createBooleanRule(true));

	@Override
	public void onInitialize() {
		// load gamerule
	}
}
