package com.wu_meng.hungerreworkedreforged.client.overlay;

import com.wu_meng.hungerreworkedreforged.common.attachment.PlayerStomach;
import com.wu_meng.hungerreworkedreforged.common.init.HRRAttachmentTypes;
import com.wu_meng.hungerreworkedreforged.common.init.HRRMobEffects;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import static com.wu_meng.hungerreworkedreforged.HungerReworked.MOD_ID;

/**
 * 胃容量 HUD 渲染层，用于在游戏界面上显示玩家的胃容量喵~
 *
 * @author liudongyu
 */
@OnlyIn(Dist.CLIENT)
public class StomachOverlay implements LayeredDraw.Layer {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(MOD_ID, "stomach_hud");
	private static final int MAX_RENDERED_FOOD = 512;

    protected static final ResourceLocation TEXTURE_LOCATION = ResourceLocation.fromNamespaceAndPath(MOD_ID, "textures/gui/stomach.png");

    /**
     * 渲染胃容量 HUD 喵~
     * <p>
     * 根据胃中食物的数量和胃容量，渲染多行食物图标喵~
     * 铁胃效果会使部分图标显示为特殊颜色喵~
     * </p>
     *
     * @param transform    GUI 图形上下文喵~
     * @param deltaTracker 帧时间追踪器喵~
     */
    @Override
    public void render(GuiGraphics transform, DeltaTracker deltaTracker) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || !this.shouldRenderOverlay(mc, mc.player)) {
            return;
        }

        PlayerStomach stomach = mc.player.getData(HRRAttachmentTypes.PLAYER_STOMACH);
        int foodAmount = stomach.getTotalFood();

        MobEffectInstance ss = mc.player.getEffect(HRRMobEffects.STRONG_STOMACH);
        int ssa = ss == null ? 0 : ss.getAmplifier() + 1;
        int left = transform.guiWidth() / 2 + 91;

        int top =transform.guiHeight() - mc.gui.rightHeight;
		long uncappedMaxAmount = Math.max((long)foodAmount,
				(long)PlayerStomach.getStomachCapability(mc.player) + (long)ssa * 8);
		int maxAmount = (int)Math.clamp(uncappedMaxAmount, 0, MAX_RENDERED_FOOD);
		int renderedFoodAmount = Math.min(foodAmount, MAX_RENDERED_FOOD);

        int rows = Math.max(1, (maxAmount + 19) / 20);
        int rowHeight = Math.max(3, 12 - rows * 2);
        for (int j = rows - 1; j > -1; j--) {
            for (int i = 0; i < Math.min(10, j == 0 ? 10 : ((maxAmount + 1) / 2) - j * 10); i++) {
                transform.blit(
                        TEXTURE_LOCATION,
                        left - 9 - 8 * i,
                        top - rowHeight * j,
                        8, 8,
						getUOffset(renderedFoodAmount, i, j),
                        j > 0 && i + j * 10 - 10 < ssa * 4 ? 16 : 0,
                        16, 16, 48, 32
                );
            }
		}
		if (foodAmount > MAX_RENDERED_FOOD) {
			String totalText = Integer.toString(foodAmount);
			transform.drawString(mc.font, totalText, left + 2,
					top - rowHeight * (rows - 1), 0xFFFFFF, true);
		}

		mc.gui.rightHeight += rowHeight * (rows - 1) + 10;
    }

    /**
     * 根据食物量和位置计算纹理 U 偏移量喵~
     *
     * @param foodAmount 食物总量喵~
     * @param i          横向索引喵~
     * @param j          纵向索引喵~
     * @return 纹理 U 偏移量喵~
     */
    private static int getUOffset(int foodAmount, int i, int j) {
        if (foodAmount - i * 2 - j * 20 < 1) {
            return 0;
        }
        return foodAmount - i * 2 - j * 20 == 1 ? 16 : 32;
    }

    /**
     * 判断是否应该渲染胃容量 HUD 喵~
     *
     * @param mc     Minecraft 客户端实例喵~
     * @param player 玩家实体喵~
     * @return 是否应该渲染喵~
     */
    public boolean shouldRenderOverlay(Minecraft mc, Player player) {
        return !mc.options.hideGui && mc.gameMode != null && !player.getAbilities().instabuild && !player.isSpectator();
    }
}
