package snownee.companion;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.gamerules.GameRule;
import net.minecraft.world.level.gamerules.GameRuleCategory;
import net.minecraft.world.level.gamerules.GameRules;

public class Companion {
	public static final String ID = "companion";
	public static final Logger LOGGER = LogUtils.getLogger();

	public static final GameRule<Boolean> PET_FRIENDLY_FIRE = GameRules.registerBoolean(
			Companion.ID + ":pet_friendly_fire",
			GameRuleCategory.PLAYER,
			true);
	public static final GameRule<Boolean> IMMORTAL_PETS = GameRules.registerBoolean(
			Companion.ID + ":immortal_pets",
			GameRuleCategory.PLAYER,
			false);
	public static final GameRule<Boolean> ALWAYS_TELEPORT_HORSES = GameRules.registerBoolean(
			Companion.ID + ":always_teleport_horses",
			GameRuleCategory.PLAYER,
			false);

	public static final TagKey<EntityType<?>> IMMORTAL_BLACKLIST = TagKey.create(Registries.ENTITY_TYPE, id("immortal_blacklist"));
	public static final TagKey<Item> CHARGED_RANGED_WEAPONS = TagKey.create(Registries.ITEM, id("charged_ranged_weapons"));

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(ID, path);
	}
}
