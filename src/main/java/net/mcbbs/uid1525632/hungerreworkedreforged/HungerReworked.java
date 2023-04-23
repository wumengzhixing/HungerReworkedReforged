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
package net.mcbbs.uid1525632.hungerreworkedreforged;

import net.mcbbs.uid1525632.hungerreworkedreforged.init.ClientSide;
import net.mcbbs.uid1525632.hungerreworkedreforged.init.CommonSide;
import net.mcbbs.uid1525632.hungerreworkedreforged.init.Registration;
import net.mcbbs.uid1525632.hungerreworkedreforged.util.Configuration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(HungerReworked.MOD_ID)
public class HungerReworked
{
    public static final String MOD_ID = "hunger_reworked_reforged";

    public static final TagKey<Item> IGNORE_STOMACH = ItemTags.create(new ResourceLocation(MOD_ID,"ignore_stomach"));

    public HungerReworked()
    {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Configuration.COMMON_CONFIG);
		Registration.init();
        IEventBus modbus = FMLJavaModLoadingContext.get().getModEventBus();
        modbus.register(CommonSide.class);
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> modbus.register(ClientSide.class));
    }
}
