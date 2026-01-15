package snownee.companion;

import java.util.concurrent.CompletableFuture;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
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

	public static class ItemTags extends FabricTagsProvider.ItemTagsProvider {
		public ItemTags(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> completableFuture) {
			super(output, completableFuture);
		}

		@Override
		protected void addTags(HolderLookup.Provider provider) {
			valueLookupBuilder(Companion.CHARGED_RANGED_WEAPONS).add(Items.TRIDENT).forceAddTag(ConventionalItemTags.CROSSBOW_TOOLS);
		}
	}
}
