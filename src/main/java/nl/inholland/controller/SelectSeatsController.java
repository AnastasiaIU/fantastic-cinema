package nl.inholland.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import javafx.stage.Stage;
import nl.inholland.Database;
import nl.inholland.model.Selling;
import nl.inholland.model.Showing;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.ResourceBundle;

/**
 * Controller for the seat selection view in the application.
 * This class manages the seat selection process, allowing users to select available seats and finalize their selection.
 * It implements the {@link Initializable} interface to set up the view when the controller is loaded.
 */
public class SelectSeatsController implements Initializable {
    // Reference to the shared Database instance
    private final Database database;
    // Reference to the root VBox container of the scene
    private final VBox root;
    // Reference to the currently selected showing
    private final Showing selectedShowing;
    // Observable list to keep track of the chosen seats
    private ObservableList<int[]> chosenSeats;

    // FXML-injected components
    @FXML
    private Label selectedShowingLabel;
    @FXML
    private Label screenLabel;
    @FXML
    private Button sellButton;
    @FXML
    private Button cancelButton;
    @FXML
    private VBox roomSeatsVBox;
    @FXML
    private ListView<int[]> selectedSeatsListView;
    @FXML
    private TextField customerTextField;
    @FXML
    private Line screenLine;

    /**
     * Constructor for the SelectSeatsController.
     *
     * @param database        The shared database instance.
     * @param root            The root layout component.
     * @param selectedShowing The showing for which seats are being selected.
     */
    public SelectSeatsController(Database database, VBox root, Showing selectedShowing) {
        this.database = database;
        this.selectedShowing = selectedShowing;
        this.root = root;
    }

    /**
     * Initializes the controller and sets up the initial state of the seat selection view.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        chosenSeats = FXCollections.observableArrayList();
        selectedSeatsListView.setItems(chosenSeats);
        selectedShowingLabel.setText(getFormattedSelectedShowing());

        setCellFactory();
        displaySeats();
        addListenersForDisablingSellButton();
        addListenersToButtons();
        addListenerForDrawingScreenLine();
    }

    /**
     * Draws the screen line relative to the position of the row label.
     *
     * @param rowName The label representing the row.
     */
    private void drawScreenLine(Label rowName) {
        double labelWidth = rowName.getLayoutBounds().getWidth();
        double rowWidth = rowName.getParent().getLayoutBounds().getWidth();
        VBox.setMargin(screenLine, new Insets(0, 0, 0, labelWidth));
        screenLine.setEndX(rowWidth - labelWidth);
        double screenLabelX = labelWidth + (rowWidth - labelWidth) / 2 - screenLabel.getLayoutBounds().getWidth() / 2;
        VBox.setMargin(screenLabel, new Insets(0, 0, 6, screenLabelX));
    }

    /**
     * Adds a listener to draw the screen line after the scene is shown to ensure the line is correctly positioned.
     */
    private void addListenerForDrawingScreenLine() {
        Label rowName = getRowNameLabel();
        rowName.widthProperty().addListener((observable, oldValue, newValue) -> {
            drawScreenLine(rowName);
        });
    }

    /**
     * Adds listeners to enable or disable the sell button based on the seat and customer name selections.
     */
    private void addListenersForDisablingSellButton() {
        chosenSeats.addListener(
                (javafx.collections.ListChangeListener.Change<? extends int[]> c) -> {
                    sellButton.setDisable(this.chosenSeats.isEmpty() || customerTextField.getText().trim().isEmpty());

                    if (this.chosenSeats.isEmpty()) {
                        sellButton.setText("Sell ticket(s)");
                    } else {
                        sellButton.setText(String.format("Sell %d ticket(s)", this.chosenSeats.size()));
                    }
                }
        );

        customerTextField.textProperty().addListener(
                (observable, oldValue, newValue) -> {
                    sellButton.setDisable(this.chosenSeats.isEmpty() || newValue.trim().isEmpty());
                }
        );
    }

