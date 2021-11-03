package cn.qzlyhua.assistant.util.word;

import com.aspose.words.Document;
import com.aspose.words.ImportFormatMode;
import com.aspose.words.License;
import com.aspose.words.SaveFormat;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 基于Aspose的Word转PDF
 *
 * @author yanghua
 */
public class Word2PdfAsposeUtil {
    public static boolean getLicense() {
        boolean result = false;
        InputStream is = null;
        try {
            Resource resource = new ClassPathResource("license.xml");
            is = resource.getInputStream();
            //InputStream is = Word2PdfAsposeUtil.class.getClassLoader().getResourceAsStream("license.xml"); // license.xml应放在..\WebRoot\WEB-INF\classes路径下
            License aposeLic = new License();
            aposeLic.setLicense(is);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    /**
     * @param inPath
     * @param outPath
     * @return
     */
    public static boolean doc2pdf(String inPath, String outPath, int saveFormat) {
        // 验证License 若不验证则转化出的pdf文档会有水印产生
        if (!getLicense()) {
            return false;
        }

        FileOutputStream os = null;

        try {
            long old = System.currentTimeMillis();
            // 新建一个空白pdf文档
            File file = new File(outPath);
            os = new FileOutputStream(file);
            // Address是将要被转化的word文档
            Document doc = new Document(inPath);
            doc.save(os, saveFormat);

            long now = System.currentTimeMillis();
            // 转化用时
            System.out.println("pdf转换成功，共耗时：" + ((now - old) / 1000.0) + "秒");
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (os != null) {
                try {
                    os.flush();
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return true;
    }

    public static void formatConversion(String wordFile, int type) {
        switch (type) {
            case 40: {
                doc2pdf(wordFile, wordFile.substring(0, wordFile.lastIndexOf(".")) + ".pdf", SaveFormat.PDF);
                break;
            }
            case 50: {
                doc2pdf(wordFile, wordFile.substring(0, wordFile.lastIndexOf(".")) + ".html", SaveFormat.HTML);
                break;
            }
            default: {
                doc2pdf(wordFile, wordFile.substring(0, wordFile.lastIndexOf(".")) + ".png", SaveFormat.PNG);
                break;
            }
        }
    }

    public static void main(String[] arg) {
        String docPath = "/Users/yanghua/Downloads/传输规范-PP013.docx";
        formatConversion(docPath, SaveFormat.HTML);
    }
}
