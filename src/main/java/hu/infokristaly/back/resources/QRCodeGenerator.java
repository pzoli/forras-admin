package hu.infokristaly.back.resources;

import java.io.File;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Locale;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

public class QRCodeGenerator {

    private static final BarcodeFormat DEFAULT_BARCODE_FORMAT = BarcodeFormat.QR_CODE;
    private static final String DEFAULT_IMAGE_FORMAT = "JPG";
    private static final String DEFAULT_OUTPUT_FILE = "out";
    private static final int DEFAULT_WIDTH = 300;
    private static final int DEFAULT_HEIGHT = 300;

    @SuppressWarnings("unchecked")
    public static void generateFile(String title, String contents, String oFileName) throws WriterException, IOException {
        BarcodeFormat barcodeFormat = DEFAULT_BARCODE_FORMAT;
        String imageFormat = DEFAULT_IMAGE_FORMAT;
        String outFileString = (oFileName == null ? DEFAULT_OUTPUT_FILE : oFileName);
        int width = DEFAULT_WIDTH;
        int height = DEFAULT_HEIGHT;
        
        if (DEFAULT_OUTPUT_FILE.equals(outFileString)) {
            outFileString += '.' + imageFormat.toLowerCase(Locale.ENGLISH);
        }

        @SuppressWarnings("rawtypes")
        Hashtable hints = new Hashtable();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        MultiFormatWriter barcodeWriter = new MultiFormatWriter();
        BitMatrix matrix = barcodeWriter.encode(contents, barcodeFormat, width, height, hints);
        MatrixToImageWriter.writeToFile(title, matrix, imageFormat, new File(outFileString));
    }
}
