package org.example.ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.model.User;
import org.example.service.LoginService;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
    @FXML
    private TextField usernameTF;
    @FXML
    private PasswordField passwordPF;
    @FXML
    private Button loginButton;
    @FXML
    private Label promptLbl;

    private LoginService loginService;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loginService = new LoginService();
    }

    @FXML
    protected void onLoginButtonClick() {
        promptLbl.setVisible(false);

        String username = usernameTF.getText();
        String password = passwordPF.getText();

        User user = loginService.login(username, password);
        if (user != null) {
            // Successful login, open welcome-view.fxml
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("welcome-view.fxml"));
                Parent root = fxmlLoader.load();

                Stage stage = (Stage)loginButton.getScene().getWindow();
                Scene scene = new Scene(root);
                scene.getStylesheets().add(getClass().getResource("/org/example/css/welcome-view.css").toExternalForm());

                InitializableMenu controller = fxmlLoader.getController();
                controller.initialize(user, null);

                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            // Invalid login
            promptLbl.setVisible(true);
        }
    }
}
