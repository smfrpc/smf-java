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
        final long meta = msg.getMethodMeta();
        final int sessionId = HARDCODED_SESSION_ID;
        final byte compression = (byte) 0;
        final byte bitFlags = (byte) 0;

        final long checkSum = MAX_UNSIGNED_INT & LongHashFunction.xx().hashBytes(msg.getBody());

        final FlatBufferBuilder internalRequest = new FlatBufferBuilder(0);

        final int length = msg.getBody().length;
        int headerPosition = Header.createHeader(internalRequest, compression, bitFlags, sessionId, length, checkSum, meta);
        internalRequest.finish(headerPosition);

        final ByteBuf byteBuf = ctx.alloc().buffer().writeBytes(internalRequest.dataBuffer());
        out.add(byteBuf);
    }
}
