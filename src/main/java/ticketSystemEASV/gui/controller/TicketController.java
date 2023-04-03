package ticketSystemEASV.gui.controller;

import io.github.palexdev.materialfx.controls.MFXTableColumn;
import io.github.palexdev.materialfx.controls.MFXTableView;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.controls.cell.MFXTableRowCell;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import ticketSystemEASV.be.Customer;
import ticketSystemEASV.be.Event;
import ticketSystemEASV.be.Ticket;
import ticketSystemEASV.gui.model.TicketModel;


public class TicketController {
    private TicketModel ticketModel;
    private Event event;
    private ObservableList<Ticket> tickets;
    @FXML private MFXTextField txtEventId, txtCustomerName, txtCustomerEmail, txtNumberOfTickets, txtNumberOfGeneratedTickets;
    @FXML private MFXTableView<Ticket> tblTickets;

    @FXML
    private void generateTicket(ActionEvent actionEvent) {
        ticketModel.generateTicket(event, new Customer(txtCustomerName.getText().trim(), txtCustomerEmail.getText().trim()));
        refreshTableView();
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
        setUpTableView();

        //TODO multithreading
        refreshTableView();

        txtEventId.setText("" + event.getId() + " - " + event.getEventName());
        Platform.runLater(() -> {
            txtNumberOfGeneratedTickets.setText("" + tickets.size());
        });
    }

    private void setUpTableView() {
        MFXTableColumn<Ticket> customerID = new MFXTableColumn<>("Customer ID", true);
        MFXTableColumn<Ticket> customerName = new MFXTableColumn<>("Customer name", true);
        MFXTableColumn<Ticket> customerEmail = new MFXTableColumn<>("Customer e-mail", true);
        MFXTableColumn<Ticket> ticketType = new MFXTableColumn<>("Ticket type", true);

        customerID.setRowCellFactory(customer -> new MFXTableRowCell<>(t -> t.getCustomer().getId()));
        customerName.setRowCellFactory(customer -> new MFXTableRowCell<>(t -> t.getCustomer().getName()));
        customerEmail.setRowCellFactory(customer -> new MFXTableRowCell<>(t -> t.getCustomer().getEmail()));
        ticketType.setRowCellFactory(type -> new MFXTableRowCell<>(Ticket::getTicketType));

        tblTickets.getTableColumns().addAll(customerID, customerName, customerEmail, ticketType);
    }

    private void refreshTableView(){
        Runnable getAllTickets = () -> {
            tickets = FXCollections.observableArrayList(ticketModel.getAllTicketsForEvent(event));
        };
        getAllTickets.run();
        tblTickets.getItems().setAll(tickets);
        txtNumberOfGeneratedTickets.setText("" + tickets.size());
    }
}
