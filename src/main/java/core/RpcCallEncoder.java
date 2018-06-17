package core;


import com.google.flatbuffers.FlatBufferBuilder;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import net.openhft.hashing.LongHashFunction;
import smf.Header;

import java.util.List;

public class RpcCallEncoder extends MessageToMessageEncoder<RpcCall> {

    private final static long MAX_UNSIGNED_INT = (long) (Math.pow(2, 32) - 1);

    //FIXME
    public static final int HARDCODED_SESSION_ID = 1;

    @Override
    protected void encode(final ChannelHandlerContext ctx, final RpcCall msg, final List<Object> out) {

        final byte[] body = msg.getBody();
        final long length = body.length;

        //make request
        final long meta = 212494116 ^ 1719559449; //example method call
        final int sessionId = 1;
        final byte compression = (byte) 0;
        final byte bitFlags = (byte) 0;

        final long maxUnsignedInt = MAX_UNSIGNED_INT;
        final long checkSum = maxUnsignedInt & LongHashFunction.xx().hashBytes(body);

        final FlatBufferBuilder internalRequest = new FlatBufferBuilder(20);
        int headerPosition = Header.createHeader(internalRequest, compression, bitFlags, sessionId, length, checkSum, meta);
        internalRequest.finish(headerPosition);
        byte[] bytes = internalRequest.sizedByteArray();

        byte[] dest = new byte[16];

        //fixme - I cannot even comment on this (｡◕‿‿◕｡)
        System.arraycopy(bytes, 4, dest, 0, 16 );

        final ByteBuf byteBuf = ctx.alloc().heapBuffer()
                .writeBytes(dest)
                .writeBytes(body);

        out.add(byteBuf);
    }
}
