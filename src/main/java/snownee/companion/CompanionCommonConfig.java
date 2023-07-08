package snownee.companion;

import snownee.kiwi.config.KiwiConfig;
import snownee.kiwi.config.KiwiConfig.Path;

@KiwiConfig
public final class CompanionCommonConfig {

	@Path("shoulderPets.smartDismount")
	public static boolean shoulderDismountSmartMode = true;
	@Path("shoulderPets.dismountInWater")
	public static boolean shoulderDismountInWater = false;
	@Path("shoulderPets.dismountUnderWater")
	public static boolean shoulderDismountUnderWater = true;
	@Path("shoulderPets.dismountWhileFlying")
	public static boolean shoulderDismountWhileFlying = false;
	@Path("shoulderPets.dismountFallDistance")
	public static float shoulderDismountFallDistance = 4;
	@Path("shoulderPets.dismountDamageThreshold")
	public static float shoulderDismountDamageThreshold = 0.5F;

	@Path("follow.portalTeleportingPets")
	public static boolean portalTeleportingPets = true;
	@Path("follow.portalMaxTeleportedPets")
	public static int portalMaxTeleportedPets = -1;
	@Path("follow.petWontChangeDimensionUnlessOwnerIsNearbyRadius")
	public static float petWontChangeDimensionUnlessOwnerIsNearbyRadius = 3;
	@Path("follow.petForceTeleportingIfFollowFailed")
	public static boolean petForceTeleportingIfFollowFailed = true;

	@Path("injuredStatus.healthRatio")
	public static float petInjuredStatusHealthRatio = 0.2F;
	@Path("injuredStatus.stopAttacking")
	public static boolean petWontAttackWhenInjured = true;
	@Path("injuredStatus.teleportToOwner")
	public static boolean petTeleportToOwnerWhenInjured = true;

	@Path("combat.betterSweepingEdgeEffect")
	public static boolean betterSweepingEdgeEffect = true;

}
