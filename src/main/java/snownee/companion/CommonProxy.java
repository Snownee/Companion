package snownee.companion;

import java.util.Objects;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import snownee.kiwi.Mod;

@Mod(Companion.ID)
public class CommonProxy implements ModInitializer {
	@Override
	public void onInitialize() {
		Objects.requireNonNull(Companion.PET_FRIENDLY_FIRE);
	}

	public static boolean isRangedWeapon(ItemStack itemStack) {
		return itemStack.is(ConventionalItemTags.RANGED_WEAPON_TOOLS);
	}

	public static GameRules.Key<GameRules.BooleanValue> registerRule(String name, GameRules.Category category, boolean defaultValue) {
		return GameRuleRegistry.register(Companion.ID + ":" + name, category, GameRuleFactory.createBooleanRule(defaultValue));
	}
}
