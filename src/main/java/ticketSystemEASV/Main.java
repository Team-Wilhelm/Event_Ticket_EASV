package ticketSystemEASV;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import ticketSystemEASV.be.Customer;
import ticketSystemEASV.be.Ticket;
import ticketSystemEASV.be.views.TicketView;
import ticketSystemEASV.gui.model.EventModel;
import ticketSystemEASV.gui.model.Model;

import java.util.Objects;
import java.util.UUID;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root;
        if(!true) //TODO: Remove this if
            root = FXMLLoader.load(Objects.requireNonNull(Main.class.getResource("/views/Root.fxml")));
        else
            root = FXMLLoader.load(Objects.requireNonNull(Main.class.getResource("/views/LoginView.fxml")));

        primaryStage.setTitle("EASV Ticket System");
        primaryStage.setScene(new Scene(root));
        primaryStage.getIcons().add(new Image(Objects.requireNonNull(Main.class.getResourceAsStream("/images/icons/chicken.jpg"))));
        primaryStage.show();
    }

    public static void main(String[] args) {
        //StartUp.configure();
        //generateTicketsTest();
        launch(args);
    }

    private static void generateTicketsTest() {
        EventModel eventModel = new EventModel();
        Ticket ticket1 = new Ticket(UUID.randomUUID() , eventModel.getAllEvents().get(0), new Customer("Beckeigh", "beckeigh@nielsen.dk"), "I'm a loser", "No QR");
        Ticket ticket2 = new Ticket(UUID.randomUUID(), eventModel.getAllEvents().get(5), new Customer("Ashghhleigh", "real@mail.com"), "I'm a winner", "No QR");
        TicketView ticketView = new TicketView();
        ticketView.generateTicket(ticket2);
        ticketView.generateTicket(ticket1);
    }
}