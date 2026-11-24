package bw.ac.thuto.bank.view;

import bw.ac.thuto.bank.controller.AccountController;
import bw.ac.thuto.bank.dao.AccountDAO;
import bw.ac.thuto.bank.dao.CustomerDAO;
import bw.ac.thuto.bank.dao.TransactionDAO;
import bw.ac.thuto.bank.model.Account;
import bw.ac.thuto.bank.model.Customer;
import bw.ac.thuto.bank.model.InterestBearing;
import bw.ac.thuto.bank.model.SavingsAccount;
import bw.ac.thuto.bank.model.Transaction;
import bw.ac.thuto.bank.model.UserAccount;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.sql.SQLException;
import java.util.List;

public class CustomerDashboardView {

    private final Stage stage;
    private final UserAccount loggedInUser;
    private final AccountController controller = new AccountController();
    private final AccountDAO accountDAO = new AccountDAO();
    private final CustomerDAO customerDAO = new CustomerDAO();
    private final TransactionDAO transactionDAO = new TransactionDAO();

    private final Label balanceLabel = new Label("BWP 0.00");
    private final ComboBox<Account> accountBox = new ComboBox<>();
    private final ComboBox<String> actionBox = new ComboBox<>();
    private final TableView<Transaction> historyTable = new TableView<>();

    public CustomerDashboardView(Stage stage, UserAccount loggedInUser) {
        this.stage = stage;
        this.loggedInUser = loggedInUser;
    }

