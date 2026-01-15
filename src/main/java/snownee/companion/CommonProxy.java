package snownee.companion;

import java.util.Objects;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.minecraft.world.item.ItemStack;
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
}
