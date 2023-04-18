package ticketSystemEASV.gui.controller;

import io.github.palexdev.materialfx.controls.*;
import io.github.palexdev.materialfx.controls.cell.MFXComboBoxCell;
import io.github.palexdev.materialfx.controls.cell.MFXTableRowCell;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextFormatter;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import ticketSystemEASV.be.*;
import ticketSystemEASV.bll.util.AlertManager;
import ticketSystemEASV.gui.controller.addController.AddObjectController;
import ticketSystemEASV.gui.model.TicketModel;
import ticketSystemEASV.gui.model.VoucherModel;
import ticketSystemEASV.gui.tasks.SaveTask;
import ticketSystemEASV.gui.tasks.TaskState;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

public class TicketController extends AddObjectController implements Initializable {
    @FXML
    private MFXTextField txtEventId, txtCustomerName, txtCustomerEmail, txtNumberOfGeneratedTickets, txtMaxNumOfGenTickets;
    @FXML
    private MFXTableView<Ticket> tblTickets;
    @FXML
    private MFXProgressSpinner progressSpinner;
    @FXML
    private MFXComboBox comboTicketType;
    @FXML
    private MFXButton btnGenerateTicket;
    @FXML
    private Label progressLabel;
    @FXML
    private GridPane gridPane;
    private TicketModel ticketModel;
    private VoucherModel voucherModel;
    private ObservableList<Ticket> tickets = FXCollections.observableArrayList();
    private Event event;
    private Task<TaskState> task;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        progressSpinner.setVisible(false);
        progressLabel.setVisible(false);

        Platform.runLater(() -> {
            txtMaxNumOfGenTickets.setText(String.valueOf(event.getNumberOfTickets()));
            txtMaxNumOfGenTickets.setEditable(false);
            txtMaxNumOfGenTickets.setSelectable(false);
        });

