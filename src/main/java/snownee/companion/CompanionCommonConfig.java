package snownee.companion;

import snownee.kiwi.config.KiwiConfig;
import snownee.kiwi.config.KiwiConfig.Comment;

@KiwiConfig
public final class CompanionCommonConfig {

	@Comment("Jump in place to dismount shoulder pets")
	public static boolean shoulderDismountSmartMode = true;
	@Comment("Vanilla is true")
	public static boolean shoulderDismountInWater = false;
	@Comment("Vanilla is true")
	public static boolean shoulderDismountUnderWater = true;
	@Comment("Vanilla is true")
	public static boolean shoulderDismountWhileFlying = false;
	@Comment("Vanilla is 0.5")
	public static float shoulderDismountFallDistance = 4;
	@Comment("Vanilla is 0")
	public static float shoulderDismountDamageThreshold = 0.5F;

	@Comment("When you travel through portals, teleport your pets and leashed animals as well")
	public static boolean portalTeleportingPets = true;
	@Comment(
		{ "Max number of animals that will be teleported with you", "-1 = Use the value of maxEntityCramming gamerule" }
	)
	public static int portalMaxTeleportedPets = -1;
	public static float petWontChangeDimensionUnlessOwnerIsNearbyRadius = 3;
	public static boolean petForceTeleportingIfFollowFailed = true;

	public static float petInjuredStatusHealthRatio = 0.2F;
	public static boolean petWontAttackWhenInjured = true;
	public static boolean petTeleportToOwnerWhenInjured = true;

}
