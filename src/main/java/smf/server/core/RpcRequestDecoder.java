package smf.server.core;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;

public class RpcRequestDecoder extends ByteToMessageDecoder {
    private final static Logger LOG = LogManager.getLogger();

    @Override
    protected void decode(final ChannelHandlerContext ctx, final ByteBuf request, final List<Object> out) {
        /**
         * TODO FIXME merge it with encoder/decoder stuff from client.core - remove duplication
         */
        request.markReaderIndex();
        request.markWriterIndex();

        try {
            byte[] hdrbytes = new byte[16];
            request.readBytes(hdrbytes);
            ByteBuffer bb = ByteBuffer.wrap(hdrbytes);
            bb.order(ByteOrder.LITTLE_ENDIAN);
            smf.Header header = new smf.Header();
            header.__init(0, bb);

            final byte[] requestBody = new byte[(int) header.size()];
            request.readBytes(requestBody);

            if (LOG.isDebugEnabled()) {
                LOG.debug("[session {}] Decoding response", header.session());
            }

            out.add(new RpcRequest(header, ByteBuffer.wrap(requestBody)));

        } catch (final Exception ex) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Failed to parse ! postpone ...");

            }
            request.resetReaderIndex();
            request.resetWriterIndex();
        }

    }
}
