package ticketSystemEASV.gui.controller.viewControllers;

import io.github.palexdev.materialfx.controls.MFXToggleButton;
import javafx.application.Platform;
import javafx.embed.swing.SwingNode;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.scene.layout.HBox;
import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;

public class RedeemController implements Initializable {
    private static final int WEBCAM_WIDTH = 640;
    private static final int WEBCAM_HEIGHT = 480;
    @FXML
    public HBox camera;
    @FXML
    public MFXToggleButton toggle;
    private WebcamPanel panel;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println("RedeemController initialized");

        Webcam webcam = Webcam.getDefault();
        webcam.setViewSize(new java.awt.Dimension(WEBCAM_WIDTH, WEBCAM_HEIGHT));
        panel = new WebcamPanel(webcam);
        panel.setMirrored(true);
        panel.setFPSDisplayed(true);
        panel.setFPSLimit(20);
        panel.stop();

        final SwingNode swingNode = new SwingNode();
        SwingUtilities.invokeLater(() -> swingNode.setContent(panel));

        camera.getChildren().add(swingNode);

        toggle.setOnAction(actionEvent -> {
            if (toggle.isSelected()) {
                panel.start();
            } else {
                panel.stop();
            }
        });

        Platform.runLater(() -> {
            new Thread(() -> {
                while (true) {
                    try {
                        BufferedImage image = webcam.getImage();
                        Result result = readQRCode(image);
                        if (result != null) {
                            System.out.println("QR Code Text: " + result.getText());
                        }
                    } catch (IOException e) {

                    } catch (Exception e) {

                    }
                }
            }).start();
        });
    }
    private Result readQRCode(BufferedImage image) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        ByteArrayInputStream bis = new ByteArrayInputStream(baos.toByteArray());
        BufferedImage bufferedImage = ImageIO.read(bis);
        BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(new BufferedImageLuminanceSource(bufferedImage)));
        Result result = null;
        try {
            result = new MultiFormatReader().decode(binaryBitmap);
        } catch (Exception e) {
            // No QR code found in the image
        }
        return result;
    }
    public void redeemTicket(ActionEvent actionEvent) {
    }
}
