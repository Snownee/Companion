package snownee.companion;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.GameRules;

public class Companion {
	public static final String ID = "companion";

	public static final Logger LOGGER = LogUtils.getLogger();

	public static final GameRules.Key<GameRules.BooleanValue> PET_FRIENDLY_FIRE = CommonProxy.registerRule(
			"petFriendlyFire",
			GameRules.Category.PLAYER,
			true);
	public static final GameRules.Key<GameRules.BooleanValue> IMMORTAL_PETS = CommonProxy.registerRule(
			"immortalPets",
			GameRules.Category.PLAYER,
			false);
	public static final GameRules.Key<GameRules.BooleanValue> ALWAYS_TELEPORT_HORSES = CommonProxy.registerRule(
			"alwaysTeleportHorses",
			GameRules.Category.PLAYER,
			false);

	public static final TagKey<EntityType<?>> IMMORTAL_BLACKLIST = TagKey.create(Registries.ENTITY_TYPE, id("immortal_blacklist"));
	public static final TagKey<Item> CHARGED_RANGED_WEAPONS = TagKey.create(Registries.ITEM, id("charged_ranged_weapons"));

	public static ResourceLocation id(String path) {
		return ResourceLocation.fromNamespaceAndPath(ID, path);
	}
}
