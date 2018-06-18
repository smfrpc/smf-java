package core;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import smf.Header;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * Dispatcher is responsible for managing callbacks based on sessionId (fixme ensure sessionId uniques)
 * <p>
 * Inspired by Dispatcher inside Datastax's Cassandra Java Driver.
 */
public class Dispatcher extends SimpleChannelInboundHandler<RpcResponse> {

    private final static Logger LOG = LogManager.getLogger();

    private final ConcurrentHashMap<Integer, Consumer<ByteBuf>> pendingRpcCalls = new ConcurrentHashMap<>();

    private SessionIdGenerator sessionIdGenerator;

    public Dispatcher(final SessionIdGenerator sessionIdGenerator) {
        this.sessionIdGenerator = sessionIdGenerator;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcResponse msg) {

        LOG.debug("[session {}] received to dispatch", msg.getHeader().session());
        final Header header = msg.getHeader();
        Consumer consumer = pendingRpcCalls.remove(header.session());

        if (consumer == null) {
            LOG.debug("[session {}] registered handler is null", msg.getHeader().session());
        } else {
            try {
                //FIXME should it be called within event loop ?
                consumer.accept(msg.getResponseBody());
            } catch (final Exception ex) {
                LOG.error("[session {}] {}", msg.getHeader().session(), ex);
            }
        }

        sessionIdGenerator.release(header.session());
    }

    public void assignCallback(final int sessionId, final Consumer callback) {
        pendingRpcCalls.put(sessionId, callback);
    }

}
