package snownee.companion;

import java.util.Objects;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.Tags;

@Mod(Companion.ID)
public class CommonProxy {
	public CommonProxy() {
		Objects.requireNonNull(Companion.PET_FRIENDLY_FIRE);
	}

	public static boolean isRangedWeapon(ItemStack itemStack) {
		return itemStack.is(Tags.Items.RANGED_WEAPON_TOOLS);
	}

	public static GameRules.Key<GameRules.BooleanValue> registerRule(String name, GameRules.Category category, boolean defaultValue) {
		return GameRules.register(Companion.ID + ":" + name, category, GameRules.BooleanValue.create(defaultValue));
	}
}
