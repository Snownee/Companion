package snownee.companion;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.world.level.GameRules;
import net.minecraftforge.fml.common.Mod;
import snownee.companion.mixin.BooleanValueAccess;

@Mod(Companion.ID)
public class Companion {
	public static final String ID = "companion";

	public static final Logger LOGGER = LogUtils.getLogger();

	public static final GameRules.Key<GameRules.BooleanValue> PET_FRIENDLY_FIRE = GameRules.register("companion:petFriendlyFire", GameRules.Category.PLAYER, BooleanValueAccess.callCreate(true));
	public static final GameRules.Key<GameRules.BooleanValue> ALWAYS_TELEPORT_HORSES = GameRules.register("companion:alwaysTeleportHorses", GameRules.Category.PLAYER, BooleanValueAccess.callCreate(false));
}
