package org.example.ui;

import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.model.Showing;
import org.example.model.User;
import org.example.service.ShowingService;

import java.io.IOException;
import java.time.format.DateTimeFormatter;

public class SelectSeatsController extends BaseController {
    private final PseudoClass errorClass = PseudoClass.getPseudoClass("error");
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private ShowingService showingService;
    private Showing selectedShowing;

    @FXML
    private Label selectedShowingLbl;
    @FXML
    private Button sellButton;

    public void initialize(User currentUser, Menu clickedMenu, Showing selectedShowing) {
        super.initialize(currentUser, clickedMenu);

        showingService = new ShowingService();
        this.selectedShowing = selectedShowing;

        setSelectedShowingLabel();
    }

    private void setSelectedShowingLabel() {
        String date = dateFormatter.format(selectedShowing.getStartDateTime().toLocalDate());
        String time = selectedShowing.getStartDateTime().toLocalTime().toString();
        selectedShowingLbl.setText(date + " " + time + " " + selectedShowing.getTitle());
    }

    @FXML
    protected void onSellButtonClick() {}

    @FXML
    protected void onCancelButtonClick() {
        openSellView();
    }

    private void openSellView() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("sell-view.fxml"));
            Parent root = fxmlLoader.load();

            Stage stage = (Stage)sellButton.getScene().getWindow();
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
}
