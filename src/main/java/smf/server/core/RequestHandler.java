// Copyright 2019 SMF Authors
//

package smf.server.core;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import java.nio.ByteBuffer;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import smf.Header;
import smf.common.RpcRequest;
import smf.common.RpcResponse;

@ChannelHandler.Sharable
public class RequestHandler extends SimpleChannelInboundHandler<RpcRequest> {
  private final static Logger LOG = LogManager.getLogger();

  final CopyOnWriteArrayList<RpcService> serviceIdToFunctionHandler =
    new CopyOnWriteArrayList<>();

  @Override
  protected void
  channelRead0(final ChannelHandlerContext ctx, final RpcRequest request) {
    final Header header = request.getHeader();
    long meta = header.meta();

    Optional<Function<byte[], byte[]>> requestHandler =
      serviceIdToFunctionHandler.stream()
        .map(rpcService -> rpcService.getHandler(meta))
        .filter(Objects::nonNull)
        .findFirst();

    if (!requestHandler.isPresent()) {
      LOG.error("Request handler for request is not registered !");
      // TODO handle the case where meta is not registered
    }

    Function<byte[], byte[]> rpcRequestFunction = requestHandler.get();

    final byte[] body = new byte[request.getBody().remaining()];
    request.getBody().get(body);

    byte[] response = rpcRequestFunction.apply(body);


    final RpcResponse rpcResponse =
      new RpcResponse(header, ByteBuffer.wrap(response));

    /*
     * writeAndFlush fired for each single request can potentially cause problems, but atm
     * that is ok. In future please consider request coalescing and some flush-scheduler.
     */
    ctx.channel().writeAndFlush(rpcResponse);
  }

  public void
  registerStorageService(final RpcService rpcService) {
    serviceIdToFunctionHandler.add(rpcService);
  }
}
