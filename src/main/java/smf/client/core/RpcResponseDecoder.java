// Copyright 2019 SMF Authors
//

package smf.client.core;

import static smf.common.CodingHelper.calculateCheckSum;
import static smf.common.CodingHelper.initHeader;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import java.nio.ByteBuffer;
import java.util.List;
import net.openhft.hashing.LongHashFunction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import smf.Header;
import smf.common.InvalidRpcResponse;
import smf.common.RpcResponse;
import smf.common.compression.CompressionService;
import smf.common.exceptions.InvalidChecksumException;

/**
 * Parse incoming byte-stream into logical [smf.Header + response] pairs.
 * Logic behind is simple and of course not efficient - but is highly probable
 * that it just works. <p> RpcResponseDecoder will try to decode each received
 * bytes at once, if this operation fails, it will postpone this operation and
 * try when next chunk arrive.
 */
public class RpcResponseDecoder extends ByteToMessageDecoder {
  private final static Logger LOG = LogManager.getLogger();

  private final CompressionService compressionService;

  public RpcResponseDecoder(final CompressionService compressionService) {
    this.compressionService = compressionService;
  }

  @Override
  protected void
  decode(ChannelHandlerContext ctx, ByteBuf response, List<Object> out) {

    response.markReaderIndex();
    response.markWriterIndex();

    try {
      byte[] hdrbytes = new byte[16];
      response.readBytes(hdrbytes);
      Header header = initHeader(hdrbytes);

      // decompress if-needed
      final byte[] bodyArray = new byte[response.readableBytes()];
      response.readBytes(bodyArray);

      byte[] decompressBody =
        compressionService.decompressBody(header.compression(), bodyArray);

      if (LOG.isDebugEnabled()) {
        LOG.debug("[session {}] Decoding response", header.session());
      }

      /**
       * checksum is always executed on original body.
       */
      final long checkSum = calculateCheckSum(bodyArray);

      if (checkSum != header.checksum()) {
        InvalidChecksumException exception = new InvalidChecksumException(
          "Received checksum is invalid, expected : " + checkSum +
          " and received " + header.checksum());
        out.add(new InvalidRpcResponse(header, exception));
      } else {
        out.add(new RpcResponse(header, ByteBuffer.wrap(decompressBody)));
      }

    } catch (final Exception ex) {
      if (LOG.isDebugEnabled()) { LOG.debug("Failed to parse ! postpone ..."); }
      response.resetReaderIndex();
      response.resetWriterIndex();
    }
  }
}