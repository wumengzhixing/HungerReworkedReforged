package com.wu_meng.hungerreworkedreforged.common.effect;

import com.wu_meng.hungerreworkedreforged.common.attachment.PlayerStomach;
import com.wu_meng.hungerreworkedreforged.common.init.HRRAttachmentTypes;
import net.minecraft.SharedConstants;
import net.minecraft.server.Bootstrap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.neoforged.fml.loading.LoadingModList;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * 食物效果类的单元测试喵~
 *
 * @author liudongyu
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("食物效果类测试")
class FoodMobEffectTest {

	@Mock
	private ServerPlayer player;

	@Mock
	private ServerLevel level;

	@Mock
	private FoodData foodData;

	@Mock
	private PlayerStomach stomach;

	private OverstuffedEffect overstuffedEffect;
	private VomitingEffect vomitingEffect;
	private StrongStomachEffect strongStomachEffect;

	/**
	 * 欺骗核心加载类，让它们认为处于游戏加载状态，避免单测报错
	 */
	@BeforeAll
	static void bootstrap() {
		LoadingModList.of(List.of(), List.of(), List.of(), List.of(), Map.of());
		SharedConstants.tryDetectVersion();
		Bootstrap.bootStrap();
	}

	@BeforeEach
	void setUp() {
		// 创建测试用的效果实例
		this.overstuffedEffect = new OverstuffedEffect(MobEffectCategory.HARMFUL, 0x8B4513);
		this.vomitingEffect = new VomitingEffect(MobEffectCategory.HARMFUL, 0x228B22);
		this.strongStomachEffect = new StrongStomachEffect(MobEffectCategory.BENEFICIAL, 0xFFD700);

		// 设置基本的 mock 行为
		lenient().when(this.player.level()).thenReturn(this.level);
		lenient().when(this.player.serverLevel()).thenReturn(this.level);
		lenient().when(this.player.getFoodData()).thenReturn(this.foodData);
		lenient().when(this.player.getData(HRRAttachmentTypes.PLAYER_STOMACH)).thenReturn(this.stomach);
		lenient().when(this.level.getRandom()).thenReturn(new LegacyRandomSource(12345L)); // 固定随机种子

		// Mock 音效和粒子效果相关方法
		lenient().when(this.player.blockPosition()).thenReturn(mock());
		lenient().when(this.player.getEyePosition()).thenReturn(mock());
	}

	@Test
	@DisplayName("测试过饱效果的触发频率")
	void testOverstuffedEffectTickFrequency() {
		// 应该每 20 tick 触发一次
		assertTrue(this.overstuffedEffect.shouldApplyEffectTickThisTick(20, 0), "第 20 tick 应该触发喵~");
		assertTrue(this.overstuffedEffect.shouldApplyEffectTickThisTick(40, 0), "第 40 tick 应该触发喵~");
		assertFalse(this.overstuffedEffect.shouldApplyEffectTickThisTick(19, 0), "第 19 tick 不应该触发喵~");
		assertFalse(this.overstuffedEffect.shouldApplyEffectTickThisTick(21, 0), "第 21 tick 不应该触发喵~");
	}

	@Test
	@DisplayName("测试呕吐效果的触发频率")
	void testVomitingEffectTickFrequency() {
		// 应该每 4 tick 触发一次
		assertTrue(this.vomitingEffect.shouldApplyEffectTickThisTick(4, 0), "第 4 tick 应该触发喵~");
		assertTrue(this.vomitingEffect.shouldApplyEffectTickThisTick(8, 0), "第 8 tick 应该触发喵~");
		assertFalse(this.vomitingEffect.shouldApplyEffectTickThisTick(3, 0), "第 3 tick 不应该触发喵~");
		assertFalse(this.vomitingEffect.shouldApplyEffectTickThisTick(5, 0), "第 5 tick 不应该触发喵~");
	}

	@Test
	@DisplayName("测试铁胃效果的触发频率")
	void testStrongStomachEffectTickFrequency() {
		// 应该每 10 tick 触发一次
		assertTrue(this.strongStomachEffect.shouldApplyEffectTickThisTick(10, 0), "第 10 tick 应该触发喵~");
		assertTrue(this.strongStomachEffect.shouldApplyEffectTickThisTick(20, 0), "第 20 tick 应该触发喵~");
		assertFalse(this.strongStomachEffect.shouldApplyEffectTickThisTick(9, 0), "第 9 tick 不应该触发喵~");
		assertFalse(this.strongStomachEffect.shouldApplyEffectTickThisTick(11, 0), "第 11 tick 不应该触发喵~");
	}

	@Test
	@DisplayName("测试过饱效果转变为呕吐效果")
	void testOverstuffedToVomitingTransition() {
		// 使用 Spy 来 Mock transitionToVomiting 方法
		OverstuffedEffect spyEffect = spy(this.overstuffedEffect);
		doNothing().when(spyEffect).transitionToVomiting(any(), anyInt());

		// 使用固定的随机种子，测试概率转换
		// 对于 amplifier=100，概率大于 100%，必然转化
		RandomSource testRandom = new LegacyRandomSource(999L); // 选择一个会触发转换的种子
		when(this.level.getRandom()).thenReturn(testRandom);

		// 极高的状态效果等级必然有一次触发转换
		spyEffect.applyEffectTick(this.player, 100);
		verify(spyEffect, times(1)).transitionToVomiting(this.player, 100);
	}

