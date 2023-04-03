package ticketSystemEASV.gui.controller;

import io.github.palexdev.materialfx.controls.MFXTableColumn;
import io.github.palexdev.materialfx.controls.MFXTableView;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.controls.cell.MFXTableRowCell;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.input.MouseEvent;
import ticketSystemEASV.be.Customer;
import ticketSystemEASV.be.Event;
import ticketSystemEASV.be.Ticket;
import ticketSystemEASV.bll.AlertManager;
import ticketSystemEASV.gui.model.TicketModel;

public class TicketController {
    private TicketModel ticketModel;
    private Event event;
    private ObservableList<Ticket> tickets = FXCollections.observableArrayList();
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
    }

    private void setUpTableView() {
        Bindings.bindContentBidirectional(tblTickets.getItems(), tickets);
        txtNumberOfGeneratedTickets.textProperty().bind(Bindings.size(tblTickets.getItems()).asString());

        MFXTableColumn<Ticket> customerID = new MFXTableColumn<>("Customer ID", true);
        MFXTableColumn<Ticket> customerName = new MFXTableColumn<>("Customer name", true);
        MFXTableColumn<Ticket> customerEmail = new MFXTableColumn<>("Customer e-mail", true);
        MFXTableColumn<Ticket> ticketType = new MFXTableColumn<>("Ticket type", true);

        customerID.setRowCellFactory(customer -> {
            MFXTableRowCell<Ticket, String> row = new MFXTableRowCell<>(t -> String.valueOf(t.getCustomer().getId()));
            row.setOnMouseClicked(this::tableViewDoubleClickAction);
            return row;
        });

        customerName.setRowCellFactory(customer -> {
            MFXTableRowCell<Ticket, String> row = new MFXTableRowCell<>(t -> t.getCustomer().getName());
            row.setOnMouseClicked(this::tableViewDoubleClickAction);
            return row;
        });

        customerEmail.setRowCellFactory(customer -> {
            MFXTableRowCell<Ticket, String> row = new MFXTableRowCell<>(t -> t.getCustomer().getEmail());
            row.setOnMouseClicked(this::tableViewDoubleClickAction);
            return row;
        });

        ticketType.setRowCellFactory(type -> {
            MFXTableRowCell<Ticket, String> row = new MFXTableRowCell<>(Ticket::getTicketType);
            row.setOnMouseClicked(this::tableViewDoubleClickAction);
            return row;
        });

        tblTickets.getTableColumns().addAll(customerID, customerName, customerEmail, ticketType);
    }

    private void refreshTableView(){
        Runnable getAllTickets = () -> {
            tickets.setAll(ticketModel.getAllTicketsForEvent(event));
        };
        getAllTickets.run();
    }

    @FXML
    private void tableViewDoubleClickAction(MouseEvent event) {
        if (event.getClickCount() == 2) {
            if (!tblTickets.getSelectionModel().getSelection().isEmpty()) {
                Ticket ticket = tblTickets.getSelectionModel().getSelectedValues().get(0);
                ticketModel.openTicket(ticket);
            }
            else {
                AlertManager.getInstance().getAlert(Alert.AlertType.ERROR, "Please select a ticket!", event).showAndWait();
            }
        }
    }
}
