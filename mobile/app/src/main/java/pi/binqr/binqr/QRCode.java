package pi.binqr.binqr;

import java.util.Arrays;

public class QRCode {

    private final byte[] data;
    private Metadata metadata;

    private QRCode(Metadata metadata, byte[] data) {
        this.metadata = metadata;
        this.data = data;
    }

    public static QRCode fromBytes(byte[] qrCodeBytes) {
        Metadata metadata = Metadata.fromBytes(Arrays.copyOfRange(qrCodeBytes, 0, 290));
        byte[] data = Arrays.copyOfRange(qrCodeBytes, 290, qrCodeBytes.length);

        return new QRCode(metadata, data);
    }

    public Metadata getMetadata() {
        return metadata;
    }

    public byte[] getData() {
        return data;
    }
}
