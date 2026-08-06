package com.wu_meng.hungerreworkedreforged.gametest;

import com.mojang.authlib.GameProfile;
import com.wu_meng.hungerreworkedreforged.common.CommonSide;
import com.wu_meng.hungerreworkedreforged.common.FoodConsumptionTracker;
import com.wu_meng.hungerreworkedreforged.common.attachment.PlayerStomach;
import com.wu_meng.hungerreworkedreforged.common.diet.ProxyIDietGroup;
import com.wu_meng.hungerreworkedreforged.common.diet.ProxyIDietTracker;
import com.wu_meng.hungerreworkedreforged.common.init.HRRAttachmentTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ClientInformation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import static com.wu_meng.hungerreworkedreforged.HungerReworked.MOD_ID;

/** 覆盖无法通过普通单元测试执行的 Mixin 与 AppleSeed 端到端路径。 */
@GameTestHolder(MOD_ID)
@PrefixGameTestTemplate(false)
public final class HRRGameTests {
	@GameTest(templateNamespace = "minecraft", template = "empty", timeoutTicks = 40)
	public static void itemFoodEntersStomachWithoutImmediateHunger(GameTestHelper helper) {
		ServerPlayer player = makeServerPlayer(helper);
		player.getFoodData().setFoodLevel(10);
		ItemStack apple = new ItemStack(Items.APPLE);
		FoodProperties properties = apple.getFoodProperties(player);

		player.eat(helper.getLevel(), apple, properties);

		PlayerStomach stomach = player.getData(HRRAttachmentTypes.PLAYER_STOMACH);
		helper.assertValueEqual(player.getFoodData().getFoodLevel(), 10,
				"FoodData must not increase before digestion");
		helper.assertValueEqual(stomach.getTotalFood(), properties.nutrition(),
				"The consumed apple must be stored in the stomach");
		helper.assertTrue(apple.isEmpty(), "The consumed stack must still shrink");
		FoodConsumptionTracker.consumeDeferred(player, apple);
		helper.succeed();
	}

	@GameTest(templateNamespace = "minecraft", template = "empty", timeoutTicks = 60)
	public static void cakeNutritionIsAppliedOnlyAfterDigestion(GameTestHelper helper) {
		ServerPlayer player = makeServerPlayer(helper);
		player.getFoodData().setFoodLevel(10);
		BlockPos relativePos = new BlockPos(1, 1, 1);
		helper.setBlock(relativePos, Blocks.CAKE);
		BlockPos pos = helper.absolutePos(relativePos);
		BlockHitResult hit = new BlockHitResult(Vec3.atCenterOf(pos), Direction.UP, pos, false);

		ProxyIDietTracker diet = CommonSide.DIET_PROXY.get(player);
		helper.assertTrue(CommonSide.isDietIntegrationActive() && diet != null,
				"The development GameTest runtime must include AppleSeed");
		Map<String, Float> before = new LinkedHashMap<>();
		for (ProxyIDietGroup group : CommonSide.DIET_PROXY.getGroups()) {
			before.put(group.getName(), diet.getValue(group.getName()));
		}
		helper.assertFalse(before.isEmpty(), "AppleSeed must expose at least one diet group");

		NeoForge.EVENT_BUS.post(new PlayerInteractEvent.RightClickBlock(
				player, InteractionHand.MAIN_HAND, pos, hit));
		assertDietValues(helper, diet, before, "AppleSeed applied cake nutrition before the cake bite succeeded");

		helper.getLevel().getBlockState(pos).useWithoutItem(helper.getLevel(), player, hit);
		PlayerStomach stomach = player.getData(HRRAttachmentTypes.PLAYER_STOMACH);
		helper.assertValueEqual(stomach.getTotalFood(), 2, "The cake bite must enter the stomach");
		helper.assertValueEqual(player.getFoodData().getFoodLevel(), 10,
				"Cake hunger must remain delayed");
		assertDietValues(helper, diet, before, "Cake nutrition changed before digestion completed");

		stomach.digest(player, 10.0D, true);
		helper.assertValueEqual(stomach.getTotalFood(), 0, "The cake bite must finish digesting");
		helper.assertValueEqual(player.getFoodData().getFoodLevel(), 12,
				"The digested cake bite must restore hunger");
		boolean increased = before.entrySet().stream()
				.anyMatch(entry -> diet.getValue(entry.getKey()) > entry.getValue());
		helper.assertTrue(increased, "AppleSeed nutrition must increase after digestion");
		helper.succeed();
	}

	private static void assertDietValues(GameTestHelper helper, ProxyIDietTracker diet,
			Map<String, Float> expected, String message) {
		for (Map.Entry<String, Float> entry : expected.entrySet()) {
			helper.assertValueEqual(diet.getValue(entry.getKey()), entry.getValue(), message);
		}
	}

	private static ServerPlayer makeServerPlayer(GameTestHelper helper) {
		return new ServerPlayer(
				helper.getLevel().getServer(),
				helper.getLevel(),
				new GameProfile(UUID.randomUUID(), "hrr-gametest-player"),
				ClientInformation.createDefault()
		);
	}

	private HRRGameTests() {
	}
}
