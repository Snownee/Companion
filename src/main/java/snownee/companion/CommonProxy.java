package snownee.companion;

import java.util.Objects;

import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.Tags;

@Mod(Companion.ID)
public class CommonProxy {
	public CommonProxy(IEventBus modBus) {
		Objects.requireNonNull(Companion.PET_FRIENDLY_FIRE);
		modBus.addListener(CompanionDataGen::gatherData);
	}

	public static boolean isRangedWeapon(ItemStack itemStack) {
		return itemStack.is(Tags.Items.RANGED_WEAPON_TOOLS);
	}
}
