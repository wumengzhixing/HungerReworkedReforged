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
package net.mcbbs.uid1525632.hungerreworkedreforged.network;

import net.mcbbs.uid1525632.hungerreworkedreforged.HungerReworked;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class Messages
{

	private static SimpleChannel INSTANCE;

	private static int packetId = 0;

	private static int id()
	{
		return packetId++;
	}

	public static void register()
	{
		SimpleChannel net = NetworkRegistry.ChannelBuilder.named(new ResourceLocation(HungerReworked.MOD_ID, "messages")).networkProtocolVersion(() -> "1.0")
				.clientAcceptedVersions(s -> true).serverAcceptedVersions(s -> true).simpleChannel();
		INSTANCE = net;

		net.messageBuilder(StomachPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT).decoder(StomachPacket::new)
				.encoder(StomachPacket::toBytes).consumer(StomachPacket::handle).add();
	}
	public static <MSG> void sendToPlayer(MSG message, ServerPlayer player)
	{
		INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), message);
	}
}
