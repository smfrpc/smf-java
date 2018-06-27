package smf.server.core;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import smf.Header;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;

@ChannelHandler.Sharable
public class RequestHandler extends SimpleChannelInboundHandler<RpcGeneric> {
    final CopyOnWriteArrayList<RpcService> serviceIdToFunctionHandler = new CopyOnWriteArrayList<>();

    @Override
    protected void channelRead0(final ChannelHandlerContext ctx, final RpcGeneric msg) {
        final Header header = msg.getHeader();
        long meta = header.meta();

        Optional<Function<byte[], byte[]>> requestHandler = serviceIdToFunctionHandler.stream()
                .map(rpcService -> rpcService.getHandler(meta))//
                .filter(Objects::nonNull)//
                .findFirst();

        if (!requestHandler.isPresent()) {
            //TODO handler the case where meta is not registered
        }

        Function<byte[], byte[]> rpcRequestFunction = requestHandler.get();

        byte[] response = rpcRequestFunction.apply(msg.getRequestBody());

        //kek XD
        final RpcGeneric rpcResponse = new RpcGeneric(header, response);
        ctx.channel().writeAndFlush(rpcResponse);
    }

    public void registerStorageService(final RpcService rpcService) {
        serviceIdToFunctionHandler.add(rpcService);
    }
}
