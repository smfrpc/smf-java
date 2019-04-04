// Copyright 2019 SMF Authors
//

package smf.client.core;

import static smf.common.CodingHelper.encodeHeader;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import smf.common.compression.CompressionService;

public class RpcRequestEncoder
  extends MessageToMessageEncoder<PreparedRpcRequest> {
  private final static Logger LOG = LogManager.getLogger();

  private final CompressionService compressionService;

  public RpcRequestEncoder(final CompressionService compressionService) {
    this.compressionService = compressionService;
  }

  @Override
  protected void
  encode(final ChannelHandlerContext ctx, final PreparedRpcRequest msg,
         final List<Object> out) {

    if (LOG.isDebugEnabled()) {
      LOG.debug("[session {}] encoding PreparedRpcRequest", msg.getSessionId());
    }

    final RpcRequestOptions rpcRequestOptions = msg.getRpcRequestOptions();

    final byte[] body = compressionService.compressBody(
      rpcRequestOptions.getCompression(), msg.getBody());

    final byte[] dest =
      encodeHeader(msg.getMethodMeta(), msg.getSessionId(),
                   rpcRequestOptions.getCompression(), (byte)0, body);

    final ByteBuf byteBuf =
      ctx.alloc().heapBuffer().writeBytes(dest).writeBytes(body);

    out.add(byteBuf);
  }
}
