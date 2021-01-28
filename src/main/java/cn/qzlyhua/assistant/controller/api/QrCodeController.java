package cn.qzlyhua.assistant.controller.api;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.qrcode.QrCodeUtil;
import cn.qzlyhua.assistant.util.BrQrCodeUtil;
import cn.qzlyhua.assistant.util.barcode.util.ImageUtil;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * @author yanghua
 */
@Controller
@RequestMapping("/assets")
public class QrCodeController {

    @GetMapping("/bar/{code}")
    public void getBarCode(@RequestParam(required = false) String width, @RequestParam(required = false) String height,
                           @PathVariable String code, HttpServletResponse response) throws IOException, WriterException {
        Assert.isTrue(StrUtil.isNotBlank(code), "编码内容不允许为空");
        Assert.isTrue(code.length() < 81, "编码内容长度不允许超过80！");

        int w = StrUtil.isNotBlank(width) && Integer.valueOf(width) > 100 ? Integer.valueOf(width) : 200;
        int h = StrUtil.isNotBlank(height) && Integer.valueOf(height) > 30 ? Integer.valueOf(height) : 68;

        Image image = BrQrCodeUtil.brEncode(code, BarcodeFormat.CODE_128, w, h);

        response.setContentType("image/jpeg");
        ServletOutputStream sos = response.getOutputStream();
        ImageIO.write(ImageUtil.toBufferedImage(image), "jpeg", sos);
        sos.close();
    }

    @GetMapping("/qr/{code}")
    public void getQrCode(@RequestParam(required = false) String width, @RequestParam(required = false) String height,
                          @PathVariable String code, HttpServletResponse response) throws IOException {
        Assert.isTrue(StrUtil.isNotBlank(code), "编码内容不允许为空");

        int w = StrUtil.isNotBlank(width) && Integer.valueOf(width) > 100 ? Integer.valueOf(width) : 300;
        int h = StrUtil.isNotBlank(height) && Integer.valueOf(height) > 100 ? Integer.valueOf(height) : 300;

        BufferedImage bufferedImage = QrCodeUtil.generate(code, BarcodeFormat.QR_CODE, w, h);
        response.setContentType("image/jpeg");
        ServletOutputStream sos = response.getOutputStream();
        ImageIO.write(bufferedImage, "jpeg", sos);
        sos.close();
    }
}
