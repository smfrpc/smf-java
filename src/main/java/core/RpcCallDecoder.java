package core;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.List;

public class RpcCallDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf response, List<Object> out) {

        byte[] hdrbytes = new byte[16];
        Arrays.fill(hdrbytes, (byte) 0);
        response.readBytes(hdrbytes);
        ByteBuffer bb = ByteBuffer.wrap(hdrbytes);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        smf.Header header = new smf.Header();
        header.__init(0, bb);
        System.out.println("[SESSION " + header.session() + "] Received to decode");
        final byte[] responseBody = new byte[response.readableBytes()];
        response.readBytes(responseBody);

        out.add(new RpcResponse(header, ByteBuffer.wrap(responseBody)));
    }
}
