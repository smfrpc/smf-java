package core;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.nio.ByteBuffer;
import java.util.function.Consumer;

public class SmfClient {

    private EventLoopGroup group;
    private final Bootstrap bootstrap;
    private final Dispatcher dispatcher;
    private volatile Channel channel;
    private final SessionIdGenerator sessionIdGenerator;

    public SmfClient(final String host, final int port) throws InterruptedException {
        sessionIdGenerator = new SessionIdGenerator();

        group = new NioEventLoopGroup(1);

        dispatcher = new Dispatcher(sessionIdGenerator);

        RpcCallEncoder rpcCallEncoder = new RpcCallEncoder();
        RpcCallDecoder rpcCallDecoder = new RpcCallDecoder();

        bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(final SocketChannel ch) {
                        ChannelPipeline p = ch.pipeline();
                        p.addLast(rpcCallEncoder);
                        p.addLast(rpcCallDecoder);
                        p.addLast(dispatcher);
                    }
                });

        System.out.println("Going to connect to 127.0.0.1 on port 7000");

        ChannelFuture connect = bootstrap.connect(host, port);

        //ヽ( ͠°෴ °)ﾉ
        connect.addListener(result -> channel = connect.channel());

        //fixme not best solution - but most important is to have working client
        connect.sync().await();
    }

    /**
     * schedule RPC call
     *
     * @param methodMeta
     * @param body
     * @param callback
     */
    public void executeAsync(long methodMeta, byte[] body, final Consumer<ByteBuffer> callback) {

        int sessionId = sessionIdGenerator.next();
        System.out.println("[executeAsync.GENERATED] " + sessionId);

        final RpcCall rpcCall = new RpcCall(sessionId, methodMeta, body, callback);

        //fixme does put with concurrentHashMap guarantees that assignCallback will always HAPPENS BEFORE channe.write?
        dispatcher.assignCallback(sessionId, rpcCall.getCallback());
        channel.writeAndFlush(rpcCall);
    }

    public void closeGracefully() throws InterruptedException {
        group.shutdownGracefully().await().sync();
    }

}
