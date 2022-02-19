package snownee.companion.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.world.level.GameRules;

@Mixin(GameRules.BooleanValue.class)
public interface BooleanValueAccess {

	@Invoker
	static GameRules.Type<GameRules.BooleanValue> callCreate(boolean value) {
		throw new AssertionError();
	}

}
