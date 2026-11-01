package aeu.ratha.beckie_deal_app.utils.helpers;

import android.graphics.Bitmap;
import android.graphics.Color;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.util.Hashtable;

/**
 * Helper utility to generate QR code Bitmaps locally from backend-provided qr_code_data / qr_string
 * using ZXing with ARGB_8888, ErrorCorrectionLevel.M, and MARGIN = 2.
 */
public class QrCodeHelper {

    /**
     * Generates a ZXing QR Code bitmap directly from exact khqrPayload (qr_code_data) without modification.
     */
    public static Bitmap generateQRCode(String khqrPayload, int width, int height) {
        if (khqrPayload == null || khqrPayload.isEmpty()) {
            return null;
        }
        try {
            Hashtable<EncodeHintType, Object> hints = new Hashtable<>();
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            hints.put(EncodeHintType.MARGIN, 2);
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);

            MultiFormatWriter writer = new MultiFormatWriter();
            BitMatrix bitMatrix = writer.encode(khqrPayload, BarcodeFormat.QR_CODE, width, height, hints);
            
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bitmap.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