	@Test
	@DisplayName("测试一级过饱效果也有呕吐概率")
	void testBaseOverstuffedCanTransition() {
		OverstuffedEffect spyEffect = spy(this.overstuffedEffect);
		doNothing().when(spyEffect).transitionToVomiting(any(), anyInt());
		RandomSource random = mock(RandomSource.class);
		when(random.nextFloat()).thenReturn(0.0F);
		when(this.level.getRandom()).thenReturn(random);

		spyEffect.applyEffectTick(this.player, 0);

		verify(spyEffect).transitionToVomiting(this.player, 0);
	}

	@Test
	@DisplayName("测试铁胃效果移除过饱状态")
	void testStrongStomachRemovesOverstuffed() {
		// 使用 Spy 来 Mock hasOverstuffedEffect 和 removeOverstuffedEffect 方法
		StrongStomachEffect spyEffect = spy(this.strongStomachEffect);
		doReturn(true).when(spyEffect).hasOverstuffedEffect(this.player);
		doNothing().when(spyEffect).removeOverstuffedEffect(this.player);

		// 胃容量降低到阈值以下（总食物 20，胃容量 20，铁胃等级 0，阈值 = 20 + 8 = 28）
		when(this.stomach.getTotalFood()).thenReturn(20);
		when(this.player.getAttribute(any())).thenReturn(mock());
		when(this.player.getAttribute(any()).getValue()).thenReturn(0.0);

		spyEffect.applyEffectTick(this.player, 0);

		// 验证过饱效果被移除
		verify(spyEffect).removeOverstuffedEffect(this.player);
	}

	@Test
	@DisplayName("测试铁胃效果不移除过饱状态（胃容量仍超标）")
	void testStrongStomachDoesNotRemoveOverstuffedWhenStillOverfull() {
		// 使用 Spy 来 Mock hasOverstuffedEffect 和 removeOverstuffedEffect 方法
		StrongStomachEffect spyEffect = spy(this.strongStomachEffect);
		doReturn(true).when(spyEffect).hasOverstuffedEffect(this.player);

		// 胃容量仍然超标（总食物 30，胃容量 20，铁胃等级 0，阈值 = 20 + 8 = 28）
		when(this.stomach.getTotalFood()).thenReturn(30);
		when(this.player.getAttribute(any())).thenReturn(mock());
		when(this.player.getAttribute(any()).getValue()).thenReturn(0.0);

		spyEffect.applyEffectTick(this.player, 0);

		// 验证过饱效果没有被移除
		verify(spyEffect, never()).removeOverstuffedEffect(this.player);
	}

	@Test
	@DisplayName("测试呕吐效果清空胃中的食物")
	void testVomitingEmptiesStomach() {
		// 胃中有食物
		when(this.stomach.isEmpty()).thenReturn(false);

		this.vomitingEffect.applyEffectTick(this.player, 0);

		// 验证调用了 popFood
		verify(this.stomach).popFood(this.player);
	}

	@Test
	@DisplayName("测试呕吐效果在胃空时减少饥饿值")
	void testVomitingReducesFoodLevelWhenStomachEmpty() {
		// 胃是空的
		when(this.stomach.isEmpty()).thenReturn(true);

		// 当前饥饿值 15
		when(this.foodData.getFoodLevel()).thenReturn(15);

		this.vomitingEffect.applyEffectTick(this.player, 0);

		// 验证饥饿值减少 1
		verify(this.foodData).setFoodLevel(14);
	}

	@Test
	@DisplayName("测试呕吐效果不会让饥饿值低于阈值")
	void testVomitingDoesNotReduceFoodLevelBelowThreshold() {
		// 胃是空的
		when(this.stomach.isEmpty()).thenReturn(true);

		// 当前饥饿值 8（低于阈值 10）
		when(this.foodData.getFoodLevel()).thenReturn(8);

		this.vomitingEffect.applyEffectTick(this.player, 0);

		// 验证饥饿值没有减少
		verify(this.foodData, never()).setFoodLevel(anyInt());
	}

	@Test
	@DisplayName("测试呕吐效果的等级影响饥饿值阈值")
	void testVomitingAmplifierAffectsThreshold() {
		// 胃是空的
		when(this.stomach.isEmpty()).thenReturn(true);

		// 当前饥饿值 7，呕吐等级 1（阈值 = 10 - 2*1 = 8）
		when(this.foodData.getFoodLevel()).thenReturn(7);

		this.vomitingEffect.applyEffectTick(this.player, 1);

		// 验证饥饿值没有减少（因为 7 < 8）
		verify(this.foodData, never()).setFoodLevel(anyInt());

		// 当前饥饿值 9，应该减少
		when(this.foodData.getFoodLevel()).thenReturn(9);
		this.vomitingEffect.applyEffectTick(this.player, 1);

		// 验证饥饿值减少
		verify(this.foodData).setFoodLevel(8);
	}
}
