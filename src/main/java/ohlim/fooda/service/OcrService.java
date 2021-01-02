package ohlim.fooda.service;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import ohlim.fooda.domain.Restaurant;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

@Service
public class OcrService {
    public Restaurant extractCapture(MultipartFile capture, String username) throws IOException, TesseractException {
        BufferedImage img = ImageIO.read(capture.getInputStream());
        Tesseract tesseract = new Tesseract();
        tesseract.setLanguage("eng+kor");
        String resultado = tesseract.doOCR(img);
        System.out.println(resultado);
        return null;
    }
}
