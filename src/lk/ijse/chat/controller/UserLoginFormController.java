package lk.ijse.chat.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class UserLoginFormController {
    public TextField txtName;
    static String userName;
    public Label EnterNametxt;

    public void JoinOnAction(ActionEvent actionEvent) throws IOException {
        userName=txtName.getText();
        Stage stage = new Stage();
        stage.setScene(new Scene(FXMLLoader.
                load(UserLoginFormController.class.getResource("../view/ClientForm.fxml"))));
        stage.close();
        stage.centerOnScreen();
        stage.show();
        txtName.clear();
    }

}
