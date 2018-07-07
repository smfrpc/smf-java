package smf.common.compression;

import com.github.luben.zstd.Zstd;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import smf.CompressionFlags;

import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * Provide simple interface for compression/decompression operations using algorithms supported by SMF.
 */
public class CompressionService {

    private final Zstd zstd;

    public CompressionService() {
        /**
         * Lack of documentation for this - but I assume thread-safty :D
         */
        this.zstd = new Zstd();
    }

    /**
     * Based on {@param compressionFlags} compress {@param body} using appropriate compression algorithm or
     * do nothing if compression was not requested.
     *
     * @return processed {@param body}, in case of no compression, just received array is returned.
     */
    public byte[] processBody(byte compressionFlags, byte[] body) {
        switch (compressionFlags) {
            case CompressionFlags.Disabled:
            case CompressionFlags.None:
                return body;
            case CompressionFlags.Zstd:
                return compressUsingZstd(body);
            default:
                throw new UnsupportedOperationException("Compression algorithm is not supported");
        }
    }

    /**
     * @see {@link #processBody(byte, byte[]) processBody}
     */
    public byte[] processBody(byte compressionFlags, final ByteBuffer body) {
        final byte[] bodyArray = new byte[body.remaining()];
        body.get(bodyArray);
        return processBody(compressionFlags, bodyArray);
    }

    private byte[] compressUsingZstd(byte[] body) {

        /**
         * for ZSTD, SMF add size on the begging of body.
         */
        byte[] dstBuff = new byte[(int)zstd.compressBound(body.length)];
        int bytesWritten = (int)zstd.compress(dstBuff, body, 3);

        byte[] onlyCompressedBytes = Arrays.copyOfRange(dstBuff, 0, bytesWritten);

        // XD
        ByteBuf buff = ByteBufAllocator.DEFAULT.buffer(bytesWritten + 4);

        buff.writeInt(bytesWritten);
        buff.writeBytes(onlyCompressedBytes);
        buff.resetReaderIndex();

        final byte[] finalDestination = new byte[buff.readableBytes()];
        buff.readBytes(finalDestination);
        return finalDestination;
    }

    /**
     * Based on {@param compressionFlags} decompress {@param body} using appropriate compression algorithm or
     * do nothing if decompression was not requested.
     *
     * @return processed {@param body}, in case of no compression, just received array is returned.
     */
    public byte[] decompressBody(byte compressionFlags, byte[] body) {
        switch (compressionFlags) {
            case CompressionFlags.Disabled:
            case CompressionFlags.None:
                return body;
            case CompressionFlags.Zstd:
                return decompressUsingZstd(body);
            default:
                throw new UnsupportedOperationException("Compression algorithm is not supported");
        }
    }

    private byte[] decompressUsingZstd(byte[] body) {
        long decompressedSize = zstd.decompressedSize(body);
        byte[] decompressedDst = new byte[(int) decompressedSize];
        zstd.decompress(decompressedDst, body);
        return decompressedDst;
    }

}
