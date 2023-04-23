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
package net.mcbbs.uid1525632.hungerreworkedreforged.event;

import net.mcbbs.uid1525632.hungerreworkedreforged.HungerReworked;
import net.mcbbs.uid1525632.hungerreworkedreforged.capability.PlayerStomach;
import net.mcbbs.uid1525632.hungerreworkedreforged.capability.PlayerStomachProvider;
import net.mcbbs.uid1525632.hungerreworkedreforged.init.Registration;
import net.mcbbs.uid1525632.hungerreworkedreforged.util.Configuration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.Stats;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CakeBlock;
import net.minecraft.world.level.block.CandleBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.stream.Collectors;

public class FoodEventHandler
{

	@SubscribeEvent
	public static void onAttachCapabilitiesPlayer(AttachCapabilitiesEvent<Entity> event)
	{
		if (event.getObject() instanceof Player && !event.getObject().getCapability(PlayerStomachProvider.PLAYER_STOMACH).isPresent())
			event.addCapability(new ResourceLocation(HungerReworked.MOD_ID, "player_stomach"), new PlayerStomachProvider());
	}

	@SubscribeEvent
	public static void onPlayerCloned(PlayerEvent.Clone event)
	{
		if (!event.isWasDeath())
		{
			event.getOriginal().reviveCaps();
			event.getOriginal().getCapability(PlayerStomachProvider.PLAYER_STOMACH).ifPresent(oldStore -> {
				event.getPlayer().getCapability(PlayerStomachProvider.PLAYER_STOMACH).ifPresent(newStore -> {
					newStore.copyFrom(oldStore);
				});
			});
			event.getOriginal().invalidateCaps();
		}
	}

	@SubscribeEvent
	public static void onPlayerJoined(PlayerLoggedInEvent event)
	{
		Player player = event.getPlayer();
		if (!player.level.isClientSide)
			PlayerStomach.sendUpdatePacket(player);
	}

	@SubscribeEvent
	public static void onRegisterCapabilities(RegisterCapabilitiesEvent event)
	{
		event.register(PlayerStomach.class);
	}

	@SubscribeEvent
	public static void onTick(TickEvent.PlayerTickEvent event)
	{
		// No level.isClientside since the Capability has no proper syncing mechanism
		if (event.phase == TickEvent.Phase.END){
			var player = event.player;
			if (player.level.getGameTime() % 60 == 0)
			{
				if (!event.player.getAbilities().instabuild)
					event.player.getCapability(PlayerStomachProvider.PLAYER_STOMACH).ifPresent((stomach) -> stomach.digest(event.player, 0.1));
			}
			var effect = player.getEffect(Registration.FAST_DIGESTION.get());
			if (player.level.getGameTime() % Configuration.FAST_DIGEST_BASE_TICK_RATE.get() == 0 && effect!=null)
			{
				var amp = effect.getAmplifier() + 1;
				if (!event.player.getAbilities().instabuild)
					event.player.getCapability(PlayerStomachProvider.PLAYER_STOMACH).ifPresent((stomach) -> stomach.digest(event.player, amp * Configuration.FAST_DIGEST_CONSUME_RATE.get()));
			}
		}

	}
}
