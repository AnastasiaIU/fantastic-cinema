package org.example.ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.model.Selling;
import org.example.model.Showing;
import org.example.model.User;
import org.example.service.SellingService;
import org.example.service.ShowingService;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SelectSeatsController extends BaseController {
    private ShowingService showingService;
    private SellingService sellingService;
    private Showing selectedShowing;
    private ObservableList<int[]> chosenSeats;

    @FXML
    private Label selectedShowingLbl;
    @FXML
    private Button sellButton;
    @FXML
    private VBox roomSeatsVBox;
    @FXML
    private ListView<int[]> selectedSeatsListView;
    @FXML
    private TextField customerTextField;

    public void initialize(User currentUser, Menu clickedMenu, Showing selectedShowing) {
        super.initialize(currentUser, clickedMenu);

        showingService = new ShowingService();
        sellingService = new SellingService();

        this.selectedShowing = selectedShowing;

        chosenSeats = FXCollections.observableArrayList();
        selectedSeatsListView.setItems(chosenSeats);
        setCellValueFactories();

        selectedShowingLbl.setText(getSelectedShowing());
        displaySeats();

        setListeners();
    }

    private void setListeners() {
        chosenSeats.addListener(
                (javafx.collections.ListChangeListener.Change<? extends int[]> c) -> {
                    sellButton.setDisable(this.chosenSeats.isEmpty() || customerTextField.getText().isEmpty());

                    if (this.chosenSeats.isEmpty()) {
                        sellButton.setText("Sell ticket(s)");
                    } else {
                        sellButton.setText(String.format("Sell %d ticket(s)", this.chosenSeats.size()));
                    }
                }
        );

        customerTextField.textProperty().addListener(
                (observable, oldValue, newValue) -> {
                    sellButton.setDisable(this.chosenSeats.isEmpty() || newValue.isEmpty());
                }
        );
    }

    private String getSelectedShowing() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        String date = formatter.format(selectedShowing.getStartDateTime());
        return date + " " + selectedShowing.getTitle();
    }

    @FXML
    protected void onSellButtonClick() {
        String customerName = customerTextField.getText();
        LocalDateTime now = LocalDateTime.now();
        int ticketsSold = chosenSeats.size();
        String showing = getSelectedShowing();
        Selling selling = new Selling(-1, now, ticketsSold, showing, customerName);

        sellingService.addSelling(selling);

        for (int[] chosenSeat : chosenSeats) {
            showingService.sellTicket(selectedShowing.getId(), chosenSeat);
        }

        openSellView();
    }

    @FXML
    protected void onCancelButtonClick() {
        openSellView();
    }

    private void openSellView() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("sell-view.fxml"));
            Parent root = fxmlLoader.load();

            Stage stage = (Stage) sellButton.getScene().getWindow();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/org/example/css/sell-view.css").toExternalForm());

            InitializableMenu controller = fxmlLoader.getController();
            controller.initialize(currentUser, showingsMenu);

            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void displaySeats() {
        roomSeatsVBox.getChildren().clear();
        boolean[][] seats = selectedShowing.getReservedSeats();

        for (int i = 0; i < seats.length; i++) {
            HBox rowHBox = createRow(i + 1);

            for (int j = 0; j < seats[i].length; j++) {
                Button seatButton = createSeatButton(seats, i, j);
                rowHBox.getChildren().add(seatButton);
            }
        }
    }

    private Button createSeatButton(boolean[][] seats, int row, int column) {
        Button seatButton = new Button();
        seatButton.setText(String.valueOf(column + 1));
        seatButton.getStyleClass().add("seat-button");

        // Store row and column indices as user data
        seatButton.setUserData(new int[]{row, column});

        if (seats[row][column]) {
            seatButton.getStyleClass().add("seat-taken");
            seatButton.setDisable(true);
        } else {
            seatButton.getStyleClass().add("seat-free");
        }

        // Add button click event handler
        seatButton.setOnAction(event -> {
            selectSeat(seatButton);
        });

        return seatButton;
    }

    private HBox createRow(int number) {
        HBox rowHBox = new HBox();
        Label rowName = new Label(String.format("Row %d", number));
        rowName.getStyleClass().clear();
        rowName.getStyleClass().add("row-name");
        rowHBox.getChildren().add(rowName);
        roomSeatsVBox.getChildren().add(rowHBox);
        return rowHBox;
    }

    private void selectSeat(Button seatButton) {
        int[] seat = (int[]) seatButton.getUserData();

        if (chosenSeats.contains(seat)) {
            chosenSeats.remove(seatButton.getUserData());
            seatButton.getStyleClass().remove("seat-chosen");
        } else {
            chosenSeats.add(seat);
            seatButton.getStyleClass().add("seat-chosen");
        }
    }

    private void setCellValueFactories() {
        selectedSeatsListView.setCellFactory(
                param -> new ListCell<>() {
                    @Override
                    protected void updateItem(int[] item, boolean empty) {
                        super.updateItem(item, empty);

                        if (empty || item == null) {
                            setText(null);
                        } else {
                            setText(String.format("Row %d / Seat %d", item[0] + 1, item[1] + 1));
                        }
                    }
                }
        );
    }
}
