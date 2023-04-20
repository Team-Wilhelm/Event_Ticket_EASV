package ticketSystemEASV.gui.controller.viewControllers;

import io.github.palexdev.materialfx.controls.MFXToggleButton;
import javafx.application.Platform;
import javafx.embed.swing.SwingNode;
import javafx.event.ActionEvent;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;

import java.awt.event.WindowAdapter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import ticketSystemEASV.gui.model.TicketModel;
import ticketSystemEASV.gui.tasks.QRCodeScanner;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.util.concurrent.CountDownLatch;

public class RedeemController implements Initializable {
    private static final int WEBCAM_WIDTH = 640;
    private static final int WEBCAM_HEIGHT = 480;
    @FXML
    public HBox camera;
    @FXML
    public MFXToggleButton toggle;
    @FXML
    public Label ticketType, ticketValue, ticketEvent, ticketDate, ticketTime, ticketId, ticketLocation;
    private WebcamPanel panel;
    private TicketModel ticketModel;
    private Thread qrCodeScannerThread;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
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
                qrCodeScannerThread.start();
            } else {
                panel.stop();
                qrCodeScannerThread.interrupt();
            }
        });

        Platform.runLater(() -> {
            qrCodeScannerThread = new Thread(new QRCodeScanner(webcam, ticketModel, ticketType, ticketValue, ticketEvent, ticketDate, ticketTime, ticketId, ticketLocation));

            qrCodeScannerThread.setDaemon(true);
        });
    }
    public void setModels(TicketModel ticketModel) {
        this.ticketModel = ticketModel;
    }

    public void redeemTicket(ActionEvent actionEvent) {
        var ticket = ticketModel.getAllTickets().stream().filter(t -> t.getId().equals(ticketId.getText())).findFirst().get();
        ticketModel.redeemTicket(ticket);
        ticketModel.getTicketsFromManager(new CountDownLatch(0));
    }
}

