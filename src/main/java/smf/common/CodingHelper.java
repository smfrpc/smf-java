// Copyright 2019 SMF Authors
//

package smf.common;

import com.google.flatbuffers.FlatBufferBuilder;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import net.openhft.hashing.LongHashFunction;
import smf.Header;

/*
 * Group all common stuff used in encoding/decoding related to flatbuffers.
 */
public class CodingHelper {
  private final static long MAX_UNSIGNED_INT = (long)(Math.pow(2, 32) - 1);

  /**
   * Construct {@class smf.Header} object from given input.
   *
   * @param meta
   * @param sessionId
   * @param compression
   * @param bitFlags
   * @param body
   * @return byte encoded {@class smf.Header} object
   */
  public static byte[] encodeHeader(final long meta, final int sessionId,
                                    final byte compression, final byte bitFlags,
                                    final byte[] body) {
    final long length = body.length;
    final long checkSum = calculateCheckSum(body);

    final FlatBufferBuilder internalRequest = new FlatBufferBuilder(20);
    int headerPosition =
      Header.createHeader(internalRequest, compression, bitFlags, sessionId,
                          length, checkSum, meta);

    internalRequest.finish(headerPosition);
    byte[] bytes = internalRequest.sizedByteArray();

    byte[] dest = new byte[16];

    /*
     * That have to be done because for unknown reason  Header.createHeader
     * method call, prepends 4additional bytes into result array, probably that
     * is place for array length (can be used or not, depends on finish() method
     * parameters. arraycopy should be quite fast, it is intrinsic, so almost no
     * overhead.
     */
    System.arraycopy(bytes, 4, dest, 0, 16);

    return dest;
  }

  /**
   * Construct {@class smf.Header} based on received bytes {@param hdrBytes}
   * @param hdrBytes
   * @return byte encoded {@class smf.Header} object
   */
  public static Header
  initHeader(byte[] hdrBytes) {
    final ByteBuffer bb = ByteBuffer.wrap(hdrBytes);
    bb.order(ByteOrder.LITTLE_ENDIAN);
    final Header header = new Header();
    header.__init(0, bb);
    return header;
  }

  /**
   * Calculate of received body.
   * Please note that checksum is always calculated on original body, that
   * means, on compressed if compression was applied.
   * @param body
   * @return
   */
  public static long
  calculateCheckSum(final byte[] body) {
    return MAX_UNSIGNED_INT & LongHashFunction.xx().hashBytes(body);
  }
}