        setUpTableView();
        setUpComboBox();
    }

    @FXML
    private void generateTicket(ActionEvent actionEvent) {
        if (txtCustomerName.getText().trim().isEmpty() || txtCustomerEmail.getText().trim().isEmpty()) {
            AlertManager.getInstance().getAlert(Alert.AlertType.ERROR, "Please fill in all fields", actionEvent).showAndWait();
            return;
        }

        if (comboTicketType.getSelectionModel().getSelectedItem() == "Tickets") {
            Ticket ticket = new Ticket(event, new Customer(txtCustomerName.getText().trim(), txtCustomerEmail.getText().trim()));
            task = new SaveTask(ticket, false, ticketModel);
            setUpTask(task);
            executeTask(task);
        } else if (comboTicketType.getSelectionModel().getSelectedItem() == "Vouchers") {
            // txtCustomerEmail == VoucherType
            int amount = Integer.parseInt(txtCustomerName.getText().trim());
            List<Voucher> vouchers = new ArrayList<>(amount);
            for (int i = 0; i < amount; i++) {
                vouchers.add(new Voucher(event, txtCustomerEmail.getText().trim()));
            }
            System.out.println(vouchers);
            voucherModel.addMultiple(vouchers);
        }
    }

    public void setTicketModel(TicketModel ticketModel, VoucherModel voucherModel) {
        this.ticketModel = ticketModel;
        this.voucherModel = voucherModel;
    }

    public void setEvent(Event event) {
        this.event = event;
        refreshTableView(TicketType.ALL);
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
            MFXTableRowCell<Ticket, String> row = new MFXTableRowCell<>(t -> t.getCustomer()!= null ? String.valueOf(t.getCustomer().getId()) : "-");
            row.setOnMouseClicked(this::tableViewDoubleClickAction);
            return row;
        });

        customerName.setRowCellFactory(customer -> {
            MFXTableRowCell<Ticket, String> row = new MFXTableRowCell<>(t -> t.getCustomer()!= null ? t.getCustomer().getName() : "-");
            row.setOnMouseClicked(this::tableViewDoubleClickAction);
            return row;
        });

        customerEmail.setRowCellFactory(customer -> {
            MFXTableRowCell<Ticket, String> row = new MFXTableRowCell<>(t -> t.getCustomer()!= null ? t.getCustomer().getEmail() : "-");
            row.setOnMouseClicked(this::tableViewDoubleClickAction);
            return row;
        });

        ticketType.setRowCellFactory(type -> {
            MFXTableRowCell<Ticket, String> row = new MFXTableRowCell<>(t -> t.getTicketType().toString());
            row.setOnMouseClicked(this::tableViewDoubleClickAction);
            return row;
        });

        tblTickets.getTableColumns().addAll(customerID, customerName, customerEmail, ticketType);
    }

    private void refreshTableView(TicketType ticketType){
        if (ticketType == TicketType.ALL) {
            Platform.runLater(() -> {
                if(event.getTickets().values().size() >= event.getNumberOfTickets()) {
                    txtCustomerEmail.setDisable(true);
                    txtCustomerName.setDisable(true);
                    btnGenerateTicket.setDisable(true);
                }else {
                    txtCustomerEmail.setDisable(false);
                    txtCustomerName.setDisable(false);
                    btnGenerateTicket.setDisable(false);
                }

                tickets.setAll(Stream.concat(
                        event.getTickets().values().stream(),
                        event.getVouchers().stream()
                ).toList());
            });

        }
        else if (ticketType == TicketType.TICKET)
            tickets.setAll(event.getTickets().values());
        else if (ticketType == TicketType.VOUCHER)
            tickets.setAll(event.getVouchers());
    }

    private void setUpComboBox() {
        comboTicketType.getItems().setAll("All", "Tickets", "Vouchers");
        comboTicketType.selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            switchScene(newValue.toString());
        });
        comboTicketType.getSelectionModel().selectFirst();
    }

    private void switchScene(String type) {
        txtCustomerName.clear();
        txtCustomerEmail.clear();
        if (type.equals("All")) {
            //txtCustomerName.setTextFormatter(null);
            refreshTableView(TicketType.ALL);

            txtNumberOfGeneratedTickets.setFloatingText("Generated tickets and vouchers");
            txtCustomerName.setFloatingText("Search...");

            txtCustomerEmail.setVisible(false);
            txtCustomerEmail.setManaged(false);

            btnGenerateTicket.setVisible(false);
            btnGenerateTicket.setManaged(false);
        } else if (type.equals("Tickets")) {
            refreshTableView(TicketType.TICKET);
            //txtCustomerName.setTextFormatter(null);

            txtNumberOfGeneratedTickets.setFloatingText("Number of generated tickets");
            txtCustomerName.setFloatingText("Customer name");

            txtCustomerEmail.setFloatingText("Customer e-mail");
            txtCustomerEmail.setVisible(true);
            txtCustomerEmail.setManaged(true);
            txtCustomerEmail.clear();

            btnGenerateTicket.setText("Generate Tickets");
            btnGenerateTicket.setVisible(true);
            btnGenerateTicket.setManaged(true);
        } else if (type.equals("Vouchers")) {
            refreshTableView(TicketType.VOUCHER);
            //txtCustomerName.setTextFormatter(new TextFormatter<>(numberFilter));

            txtNumberOfGeneratedTickets.setFloatingText("Number of generated vouchers");
            txtCustomerName.setFloatingText("Number of vouchers");

            txtCustomerEmail.setFloatingText("Voucher type");
            txtCustomerEmail.setVisible(true);
            txtCustomerEmail.setManaged(true);

            btnGenerateTicket.setVisible(true);
            btnGenerateTicket.setManaged(true);
            btnGenerateTicket.setText("Generate vouchers");
        }
    }

    private UnaryOperator<TextFormatter.Change> numberFilter = change -> {
        String text = change.getText();

        if (text.matches("\\d?")) { // this is the important line
            return change;
        }

        return null;
    };

    private ChangeListener<String> numberListener = new ChangeListener<String>() {
        @Override
        public void changed(ObservableValue<? extends String> observable,
                            String oldValue, String newValue) {
            System.out.println("Text changed from " + oldValue + " to " + newValue);
            // Check if the new value matches the Integer pattern
            if (!newValue.matches("\\d*")) {
                // if the new value does not match the Integer pattern, revert to the old value
                txtCustomerName.setText(newValue.replaceAll("[^\\d]", ""));
            }
        }
    };

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
                refreshTableView(TicketType.ALL);
                txtCustomerName.clear();
                txtCustomerEmail.clear();
                //txtNumberOfTickets.clear();
            }
            else {
                AlertManager.getInstance().getAlert(Alert.AlertType.ERROR, "Ticket not saved!", event).showAndWait();
            }
        });
    }
}
