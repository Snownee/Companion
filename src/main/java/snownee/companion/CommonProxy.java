package snownee.companion;

import java.util.Objects;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraftforge.fml.common.Mod;

@Mod(Companion.ID)
public class CommonProxy {
	public static final TagKey<Item> RANGED_WEAPON = TagKey.create(Registries.ITEM, Companion.RL("ranged_weapon"));

	public static final TagKey<EntityType<?>> IMMORTAL_BLACKLIST = TagKey.create(
			Registries.ENTITY_TYPE,
			Companion.RL("immortal_blacklist"));


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