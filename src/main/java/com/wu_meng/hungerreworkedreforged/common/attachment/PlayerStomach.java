/*
MIT License

        Copyright (c) 2022 Adneths

        Permission is hereby granted, free of charge, to any person obtaining a copy
        of this software and associated documentation files (the "Software"), to deal
        in the Software without restriction, including without limitation the rights
        to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
        copies of the Software, and to permit persons to whom the Software is
        furnished to do so, subject to the following conditions:

        The above copyright notice and this permission notice shall be included in all
        copies or substantial portions of the Software.

        THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
        IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
        FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
        AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
        LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
        OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
        SOFTWARE.
 */
package com.wu_meng.hungerreworkedreforged.common.attachment;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wu_meng.hungerreworkedreforged.common.CommonSide;
import com.wu_meng.hungerreworkedreforged.common.diet.ProxyIDietTracker;
import com.wu_meng.hungerreworkedreforged.common.init.HRRAttachmentTypes;
import com.wu_meng.hungerreworkedreforged.common.init.HRRAttributes;
import com.wu_meng.hungerreworkedreforged.common.init.HRRItemTags;
import com.wu_meng.hungerreworkedreforged.common.init.HRRMobEffects;
import com.wu_meng.hungerreworkedreforged.network.ClientboundStomachPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 玩家胃数据附件类，用于存储和管理玩家胃中的食物及其消化状态喵~
 * <p>
 * 该类实现了胃容量系统，食物不会立即生效，而是先存储在胃中，然后逐渐消化喵~
 * </p>
 *
 * @author liudongyu
 */
public class PlayerStomach {
	/** 防止恶意或异常食物把玩家附件及 HUD 无限撑大的安全上限。 */
	public static final int MAX_TOTAL_FOOD = 4096;
	public static final int MAX_CONTENT_ENTRIES = 512;

	public static final Codec<PlayerStomach> CODEC = Food.CODEC.listOf().fieldOf("content")
			.xmap(PlayerStomach::new, PlayerStomach::getContent)
			.codec();
	private final List<Food> content;
	private int totalFood;

	/**
	 * 默认构造方法，创建一个空的胃喵~
	 */
	public PlayerStomach() {
		this(Lists.newArrayList());
	}

	/** 创建一份完全独立的胃数据副本。 */
	public PlayerStomach(PlayerStomach source) {
		this(source.content.stream().map(Food::copy).toList());
	}

	/**
	 * 完整构造方法，用于反序列化或创建指定状态的胃喵~
	 *
	 * @param content 胃中的食物列表喵~
	 */
	protected PlayerStomach(List<Food> content) {
		this.content = new ArrayList<>();
		int runningTotal = 0;
		for (Food food : content) {
			int remaining = food.foodRemaining();
			if (remaining <= 0 || this.content.size() >= MAX_CONTENT_ENTRIES ||
					runningTotal > MAX_TOTAL_FOOD - remaining) {
				continue;
			}
			this.content.add(food);
			runningTotal += remaining;
		}
		this.totalFood = runningTotal;
	}

	/**
	 * 获取胃中的食物列表喵~
	 *
	 * @return 食物列表喵~
	 */
	public List<Food> getContent() {
		return List.copyOf(this.content);
	}

	/** 无需复制整个内容列表即可判断胃是否为空。 */
	public boolean isEmpty() {
		return this.content.isEmpty();
	}

	/**
	 * 获取胃中食物的总量喵~
	 *
	 * @return 食物总量喵~
	 */
	public int getTotalFood() {
		return this.totalFood;
	}

	/**
	 * 设置胃中食物的总量喵~
	 *
	 * @param totalFood 食物总量喵~
	 */
	public void setTotalFood(int totalFood) {
		this.totalFood = Math.clamp(totalFood, 0, MAX_TOTAL_FOOD);
	}

	/**
	 * 获取玩家的胃容量喵~
	 * <p>
	 * 基础胃容量为 20，可以通过额外胃容量属性增加喵~
	 * </p>
	 *
	 * @param player 玩家实体喵~
	 * @return 胃容量喵~
	 */
	public static int getStomachCapability(Player player) {
		AttributeInstance instance = player.getAttribute(HRRAttributes.EXTRA_STOMACH);
		return (instance == null ? 0 : (int)instance.getValue()) + 20;
	}

