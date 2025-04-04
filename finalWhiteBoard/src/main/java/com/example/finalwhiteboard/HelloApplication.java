package com.example.finalwhiteboard;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {


    @Override
    public void start(Stage stage) throws IOException {

        Scene scene = new Scene(createUI(stage), 990, 580);
        scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }

    //method to create my interface
    private Parent createUI(Stage stage){
        //creating class for controller

        //create a scene layout
        BorderPane borderPane = new BorderPane();
        borderPane.setStyle("-fx-background-color: #D3D3D3; "  + "-fx-border-width: 0px;");

        //Creating a canvas
        Canvas canvas = new Canvas(900, 500);
        canvas.setStyle("-fx-background-color: white; " + "-fx-border-color: white; " + "-fx-border-width: 1px;");

        //set canvas properties
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        gc.setLineWidth(2);
        gc.setFont(new Font(20));

        //grouping our canvas contents
        Group canvasGroup = new Group(canvas);

        //creating buttons
        MenuButton file = new MenuButton("File");
        MenuItem saveAs = new MenuItem("Save as");
        MenuItem save = new MenuItem("save");
        file.getItems().addAll(saveAs, save);
        file.getStyleClass().add("fileBtn");

        Button canvasFill = new Button("Fill Can");
        canvasFill.getStyleClass().add("fillBtn");

        Button clearButton = new Button("Clear");
        clearButton.getStyleClass().add("clearBtn");

        Button saveButton = new Button("Save");
        saveButton.getStyleClass().add("saveBtn");

        Button redo = new Button("redo");
        redo.getStyleClass().add("redo");

        Button undo = new Button("undo");
        undo.getStyleClass().add("undoBtn");

        Button textButton = new Button("✏🖊A");
        Tooltip tooltip = new Tooltip("Text");
        Tooltip.install(textButton, tooltip);
        textButton.getStyleClass().add("textBtn");

        Button draw = new Button("Draw");
        draw.getStyleClass().add("drawBtn");

        Button erase = new Button("Erase");
        erase.getStyleClass().add("eraseBtn");

        ColorPicker colorPicker = new ColorPicker(Color.BLACK);
        colorPicker.setOnAction(e -> {
            gc.setStroke(colorPicker.getValue());
        });


        //adding a vertical toolbar for  buttons
        VBox verticalToolbar = new VBox(10);
        verticalToolbar.setStyle("-fx-background-color: #F4F4F4; -fx-padding: 10;");
        verticalToolbar.getChildren().addAll(textButton, redo, undo, draw, erase, canvasFill);

        //creating set, horizontal buttons
        MenuButton addMenuButton = new MenuButton("Add..");
        MenuItem videoItem = new MenuItem("Video");
        MenuItem musicItem = new MenuItem("Music");
        MenuItem photoItem = new MenuItem("Photo");
        MenuItem addCanvas = new MenuItem("New Canvas");
        addMenuButton.getStyleClass().add("addMenuButton");

        addMenuButton.getItems().addAll(videoItem, musicItem, photoItem, addCanvas);

        ToolBar bar = new ToolBar(file, addMenuButton, saveButton, clearButton, colorPicker);
        bar.getStyleClass().add("tool-bar");

        //using my logic class
        MyFunctionality myFunctionality = new MyFunctionality();

        draw.setOnAction(event -> myFunctionality.setUpDrawing(canvas, gc));
        saveAs.setOnAction(event -> myFunctionality.saveSession(canvas,canvasGroup));
        saveButton.setOnAction(event -> myFunctionality.saveState(canvas,canvasGroup));
        erase.setOnAction(event -> myFunctionality.setUpEraser(canvas,gc,erase) );
        redo.setOnAction(event -> myFunctionality.redo(gc,canvas));
        undo.setOnAction(event -> myFunctionality.undo(gc,canvas));
        textButton.setOnAction(event -> myFunctionality.enableTextInput(canvas,canvasGroup,gc));
        clearButton.setOnAction(event -> myFunctionality.clearCanvas(canvas,gc));
        videoItem.setOnAction(event -> myFunctionality.loadVideo(stage,canvasGroup));
        musicItem.setOnAction(event -> myFunctionality.loadMusic(stage,canvasGroup));
        photoItem.setOnAction(event -> myFunctionality.loadPhoto(stage, canvasGroup));
        addCanvas.setOnAction(event -> myFunctionality.createNewCanvas(canvas,gc));
        canvasFill.setOnAction(event -> myFunctionality.fillCanvasColor(canvas, colorPicker));



        //Set my
        borderPane.setTop(bar);
        borderPane.setLeft(verticalToolbar);
        borderPane.setCenter(canvasGroup);

        return borderPane;

    }

    public static void main(String[] args) {
        launch();
    }
}