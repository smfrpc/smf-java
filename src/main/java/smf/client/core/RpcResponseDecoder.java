package smf.client.core;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import net.openhft.hashing.LongHashFunction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import smf.exceptions.InvalidChecksumException;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;

/**
 * Parse incoming byte-stream into logical [smf.Header + response] pairs.
 * Logic behind is simple and of course not efficient - but is highly probable that it just works.
 * <p>
 * RpcResponseDecoder will try to decode each received bytes at once, if this operation fails, it will postpone
 * this operation and try when next chunk arrive.
 */
public class RpcResponseDecoder extends ByteToMessageDecoder {
    private final static Logger LOG = LogManager.getLogger();
    private final static long MAX_UNSIGNED_INT = (long) (Math.pow(2, 32) - 1);

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf response, List<Object> out) {

        response.markReaderIndex();
        response.markWriterIndex();

        try {
            byte[] hdrbytes = new byte[16];
            response.readBytes(hdrbytes);
            ByteBuffer bb = ByteBuffer.wrap(hdrbytes);
            bb.order(ByteOrder.LITTLE_ENDIAN);
            smf.Header header = new smf.Header();
            header.__init(0, bb);

            final byte[] responseBody = new byte[(int) header.size()];
            response.readBytes(responseBody);

            LOG.debug("[session {}] Decoding response", header.session());

            final long checkSum = MAX_UNSIGNED_INT & LongHashFunction.xx().hashBytes(responseBody);

            if (checkSum != header.checksum()) {
                InvalidChecksumException exception = new InvalidChecksumException("Received checksum is invalid, expected : " + checkSum + " and received " + header.checksum());
                out.add(new InvalidRpcResponse(header, exception));
            } else {
                out.add(new RpcResponse(header, ByteBuffer.wrap(responseBody)));
            }

        } catch (final Exception ex) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Failed to parse ! postpone ...");

            }
            response.resetReaderIndex();
            response.resetWriterIndex();
        }

    }
}