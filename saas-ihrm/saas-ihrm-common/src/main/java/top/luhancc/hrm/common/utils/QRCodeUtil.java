package top.luhancc.hrm.common.utils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

/**
 * 二维码工具类
 *
 * @author luHan
 * @create 2021/5/21 13:33
 * @since 1.0.0
 */
public final class QRCodeUtil {

    /**
     * 根据content信息生成对应的Data URL形式的二维码图片
     *
     * @param content 二维码中的内容
     * @return
     */
    public static String createQRCode(String content) throws Exception {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, 200, 200);
            BufferedImage bufferedImage = MatrixToImageWriter.toBufferedImage(bitMatrix);
            ImageIO.write(bufferedImage, "png", bos);
            return new String("data:image/png;base64," + Base64.encode(bos.toByteArray()));
        } finally {
            bos.close();
        }
    }
}
