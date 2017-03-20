/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.iteso.desi.vision;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;



/**
 *
 * @author parres
 */
public class ImagesMatUtils {

    public static JLabel MatToJLabel(Mat mat) {
        
        JLabel ret = null;
        
        if(mat.empty()) {
            return null;
        }
        
        BufferedImage bufImage = null;
        
        try {
           InputStream in = MatToInputStream(mat);
            bufImage = ImageIO.read(in);
            ret = new JLabel(new ImageIcon(bufImage));
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return ret;
    }
    
    public static ImageIcon MatToImageIcon(Mat mat) {
        ImageIcon ret = null;
        
        BufferedImage bufImage = null;
        
        try {
            InputStream in = MatToInputStream(mat);
            bufImage = ImageIO.read(in);
            ret = new ImageIcon(bufImage);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return ret;
        
    }
    
    public static InputStream MatToInputStream(Mat mat) {
        InputStream ret = null;
        MatOfByte matOfByte = new MatOfByte();
        Imgcodecs.imencode(".jpg", mat, matOfByte);
        byte[] byteArray = matOfByte.toArray();

        ret = new ByteArrayInputStream(byteArray);
        return ret;
    }
    
}