	/** 是否应由胃系统管理该食物，而不是立即走原版结算。 */
	public static boolean isStomachManagedFood(Player player, ItemStack stack) {
		FoodProperties properties = stack.getFoodProperties(player);
		return !player.getAbilities().instabuild && !player.isSpectator() &&
				properties != null && properties.nutrition() > 0 && !stack.is(HRRItemTags.IGNORE_STOMACH);
	}

	/** 判断指定营养值是否仍能安全加入胃数据。 */
	public boolean canAcceptNutrition(int nutrition) {
		return nutrition > 0 && this.content.size() < MAX_CONTENT_ENTRIES &&
				this.totalFood <= MAX_TOTAL_FOOD - nutrition;
	}

	/**
	 * 向胃中添加食物喵~
	 *
	 * @param player 玩家实体喵~
	 * @param food 要添加的食物喵~
	 */
	public boolean addFood(ServerPlayer player, Food food) {
		int remaining = food.foodRemaining();
		if (!this.canAcceptNutrition(remaining)) {
			return false;
		}
		this.content.add(food);
		this.totalFood += remaining;
		this.sendUpdatePacket(player);
		return true;
	}

	/**
	 * 从胃中移除最后一个食物（呕吐）喵~
	 *
	 * @param player 玩家实体喵~
	 */
	public void popFood(ServerPlayer player) {
		if (this.content.isEmpty()) {
			return;
		}
		this.content.removeLast();
		if(!player.level().isClientSide()) {
			this.totalFood = this.calculateTotalFood();
			this.sendUpdatePacket(player);
		}
	}

	/**
	 * 消化单个食物的内部方法喵~
	 *
	 * @param food 要消化的食物喵~
	 * @param amount 消化量喵~
	 * @param maxGain 最大可获得的食物值喵~
	 * @return 溢出的消化量喵~
	 */
	private DigestionResult digestFood(Food food, double amount, int maxGain) {
		if (amount <= 0) {
			return new DigestionResult(0, 0, 0);
		}
		double oldProgress = food.progress;
		double nProgress = food.digestionWeight <= 0 ? 1 :
				Math.min(1, oldProgress + amount / food.digestionWeight);
		int foodGain = food.foodGained(nProgress);
		if(maxGain < foodGain) {
			nProgress = food.factorDigestNeeded(maxGain);
			foodGain = maxGain;
		}
		float saturationGain = food.satGained(nProgress);

		food.progress = nProgress;

		double amountUsed = food.digestionWeight <= 0 ? 0 :
				Math.max(0, nProgress - oldProgress) * food.digestionWeight;
		return new DigestionResult(Math.max(0, amount - amountUsed), foodGain, saturationGain);
	}

	/**
	 * 执行消化过程，将胃中的食物转化为饥饿值和饱和度喵~
	 * <p>
	 * 消化过程会：
	 * <ul>
	 *     <li>按顺序消化胃中的食物，越早吃的食物消化越快喵~</li>
	 *     <li>将消化的食物转化为饥饿值和饱和度喵~</li>
	 *     <li>触发食物的药水效果喵~</li>
	 *     <li>检查是否过饱，如果是则添加过饱效果喵~</li>
	 * </ul>
	 * </p>
	 *
	 * @param player 玩家实体喵~
	 * @param amount 消化速率喵~
	 */
	public void digest(ServerPlayer player, double amount) {
		this.digest(player, amount, false);
	}

