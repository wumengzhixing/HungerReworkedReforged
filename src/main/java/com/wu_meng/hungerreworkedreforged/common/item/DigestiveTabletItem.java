package com.wu_meng.hungerreworkedreforged.common.item;

import com.wu_meng.hungerreworkedreforged.common.init.HRRMobEffects;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;

import java.util.List;

/** 服用后提供短时助消化效果的非食物类药片。 */
public final class DigestiveTabletItem extends Item {
	public static final int USE_DURATION = 16;
	public static final int EFFECT_DURATION = 400;

	public DigestiveTabletItem(Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
		ItemStack stack = player.getItemInHand(usedHand);
		player.startUsingItem(usedHand);
		return InteractionResultHolder.consume(stack);
	}

	@Override
	public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity livingEntity) {
		if (!level.isClientSide() && livingEntity instanceof ServerPlayer player) {
			player.addEffect(new MobEffectInstance(
					HRRMobEffects.holder(HRRMobEffects.FAST_DIGESTION), EFFECT_DURATION, 0));
			player.awardStat(Stats.ITEM_USED.get(this));
			level.playSound(null, player.blockPosition(), SoundEvents.GENERIC_EAT,
					SoundSource.PLAYERS, 0.8F, 1.15F);
			player.gameEvent(GameEvent.EAT);
			if (!player.getAbilities().instabuild) {
				stack.shrink(1);
			}
		}
		return stack;
	}

	@Override
	public UseAnim getUseAnimation(ItemStack stack) {
		return UseAnim.EAT;
	}

	@Override
	public int getUseDuration(ItemStack stack, LivingEntity entity) {
		return USE_DURATION;
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context,
			List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
		tooltipComponents.add(Component.translatable(
				"item.hunger_reworked_reforged.digestive_tablet.tooltip").withStyle(ChatFormatting.GRAY));
	}
}
