package bw.ac.thuto.bank.view;

import bw.ac.thuto.bank.controller.LoginController;
import bw.ac.thuto.bank.model.UserAccount;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LoginView {

    private final Stage stage;
    private final LoginController controller = new LoginController();

    public LoginView(Stage stage) {
        this.stage = stage;
    }

    public void show() {
        stage.setTitle("Thuto Bank - Secure Login");

        Label title = new Label("THUTO BANK");
        title.getStyleClass().add("header-label");

        Label subtitle = new Label("Welcome back. Please log in.");
        subtitle.getStyleClass().add("sub-header");

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        usernameField.setMaxWidth(300);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setMaxWidth(300);

        Button loginBtn = new Button("LOGIN");
        loginBtn.getStyleClass().add("button-primary");
        loginBtn.setMaxWidth(300);

        Label message = new Label();
        message.setStyle("-fx-text-fill: #dc3545; -fx-font-weight: bold;");

        loginBtn.setOnAction(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();

            UserAccount user = controller.login(username, password);

            if (user != null) {

                if ("CUSTOMER".equals(user.getRole())) {
                    new CustomerDashboardView(stage, user).show();
                } else {
                    new StaffDashboardView(stage, user).show();
                }
            } else {
                message.setText("Invalid username or password");
            }
        });

        VBox card = new VBox(15, title, subtitle, usernameField, passwordField, loginBtn, message);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(40));
        card.getStyleClass().add("card");
        card.setMaxWidth(400);

        VBox root = new VBox(card);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(20));
        root.getStyleClass().add("root");

        Scene scene = new Scene(root, 500, 500);

        if (getClass().getResource("/style.css") != null) {
            scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        }

        stage.setScene(scene);
        stage.show();
    }
}


