package snownee.companion;

import snownee.kiwi.config.KiwiConfig;
import snownee.kiwi.config.KiwiConfig.Comment;
import snownee.kiwi.config.KiwiConfig.Path;

@KiwiConfig
public final class CompanionCommonConfig {

	@Path("shoulderPets.smartDismount")
	@Comment("Jump in place to dismount shoulder pets")
	public static boolean shoulderDismountSmartMode = true;
	@Path("shoulderPets.dismountInWater")
	@Comment("Vanilla is true")
	public static boolean shoulderDismountInWater = false;
	@Path("shoulderPets.dismountUnderWater")
	@Comment("Vanilla is true")
	public static boolean shoulderDismountUnderWater = true;
	@Path("shoulderPets.dismountWhileFlying")
	@Comment("Vanilla is true")
	public static boolean shoulderDismountWhileFlying = false;
	@Path("shoulderPets.dismountFallDistance")
	@Comment("Vanilla is 0.5")
	public static float shoulderDismountFallDistance = 4;
	@Path("shoulderPets.dismountDamageThreshold")
	@Comment("Vanilla is 0")
	public static float shoulderDismountDamageThreshold = 0.5F;

	@Path("follow.portalTeleportingPets")
	@Comment("When you travel through portals, teleport your pets and leashed animals as well")
	public static boolean portalTeleportingPets = true;
	@Path("follow.portalMaxTeleportedPets")
	@Comment(
		{ "Max number of animals that will be teleported with you", "-1 = Use the value of maxEntityCramming gamerule" }
	)
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
