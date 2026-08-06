package com.wu_meng.hungerreworkedreforged.common.attachment;

import com.wu_meng.hungerreworkedreforged.common.CommonSide;
import com.wu_meng.hungerreworkedreforged.common.attachment.PlayerStomach.Food;
import com.wu_meng.hungerreworkedreforged.common.diet.ProxyDiet;
import com.wu_meng.hungerreworkedreforged.common.init.HRRAttributes;
import net.minecraft.SharedConstants;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.server.Bootstrap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.neoforged.fml.loading.LoadingModList;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

/**
 * PlayerStomach 类的单元测试喵~
 *
 * @author liudongyu
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("PlayerStomach 类测试")
class PlayerStomachTest {

	@Mock
	private ServerPlayer player;

	@Mock
	private ServerLevel level;

	@Mock
	private FoodData foodData;

	@Mock
	private AttributeInstance extraStomachAttribute;

	private PlayerStomach stomach;

	/**
	 * 欺骗核心加载类，让它们认为处于游戏加载状态，避免单测报错
	 */
	@BeforeAll
	static void bootstrap() {
		LoadingModList.of(List.of(), List.of(), List.of(), List.of(), Map.of());
		SharedConstants.tryDetectVersion();
		Bootstrap.bootStrap();
		CommonSide.DIET_PROXY = new ProxyDiet() {};
	}

	@BeforeEach
	void setUp() {
		this.stomach = new PlayerStomach();

		// 设置基本的 mock 行为
		lenient().when(this.player.getFoodData()).thenReturn(this.foodData);
		lenient().when(this.player.serverLevel()).thenReturn(this.level);
		lenient().when(this.player.level()).thenReturn(this.level);
		lenient().when(this.level.getRandom()).thenReturn(new LegacyRandomSource(12345L)); // 固定随机种子以便测试
		lenient().when(this.level.isClientSide()).thenReturn(false);

		// 设置默认的饥饿值和饱和度
		lenient().when(this.foodData.getFoodLevel()).thenReturn(10);
		lenient().when(this.foodData.getSaturationLevel()).thenReturn(5.0F);

		// 设置额外胃容量属性为 0（基础胃容量 20）
		lenient().when(this.player.getAttribute(HRRAttributes.EXTRA_STOMACH)).thenReturn(this.extraStomachAttribute);
		lenient().when(this.extraStomachAttribute.getValue()).thenReturn(0.0);

		// 设置没有任何效果
		lenient().when(this.player.hasEffect(any())).thenReturn(false);
		lenient().when(this.player.getEffect(any())).thenReturn(null);
	}

	@Test
	@DisplayName("测试空胃的初始状态")
	void testEmptyStomach() {
		assertEquals(0, this.stomach.getTotalFood(), "新创建的胃应该是空的喵~");
		assertTrue(this.stomach.getContent().isEmpty(), "新创建的胃内容应该是空列表喵~");
	}

	@Test
	@DisplayName("测试获取胃容量")
	void testGetStomachCapability() {
		// 基础胃容量是 20
		assertEquals(20, PlayerStomach.getStomachCapability(this.player), "基础胃容量应该是 20 喵~");

		// 设置额外胃容量为 10
		when(this.extraStomachAttribute.getValue()).thenReturn(10.0);
		assertEquals(30, PlayerStomach.getStomachCapability(this.player), "有额外胃容量时应该是 30 喵~");
	}

	@Test
	@DisplayName("测试添加食物到胃中")
	void testAddFood() {
		// 使用 Spy 来 Mock sendUpdatePacket 方法
		PlayerStomach spyEffect = spy(this.stomach);
		doNothing().when(spyEffect).sendUpdatePacket(this.player);

		FoodProperties properties = new FoodProperties.Builder()
				.nutrition(4)
				.saturationModifier(0.3F)
				.build();
		Food food = new Food(properties);

		assertTrue(spyEffect.addFood(this.player, food));

		assertEquals(1, spyEffect.getContent().size(), "添加食物后胃中应该有 1 个食物喵~");
		assertEquals(4, spyEffect.getTotalFood(), "添加 4 点食物后总量应该是 4 喵~");
		verify(spyEffect, times(1)).sendUpdatePacket(this.player);
	}

	@Test
	@DisplayName("测试单个食物的消化逻辑")
	void testDigestSingleFood() {
		// 创建一个 10 点食物值、5.0 饱和度的食物
		Food food = new Food(ItemStack.EMPTY, 10, 0.25F, 5.0F, 0.0, List.of());
		PlayerStomach spyEffect = spy(new PlayerStomach(List.of(food)));
		doNothing().when(spyEffect).sendUpdatePacket(this.player);

		// 当前饥饿值 10，最多可以获得 10 点食物
		when(this.foodData.getFoodLevel()).thenReturn(10);

		// 消化速率 0.1，对于 sat=0.25 的食物，进度增加 0.1/0.25 = 0.4
		spyEffect.digest(this.player, 0.1);

		// 验证食物进度
		assertEquals(0.4, food.progress(), 0.001, "消化后进度应该是 0.4 喵~");

		// 验证饥饿值增加（10 * 0.4 = 4）
		verify(this.foodData).setFoodLevel(14);

		// 验证饱和度增加（5.0 * 0.4 = 2.0）
		verify(this.foodData).setSaturation(7.0F);

		verify(spyEffect, times(1)).sendUpdatePacket(this.player);
	}

	@Test
	@DisplayName("测试消化完成后移除食物")
	void testDigestRemovesCompletedFood() {
		// 创建一个接近完成的食物
		Food food = new Food(ItemStack.EMPTY, 10, 0.25F, 5.0F, 0.95, List.of());
		PlayerStomach spyEffect = spy(new PlayerStomach(List.of(food)));
		doNothing().when(spyEffect).sendUpdatePacket(this.player);

		when(this.foodData.getFoodLevel()).thenReturn(10);

		// 消化速率足够完成剩余部分
		spyEffect.digest(this.player, 0.1);

		// 验证食物已被移除
		assertTrue(spyEffect.getContent().isEmpty(), "完全消化后食物应该被移除喵~");
		assertEquals(0, spyEffect.getTotalFood(), "完全消化后总食物量应该是 0 喵~");
		verify(spyEffect, times(1)).sendUpdatePacket(this.player);
	}

	@Test
	@DisplayName("测试多个食物的消化优先级")
	void testDigestMultipleFoodsInOrder() {
		// 添加三个食物
		Food food1 = new Food(ItemStack.EMPTY, 6, 0.25F, 3.0F, 0.0, List.of());
		Food food2 = new Food(ItemStack.EMPTY, 6, 0.25F, 3.0F, 0.0, List.of());
		Food food3 = new Food(ItemStack.EMPTY, 6, 0.25F, 3.0F, 0.0, List.of());
		PlayerStomach spyEffect = spy(new PlayerStomach(List.of(food1, food2, food3)));
		doNothing().when(spyEffect).sendUpdatePacket(this.player);

		when(this.foodData.getFoodLevel()).thenReturn(6);

		// 消化速率 0.1
		spyEffect.digest(this.player, 0.1);

		// 第一个食物应该获得更多的消化（因为有优先级）
		// 第一个食物获得 0.2 的消化量
		// 第二个食物获得 0.2 / 2 = 0.1 的消化量
		assertTrue(food1.progress() > 0, "第一个食物应该被消化喵~");
		assertTrue(food2.progress() < food1.progress(), "第一个食物应该消化更快喵~");
		verify(spyEffect, times(1)).sendUpdatePacket(this.player);
	}

	@Test
	@DisplayName("测试饥饿值满时停止消化")
	void testDigestStopsWhenFull() {
		Food food = new Food(ItemStack.EMPTY, 10, 0.25F, 5.0F, 0.0, List.of());
		PlayerStomach spyEffect = spy(new PlayerStomach(List.of(food)));

		// 饥饿值已满
		when(this.foodData.getFoodLevel()).thenReturn(20);

		spyEffect.digest(this.player, 0.1);

		// 验证食物没有被消化
		assertEquals(0.0, food.progress(), 0.001, "饥饿值满时不应该消化喵~");
		verify(this.foodData, never()).setFoodLevel(anyInt());
		verify(spyEffect, never()).sendUpdatePacket(this.player);
	}

	@Test
	@DisplayName("测试助消化效果可在饥饿值满时推进胃内容")
	void testForcedDigestWorksWhenFull() {
		Food food = new Food(ItemStack.EMPTY, 4, 0.25F, 2.0F, 0.0, List.of());
		PlayerStomach spyEffect = spy(new PlayerStomach(List.of(food)));
		doNothing().when(spyEffect).sendUpdatePacket(this.player);
		when(this.foodData.getFoodLevel()).thenReturn(20);

		spyEffect.digest(this.player, 0.25, true);

		assertTrue(spyEffect.isEmpty(), "强制消化应在满饥饿时清空已完成的食物");
		verify(this.foodData).setFoodLevel(20);
		verify(this.foodData).setSaturation(7.0F);
		verify(spyEffect).sendUpdatePacket(this.player);
	}

	@Test
	@DisplayName("测试胃安全上限拒绝异常超大食物")
	void testRejectsFoodOverSafetyLimit() {
		PlayerStomach spyEffect = spy(this.stomach);
		Food oversized = new Food(ItemStack.EMPTY, PlayerStomach.MAX_TOTAL_FOOD + 1,
				0.1F, 1.0F, 0.0, List.of());

		assertFalse(spyEffect.addFood(this.player, oversized));
		assertTrue(spyEffect.isEmpty());
		verify(spyEffect, never()).sendUpdatePacket(this.player);
	}

	@Test
	@DisplayName("测试移除最后一个食物（呕吐）")
	void testPopFood() {
		// 添加两个食物，使用 1e-6 避免精度问题
		Food food1 = new Food(ItemStack.EMPTY, 5, 0.25F, 0.600001, List.of());
		Food food2 = new Food(ItemStack.EMPTY, 3, 0.25F, 0.0, List.of());
		PlayerStomach spyEffect = spy(new PlayerStomach(List.of(food1, food2)));
		doNothing().when(spyEffect).sendUpdatePacket(this.player);

		spyEffect.popFood(this.player);

		// 验证最后一个食物被移除
		assertEquals(1, spyEffect.getContent().size(), "移除后应该剩 1 个食物喵~");
		assertEquals(food1, spyEffect.getContent().getFirst(), "应该移除最后一个食物喵~");

		// 验证总食物量重新计算（5 * (1 - 0.6) = 2.0）
		assertEquals(2, spyEffect.getTotalFood(), "移除后总食物量应该重新计算喵~");
		verify(spyEffect, times(1)).sendUpdatePacket(this.player);
	}

	@Test
	@DisplayName("测试从另一个胃复制数据")
	void testCopyFrom() {
		Food food = new Food(ItemStack.EMPTY, 10, 0.25F, 0.5, List.of());
		PlayerStomach source = new PlayerStomach(List.of(food));

		this.stomach.copyFrom(source);

		assertEquals(1, this.stomach.getContent().size(), "复制后应该有 1 个食物喵~");
		assertEquals(5, this.stomach.getTotalFood(), "复制后总食物量应该一致喵~");
		assertNotSame(source.getContent().getFirst(), this.stomach.getContent().getFirst(),
				"克隆后的食物对象必须独立，避免两个玩家共享消化进度喵~");
	}

	@Test
	@DisplayName("测试胃内容列表不可被外部修改")
	void testContentIsReadOnly() {
		assertThrows(UnsupportedOperationException.class,
				() -> this.stomach.getContent().add(new Food(ItemStack.EMPTY, 1, 0.1F, 0, List.of())));
	}

	@Test
	@DisplayName("测试空胃不会发送无意义同步包")
	void testEmptyStomachDoesNotSync() {
		PlayerStomach spyEffect = spy(this.stomach);

		spyEffect.digest(this.player, 0.1);

		verify(spyEffect, never()).sendUpdatePacket(this.player);
	}

	@Test
	@DisplayName("测试存档不再写入可由内容推导的总食物量")
	void testCodecOnlyPersistsContent() {
		Tag encoded = PlayerStomach.CODEC.encodeStart(NbtOps.INSTANCE, this.stomach).getOrThrow();
		assertTrue(encoded instanceof CompoundTag);
		CompoundTag tag = (CompoundTag)encoded;
		assertTrue(tag.contains("content"));
		assertFalse(tag.contains("total_food"));
	}

	@Test
	@DisplayName("测试仍可读取带旧冗余字段的 1.21 存档")
	void testCodecAcceptsLegacyTotalFoodField() {
		CompoundTag tag = new CompoundTag();
		tag.put("content", new net.minecraft.nbt.ListTag());
		tag.putInt("total_food", 123);

		PlayerStomach decoded = PlayerStomach.CODEC.parse(NbtOps.INSTANCE, tag).getOrThrow();

		assertTrue(decoded.isEmpty());
		assertEquals(0, decoded.getTotalFood());
	}
}
