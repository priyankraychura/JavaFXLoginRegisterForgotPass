package com.example.javafxloginregisterforgotpass;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class FormController implements Initializable {
    @FXML
    private Button changePass_backBtn;

    @FXML
    private PasswordField changePass_cPassword;

    @FXML
    private AnchorPane changePass_form;

    @FXML
    private PasswordField changePass_password;

    @FXML
    private Button changePass_proceedBtn;

    @FXML
    private TextField forgot_answer;

    @FXML
    private Button forgot_backBtn;

    @FXML
    private AnchorPane forgot_form;

    @FXML
    private Button forgot_proceedBtn;

    @FXML
    private ComboBox<?> forgot_selectQuestion;

    @FXML
    private TextField forgot_username;

    @FXML
    private Button login_btn;

    @FXML
    private Button login_createAccount;

    @FXML
    private Hyperlink login_forgotPassword;

    @FXML
    private AnchorPane login_form;

    @FXML
    private PasswordField login_password;

    @FXML
    private CheckBox login_selectShowPassword;

    @FXML
    private TextField login_showPassword;

    @FXML
    private TextField login_username;

    @FXML
    private AnchorPane main_form;

    @FXML
    private TextField signup_answer;

    @FXML
    private Button signup_btn;

    @FXML
    private PasswordField signup_cPassword;

    @FXML
    private TextField signup_email;

    @FXML
    private AnchorPane signup_form;

    @FXML
    private Button signup_loginAccount;

    @FXML
    private PasswordField signup_password;

    @FXML
    private ComboBox<?> signup_selectQuestion;

    @FXML
    private TextField signup_username;

    private Connection connect;
    private PreparedStatement prepare;
    private ResultSet result;
    private Statement statement;

    public Connection connectDB(){
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            String url = "jdbc:mysql://localhost/useraccount";
            Connection connect = DriverManager.getConnection(url, "root", "");
            return connect;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void login(){
        AlertMessage alert = new AlertMessage();

        if (login_username.getText().isEmpty() || login_password.getText().isEmpty()){
            alert.errorMessage("All fields are necessary to be filled");
        } else {
            String selectDate = "SELECT username, password FROM users WHERE username = ? AND password = ?";
            connect = connectDB();

            if (login_selectShowPassword.isSelected()){
                login_password.setText(login_showPassword.getText());
            } else {
                login_showPassword.setText(login_password.getText());
            }

            try {
                prepare = connect.prepareStatement(selectDate);
                prepare.setString(1, login_username.getText());
                prepare.setString(2, login_password.getText());

                result = prepare.executeQuery();

                if (result.next()){
                    //IF ALL THE DATA ARE CORRECT THEN WE WILL PROCESS TO THE MAIN FORM
                    alert.successMessage("Successfully Login!");

                    //TO LINK THE MAIN FORM
                    Parent root = null;
                    try {
                        root = FXMLLoader.load(getClass().getResource("mainForm.fxml"));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    Stage stage = new Stage();
                    Scene scene = new Scene(root);

                    stage.setScene(scene);
                    //TO SHOW THE MAIN FORM
                    stage.show();

                    //TO HIDE LOGIN FORM
                    login_btn.getScene().getWindow().hide();
                } else {
                    alert.errorMessage("Incorrect Username/Password");
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void showPassword(){
        if (login_selectShowPassword.isSelected()){
            login_showPassword.setText(login_password.getText());
            login_showPassword.setVisible(true);
            login_password.setVisible(false);
        } else {
            login_password.setText(login_showPassword.getText());
            login_showPassword.setVisible(false);
            login_password.setVisible(true);
        }
    }

    public void forgotPass(){
        AlertMessage alert = new AlertMessage();

        if (forgot_username.getText().isEmpty() || forgot_selectQuestion.getSelectionModel().getSelectedItem() == null ||
            forgot_answer.getText().isEmpty()){
            alert.errorMessage("Please fill all blank fields");
        } else{
            String checkData = "SELECT username, question, answer FROM users "
                    + "WHERE username = ? AND question = ? AND answer = ?";
            connect = connectDB();

            try {
                prepare = connect.prepareStatement(checkData);
                prepare.setString(1, forgot_username.getText());
                prepare.setString(2, (String)forgot_selectQuestion.getSelectionModel().getSelectedItem());
                prepare.setString(3, forgot_answer.getText());

                result = prepare.executeQuery();

                if (result.next()){
                    //PROCEED TO CHANGE THE PASSWORD
                    signup_form.setVisible(false);
                    login_form.setVisible(false);
                    forgot_form.setVisible(false);
                    changePass_form.setVisible(true);
                } else{
                    alert.errorMessage("Incorrect Information");
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void forgotListQuestion(){
        List<String> listQ = new ArrayList<>();

        for (String data: questionList){
            listQ.add(data);
        }

        ObservableList listData = FXCollections.observableArrayList(listQ);
        forgot_selectQuestion.setItems(listData);
    }

    public void register(){
        AlertMessage alert = new AlertMessage();

        //CHECK IF ANY FIELDS ARE EMPTY
        if (signup_email.getText().isEmpty() || signup_username.getText().isEmpty() || signup_password.getText().isEmpty() ||
            signup_cPassword.getText().isEmpty() || signup_selectQuestion.getSelectionModel().getSelectedItem() == null ||
            signup_answer.getText().isEmpty())
        {
            alert.errorMessage("All fields are necessary to be filled");
        } else if (signup_password.getText() == signup_cPassword.getText()) {
            alert.errorMessage("Password does not match");
        } else if (signup_password.getText().length() < 8) {
            alert.errorMessage("Invalid Password, atleast 8 characters needed");
        } else {
            //  CHECK IF THE USERNAME IS ALREADY TAKEN OR NOT
            String checkUsername = "SELECT * FROM users WHERE username = '"+ signup_username.getText() +"'";
            connect = connectDB();

            try {
                statement = connect.createStatement();
                result = statement.executeQuery(checkUsername);

                if (result.next()){
                    alert.errorMessage(signup_username.getText() + " is already taken");
                } else {
                    String insertDate = "INSERT INTO users(email, username, password, question, answer, date) " +
                                        "VALUES(?, ?, ?, ?, ?, ?)";

                    prepare = connect.prepareStatement(insertDate);
                    prepare.setString(1, signup_email.getText());
                    prepare.setString(2, signup_username.getText());
                    prepare.setString(3, signup_password.getText());
                    prepare.setString(4, (String)signup_selectQuestion.getSelectionModel().getSelectedItem());
                    prepare.setString(5, signup_answer.getText());

                    java.util.Date date = new java.util.Date();
                    java.sql.Date sqlDate = new java.sql.Date(date.getTime());

                    prepare.setString(6, String.valueOf(sqlDate));
                    prepare.executeUpdate();

                    alert.successMessage("Registered Successfully!");
                    
                    registerClearFields();

                    signup_form.setVisible(false);
                    login_form.setVisible(true);

                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }
    
    //TO CLEAR ALL FIELDS OF REGISTRATION FORM
    public void registerClearFields(){
        signup_email.setText("");
        signup_username.setText("");
        signup_password.setText("");
        signup_cPassword.setText("");
        signup_selectQuestion.getSelectionModel().clearSelection();
        signup_answer.setText("");
    }

    public void changePassword(){
        AlertMessage alert = new AlertMessage();

        if (changePass_password.getText().isEmpty() || changePass_cPassword.getText().isEmpty()){
            alert.errorMessage("Please fill all blank fields");
        } else if (!changePass_password.getText().equals(changePass_cPassword.getText())){
            //CHECK IF THE PASSWORD AND CONFIRM PASSWORD ARE NOT MATCH
            alert.errorMessage("Password does not match");
        } else if (changePass_password.getText().length() < 8){
            //CHECK THE LENGTH OF THE PASSWORD
            alert.errorMessage("Invalid password, at least 8 characters needed");
        } else{
            //USERNAME IS OUR REFERENCE TO UPDATE THE DATA OF THE USER
            String updateData = "UPDATE users SET password = ?, update_date = ? WHERE username = '"+ forgot_username.getText() +"'";
            connect = connectDB();

            try {
                prepare = connect.prepareStatement(updateData);
                prepare.setString(1, changePass_password.getText());

                java.util.Date date = new java.util.Date();
                java.sql.Date sqlDate = new java.sql.Date(date.getTime());

                prepare.setString(2, String.valueOf(sqlDate));
                prepare.executeUpdate();

                alert.successMessage("Successfully changed Password");

                //LOGIN FORM WILL APPEAR
                signup_form.setVisible(false);
                login_form.setVisible(true);
                forgot_form.setVisible(false);
                changePass_form.setVisible(false);

                login_username.setText("");
                login_password.setVisible(true);
                login_password.setText("");
                login_showPassword.setVisible(false);

                login_selectShowPassword.setSelected(false);

                changePass_password.setText("");
                changePass_cPassword.setText("");

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void switchForm(ActionEvent event){
        if (event.getSource() == signup_loginAccount || event.getSource() == forgot_backBtn){
            signup_form.setVisible(false);
            login_form.setVisible(true);
            forgot_form.setVisible(false);
            changePass_form.setVisible(false);
        } else if (event.getSource() == login_createAccount) {
            signup_form.setVisible(true);
            login_form.setVisible(false);
            forgot_form.setVisible(false);
            changePass_form.setVisible(false);
        } else if (event.getSource() == login_forgotPassword) {
            signup_form.setVisible(false);
            login_form.setVisible(false);
            forgot_form.setVisible(true);
            changePass_form.setVisible(false);

            //TO SHOW THE DATA IN THE COMBOBOX
            forgotListQuestion();
        } else if (event.getSource() == changePass_backBtn) {
            signup_form.setVisible(false);
            login_form.setVisible(false);
            forgot_form.setVisible(true);
            changePass_form.setVisible(false);
        }
    }

    private String[] questionList = {"What is your favorite food?", "What is your favorite color?",
                                    "What is the name of your pet?", "What is your most favorite sport?"};
    public void questions(){
        List<String> listQ = new ArrayList<>();

        for (String data: questionList){
            listQ.add(data);
        }

        ObservableList listData = FXCollections.observableArrayList(listQ);
        signup_selectQuestion.setItems(listData);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        questions();
        forgotListQuestion();
    }
}