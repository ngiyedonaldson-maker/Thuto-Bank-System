package bw.ac.thuto.bank.view;

import bw.ac.thuto.bank.dao.AccountDAO;
import bw.ac.thuto.bank.dao.CustomerDAO;
import bw.ac.thuto.bank.model.Account;
import bw.ac.thuto.bank.model.ChequeAccount;
import bw.ac.thuto.bank.model.Customer;
import bw.ac.thuto.bank.model.InvestmentAccount;
import bw.ac.thuto.bank.model.SavingsAccount;
import bw.ac.thuto.bank.model.UserAccount;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.List;

public class StaffDashboardView {
    private final Stage stage;
    private final UserAccount user;
    private final CustomerDAO customerDAO = new CustomerDAO();
    private final AccountDAO accountDAO = new AccountDAO();

    public StaffDashboardView(Stage stage, UserAccount user) {
        this.stage = stage;
        this.user = user;
    }

    public void show() {
        stage.setTitle("Thuto Bank - Staff Portal");

        // 1. TOP BAR
        Label bankLogo = new Label("THUTO BANK - STAFF");
        bankLogo.getStyleClass().add("top-bar-label");

        Label userInfo = new Label("Logged in: " + user.getUsername() + " (" + user.getRole() + ")");
        userInfo.setStyle("-fx-text-fill: #b0c4de; -fx-font-size: 14px;");

        Button logoutBtn = new Button("Logout");
        logoutBtn.getStyleClass().add("button-danger");
        logoutBtn.setOnAction(e -> new LoginView(stage).show());

        HBox topBar = new HBox(20, bankLogo, userInfo, logoutBtn);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.getStyleClass().add("top-bar");
        HBox.setHgrow(userInfo, Priority.ALWAYS);

        // 2. SEARCH AREA
        Label searchTitle = new Label("Customer Lookup");
        searchTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #002B5B;");

        TextField searchField = new TextField();
        searchField.setPromptText("Enter Customer ID (e.g., C100)");
        searchField.setPrefWidth(250);

        Button searchBtn = new Button("SEARCH");
        searchBtn.getStyleClass().add("button-primary");

        VBox resultsBox = new VBox(15);
        resultsBox.setPadding(new Insets(10, 0, 0, 0));

        searchBtn.setOnAction(e -> {
            resultsBox.getChildren().clear();
            String searchId = searchField.getText().trim();

            if (searchId.isEmpty()) {
                resultsBox.getChildren().add(new Label("Please enter a Customer ID."));
                return;
            }

            try {
                Customer customer = customerDAO.findByCustomerId(searchId);

                if (customer == null) {
                    resultsBox.getChildren().add(new Label("No customer found with ID: " + searchId));
                } else {
                    // Customer Details
                    Label nameLabel = new Label("Name: " + customer.getFullName());
                    Label phoneLabel = new Label("Phone: " + customer.getPhone());
                    Label emailLabel = new Label("Email: " + customer.getEmail());

                    // UPDATE CONTACT INFO FORM
                    Label updateTitle = new Label("Update Contact Info:");
                    updateTitle.setStyle("-fx-font-weight: bold; -fx-padding: 10 0 0 0;");

                    TextField newPhoneField = new TextField(customer.getPhone());
                    newPhoneField.setPromptText("New Phone");
                    newPhoneField.setPrefWidth(150);

                    TextField newEmailField = new TextField(customer.getEmail());
                    newEmailField.setPromptText("New Email");
                    newEmailField.setPrefWidth(200);

                    Button updateBtn = new Button("UPDATE");
                    updateBtn.getStyleClass().add("button-primary");

                    Label updateMsg = new Label();

                    updateBtn.setOnAction(ev -> {
                        try {
                            customerDAO.updateContactInfo(customer.getCustomerId(), newPhoneField.getText(), newEmailField.getText());
                            updateMsg.setText("Contact info updated successfully!");
                            updateMsg.setStyle("-fx-text-fill: green;");

                            searchBtn.fire();

                        } catch (SQLException ex) {
                            updateMsg.setText("Error updating: " + ex.getMessage());
                            updateMsg.setStyle("-fx-text-fill: red;");
                        }
                    });

                    HBox updateRow = new HBox(10, newPhoneField, newEmailField, updateBtn);
                    updateRow.setAlignment(Pos.CENTER_LEFT);

                    Label accountsTitle = new Label("Accounts:");
                    accountsTitle.setStyle("-fx-font-weight: bold;");
                    VBox accountList = new VBox(5);
                    List<Account> accounts = accountDAO.findByCustomer(customer.getCustomerId(), customer);

                    if (accounts.isEmpty()) {
                        accountList.getChildren().add(new Label("  (No accounts opened yet)"));
                    } else {
                        for (Account acc : accounts) {
                            Label accRow = new Label(String.format("  %s • %s • Balance: BWP %,.2f",
                                    acc.getAccountType(), acc.getAccountNumber(), acc.getBalance()));
                            accountList.getChildren().add(accRow);
                        }
                    }

                    Label openTitle = new Label("Open New Account:");
                    openTitle.setStyle("-fx-font-weight: bold; -fx-padding: 10 0 0 0;");

                    ComboBox<String> typeBox = new ComboBox<>();
                    typeBox.getItems().addAll("Savings Account", "Investment Account", "Cheque Account");
                    typeBox.getSelectionModel().selectFirst();

                    Button openBtn = new Button("OPEN ACCOUNT");
                    openBtn.getStyleClass().add("button-primary");

                    Label openMsg = new Label();

                    openBtn.setOnAction(ev -> {
                        String type = typeBox.getValue();
                        try {
                            String accNum = "ACC" + (1000 + (int)(Math.random() * 9000));
                            Account newAccount;

                            if (type.equals("Savings Account")) {
                                newAccount = new SavingsAccount(accNum, "Main Branch", customer);
                            } else if (type.equals("Investment Account")) {
                                newAccount = new InvestmentAccount(accNum, "Main Branch", customer);
                            } else {
                                newAccount = new ChequeAccount(accNum, "Main Branch", customer);
                            }

                            accountDAO.save(newAccount);
                            openMsg.setText("Success! Created " + accNum);
                            openMsg.setStyle("-fx-text-fill: green;");

                            searchBtn.fire();

                        } catch (Exception ex) {
                            openMsg.setText("Error: " + ex.getMessage());
                            openMsg.setStyle("-fx-text-fill: red;");
                        }
                    });

                    HBox openRow = new HBox(10, typeBox, openBtn);
                    openRow.setAlignment(Pos.CENTER_LEFT);

                    resultsBox.getChildren().addAll(
                            nameLabel, phoneLabel, emailLabel,
                            updateTitle, updateRow, updateMsg,
                            accountsTitle, accountList,
                            openTitle, openRow, openMsg
                    );
                }
            } catch (SQLException ex) {
                resultsBox.getChildren().add(new Label("Database error: " + ex.getMessage()));
            }
        });

        HBox searchRow = new HBox(10, searchField, searchBtn);
        searchRow.setAlignment(Pos.CENTER_LEFT);

        VBox searchCard = new VBox(15, searchTitle, searchRow, resultsBox);
        searchCard.getStyleClass().add("card");

        VBox contentArea = new VBox(20, searchCard);
        contentArea.setPadding(new Insets(30));

        BorderPane root = new BorderPane();
        root.setTop(topBar);
        root.setCenter(contentArea);
        root.getStyleClass().add("root");

        Scene scene = new Scene(root, 850, 700);
        if (getClass().getResource("/style.css") != null) {
            scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        }
        stage.setScene(scene);
        stage.show();
    }
}