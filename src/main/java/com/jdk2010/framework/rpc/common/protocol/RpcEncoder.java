package com.jdk2010.framework.rpc.common.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import com.jdk2010.framework.rpc.common.serialization.kryo.KryoSerializer;

public class RpcEncoder extends MessageToByteEncoder {

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        byte[] data = KryoSerializer.serialize(msg);
        out.writeInt(data.length);
        out.writeBytes(data);
    }

}
