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
package com.wu_meng.hungerreworkedreforged.common.init;

import com.wu_meng.hungerreworkedreforged.HungerReworked;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Hunger Reworked Reforged 属性注册器喵~
 * <p>
 * 负责注册模组自定义的实体属性，包括额外胃容量等属性喵~
 * </p>
 *
 * @author liudongyu
 */
public final class HRRAttributes {
    /**
     * 属性延迟注册器喵~
     */
    private static final DeferredRegister<Attribute> REGISTER = DeferredRegister.create(Registries.ATTRIBUTE, HungerReworked.MOD_ID);

    /**
     * 额外胃容量属性，用于扩展玩家的饥饿值上限喵~
     * <p>
     * 基础值为 0.0，范围为 [-1.0, 300.0]，该属性会同步到客户端喵~
     * </p>
     */
    public static final DeferredHolder<Attribute, Attribute> EXTRA_STOMACH = REGISTER.register(
            "extra_stomach",
            () -> new RangedAttribute("attribute.name.hunger_reworked_reforged.extra_stomach", 0.0D, -1.0D, 300.0D)
                    .setSyncable(true)
    );

    public static final DeferredHolder<Attribute, Attribute> DIGESTION_RATE = REGISTER.register(
            "digestion_rate",
            () -> new RangedAttribute("attribute.name.hunger_reworked_reforged.digestion_rate", 0.1D, 0, 100).setSyncable(true)
    );

    /**
     * 初始化属性注册器，将其绑定到模组事件总线上喵~
     *
     * @param modBus 模组事件总线喵~
     */
    public static void init(IEventBus modBus) {
        REGISTER.register(modBus);
    }

    private HRRAttributes() {
    }
}
