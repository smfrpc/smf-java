// Copyright 2019 SMF Authors
//

package smf.server.core;

import static smf.common.codingHelper.EncoderUtils.initHeader;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import java.nio.ByteBuffer;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import smf.Header;
import smf.common.RpcRequest;
import smf.common.compression.CompressionService;

public class RpcRequestDecoder extends ByteToMessageDecoder {
  private final static Logger LOG = LogManager.getLogger();

  private final CompressionService compressionService;

  public RpcRequestDecoder(final CompressionService compressionService) {
    this.compressionService = compressionService;
  }

  @Override
  protected void
  decode(final ChannelHandlerContext ctx, final ByteBuf request,
         final List<Object> out) {

    request.markReaderIndex();
    request.markWriterIndex();

    try {
      byte[] hdrbytes = new byte[16];
      request.readBytes(hdrbytes);
      Header header = initHeader(hdrbytes);

      final byte[] requestBody = new byte[(int)header.size()];
      request.readBytes(requestBody);

      // decompress if-needed
      byte[] decompressBody =
        compressionService.decompressBody(header.compression(), requestBody);

      if (LOG.isDebugEnabled()) {
        LOG.debug("[session {}] Decoding response", header.session());
      }

      /**
       * header indicates size of received body, it will be different than body
       * passed further because decompression process.
       */
      out.add(new RpcRequest(header, ByteBuffer.wrap(decompressBody)));
    } catch (final Exception ex) {
      if (LOG.isDebugEnabled()) { LOG.debug("Failed to parse ! postpone ..."); }
      request.resetReaderIndex();
      request.resetWriterIndex();
    }
  }
}