    /**
     * Adds action listeners to the sell and cancel buttons.
     */
    private void addListenersToButtons() {
        sellButton.setOnAction(event -> {
            addSellToDatabase();
            openSellView();
        });

        cancelButton.setOnAction(event -> {
            openSellView();
        });
    }

    /**
     * Retrieves the label representing the row in the seat layout.
     *
     * @return The label for the row.
     */
    private Label getRowNameLabel() {
        Label rowName = null;

        for (Node node : roomSeatsVBox.getChildren()) {
            if (rowName != null) break;

            for (Node child : ((HBox) node).getChildren()) {
                if (child instanceof Label) {
                    rowName = (Label) child;
                    break;
                }
            }
        }

        return rowName;
    }

    /**
     * Formats the selected showing to display the date and title.
     *
     * @return A formatted string representing the showing.
     */
    private String getFormattedSelectedShowing() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        String date = formatter.format(selectedShowing.getStartDateTime());
        return date + " " + selectedShowing.getTitle();
    }

    /**
     * Adds a new sale to the database based on the selected seats and customer name.
     */
    private void addSellToDatabase() {
        String customerName = customerTextField.getText().trim();
        LocalDateTime now = LocalDateTime.now();
        int ticketsSold = chosenSeats.size();
        Selling selling = new Selling(-1, now, ticketsSold, selectedShowing, customerName, new ArrayList<>(chosenSeats));

        Stage mainStage = (Stage) root.getScene().getWindow();

        if (selling.getShowing().getIsAgeChecked()) {
            AgeCheckController ageCheckController = new AgeCheckController(selling);
            boolean isConfirmed = ageCheckController.showDialog(mainStage);

            if (isConfirmed) {
                sellTickets(selling);
            } else {
                openSellView();
            }
        } else {
            sellTickets(selling);
        }
    }

    /**
     * Processes the ticket selling operation for a given showing.
     * This method adds the selling information to the database and marks each chosen seat as reserved.
     *
     * @param selling The {@link Selling} object containing details of the transaction, such as the customer name,
     *                number of tickets sold, and showing information.
     */
    public void sellTickets(Selling selling) {
        database.addSelling(selling);

        for (int[] chosenSeat : chosenSeats) {
            database.sellTicket(selectedShowing.getId(), chosenSeat);
        }
    }

    /**
     * Opens the sell view when the user cancels or confirms the seat selection.
     */
    private void openSellView() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/nl/inholland/view/sell-view.fxml"));
            fxmlLoader.setController(new SellController(database, root));
            Scene scene = new Scene(fxmlLoader.load());

            if (root.getChildren().size() > 1)
                root.getChildren().remove(1);
            root.getChildren().add(scene.getRoot());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Displays the available and reserved seats for the selected showing.
     */
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

    /**
     * Creates a seat button for the given row and column, configuring its state and event handler.
     *
     * @param seats  The seat availability array.
     * @param row    The row index.
     * @param column The column index.
     * @return The configured Button object representing a seat.
     */
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

    /**
     * Creates a row with a label representing the row number.
     *
     * @param number The row number.
     * @return The configured HBox object representing the row.
     */
    private HBox createRow(int number) {
        HBox rowHBox = new HBox();
        Label rowName = new Label(String.format("Row %d", number));
        rowName.getStyleClass().add("row-name");
        rowHBox.getChildren().add(rowName);
        roomSeatsVBox.getChildren().add(rowHBox);
        return rowHBox;
    }

    /**
     * Handles seat selection or deselection when a seat button is clicked.
     *
     * @param seatButton The button representing the selected seat.
     */
    private void selectSeat(Button seatButton) {
        int[] seat = (int[]) seatButton.getUserData();

        if (chosenSeats.contains(seat)) {
            chosenSeats.remove(seat);
            seatButton.getStyleClass().remove("seat-chosen");
        } else {
            chosenSeats.add(seat);
            seatButton.getStyleClass().add("seat-chosen");
        }
    }

    /**
     * Sets the cell factory for the ListView to display selected seats.
     */
    private void setCellFactory() {
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