	/**
	 * 执行消化；允许溢出时即使饥饿值已满也会推进胃内容，超出的饥饿值自然舍弃。
	 */
	public void digest(ServerPlayer player, double amount, boolean allowFoodOverflow) {
		if (amount <= 0 || this.content.isEmpty()) {
			return;
		}
		int previousTotalFood = this.totalFood;
		int foodDigested = 0;
		float satDigested = 0;

		FoodData data = player.getFoodData();
		double remainingAmount = amount;
		int maxFood = allowFoodOverflow ? Integer.MAX_VALUE : 20 - data.getFoodLevel();
		List<Food> finished = Lists.newArrayList();
		for(int i = 0; i < this.content.size() && (allowFoodOverflow || maxFood > 0) && remainingAmount > 0; i++) {
			double part = i == this.content.size() - 1 ? remainingAmount : remainingAmount / 2;
			remainingAmount -= part;
			Food food = this.content.get(i);

			DigestionResult result = this.digestFood(food, part, maxFood);
			remainingAmount += result.unusedAmount();
			foodDigested += result.foodGained();
			satDigested += result.saturationGained();
			if(food.progress >= 1) {
				for(FoodProperties.PossibleEffect effect : food.effects) {
					if (player.level().getRandom().nextFloat() < effect.probability()) {
						player.addEffect(new MobEffectInstance(effect.effect()));
					}
				}
				finished.add(this.content.remove(i--));
			}
			if (!allowFoodOverflow) {
				maxFood = 20 - data.getFoodLevel() - foodDigested;
			}
			if(!allowFoodOverflow && maxFood < 1) {
				break;
			}
		}
		if (foodDigested > 0 || satDigested > 0) {
			int newFoodLevel = Math.min(20, data.getFoodLevel() + foodDigested);
			data.setFoodLevel(newFoodLevel);
			data.setSaturation(Math.min(newFoodLevel, data.getSaturationLevel() + satDigested));
		}

		ProxyIDietTracker diet = CommonSide.DIET_PROXY.get(player);
		if(diet != null && !finished.isEmpty()) {
			for (Food food : finished) {
				if (food.dietBlock.isPresent()) {
					diet.consumeBlock(food.dietBlock.get());
				} else {
					diet.consume(food.stack, food.foodCount, food.digestionWeight);
				}
			}
			diet.sync();
		}

		this.totalFood = this.calculateTotalFood();

		MobEffectInstance ss = player.getEffect(HRRMobEffects.STRONG_STOMACH);
		int ext;
		if((ext = this.totalFood - getStomachCapability(player) - (ss == null ? 0 : (ss.getAmplifier() + 1) * 8)) > 0 && !player.hasEffect(HRRMobEffects.VOMITING)) {
			player.addEffect(new MobEffectInstance(HRRMobEffects.OVERSTUFFED, 80, Math.max(0, (ext - 1) / 8)));
		}

		if (this.totalFood != previousTotalFood) {
			this.sendUpdatePacket(player);
		}
	}

	private int calculateTotalFood() {
		return this.content.stream().mapToInt(Food::foodRemaining).sum();
	}

	/**
	 * 向客户端发送胃数据更新包喵~
	 * <p>
	 * 此方法被提取为实例方法以便于单元测试喵~
	 * </p>
	 *
	 * @param player 玩家实体喵~
	 */
	protected void sendUpdatePacket(ServerPlayer player) {
		if (player.connection != null) {
			player.connection.send(new ClientboundStomachPacket(this.getTotalFood()));
		}
	}

	/**
	 * 向客户端发送胃数据更新包（静态方法，用于向后兼容）喵~
	 *
	 * @param player 玩家实体喵~
	 */
	public static void sendStomachUpdatePacket(ServerPlayer player) {
		PlayerStomach stomach = player.getData(HRRAttachmentTypes.PLAYER_STOMACH);
		stomach.sendUpdatePacket(player);
	}

	/**
	 * 从另一个胃数据复制内容喵~
	 *
	 * @param source 源胃数据喵~
	 */
	public void copyFrom(PlayerStomach source) {
		this.content.clear();
		int runningTotal = 0;
		for (Food sourceFood : source.content) {
			Food food = sourceFood.copy();
			int remaining = food.foodRemaining();
			if (remaining <= 0 || this.content.size() >= MAX_CONTENT_ENTRIES ||
					runningTotal > MAX_TOTAL_FOOD - remaining) {
				continue;
			}
			this.content.add(food);
			runningTotal += remaining;
		}
		this.totalFood = this.calculateTotalFood();
	}

	private record DigestionResult(double unusedAmount, int foodGained, float saturationGained) {
	}

	@Override
	public String toString() {
		return content.toString();
	}

