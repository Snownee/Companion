package snownee.companion.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.world.level.entity.EntityAccess;
import net.minecraft.world.level.entity.EntitySection;
import net.minecraft.world.level.entity.EntitySectionStorage;
import net.minecraft.world.level.entity.PersistentEntitySectionManager;
import net.minecraft.world.level.entity.Visibility;
import snownee.companion.Hooks;

@Mixin(PersistentEntitySectionManager.class)
public abstract class PersistentEntitySectionManagerMixin {

	@Final
	@Shadow
	private LongSet chunksToUnload;
	@Final
	@Shadow
	private Long2ObjectMap<Visibility> chunkVisibility;
	@Final
	@Shadow
	EntitySectionStorage<EntityAccess> sectionStorage;

	@Inject(
			at = @At(
					value = "INVOKE", target = "Lnet/minecraft/world/level/entity/PersistentEntitySectionManager;processUnloads()V"
			), method = "tick"
	)
	private void companion_tick(CallbackInfo ci) {
		chunksToUnload.forEach(l -> {
			if (this.chunkVisibility.get(l) != Visibility.HIDDEN || !areEntitiesLoaded(l)) {
				return;
			}
			List<EntityAccess> entities = sectionStorage.getExistingSectionsInChunk(l).flatMap(EntitySection::getEntities).toList();
			Hooks.handleChunkPreUnload(entities);
		});
	}

	@Shadow
	public abstract boolean areEntitiesLoaded(long l);

}
