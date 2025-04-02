package snownee.companion;

import java.util.concurrent.CompletableFuture;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.Items;
import snownee.kiwi.datagen.KiwiLanguageProvider;

public class CompanionDataGen implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
		FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
		pack.addProvider(KiwiLanguageProvider::new);
		pack.addProvider(ItemTags::new);
	}

	public static class ItemTags extends FabricTagProvider.ItemTagProvider {
		public ItemTags(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> completableFuture) {
			super(output, completableFuture);
		}

		@Override
		protected void addTags(HolderLookup.Provider provider) {
			tag(Hooks.CHARGED_RANGED_WEAPONS).add(Items.TRIDENT).forceAddTag(ConventionalItemTags.CROSSBOW_TOOLS);
		}
	}
}
