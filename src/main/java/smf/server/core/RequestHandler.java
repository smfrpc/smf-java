package smf.server.core;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import smf.Header;

import java.nio.ByteBuffer;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;

@ChannelHandler.Sharable
public class RequestHandler extends SimpleChannelInboundHandler<RpcRequest> {
    final CopyOnWriteArrayList<RpcService> serviceIdToFunctionHandler = new CopyOnWriteArrayList<>();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequest msg) {
        final Header header = msg.getHeader();
        long meta = header.meta();

        Optional<Function<ByteBuffer, byte[]>> requestHandler = serviceIdToFunctionHandler.stream()
                .map(rpcService -> rpcService.getHandler(meta))//
                .filter(Objects::nonNull)//
                .findFirst();

        if (!requestHandler.isPresent()) {
            //TODO handler the case where meta is not registered
        }

        Function<ByteBuffer, byte[]> rpcRequestFunction = requestHandler.get();

        byte[] response = rpcRequestFunction.apply(msg.getRequestBody());

        //kek XD
        final RpcRequest rpcResponse = new RpcRequest(header, ByteBuffer.wrap(response));
        ctx.writeAndFlush(rpcResponse);
    }

    public void registerStorageService(final RpcService rpcService) {
        serviceIdToFunctionHandler.add(rpcService);
    }
}
