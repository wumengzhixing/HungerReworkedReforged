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
package com.wu_meng.hungerreworkedreforged.network;

import io.netty.buffer.ByteBuf;
import com.wu_meng.hungerreworkedreforged.common.init.HRRAttachmentTypes;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import static com.wu_meng.hungerreworkedreforged.HungerReworked.MOD_ID;

/**
 * 客户端绑定的胃数据同步包，用于将服务端的胃数据同步到客户端喵~
 *
 * @param totalFood 胃中食物的总量喵~
 * @author liudongyu
 */
public record ClientboundStomachPacket(int totalFood) implements CustomPacketPayload, IHRRPacket {
	public static final Type<ClientboundStomachPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(MOD_ID, "stomach"));
	public static final StreamCodec<ByteBuf, ClientboundStomachPacket> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.INT, ClientboundStomachPacket::totalFood,
			ClientboundStomachPacket::new
	);

	/**
	 * 处理数据包，更新客户端玩家的胃数据喵~
	 *
	 * @param context 数据包上下文喵~
	 */
	@Override
	public void handle(IPayloadContext context) {
		context.enqueueWork(() -> context.player()
				.getData(HRRAttachmentTypes.PLAYER_STOMACH)
				.setTotalFood(this.totalFood));
	}

	@Override
	public Type<ClientboundStomachPacket> type() {
		return TYPE;
	}
}
