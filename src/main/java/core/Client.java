package core;

import com.google.flatbuffers.FlatBufferBuilder;
import demo.Request;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import net.openhft.hashing.LongHashFunction;
import smf.Header;

public class Client {

    public static void main(String... args) {
        EventLoopGroup group = new NioEventLoopGroup(1);

        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(final SocketChannel ch) throws Exception {
                            ChannelPipeline p = ch.pipeline();
//                        p.addLast(new ClientHandler());
                        }
                    });

            System.out.println("Going to connect to 127.0.0.1 on port 7000");

            final ChannelFuture sync = b.connect("127.0.0.1", 7000).sync();
            final Channel channel = sync.channel();

            //client code
            FlatBufferBuilder request = new FlatBufferBuilder(0);
            int requestPosition = request.createString("GET /something/");
            int root = Request.createRequest(request, requestPosition);
            request.finish(root);
            byte[] body = request.dataBuffer().array(); //fixme boom when there is no backing array !
            int length = body.length;

            //make request
            final long meta = 212494116 ^ 1719559449; //example method call
            final int sessionId = 1;
            final byte compression = (byte) 0;
            final byte bitFlags = (byte) 0;

            final long maxUnsignedInt = 2 ^ 32 - 1;
            final long checkSum = maxUnsignedInt & LongHashFunction.xx().hashBytes(body);

            final FlatBufferBuilder internalRequest = new FlatBufferBuilder(0);
            int headerPosition = Header.createHeader(internalRequest, compression, bitFlags, sessionId, length, checkSum, meta);
            internalRequest.finish(headerPosition);

            //this is funny :D
            final ByteBuf byteBuf = channel.alloc().heapBuffer().writeBytes(internalRequest.dataBuffer().array());

            final ChannelFuture write = channel.writeAndFlush(byteBuf);
            write.sync().await();

        } catch (final Exception ex) {
            System.err.println("Failed !! Caused by " + ex.getCause());
        } finally {
            group.shutdownGracefully();
        }


    }

}
