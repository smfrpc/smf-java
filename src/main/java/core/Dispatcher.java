package core;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import static core.RpcCallEncoder.HARDCODED_SESSION_ID;

/**
 * Dispatcher is responsible for managing callbacks based on sessionId (fixme ensure sessionId uniques)
 * <p>
 * Inspired by Dispatcher inside Datastax's Cassandra Java Driver.
 */
public class Dispatcher extends SimpleChannelInboundHandler<ByteBuf> {

    private final ConcurrentHashMap<Integer, Consumer<ByteBuf>> pendingRpcCalls = new ConcurrentHashMap<>();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) {

        Consumer consumer = pendingRpcCalls.remove(HARDCODED_SESSION_ID);

        if (consumer == null) {
            System.err.println("Registered handler is null !");
        } else {
            //fixme no-one in handler can pass this ref further - because it is reference counted.
            consumer.accept(msg);
        }
    }

    public void assignCallback(final int sessionId, final Consumer callback) {
        pendingRpcCalls.put(sessionId, callback);
    }

}
