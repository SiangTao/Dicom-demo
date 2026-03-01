package dicom;

import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.time.format.TextStyle;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import org.dcm4che3.data.Attributes;
import org.dcm4che3.imageio.plugins.dcm.DicomImageReadParam;
import org.dcm4che3.util.SafeClose;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;


public class Dcm2jpgIOStreamOutput {
    private final ImageReader imageReader = ImageIO.getImageReadersByFormatName("DICOM").next();
    private float windowCenter;
    private float windowWidth;
    private boolean autoWindowing = true;
    private int windowIndex;
    private int voiLUTIndex;
    private boolean preferWindow = true;
    private Attributes prState;
    private int overlayActivationMask = 0xffff;
    private int overlayGrayscaleValue = 0xffff;
    private int frame = 1;
    public int getFrame() {
        return frame;
    }

    /*
     *
     **/
    public  String test(){

        return null;

    }
    public void setFrame(int frame) {
        this.frame = frame;
    }

    public void setWindowCenter(float windowCenter) {
        this.windowCenter = windowCenter;
    }

    public void setWindowWidth(float windowWidth) {
        this.windowWidth = windowWidth;
    }

    public void setAutoWindowing(boolean autoWindowing) {
        this.autoWindowing = autoWindowing;
    }

    public void setWindowIndex(int windowIndex) {
        this.windowIndex = windowIndex;
    }

    public void setVoiLUTIndex(int voiLUTIndex) {
        this.voiLUTIndex = voiLUTIndex;
    }

    public void setPreferWindow(boolean preferWindow) {
        this.preferWindow = preferWindow;
    }

    public void setPrState(Attributes prState) {
        this.prState = prState;
    }

    public void setOverlayActivationMask(int overlayActivationMask) {
        this.overlayActivationMask = overlayActivationMask;
    }

    public void setOverlayGrayscaleValue(int overlayGrayscaleValue) {
        this.overlayGrayscaleValue = overlayGrayscaleValue;
    }

    public void convert(File src, File dest) throws IOException{
        Iterator<ImageReader> iter = ImageIO.getImageReadersByFormatName("DICOM");
        ImageReader reader = iter.next();
        ImageInputStream iis = ImageIO.createImageInputStream(src);
        BufferedImage bi;
        OutputStream out = null;
        try{
            reader.setInput(iis, false);
            bi = readImage(iis);
            if (bi == null) {
                System.out.println("\nError: " + src + " - couldn't read!");
                return;
            }
            out = new BufferedOutputStream(new FileOutputStream(dest));
            JPEGImageEncoder enc = JPEGCodec.createJPEGEncoder(out);  //这里也可以使用流将图像导出到web应用，用来搭建web版的PACS等。
            enc.encode(bi);
        }finally{
            SafeClose.close(iis);
            SafeClose.close(out);
        }
    }

    private ImageReadParam readParam(){
        DicomImageReadParam param = (DicomImageReadParam) imageReader.getDefaultReadParam();
        param.setWindowCenter(windowCenter);
        param.setWindowWidth(windowWidth);
        param.setAutoWindowing(autoWindowing);
        param.setWindowIndex(windowIndex);
        param.setVOILUTIndex(voiLUTIndex);
        param.setPreferWindow(preferWindow);
        param.setPresentationState(prState);
        param.setOverlayActivationMask(overlayActivationMask);
        param.setOverlayGrayscaleValue(overlayGrayscaleValue);
        return param;
    }

    private BufferedImage readImage(ImageInputStream iis) throws IOException{
        imageReader.setInput(iis);
        return imageReader.read(frame -1, readParam());
    }
}

