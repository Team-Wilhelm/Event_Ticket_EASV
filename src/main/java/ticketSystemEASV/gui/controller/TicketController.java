package ticketSystemEASV.gui.controller;

import io.github.palexdev.materialfx.controls.*;
import io.github.palexdev.materialfx.controls.cell.MFXTableRowCell;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
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
import ticketSystemEASV.gui.model.EventModel;
import ticketSystemEASV.gui.model.TicketModel;
import ticketSystemEASV.gui.model.VoucherModel;
import ticketSystemEASV.gui.tasks.SaveTask;
import ticketSystemEASV.gui.tasks.TaskState;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.CountDownLatch;
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
    private EventModel eventModel;
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
            setUpTableView();
        });
        setUpComboBox();
    }

    @FXML
    private void generateTicket(ActionEvent actionEvent) {
        if (txtCustomerName.getText().trim().isEmpty() || txtCustomerEmail.getText().trim().isEmpty()) {
            AlertManager.getInstance().getAlert(Alert.AlertType.ERROR, "Please fill in all fields", actionEvent).showAndWait();
            return;
        }

        if (TicketType.valueOf((String) comboTicketType.getSelectionModel().getSelectedItem()) == TicketType.TICKET) {
            Ticket ticket = new Ticket(event, new Customer(txtCustomerName.getText().trim(), txtCustomerEmail.getText().trim()));
            task = new SaveTask(ticket, false, ticketModel);
            setUpTask(task);
            executeTask(task);
            //ticketModel.getTicketsFromManager(new CountDownLatch(0));
            //var ticketToSend = tickets.stream().filter(t -> t.getCustomer().getEmail().equals(txtCustomerEmail.getText().trim())).findFirst().get();

            // TODO: Implement sending of the email here
            /*
            HttpPostMultipart multipart = new HttpPostMultipart("https://eventticketsystem.azurewebsites.net/api/emailapi","UTF-8");

            multipart.addFormField("event", event.getEventName());
            multipart.addFormField("ToEmail", ticket.getCustomer().getEmail());
            multipart.addFormField("ToName", ticket.getCustomer().getName());
            //multipart.addFilePart("file", new File() ticketToSend.getTicketQR());

            String response = multipart.finish();
            System.out.println("SERVER REPLIED:");
            System.out.println(response);
            */

        } else if (TicketType.valueOf((String) comboTicketType.getSelectionModel().getSelectedItem()) == TicketType.VOUCHER) {
            // txtCustomerEmail == VoucherType
            int amount = Integer.parseInt(txtCustomerName.getText().trim());
            List<Voucher> vouchers = new ArrayList<>(amount);
            for (int i = 0; i < amount; i++) {
                vouchers.add(new Voucher(event, txtCustomerEmail.getText().trim()));
            }

            Task<TaskState> task = new Task<>() {
                @Override
                protected TaskState call() {
                    if (isCancelled()) {
                        updateMessage("Saving was not successful");
                        return TaskState.NOT_SUCCESSFUL;
                    } else {
                        updateMessage("Saving...");
                        String message = voucherModel.addMultiple(vouchers);

                        if (message.isEmpty()) {
                            updateMessage("Saved successfully");
                            return TaskState.SUCCESSFUL;
                        } else {
                            updateMessage("Saving was not successful");
                            return TaskState.NOT_SUCCESSFUL;
                        }
                    }
                }
            };

            setUpTask(task);
            executeTask(task);
        }
    }

    public void setTicketModel(TicketModel ticketModel, VoucherModel voucherModel, EventModel eventModel) {
        this.ticketModel = ticketModel;
        this.voucherModel = voucherModel;
        this.eventModel = eventModel;
    }

    public void setEvent(Event event) {
        this.event = event;
        refreshTableView(TicketType.ALL);
        txtEventId.setText(event.getId() + " - " + event.getEventName());
    }

    private void setUpTableView() {
        Bindings.bindContentBidirectional(tblTickets.getItems(), tickets);
        txtNumberOfGeneratedTickets.textProperty().bind(Bindings.size(tblTickets.getItems()).asString());

        MFXTableColumn<Ticket> customerID = new MFXTableColumn<>("Customer ID/Voucher ID", true);
        MFXTableColumn<Ticket> customerName = new MFXTableColumn<>("Customer name/Voucher type", true);
        MFXTableColumn<Ticket> customerEmail = new MFXTableColumn<>("Customer e-mail", true);
        MFXTableColumn<Ticket> ticketType = new MFXTableColumn<>("Ticket type", true);

        customerID.setRowCellFactory(customer -> {
            MFXTableRowCell<Ticket, String> row = new MFXTableRowCell<>(t ->
                    t.getCustomer()!= null ? String.valueOf(t.getCustomer().getId()) : String.valueOf(t.getId()));
            row.setOnMouseClicked(this::tableViewDoubleClickAction);
            return row;
        });

        customerName.setRowCellFactory(customer -> {
            MFXTableRowCell<Ticket, String> row = new MFXTableRowCell<>(t ->
                    t.getCustomer()!= null ? t.getCustomer().getName() : ((Voucher) t).getVoucherType());
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
        if(eventModel != null)
            eventModel.seedTickets(event);
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

                System.out.println();
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
        for (TicketType type : TicketType.values())
            comboTicketType.getItems().add(type.toString());

        comboTicketType.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue observable, String oldValue, String newValue) {
                switchScene(TicketType.valueOf(newValue));
            }
        });

        comboTicketType.getSelectionModel().selectFirst();
    }

    private void switchScene(TicketType type) {
        txtCustomerName.clear();
        txtCustomerEmail.clear();
        txtCustomerName.textProperty().removeListener(searchListener);
        switch (type) {
            case ALL -> {
                refreshTableView(TicketType.ALL);
                txtNumberOfGeneratedTickets.setFloatingText("Generated tickets and vouchers");
                txtCustomerName.setFloatingText("Search...");
                txtCustomerName.textProperty().addListener(searchListener);
                txtCustomerEmail.setVisible(false);
                txtCustomerEmail.setManaged(false);
                btnGenerateTicket.setVisible(false);
                btnGenerateTicket.setManaged(false);
            }
            case TICKET -> {
                refreshTableView(TicketType.TICKET);
                txtNumberOfGeneratedTickets.setFloatingText("Number of generated tickets");
                txtCustomerName.setFloatingText("Customer name");
                txtCustomerEmail.setFloatingText("Customer e-mail");
                txtCustomerEmail.setVisible(true);
                txtCustomerEmail.setManaged(true);
                txtCustomerEmail.clear();
                btnGenerateTicket.setText("Generate Tickets");
                btnGenerateTicket.setVisible(true);
                btnGenerateTicket.setManaged(true);
            }
            case VOUCHER -> {
                refreshTableView(TicketType.VOUCHER);
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
                refreshTableView((TicketType.valueOf((String) comboTicketType.getSelectionModel().getSelectedItem())));
                txtCustomerName.clear();
                txtCustomerEmail.clear();
                //txtNumberOfTickets.clear();
                System.out.println("Ticket saved!");
            }
            else {
                AlertManager.getInstance().getAlert(Alert.AlertType.ERROR, "Ticket not saved!", event).showAndWait();
            }
        });
    }

    private ChangeListener<String> searchListener = (observable, oldValue, newValue) -> {
        if (!newValue.isEmpty())
            searchTickets(newValue);
        else
            refreshTableView(TicketType.ALL);
    };

    private void searchTickets(String query) {
            List<Ticket> filteredTickets = new ArrayList<>();
            for (Ticket ticket : tickets) {
                if (ticket.getCustomer().getEmail().toLowerCase().trim().contains(query.toLowerCase().trim())
                || ticket.getCustomer().getName().toLowerCase().trim().contains(query.toLowerCase().trim()))
                    filteredTickets.add(ticket);
            }
            tickets.setAll(filteredTickets);
            tblTickets.setItems(tickets);
    }
}
