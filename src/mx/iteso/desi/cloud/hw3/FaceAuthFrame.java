package mx.iteso.desi.cloud.hw3;

import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import static mx.iteso.desi.vision.ImagesMatUtils.MatToInputStream;
import mx.iteso.desi.vision.WebCamStream;
import org.opencv.core.Mat;

import com.amazonaws.util.IOUtils;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
public class FaceAuthFrame extends javax.swing.JFrame {

    WebCamStream webCam;
    Mat lastFrame;
    
    public FaceAuthFrame() {
        this.webCam = new WebCamStream(0);
        initComponents();
        startCam();
    }
    
    private void startCam() {
        this.webCam.startStream(this.photoPanel);
        this.authButton.setEnabled(true);
    }
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        photoPanel = new javax.swing.JPanel();
        authButton = new javax.swing.JButton();
        nameTextField = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                closeWindow(evt);
            }
            public void windowClosing(java.awt.event.WindowEvent evt) {
                closeWindow(evt);
            }
        });

        photoPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Webcam"));
        photoPanel.setAutoscrolls(true);
        photoPanel.setLayout(new javax.swing.BoxLayout(photoPanel, javax.swing.BoxLayout.LINE_AXIS));

        authButton.setText("Auth");
        authButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                authButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(photoPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 305, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(127, 127, 127)
                        .addComponent(authButton))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(nameTextField)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(photoPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 305, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(authButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(nameTextField)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void authButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_authButtonActionPerformed
        this.lastFrame = webCam.stopStream();
        this.authButton.setEnabled(false);
        Face f = this.doAuthLogic();
        if(f.name.isEmpty()) {
            this.nameTextField.setText("Not Match");
        } else {
            this.nameTextField.setText(f.name+"("+f.cofidence+")");
        }
    }//GEN-LAST:event_authButtonActionPerformed

    private Face doAuthLogic() {
        // TODO
        Face face = null;
        
        String filename=nameTextField.getText();  
        File file = new File(filename);
        if(file.length() > 5_000_000) {
            return null;
        }     
        
        try {
            //http://stackoverflow.com/questions/30026060/java-inputstream-to-bytebuffer
            InputStream in = MatToInputStream(lastFrame);
            byte[] bytes = IOUtils.toByteArray(in);
            ByteBuffer photo = ByteBuffer.wrap(bytes);

            AWSFaceCompare awscompare = new AWSFaceCompare(Config.accessKeyID, Config.secretAccessKey, Config.amazonRegion, Config.srcBucket);
            System.out.println("BEGIN COMPARING");
            face=awscompare.compare(photo);
            System.out.println("FIN COMPARING");
        } catch (IOException ex) {
            Logger.getLogger(FaceAuthFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
        
         


        return face;
    }
    
    private void closeWindow(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_closeWindow
        webCam.stopStream();
    }//GEN-LAST:event_closeWindow

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton authButton;
    private javax.swing.JTextField nameTextField;
    private javax.swing.JPanel photoPanel;
    // End of variables declaration//GEN-END:variables
}
