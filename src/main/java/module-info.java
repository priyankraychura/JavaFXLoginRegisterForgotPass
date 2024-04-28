module com.example.javafxloginregisterforgotpass {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.example.javafxloginregisterforgotpass to javafx.fxml;
    exports com.example.javafxloginregisterforgotpass;
}