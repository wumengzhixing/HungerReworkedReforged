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
package com.wu_meng.hungerreworkedreforged.client;

import com.wu_meng.hungerreworkedreforged.client.overlay.StomachOverlay;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

/**
 * 客户端侧事件处理类，负责注册 GUI 层和管理食物图标偏移量喵~
 *
 * @author liudongyu
 */
public final class ClientSide {
	/**
	 * 注册 GUI 层事件处理器喵~
	 * <p>
	 * 注册两个 GUI 层：
	 * <ul>
	 *     <li>food_offset 层：用于计算和更新食物图标的偏移量喵~</li>
	 *     <li>stomach_hud 层：用于渲染胃容量显示界面喵~</li>
	 * </ul>
	 * </p>
	 *
	 * @param event GUI 层注册事件喵~
	 */
	public static void onRegisterGuiLayers(RegisterGuiLayersEvent event) {
		event.registerAbove(VanillaGuiLayers.FOOD_LEVEL, StomachOverlay.ID, new StomachOverlay());
	}

	private ClientSide() {
	}
}
