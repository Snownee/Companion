package snownee.companion;

import java.util.Objects;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.gamerules.GameRuleCategory;
import net.minecraft.world.level.gamerules.GameRules;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.registries.RegisterEvent;

@Mod(Companion.ID)
public class CommonProxy {
	public CommonProxy(IEventBus modBus) {
		Objects.requireNonNull(Companion.IMMORTAL_BLACKLIST);
		modBus.addListener(CompanionDataGen::gatherData);
	}

	public static boolean isRangedWeapon(ItemStack itemStack) {
		return itemStack.is(Tags.Items.RANGED_WEAPON_TOOLS);
	}

	@SubscribeEvent
	static void registerSetup(RegisterEvent event) {
		if (event.getRegistry().equals(BuiltInRegistries.GAME_RULE)) {
			Companion.PET_FRIENDLY_FIRE = GameRules.registerBoolean(
					Companion.ID + ":pet_friendly_fire",
					GameRuleCategory.PLAYER,
					true);
			Companion.IMMORTAL_PETS = GameRules.registerBoolean(
					Companion.ID + ":immortal_pets",
					GameRuleCategory.PLAYER,
					false);
			Companion.ALWAYS_TELEPORT_HORSES = GameRules.registerBoolean(
					Companion.ID + ":always_teleport_horses",
					GameRuleCategory.PLAYER,
					false);
		}
	}
}
