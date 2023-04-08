package ticketSystemEASV.gui.controller;

import io.github.palexdev.materialfx.controls.MFXProgressSpinner;
import io.github.palexdev.materialfx.controls.MFXTableColumn;
import io.github.palexdev.materialfx.controls.MFXTableView;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.controls.cell.MFXTableRowCell;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import ticketSystemEASV.be.Customer;
import ticketSystemEASV.be.Event;
import ticketSystemEASV.be.Ticket;
import ticketSystemEASV.bll.AlertManager;
import ticketSystemEASV.gui.controller.addController.AddObjectController;
import ticketSystemEASV.gui.controller.viewControllers.MotherController;
import ticketSystemEASV.gui.model.TicketModel;
import ticketSystemEASV.gui.tasks.SaveTask;
import ticketSystemEASV.gui.tasks.TaskState;

import java.net.URL;
import java.util.ResourceBundle;

public class TicketController extends AddObjectController implements Initializable {
    @FXML
    private MFXTextField txtEventId, txtCustomerName, txtCustomerEmail, txtNumberOfTickets, txtNumberOfGeneratedTickets;
    @FXML
    private MFXTableView<Ticket> tblTickets;
    @FXML
    private MFXProgressSpinner progressSpinner;
    @FXML
    private Label progressLabel;
    private TicketModel ticketModel;
    private ObservableList<Ticket> tickets = FXCollections.observableArrayList();
    private Event event;
    private Task<TaskState> task;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        progressSpinner.setVisible(false);
        progressLabel.setVisible(false);

        setUpTableView();
    }

    @FXML
    private void generateTicket(ActionEvent actionEvent) {
        Ticket ticket = new Ticket(event, new Customer(txtCustomerName.getText().trim(), txtCustomerEmail.getText().trim()));
        task = new SaveTask(ticket, false, ticketModel);

        setUpTask(task);
        executeTask(task);
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

        //TODO multithreading
        refreshTableView();
        txtEventId.setText(event.getId() + " - " + event.getEventName());
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

    @Override
    protected void setIsEditing(Object objectToEdit) {
    }

    private void setUpTask(Task<TaskState> task) {
        task.setOnRunning(event -> {
            progressSpinner.progressProperty().bind(task.progressProperty());
            progressLabel.textProperty().bind(task.messageProperty());

            progressSpinner.setVisible(true);
            progressLabel.setVisible(true);
        });

        task.setOnFailed(event -> {
            progressSpinner.setVisible(false);
            progressLabel.setVisible(false);
            progressSpinner.progressProperty().unbind();
            progressLabel.textProperty().unbind();
        });

        task.setOnSucceeded(event -> {
            progressSpinner.progressProperty().unbind();
            progressSpinner.progressProperty().set(100);
            // after 3 seconds, the progress bar will be hidden
            new java.util.Timer().schedule(
                    new java.util.TimerTask() {
                        @Override
                        public void run() {
                            progressSpinner.setVisible(false);
                            progressLabel.setVisible(false);
                            progressLabel.textProperty().unbind();
                        }
                    },
                    3000
            );

            if (task.getValue() == TaskState.SUCCESSFUL) {
                refreshTableView();
                txtCustomerName.clear();
                txtCustomerEmail.clear();
                txtNumberOfTickets.clear();
            }
            else {
                AlertManager.getInstance().getAlert(Alert.AlertType.ERROR, "Ticket not saved!", event).showAndWait();
            }
        });
    }
}
