package snownee.companion;

import java.util.concurrent.CompletableFuture;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.ItemTagsProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import snownee.kiwi.datagen.KiwiLanguageProvider;

public final class CompanionDataGen {
	private CompanionDataGen() {
	}

	public static void gatherData(GatherDataEvent.Client event) {
		event.createProvider((output, lookupProvider) -> new KiwiLanguageProvider(output, Companion.ID, lookupProvider));
		event.createProvider(ItemTags::new);
	}

	public static class ItemTags extends ItemTagsProvider {

		public ItemTags(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
			super(output, lookupProvider, Companion.ID);
		}

		@Override
		protected void addTags(HolderLookup.Provider provider) {
			tag(Companion.CHARGED_RANGED_WEAPONS).add(Items.TRIDENT).addTag(Tags.Items.TOOLS_CROSSBOW);
		}
	}
}
