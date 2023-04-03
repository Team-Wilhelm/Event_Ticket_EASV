package ticketSystemEASV.gui.controller;

import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import ticketSystemEASV.be.Customer;
import ticketSystemEASV.be.Event;
import ticketSystemEASV.gui.model.TicketModel;


public class TicketController {
    private TicketModel ticketModel;
    private Event event;
    @FXML private MFXTextField txtEventId, txtCustomerName, txtCustomerEmail, txtNumberOfTickets;


    @FXML
    private void generateTicket(ActionEvent actionEvent) {
        ticketModel.generateTicket(event, new Customer(txtCustomerName.getText().trim(), txtCustomerEmail.getText().trim()));
    }

    @FXML
    private void generateEmptyTickets(ActionEvent actionEvent) {
        //TODO not sure if needed
    }

    public void setTicketModel(TicketModel ticketModel) {
        this.ticketModel = ticketModel;
    }

    public void setEvent(Event event) {
        this.event = event;
        txtEventId.setText("" + event.getId() + " - " + event.getEventName());
    }
}
