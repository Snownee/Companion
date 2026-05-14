package snownee.companion;

import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.gamerules.GameRule;

public class Companion {
	public static final String ID = "companion";
	public static final Logger LOGGER = LogUtils.getLogger();

	public static @Nullable GameRule<Boolean> PET_FRIENDLY_FIRE;
	public static @Nullable GameRule<Boolean> IMMORTAL_PETS;
	public static @Nullable GameRule<Boolean> ALWAYS_TELEPORT_HORSES;

	public static final TagKey<EntityType<?>> IMMORTAL_BLACKLIST = TagKey.create(Registries.ENTITY_TYPE, id("immortal_blacklist"));
	public static final TagKey<Item> CHARGED_RANGED_WEAPONS = TagKey.create(Registries.ITEM, id("charged_ranged_weapons"));

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(ID, path);
	}
}
