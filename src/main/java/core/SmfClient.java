package core;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import static core.RpcCallEncoder.HARDCODED_SESSION_ID;

public class SmfClient {

    private EventLoopGroup group;
    private final Bootstrap bootstrap;
    private final Dispatcher dispatcher;
    private volatile Channel channel;

    public SmfClient(final String host, final int port) throws InterruptedException {
        group = new NioEventLoopGroup(1);

        dispatcher = new Dispatcher();

        bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(final SocketChannel ch) {
                        ChannelPipeline p = ch.pipeline();
                        p.addLast(new RpcCallEncoder());
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
     * Perform RPC call.
     *
     * @param rpcCall identifying remote method.
     * @return netty's internal ChannelFuture of write itself.
     */
    public void executeAsync(final RpcCall rpcCall) {
        //fixme does put with concurrentHashMap guarantees that assignCallback will always HAPPENS BEFORE channe.write?
        dispatcher.assignCallback(HARDCODED_SESSION_ID, rpcCall.getCallback());
        channel.writeAndFlush(rpcCall);
    }

    public void closeGracefully() throws InterruptedException {
        group.shutdownGracefully().await().sync();
    }

}
