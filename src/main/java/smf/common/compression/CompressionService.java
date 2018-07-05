package smf.common.compression;

import com.github.luben.zstd.Zstd;
import smf.CompressionFlags;

import java.nio.ByteBuffer;

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

    public byte[] compressUsingZstd(byte[] body) {
        byte[] dstBuff = new byte[body.length];
        zstd.compress(dstBuff, body, 0);
        return dstBuff;
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

    public byte[] decompressUsingZstd(byte[] body) {
        long decompressedSize = zstd.decompressedSize(body);
        byte[] decompressedDst = new byte[(int)decompressedSize];
        zstd.decompress(decompressedDst, body);
        return decompressedDst;
    }

}