	/**
	 * 食物数据类，表示胃中的一份食物及其消化进度喵~
	 *
	 * @author liudongyu
	 */
	public static class Food {
		public static final Codec<Food> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				ItemStack.CODEC.fieldOf("stack").forGetter(food -> food.stack),
				Codec.intRange(0, Integer.MAX_VALUE).fieldOf("food").forGetter(food -> food.foodCount),
				Codec.floatRange(0, Float.MAX_VALUE).fieldOf("sat").forGetter(food -> food.digestionWeight),
				Codec.doubleRange(0, 1).fieldOf("progress").forGetter(food -> food.progress),
				FoodProperties.PossibleEffect.CODEC.listOf().fieldOf("effects").forGetter(food -> food.effects),
				ResourceLocation.CODEC.optionalFieldOf("diet_block").forGetter(food -> food.dietBlock),
				Codec.floatRange(0, Float.MAX_VALUE).optionalFieldOf("saturation_points")
						.forGetter(food -> Optional.of(food.saturationPoints))
		).apply(instance, Food::new));

		private final ItemStack stack;
		private final int foodCount;
		private final float digestionWeight;
		private final float saturationPoints;
		private final Optional<ResourceLocation> dietBlock;

		private double progress;
		private final List<FoodProperties.PossibleEffect> effects;

		/**
		 * 构造食物数据类
		 * @param stack 物品
		 * @param foodCount 数量
		 * @param sat 饱和度
		 * @param progress 消化进度
		 * @param effects 效果
		 */
		public Food(ItemStack stack, int foodCount, float sat, double progress, List<FoodProperties.PossibleEffect> effects) {
			this(stack, foodCount, sat, legacySaturationPoints(foodCount, sat), progress, effects, Optional.empty());
		}

		/** 完整构造方法，分别指定消化权重和原版实际饱和度点数。 */
		public Food(ItemStack stack, int foodCount, float digestionWeight, float saturationPoints,
				double progress, List<FoodProperties.PossibleEffect> effects) {
			this(stack, foodCount, digestionWeight, saturationPoints, progress, effects, Optional.empty());
		}

		private Food(ItemStack stack, int foodCount, float digestionWeight, double progress,
				List<FoodProperties.PossibleEffect> effects, Optional<ResourceLocation> dietBlock,
				Optional<Float> saturationPoints) {
			this(stack, foodCount, digestionWeight,
					saturationPoints.orElseGet(() -> legacySaturationPoints(foodCount, digestionWeight)),
					progress, effects, dietBlock);
		}

		private Food(ItemStack stack, int foodCount, float digestionWeight, float saturationPoints,
				double progress, List<FoodProperties.PossibleEffect> effects, Optional<ResourceLocation> dietBlock) {
			this.stack = stack;
			this.foodCount = foodCount;
			this.digestionWeight = digestionWeight;
			this.saturationPoints = saturationPoints;
			this.progress = progress;
			this.effects = List.copyOf(effects);
			this.dietBlock = dietBlock;
		}

		/**
		 * 根据食物属性构造食物数据
		 * @param prop 食物属性
		 */
		public Food(FoodProperties prop) {
			this(ItemStack.EMPTY, prop);
		}

		/**
		 * 根据物品和食物属性构造食物数据
		 * @param stack 物品
		 * @param prop 食物属性
		 */
		public Food(ItemStack stack, FoodProperties prop) {
			this(stack.copyWithCount(1), prop.nutrition(), saturationModifier(prop), prop.saturation(), 0,
					prop.effects(), Optional.empty());
		}

		/** 根据方块食物构造数据，以便 AppleSeed 在消化完成后按口结算营养。 */
		public Food(ResourceLocation dietBlock, FoodProperties prop) {
			this(ItemStack.EMPTY, prop.nutrition(), saturationModifier(prop), prop.saturation(), 0,
					prop.effects(), Optional.of(dietBlock));
		}

		private static float saturationModifier(FoodProperties prop) {
			return prop.nutrition() <= 0 ? 0 : prop.saturation() / (2.0F * prop.nutrition());
		}

		private static float legacySaturationPoints(int foodCount, float saturationModifier) {
			return Math.max(0, foodCount * saturationModifier * 2.0F);
		}

		private Food copy() {
			return new Food(this.stack.copy(), this.foodCount, this.digestionWeight, this.saturationPoints, this.progress,
					this.effects, this.dietBlock);
		}

		/**
		 * 获取消化进度喵~
		 * @return 消化进度喵~
		 */
		public double progress() {
			return this.progress;
		}

		protected int foodDigested() {
			return (int) (this.progress * this.foodCount);
		}

		protected int foodRemaining() {
			return this.foodCount - this.foodDigested();
		}

		protected double factorDigestNeeded(int amount) {
			return this.foodCount <= 0 ? 1 : (double) (amount + foodDigested()) / this.foodCount;
		}

		protected int foodGained(double newProg) {
			return (int) (Math.min(1, newProg) * this.foodCount) - (int) (this.progress * this.foodCount);
		}

		protected float satGained(double newProg) {
			return (float) (Math.min(1, newProg) * this.saturationPoints - this.progress * this.saturationPoints);
		}

		@Override
		public String toString() {
			return String.format("%d %.1f - %.2f", this.foodCount, this.saturationPoints, this.progress);
		}
	}
}
