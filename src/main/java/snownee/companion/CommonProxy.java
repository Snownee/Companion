package snownee.companion;

import java.util.Objects;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraftforge.fml.common.Mod;

@Mod(Companion.ID)
public class CommonProxy {
	public static final TagKey<Item> RANGED_WEAPON = TagKey.create(Registries.ITEM, new ResourceLocation(Companion.ID, "ranged_weapon"));

	public CommonProxy() {
		Objects.requireNonNull(Companion.PET_FRIENDLY_FIRE);
	}

	public static boolean isRangedWeapon(ItemStack itemStack) {
		return itemStack.is(RANGED_WEAPON);
	}

	public static GameRules.Key<GameRules.BooleanValue> registerRule(String name, GameRules.Category category, boolean defaultValue) {
		return GameRules.register(Companion.ID + ":" + name, category, GameRules.BooleanValue.create(defaultValue));
	}
}