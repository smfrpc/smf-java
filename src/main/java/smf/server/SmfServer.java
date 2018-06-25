package smf.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SmfServer {
    private final static Logger LOG = LogManager.getLogger();

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private volatile Channel channel;

    public SmfServer(final String host, final int port) throws InterruptedException {
        bossGroup = new NioEventLoopGroup(1);
        workerGroup = new NioEventLoopGroup();

        final RpcRequestDecoder rpcRequestDecoder = new RpcRequestDecoder();

        ServerBootstrap b = new ServerBootstrap();
        b.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                //.handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(new ChannelInitializer() {
                    @Override
                    protected void initChannel(Channel ch) {
                        final ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(rpcRequestDecoder);
                    }
                });

        LOG.info("Going to listen on {}:{}", host, port);

        channel = b.bind(host, port).sync().channel();
    }

    public void closeGracefully() throws InterruptedException {
        bossGroup.shutdownGracefully().await().sync();
        workerGroup.shutdownGracefully().await().sync();
    }
}