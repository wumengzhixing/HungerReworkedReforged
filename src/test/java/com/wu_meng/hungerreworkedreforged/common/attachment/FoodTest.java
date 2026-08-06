package com.wu_meng.hungerreworkedreforged.common.attachment;

import com.wu_meng.hungerreworkedreforged.common.attachment.PlayerStomach.Food;
import net.minecraft.SharedConstants;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.loading.LoadingModList;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * PlayerStomach.Food 类的单元测试喵~
 *
 * @author liudongyu
 */
@DisplayName("Food 类测试")
class FoodTest {

	/**
	 * 创建一个测试用的 Food 对象喵~
	 *
	 * @param foodCount 食物值喵~
	 * @param saturation 饱和度喵~
	 * @param progress 消化进度喵~
	 * @return Food 对象喵~
	 */
	private Food createFood(int foodCount, float saturation, double progress) {
		return new Food(ItemStack.EMPTY, foodCount, saturation / (2.0F * foodCount),
				saturation, progress, List.of());
	}

	/**
	 * 欺骗核心加载类，让它们认为处于游戏加载状态，避免单测报错
	 */
	@BeforeAll
	static void bootstrap() {
		LoadingModList.of(List.of(), List.of(), List.of(), List.of(), Map.of());
		SharedConstants.tryDetectVersion();
		Bootstrap.bootStrap();
	}

	@Test
	@DisplayName("测试已消化食物量计算")
	void testFoodDigested() {
		Food food = this.createFood(10, 5.0F, 0.5);
		assertEquals(5, food.foodDigested(), "消化进度 50% 时，应该消化了 5 点食物喵~");

		food = this.createFood(10, 5.0F, 0.0);
		assertEquals(0, food.foodDigested(), "消化进度 0% 时，应该消化了 0 点食物喵~");

		food = this.createFood(10, 5.0F, 1.0);
		assertEquals(10, food.foodDigested(), "消化进度 100% 时，应该消化了 10 点食物喵~");
	}

	@Test
	@DisplayName("测试剩余食物量计算")
	void testFoodRemaining() {
		Food food = this.createFood(10, 5.0F, 0.3);
		assertEquals(7, food.foodRemaining(), "消化进度 30% 时，应该剩余 7 点食物喵~");

		food = this.createFood(10, 5.0F, 0.0);
		assertEquals(10, food.foodRemaining(), "消化进度 0% 时，应该剩余 10 点食物喵~");

		food = this.createFood(10, 5.0F, 1.0);
		assertEquals(0, food.foodRemaining(), "消化进度 100% 时，应该剩余 0 点食物喵~");
	}

	@Test
	@DisplayName("测试达到指定消化量所需的进度因子")
	void testFactorDigestNeeded() {
		Food food = this.createFood(10, 5.0F, 0.2);
		// 当前已消化 2 点，要再消化 3 点（总共 5 点），需要的进度是 5/10 = 0.5
		assertEquals(0.5, food.factorDigestNeeded(3), 0.001, "要消化 3 点食物，需要进度达到 0.5 喵~");

		food = this.createFood(10, 5.0F, 0.0);
		assertEquals(0.5, food.factorDigestNeeded(5), 0.001, "从 0 开始消化 5 点食物，需要进度达到 0.5 喵~");
	}

	@Test
	@DisplayName("测试新进度下获得的食物量")
	void testFoodGained() {
		Food food = this.createFood(10, 5.0F, 0.2);
		// 从 0.2 进度到 0.5 进度，应该获得 (5-2) = 3 点食物
		assertEquals(3, food.foodGained(0.5), "从 20% 进度到 50% 进度，应该获得 3 点食物喵~");

		food = this.createFood(10, 5.0F, 0.0);
		assertEquals(5, food.foodGained(0.5), "从 0% 进度到 50% 进度，应该获得 5 点食物喵~");

		food = this.createFood(10, 5.0F, 0.9);
		assertEquals(1, food.foodGained(1.0), "从 90% 进度到 100% 进度，应该获得 1 点食物喵~");
	}

	@Test
	@DisplayName("测试新进度下获得的饱和度")
	void testSatGained() {
		// foodCount=10, 原版实际饱和度为 5.0
		Food food = this.createFood(10, 5.0F, 0.0);
		assertEquals(2.5F, food.satGained(0.5), 0.001F, "从 0% 进度到 50% 进度，应该获得一半饱和度喵~");

		food = this.createFood(10, 5.0F, 0.5);
		assertEquals(2.5F, food.satGained(1.0), 0.001F, "从 50% 进度到 100% 进度，应该获得剩余一半饱和度喵~");
	}

	@Test
	@DisplayName("测试进度超过 1.0 时的边界情况")
	void testBoundaryConditions() {
		Food food = this.createFood(10, 5.0F, 0.9);
		// 即使新进度超过 1.0，foodGained 也应该正确处理
		assertEquals(1, food.foodGained(1.5), "进度超过 1.0 时，应该按 1.0 计算喵~");
	}

	@Test
	@DisplayName("测试从 FoodProperties 构造 Food")
	void testConstructFromFoodProperties() {
		FoodProperties properties = new FoodProperties.Builder()
				.nutrition(8)
				.saturationModifier(0.6F)
				.build();

		Food food = new Food(properties);

		assertEquals(8, food.foodRemaining(), "新创建的食物应该有完整的食物值喵~");
		assertEquals(0, food.foodDigested(), "新创建的食物消化进度应该是 0 喵~");
		assertEquals(9.6F, food.satGained(1.0), 0.001F,
				"应该使用 FoodProperties 中的原版实际饱和度点数喵~");
	}

	@Test
	@DisplayName("测试零营养食物不会产生除零异常")
	void testZeroNutritionFood() {
		FoodProperties properties = new FoodProperties.Builder()
				.nutrition(0)
				.saturationModifier(0.0F)
				.build();

		Food food = new Food(properties);

		assertEquals(0, food.foodRemaining());
		assertEquals(1.0, food.factorDigestNeeded(1));
	}
}
