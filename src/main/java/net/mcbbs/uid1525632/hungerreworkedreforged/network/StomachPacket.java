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

import java.util.function.Supplier;

import net.mcbbs.uid1525632.hungerreworkedreforged.capability.PlayerStomach;
import net.mcbbs.uid1525632.hungerreworkedreforged.capability.PlayerStomachProvider;
import net.mcbbs.uid1525632.hungerreworkedreforged.init.ClientSide;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public class StomachPacket
{
	int totalFood;
	
	public StomachPacket(PlayerStomach stomach)
	{
		this.totalFood = stomach.totalFood;
	}

	public StomachPacket(FriendlyByteBuf buf)
	{
		totalFood = buf.readInt();
	}

	public void toBytes(FriendlyByteBuf buf)
	{
		buf.writeInt(totalFood);
	}

	public boolean handle(Supplier<NetworkEvent.Context> supplier)
	{
		NetworkEvent.Context ctx = supplier.get();
		ctx.enqueueWork(() -> {
			PlayerStomach data = ClientSide.minecraft.player.getCapability(PlayerStomachProvider.PLAYER_STOMACH).orElse(null);
			if(data != null)
				data.totalFood = this.totalFood;
		});
		return true;
	}
}
