/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.iteso.desi.vision;

import javax.swing.JPanel;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

public class WebCamStream {

    private Mat frame;
    private VideoCapture webSource;
    private WebCamThread webcamTh;
    private JPanel photoPanel;
    private int webCamNumber;
    
    private class WebCamThread implements Runnable {

        protected volatile boolean runnable = false;

        public void run() {
            synchronized (this) {
                while (runnable) {
                    if (webSource.grab()) {
                        try {
                            webSource.retrieve(frame);
                        } catch (Exception e) {
                            continue;
                        }
                        if (frame != null) {
                            showPhotoAtFrame(frame.clone());
                        }
                    }
                }
            }
        }

        private void showPhotoAtFrame(Mat photo) {
            synchronized (this) {
                if (runnable) {
                    Size actualSize = photo.size();

                    double hfactor = (actualSize.height > photoPanel.getHeight()) ? (photoPanel.getHeight() / actualSize.height) : 1;
                    double wfactor = (actualSize.width > photoPanel.getWidth()) ? (photoPanel.getWidth() / actualSize.width) : 1;

                    double factor = (hfactor < wfactor) ? hfactor : wfactor;
                    if(factor < 1) {
                        Imgproc.resize(photo, photo, new Size(actualSize.width * factor, actualSize.height * factor));
                    }
                    
                    if(!photo.empty()) {
                        photoPanel.removeAll();
                        photoPanel.add(ImagesMatUtils.MatToJLabel(photo));
                        photoPanel.revalidate();
                    }

                }
            }
        }
    }
    
    public WebCamStream(int webCamNumber) {
        this.webCamNumber = webCamNumber;
    }
    
    public void startStream(JPanel dst) {
        this.photoPanel = dst;
        this.webSource = new VideoCapture(webCamNumber);
        this.frame = new Mat();
        this.webcamTh = new WebCamThread(); 
        Thread t = new Thread(webcamTh);
        t.setDaemon(true);
        t.start();             
        webcamTh.runnable = true;
    }
    
    public Mat stopStream() {
        webcamTh.runnable = false;
        this.webSource.release();
        return frame.clone();
    }

}