    public void show() {
        stage.setTitle("Thuto Bank - Customer Dashboard");

        Label bankLogo = new Label("THUTO BANK");
        bankLogo.getStyleClass().add("top-bar-label");

        Label userInfo = new Label("Logged in: " + loggedInUser.getUsername());
        userInfo.setStyle("-fx-text-fill: #b0c4de; -fx-font-size: 14px;");

        Button logoutBtn = new Button("Logout");
        logoutBtn.getStyleClass().add("button-danger");
        logoutBtn.setOnAction(e -> new LoginView(stage).show());

        HBox topBar = new HBox(20, bankLogo, userInfo, logoutBtn);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.getStyleClass().add("top-bar");
        HBox.setHgrow(userInfo, Priority.ALWAYS);

        Customer customer = null;
        try {
            customer = customerDAO.findByCustomerId(loggedInUser.getCustomerId());
        } catch (SQLException e) {
            System.err.println("DB Error loading customer.");
        }

        if (customer == null) {
            Label err = new Label("Could not load customer data from database.");
            VBox root = new VBox(20, topBar, err);
            root.getStyleClass().add("root");
            stage.setScene(new Scene(root, 800, 500));
            stage.show();
            return;
        }

        Label accType = new Label("Your Accounts:");
        accType.getStyleClass().add("sub-header");

        accountBox.setPrefWidth(300);
        try {
            List<Account> accounts = accountDAO.findByCustomer(customer.getCustomerId(), customer);
            accountBox.getItems().setAll(accounts);
            if (!accounts.isEmpty()) {
                accountBox.getSelectionModel().selectFirst();
                updateBalanceDisplay(accounts.get(0));
                updateActionOptions(accounts.get(0));
                loadTransactionHistory(accounts.get(0).getAccountNumber());
            }
        } catch (SQLException e) {
            System.err.println("DB Error loading accounts: " + e.getMessage());
        }

        accountBox.setConverter(new StringConverter<Account>() {
            @Override public String toString(Account a) {
                return a == null ? "" : a.getAccountType() + " • " + a.getAccountNumber();
            }
            @Override public Account fromString(String s) { return null; }
        });

        accountBox.setOnAction(e -> {
            Account selected = accountBox.getValue();
            if (selected != null) {
                updateBalanceDisplay(selected);
                updateActionOptions(selected); // RULE ENFORCEMENT HERE
                loadTransactionHistory(selected.getAccountNumber());
            }
        });

        balanceLabel.getStyleClass().add("balance-text");
        balanceLabel.setWrapText(true);

        VBox balanceCard = new VBox(10, accType, accountBox, balanceLabel);
        balanceCard.getStyleClass().add("card");
        balanceCard.setPrefWidth(320);

        Label actionTitle = new Label("Quick Actions");
        actionTitle.setStyle("-fx-font-weight: bold; -fx-text-fill: #002B5B;");

        actionBox.setPrefWidth(200);

        TextField amountField = new TextField();
        amountField.setPromptText("Amount (BWP)");
        amountField.setPrefWidth(150);

        Button applyBtn = new Button("APPLY");
        applyBtn.getStyleClass().add("button-primary");

        applyBtn.setOnAction(e -> {
            Account selected = accountBox.getValue();
            if (selected == null) {
                balanceLabel.setText("Error: No account selected");
                return;
            }
            String action = actionBox.getValue();
            try {
                if ("Pay Interest".equals(action)) {
                    controller.payInterest(selected);
                } else {
                    double amount = Double.parseDouble(amountField.getText().trim());
                    if ("Deposit".equals(action)) controller.deposit(selected, amount);
                    if ("Withdraw".equals(action)) controller.withdraw(selected, amount);
                }
                updateBalanceDisplay(selected);
                loadTransactionHistory(selected.getAccountNumber());
                amountField.clear();
            } catch (NumberFormatException ex) {
                balanceLabel.setText("Error: Enter a valid number");
            } catch (Exception ex) {
                balanceLabel.setText("Error: " + ex.getMessage());
            }
        });

        HBox actionRow = new HBox(10, actionBox, amountField, applyBtn);
        actionRow.setAlignment(Pos.CENTER_LEFT);

        VBox actionCard = new VBox(15, actionTitle, actionRow);
        actionCard.getStyleClass().add("card");

        VBox leftPanel = new VBox(20, balanceCard, actionCard);

        Label historyTitle = new Label("Transaction History");
        historyTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #002B5B;");

        TableColumn<Transaction, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDateTime().substring(0, 10)));

        TableColumn<Transaction, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getType()));

        TableColumn<Transaction, Number> amountCol = new TableColumn<>("Amount (BWP)");
        amountCol.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getAmount()));

        TableColumn<Transaction, Number> balanceCol = new TableColumn<>("Balance (BWP)");
        balanceCol.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getBalanceAfter()));

        historyTable.getColumns().addAll(dateCol, typeCol, amountCol, balanceCol);
        historyTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        VBox historyCard = new VBox(15, historyTitle, historyTable);
        historyCard.getStyleClass().add("card");
        VBox.setVgrow(historyTable, Priority.ALWAYS);
        HBox.setHgrow(historyCard, Priority.ALWAYS);

        HBox contentArea = new HBox(20, leftPanel, historyCard);
        contentArea.setPadding(new Insets(30));
        contentArea.setAlignment(Pos.TOP_LEFT);

        BorderPane root = new BorderPane();
        root.setTop(topBar);
        root.setCenter(contentArea);
        root.getStyleClass().add("root");

        Scene scene = new Scene(root, 900, 550);
        if (getClass().getResource("/style.css") != null) {
            scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        }

        stage.setScene(scene);
        stage.show();
    }

    private void updateBalanceDisplay(Account account) {
        balanceLabel.setText("BWP " + String.format("%,.2f", account.getBalance()));
    }

    private void updateActionOptions(Account account) {
        actionBox.getItems().clear();

        actionBox.getItems().add("Deposit");

        if (!(account instanceof SavingsAccount)) {
            actionBox.getItems().add("Withdraw");
        }

        if (account instanceof InterestBearing) {
            actionBox.getItems().add("Pay Interest");
        }

        actionBox.getSelectionModel().selectFirst();
    }

    private void loadTransactionHistory(String accountNumber) {
        try {
            List<Transaction> list = transactionDAO.findByAccount(accountNumber);
            ObservableList<Transaction> data = FXCollections.observableArrayList(list);
            historyTable.setItems(data);
        } catch (SQLException e) {
            System.err.println("Error loading history: " + e.getMessage());
        }
    }
}
