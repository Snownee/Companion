package snownee.companion;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.minecraft.world.level.GameRules;
import snownee.kiwi.Mod;

@Mod(Companion.ID)
public class Companion implements ModInitializer {
	public static final String ID = "companion";

	public static final Logger LOGGER = LogUtils.getLogger();

	public static final GameRules.Key<GameRules.BooleanValue> PET_FRIENDLY_FIRE = GameRuleRegistry.register("companion:petFriendlyFire", GameRules.Category.PLAYER, GameRuleFactory.createBooleanRule(true));
	public static final GameRules.Key<GameRules.BooleanValue> ALWAYS_TELEPORT_HORSES = GameRuleRegistry.register("companion:alwaysTeleportHorses", GameRules.Category.PLAYER, GameRuleFactory.createBooleanRule(false));

	@Override
	public void onInitialize() {
		// load gamerules
	}
}
