package lk.ijse.chat.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import javafx.stage.Stage;


import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ClientFormController extends Thread{
    public AnchorPane emojiBox;
    public ImageView imgEmoji;
    BufferedReader reader;
    PrintWriter writer;
    Socket socket;
    String []ePath=new String[6];
    public static String userName;
    boolean isUsed=false;
    private FileChooser fileChooser;
    private File filePath;
    public AnchorPane clientFormContext;
    public Label lblClientName;
    public VBox vBox;
    public TextField txtMsg;

    public void initialize() throws IOException {
        String userName=UserLoginFormController.userName;
        lblClientName.setText(userName);
        try {
            socket = new Socket("localhost", 6000);
            System.out.println("Socket is connected with server!");
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(socket.getOutputStream(), true);

            this.start();

            {
                for (int i = 0; i < ePath.length; i++) {
                    ePath[i] = "lk/ijse/chat/assests/emojis/" + (i + 1) + ".png";
                    System.out.println(ePath[i]);

                }
                System.out.println("Emojis path set to array");
            }
            new Thread(() -> {
                String msg;
                try {
                    while (true) {
                        msg = reader.readLine();
                        String[] tokens = msg.split(" ");
                        String cmd = tokens[0];
                        StringBuilder fulMsg = new StringBuilder();
                        for (int i = 1; i < tokens.length; i++) {
                            fulMsg.append(tokens[i]);
                        }

                        if (fulMsg.toString().startsWith("assests/emojis/")) {
                            HBox hBox = new HBox();
                            System.out.println("Emoji path " + fulMsg);
                            hBox.setAlignment(Pos.CENTER_LEFT);
                            hBox.setPadding(new Insets(5, 10, 5, 5));
                            Text text = new Text(cmd + " ");
                            ImageView imageView = new ImageView();
                            Image image = new Image(String.valueOf(fulMsg));
                            imageView.setImage(image);
                            imageView.setFitWidth(50);
                            imageView.setFitHeight(50);
                            TextFlow textFlow = new TextFlow(text, imageView);
                            VBox vBox = new VBox(textFlow);
                            vBox.setAlignment(Pos.CENTER_LEFT);
                            vBox.setPadding(new Insets(5, 10, 5, 5));
                            hBox.getChildren().add(vBox);

                        }
                    }
                }catch (Exception e){
                    System.out.println("Wrong");
                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            while (true) {


                String msg = reader.readLine();
                String[] tokens = msg.split(" ");
                String cmd = tokens[0];


                StringBuilder fullMsg = new StringBuilder();
                for (int i = 1; i < tokens.length; i++) {
                    fullMsg.append(tokens[i]+" ");
                }


                String[] msgToAr = msg.split(" ");
                String st = "";
                for (int i = 0; i < msgToAr.length - 1; i++) {
                    st += msgToAr[i + 1] + " ";
                }


                Text text = new Text(st);
                String firstChars = "";
                if (st.length() > 3) {
                    firstChars = st.substring(0, 3);

                }


                if (firstChars.equalsIgnoreCase("img")) {
                    //for the Images

                    st = st.substring(3, st.length() - 1);


                    File file = new File(st);
                    Image image = new Image(file.toURI().toString());

                    ImageView imageView = new ImageView(image);

                    imageView.setFitHeight(150);
                    imageView.setFitWidth(200);


                    HBox hBox = new HBox(10);
                    hBox.setAlignment(Pos.BOTTOM_RIGHT);


                    if (!cmd.equalsIgnoreCase(lblClientName.getText())) {

                        vBox.setAlignment(Pos.TOP_LEFT);
                        hBox.setAlignment(Pos.CENTER_LEFT);


                        Text text1 = new Text("  " + cmd + " :");
                        hBox.getChildren().add(text1);
                        hBox.getChildren().add(imageView);

                    } else {
                        hBox.setAlignment(Pos.BOTTOM_RIGHT);
                        hBox.getChildren().add(imageView);
                        Text text1 = new Text(": Me ");
                        hBox.getChildren().add(text1);

                    }

                    Platform.runLater(() -> vBox.getChildren().addAll(hBox));


                } else {

                    TextFlow tempFlow = new TextFlow();

                    if (!cmd.equalsIgnoreCase(lblClientName.getText() + ":")) {
                        Text txtName = new Text(cmd + " ");
                        txtName.getStyleClass().add("txtName");
                        tempFlow.getChildren().add(txtName);
                    }

                    tempFlow.getChildren().add(text);
                    tempFlow.setMaxWidth(200); //200

                    TextFlow flow = new TextFlow(tempFlow);

                    HBox hBox = new HBox(12); //12

                    if (!cmd.equalsIgnoreCase(lblClientName.getText() + ":")) {


                        vBox.setAlignment(Pos.TOP_LEFT);
                        hBox.setAlignment(Pos.CENTER_LEFT);
                        hBox.getChildren().add(flow);

                    } else {

                        Text text2 = new Text(fullMsg + ": Me");
                        TextFlow flow2 = new TextFlow(text2);
                        hBox.setAlignment(Pos.BOTTOM_RIGHT);
                        hBox.getChildren().add(flow2);
                    }

                    Platform.runLater(() -> vBox.getChildren().addAll(hBox));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void msgSendOnAction(ActionEvent actionEvent) {
        String msg = txtMsg.getText();
        writer.println(lblClientName.getText()+": "+msg);
        txtMsg.clear();
        if (msg.equalsIgnoreCase("BYE")|| (msg.equalsIgnoreCase("logout"))){
            System.exit(0);
        }
    }

    public void ImageMouseClicked(MouseEvent mouseEvent) {
        Stage stage = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();
        fileChooser = new FileChooser();
        fileChooser.setTitle("Open Image");
        this.filePath = fileChooser.showOpenDialog(stage);
        writer.println(lblClientName.getText() + " " + "img" + filePath.getPath());
    }

    public void chooseEmojiOnAction(MouseEvent mouseEvent) {

        if (isUsed) {
            emojiBox.getChildren().clear();
            isUsed = false;
            return;
        }
        isUsed = true;
        VBox dialogVbox = new VBox(20);
        ImageView smile = new ImageView(new Image("lk/ijse/chat/assests/smile.png"));
        smile.setFitWidth(30);
        smile.setFitHeight(30);
        dialogVbox.getChildren().add(smile);
        ImageView heart = new ImageView(new Image("lk/ijse/chat/assests/heart.png"));
        heart.setFitWidth(30);
        heart.setFitHeight(30);
        dialogVbox.getChildren().add(heart);
        ImageView sadFace = new ImageView(new Image("lk/ijse/chat/assests/sad-face.png"));
        sadFace.setFitWidth(30);
        sadFace.setFitHeight(30);
        dialogVbox.getChildren().add(sadFace);
        smile.setOnMouseClicked(event1 -> {
            txtMsg.setText(txtMsg.getText() + "\uD83D\uDE0A");
        });
        heart.setOnMouseClicked(event1 -> {
            txtMsg.setText(txtMsg.getText() + "❤");
        });
        sadFace.setOnMouseClicked(event1 -> {
            txtMsg.setText(txtMsg.getText() + " \uD83D\uDE14 ");
        });
        emojiBox.getChildren().add(dialogVbox);
    }


}
