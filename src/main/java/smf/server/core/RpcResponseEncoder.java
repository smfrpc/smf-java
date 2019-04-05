// Copyright 2019 SMF Authors
//

package smf.server.core;

import static smf.common.codingHelper.EncoderUtils.encodeHeader;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import smf.Header;
import smf.common.RpcResponse;
import smf.common.compression.CompressionService;

public class RpcResponseEncoder extends MessageToMessageEncoder<RpcResponse> {
  private final static Logger LOG = LogManager.getLogger();

  private final CompressionService compressionService;

  public RpcResponseEncoder(final CompressionService compressionService) {
    this.compressionService = compressionService;
  }

  @Override
  protected void
  encode(final ChannelHandlerContext ctx, final RpcResponse response,
         final List<Object> out) {

    final Header header = response.getHeader();

    if (LOG.isDebugEnabled()) {
      LOG.debug("[session {}] encoding RpcResponse", header.session());
    }

    final byte[] body =
      compressionService.compressBody(header.compression(), response.getBody());

    final byte[] dest = encodeHeader(header.meta(), header.session(),
                                     header.compression(), (byte)0, body);

    final ByteBuf byteBuf =
      ctx.alloc().heapBuffer().writeBytes(dest).writeBytes(body);

    out.add(byteBuf);
  }
}
