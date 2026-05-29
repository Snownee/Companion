package snownee.companion;

import java.util.Objects;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import snownee.kiwi.Mod;

@Mod(Companion.ID)
public class CommonProxy implements ModInitializer {
	public static final TagKey<Item> RANGED_WEAPON = TagKey.create(Registries.ITEM, new ResourceLocation(Companion.ID, "ranged_weapon"));

	public static final TagKey<EntityType<?>> IMMORTAL_BLACKLIST = TagKey.create(
			Registries.ENTITY_TYPE,
			Companion.RL("immortal_blacklist"));

	@Override
	public void onInitialize() {
		Objects.requireNonNull(Companion.PET_FRIENDLY_FIRE);
	}

	public static boolean isRangedWeapon(ItemStack itemStack) {
		return itemStack.is(RANGED_WEAPON);
	}

	public static GameRules.Key<GameRules.BooleanValue> registerRule(String name, GameRules.Category category, boolean defaultValue) {
		return GameRuleRegistry.register(Companion.ID + ":" + name, category, GameRuleFactory.createBooleanRule(defaultValue));
	}
}