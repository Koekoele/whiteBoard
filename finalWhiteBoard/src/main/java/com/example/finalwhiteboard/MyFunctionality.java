package com.example.finalwhiteboard;

import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Stack;

public class MyFunctionality {

    protected Stack<Image> undoStack = new Stack<>();
    protected Stack<Image> redoStack = new Stack<>();
    private boolean isTextInputActive = false;
    private  File currentFile = null;

    //set up my drawing
    public void setUpDrawing(Canvas canvas, GraphicsContext gc) {
        final double[] prevX = {-1};
        final double[] prevY = {-1};

        // Mouse pressed event to start the drawing action
        canvas.setOnMousePressed(e -> {
            prevX[0] = e.getX();
            prevY[0] = e.getY();

            handleDrawingAction(canvas);

        });

        // Mouse dragged event to draw on the canvas
        canvas.setOnMouseDragged(e -> {
            if (prevX[0] != -1 && prevY[0] != -1) {
                double currentX = e.getX();
                double currentY = e.getY();

                // Draw the line
                gc.setLineWidth(2); // Set line width (can be customized)
                gc.strokeLine(prevX[0], prevY[0], currentX, currentY); // Draw the line

                // Update previous X, Y to the current ones for the next stroke
                prevX[0] = currentX;
                prevY[0] = currentY;
            }
        });

        // Mouse released event to reset previous positions
        canvas.setOnMouseReleased(e -> {
            prevX[0] = -1;
            prevY[0] = -1;
        });
    }


    //saving my session


    public void saveState(Canvas canvas, Group canvasGroup) {
        // Check if the currentFile already exists
        if (currentFile != null && currentFile.exists()) {
            // File exists, so save over it
            saveToFile(canvas, canvasGroup, currentFile);
        } else {
            // File does not exist, prompt for a file location (Save As)
            saveSession(canvas, canvasGroup);
        }
    }

    private void saveToFile(Canvas canvas, Group canvasGroup, File file) {
        // Take a snapshot of the entire canvas, including any images in the Group
        WritableImage currentImage = new WritableImage((int) canvas.getWidth(), (int) canvas.getHeight());
        canvas.snapshot(null, currentImage); // Capture the entire canvas including drawings and images

        try {
            // Convert the snapshot to a BufferedImage
            BufferedImage bufferedImage = convertWritableImageToBufferedImage(currentImage);

            // Get the file extension (for saving format)
            String fileExtension = getFileExtension(file.getName());

            // Save the image based on the file extension
            if (fileExtension.equals("png")) {
                ImageIO.write(bufferedImage, "PNG", file);
            } else if (fileExtension.equals("jpg") || fileExtension.equals("jpeg")) {
                ImageIO.write(bufferedImage, "JPG", file);
            } else if (fileExtension.equals("bmp")) {
                ImageIO.write(bufferedImage, "BMP", file);
            } else if (fileExtension.equals("gif")) {
                ImageIO.write(bufferedImage, "GIF", file);
            } else {
                ImageIO.write(bufferedImage, "PNG", file);  // Default to PNG if no extension is specified
            }

        } catch (IOException e) {
            e.printStackTrace();  // Handle any IO exceptions
        }
    }

    public void saveSession(Canvas canvas, Group canvasGroup) {
        // Open a file chooser to prompt the user to select a location and file type
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG files", "*.png"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JPEG files", "*.jpg", "*.jpeg"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("BMP files", "*.bmp"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("GIF files", "*.gif"));

        // Prompt the user to choose a file location
        currentFile = fileChooser.showSaveDialog(null);  // Store the selected file

        if (currentFile != null) {
            // If a file was selected, save the canvas to that file
            saveToFile(canvas, canvasGroup, currentFile);
        }
    }

    public String getFileExtension(String filename) {
        int dotIndex = filename.lastIndexOf(".");
        if (dotIndex > 0) {
            return filename.substring(dotIndex + 1).toLowerCase();
        }
        return "";
    }

    private BufferedImage convertWritableImageToBufferedImage(WritableImage writableImage) {
        int width = (int) writableImage.getWidth();
        int height = (int) writableImage.getHeight();
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        // Copy pixel data from WritableImage to BufferedImage
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                bufferedImage.setRGB(x, y, writableImage.getPixelReader().getArgb(x, y));
            }
        }

        return bufferedImage;
    }

    public void loadVideo(Stage stage, Group canvasGroup) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("MP4 Files", "*.mp4"));
        File file = fileChooser.showOpenDialog(stage);

        if (file != null) {

            Media media = new Media(file.toURI().toString());
            MediaPlayer mediaPlayer = new MediaPlayer(media);
            MediaView mediaView = new MediaView(mediaPlayer);
            mediaView.setFitHeight(300);
            mediaView.setFitWidth(400);

            Button btnBackward = new Button("⏪");
            btnBackward.setCursor(Cursor.HAND);

            Button btnForward = new Button("⏩");
            btnForward.setCursor(Cursor.HAND);


            Button btnPlayPause = new Button("⫸");
            btnPlayPause.setOnAction(e -> {
                if (mediaPlayer != null) {
                    if (mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
                        mediaPlayer.pause();
                        btnPlayPause.setText("⫸");
                    } else {
                        mediaPlayer.play();
                        btnPlayPause.setText("⏸");
                    }
                }
            });


            btnBackward.setOnAction(e -> {
                mediaPlayer.seek(mediaPlayer.getCurrentTime().subtract(javafx.util.Duration.seconds(10)));
            });

            btnForward.setOnAction(e -> {
                mediaPlayer.seek(mediaPlayer.getCurrentTime().add(javafx.util.Duration.seconds(10)));
            });

            // Close button
            Button btnClose = new Button("✖");
            btnClose.setStyle("-fx-background-color: red; -fx-text-fill: white;");
            btnClose.setCursor(Cursor.HAND);


            VBox videoContainer = new VBox();
            videoContainer.setPrefSize(200, 200);
            videoContainer.setStyle("-fx-background-color: rgba(0, 0, 0, 0.5); -fx-padding: 10;");
            videoContainer.setAlignment(Pos.CENTER);
            videoContainer.setSpacing(10);

            btnClose.setOnAction(e -> {
                mediaPlayer.stop();
                canvasGroup.getChildren().remove(videoContainer);
            });

            // Add the video view to the container
            videoContainer.getChildren().add(mediaView);


            HBox controls = new HBox(20, btnBackward, btnPlayPause, btnForward);
            controls.setAlignment(Pos.CENTER);
            controls.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7); -fx-padding: 10;");
            videoContainer.getChildren().add(controls);


            StackPane.setAlignment(btnClose, Pos.TOP_RIGHT);
            videoContainer.getChildren().add(btnClose);


            final double[] offsetX = {0};
            final double[] offsetY = {0};

            videoContainer.setOnMousePressed(event -> {
                offsetX[0] = event.getSceneX();
                offsetY[0] = event.getSceneY();
            });

            videoContainer.setOnMouseDragged(event -> {
                double deltaX = event.getSceneX() - offsetX[0];
                double deltaY = event.getSceneY() - offsetY[0];
                videoContainer.setLayoutX(videoContainer.getLayoutX() + deltaX);
                videoContainer.setLayoutY(videoContainer.getLayoutY() + deltaY);
            });


            canvasGroup.getChildren().add(videoContainer);


            mediaPlayer.play();
        }
    }

    // load my music
    public void loadMusic(Stage stage, Group canvasGroup) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Audio Files", "*.mp3", "*.wav"));
        File file = fileChooser.showOpenDialog(stage);

        if (file != null) {

            Media media = new Media(file.toURI().toString());
            MediaPlayer mediaPlayer = new MediaPlayer(media);


            Button btnPlayPause = new Button("⫸");
            btnPlayPause.setOnAction(e -> {
                if (mediaPlayer != null) {
                    if (mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
                        mediaPlayer.pause();
                        btnPlayPause.setText("⫸");  // Change to Play symbol
                    } else {
                        mediaPlayer.play();
                        btnPlayPause.setText("⏸");  // Change to Pause symbol
                    }
                }
            });


            Slider volumeSlider = new Slider(0, 1, 0.5);
            volumeSlider.setBlockIncrement(0.1);
            volumeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
                mediaPlayer.setVolume(newValue.doubleValue());
            });

            // Track Progress Slider
            Slider progressSlider = new Slider();
            progressSlider.setMax(100);
            progressSlider.setValue(0);
            mediaPlayer.currentTimeProperty().addListener((observable, oldValue, newValue) -> {
                double progress = newValue.toMillis() / media.getDuration().toMillis() * 100;
                progressSlider.setValue(progress);
            });

            progressSlider.setOnMousePressed(e -> {
                mediaPlayer.seek(javafx.util.Duration.millis(progressSlider.getValue() / 100 * media.getDuration().toMillis()));
            });

            // Backward Button
            Button btnBackward = new Button("⏪");
            btnBackward.setOnAction(e -> mediaPlayer.seek(mediaPlayer.getCurrentTime().subtract(javafx.util.Duration.seconds(10))));

            // Forward Button
            Button btnForward = new Button("⏩");
            btnForward.setOnAction(e -> mediaPlayer.seek(mediaPlayer.getCurrentTime().add(javafx.util.Duration.seconds(10))));

            // Close Button
            Button btnClose = new Button("✖");
            btnClose.setStyle("-fx-background-color: red; -fx-text-fill: white;");



            HBox controls = new HBox(20, btnBackward, btnPlayPause, btnForward);
            controls.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7); -fx-padding: 10;");
            controls.setAlignment(Pos.CENTER);

            VBox musicContainer = new VBox(10, controls, volumeSlider, progressSlider, btnClose);
            musicContainer.setStyle("-fx-background-color: rgba(0, 0, 0, 0.5); -fx-padding: 10;");
            musicContainer.setAlignment(Pos.CENTER);

            btnClose.setOnAction(e -> {
                mediaPlayer.stop();
                canvasGroup.getChildren().remove(musicContainer);
            });


            final double[] offsetX = {0};
            final double[] offsetY = {0};

            musicContainer.setOnMousePressed(event -> {
                offsetX[0] = event.getSceneX() - musicContainer.getLayoutX();
                offsetY[0] = event.getSceneY() - musicContainer.getLayoutY();
            });

            musicContainer.setOnMouseDragged(event -> {

                double newX = event.getSceneX() - offsetX[0];
                double newY = event.getSceneY() - offsetY[0];

                // Update the position of the music container
                musicContainer.setLayoutX(newX);
                musicContainer.setLayoutY(newY);
            });

            // Add the music controls container to the canvas group
            canvasGroup.getChildren().add(musicContainer);

            // Play the audio
            mediaPlayer.play();
        }
    }

    //load a photo
    public void loadPhoto(Stage stage, Group canvasGroup) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.bmp"));
        File file = fileChooser.showOpenDialog(stage);

        if (file != null) {
            Image image = new Image(file.toURI().toString());
            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(200);
            imageView.setFitHeight(200);

            imageView.setX(100);
            imageView.setY(100);

            canvasGroup.getChildren().add(imageView);

            final double[] offsetX = {0};
            final double[] offsetY = {0};

            imageView.setOnMousePressed(event -> {

                offsetX[0] = event.getSceneX() - imageView.getX();
                offsetY[0] = event.getSceneY() - imageView.getY();
            });

            imageView.setOnMouseDragged(event -> {

                imageView.setX(event.getSceneX() - offsetX[0]);
                imageView.setY(event.getSceneY() - offsetY[0]);
            });

        }
    }

    //text on canvas
    public void enableTextInput(Canvas canvas, Group canvasGroup, GraphicsContext gc) {
        final double[] textX = {0};
        final double[] textY = {0};
        final TextField[] textInput = {null};

        // Mouse click event to start or end text input
        canvas.setOnMouseClicked(e -> {


            textX[0] = e.getX();
            textY[0] = e.getY();

            // If there's an active TextField and the user clicks outside it, remove the TextField
            if (textInput[0] != null) {
                if (!textInput[0].contains(e.getX(), e.getY())) {
                    // Commit text when clicked outside of the TextField
                    gc.setFill(gc.getStroke());
                    gc.fillText(textInput[0].getText(), textX[0], textY[0]);

                    // Remove the TextField and deactivate text input
                    canvasGroup.getChildren().remove(textInput[0]);
                    textInput[0] = null;
                    isTextInputActive = false; // Mark text input as inactive after removal
                }
            }

            else {


            }
        });


        if (textInput[0] == null) {
            textInput[0] = new TextField();
            textInput[0].setFont(new Font(18));
            textInput[0].setStyle("-fx-background-color: white; -fx-border-color: black;");
            textInput[0].setLayoutX(textX[0]);
            textInput[0].setLayoutY(textY[0]);


            textInput[0].setOnKeyPressed(keyEvent -> {
                if (keyEvent.getCode() == KeyCode.ENTER) {

                    gc.setFill(gc.getStroke());
                    gc.fillText(textInput[0].getText(), textX[0], textY[0]);


                    canvasGroup.getChildren().remove(textInput[0]);
                    textInput[0] = null;
                    isTextInputActive = false; // Mark text input as inactive after removal
                }
            });


            canvasGroup.getChildren().add(textInput[0]);
            textInput[0].requestFocus();
        }
    }
    public void setUpEraser(Canvas canvas, GraphicsContext gc, Button erase) {
        final double[] prevX = {-1};
        final double[] prevY = {-1};

        // Define the eraser's size, change this value to make the eraser bigger or smaller
        double eraserSize = 30;

        erase.setOnAction(e -> {
            // When the eraser button is pressed, the user can erase on the canvas
            // This can toggle between drawing and erasing
            canvas.setOnMousePressed(mouseEvent -> {
                prevX[0] = mouseEvent.getX();
                prevY[0] = mouseEvent.getY();

                // Set the fill color to white (to simulate erasing)
                gc.setFill(Color.WHITE);

                // Draw a white rectangle over the area (simulating eraser action)
                gc.fillRect(prevX[0] - eraserSize / 2, prevY[0] - eraserSize / 2, eraserSize, eraserSize);
                handleDrawingAction(canvas); // Save the current state after erasing
            });

            canvas.setOnMouseDragged(mouseEvent -> {
                if (prevX[0] != -1 && prevY[0] != -1) {
                    double currentX = mouseEvent.getX();
                    double currentY = mouseEvent.getY();

                    // Erase the area by drawing a white rectangle over it
                    gc.fillRect(currentX - eraserSize / 2, currentY - eraserSize / 2, eraserSize, eraserSize);
                    prevX[0] = currentX;
                    prevY[0] = currentY;
                }
            });

            canvas.setOnMouseReleased(mouseEvent -> {
                prevX[0] = -1;
                prevY[0] = -1;
            });
        });
    }


    //keep track of the behaviour or events on canvas, triggered my undo and redo
    public void handleDrawingAction(Canvas canvas) {
        WritableImage currentImage = new WritableImage((int) canvas.getWidth(), (int) canvas.getHeight());
        canvas.snapshot(null, currentImage);
        undoStack.push(currentImage);
        redoStack.clear();
    }

    public void undo(GraphicsContext gc, Canvas canvas) {
        if (!undoStack.isEmpty()) {
            Image lastState = undoStack.pop();
            redoStack.push(new WritableImage(lastState.getPixelReader(), 0, 0, (int) canvas.getWidth(), (int) canvas.getHeight()));
            gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
            gc.drawImage(lastState, 0, 0);
        }
    }

    public void redo(GraphicsContext gc, Canvas canvas) {
        if (!redoStack.isEmpty()) {
            Image lastState = redoStack.pop();
            undoStack.push(new WritableImage(lastState.getPixelReader(), 0, 0, (int) canvas.getWidth(), (int) canvas.getHeight()));
            gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
            gc.drawImage(lastState, 0, 0);
        }
    }

    public void clearCanvas(Canvas canvas, GraphicsContext graphicsContext) {
        // Create the confirmation alert
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Clear Canvas");
        alert.setHeaderText("Are you sure you want to clear the whole canvas?");
        alert.setContentText("This action cannot be undone.");

        // Set the buttons to "OK" and "Cancel"
        alert.getButtonTypes().setAll(ButtonType.OK, ButtonType.CANCEL);

        // Show the alert and wait for the user's response
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // Clear the canvas if the user confirms
                graphicsContext.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
                graphicsContext.setFill(Color.WHITE);
                graphicsContext.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
            } else {


            }
        });
    }

    public void createNewCanvas(Canvas canvas, GraphicsContext gc) {
        // Ask for the canvas size using a TextInputDialog
        TextInputDialog widthDialog = new TextInputDialog();
        widthDialog.setTitle("Canvas Width");
        widthDialog.setHeaderText("Enter Canvas Width:");
        widthDialog.setContentText("Width:");

        // Get the width input
        widthDialog.showAndWait().ifPresent(widthInput -> {
            try {
                double width = Double.parseDouble(widthInput);

                // Ask for the height input
                TextInputDialog heightDialog = new TextInputDialog();
                heightDialog.setTitle("Canvas Height");
                heightDialog.setHeaderText("Enter Canvas Height:");
                heightDialog.setContentText("Height:");

                heightDialog.showAndWait().ifPresent(heightInput -> {
                    try {
                        double height = Double.parseDouble(heightInput);

                        // Set the new size for the canvas
                        canvas.setWidth(width);
                        canvas.setHeight(height);

                        // Clear the canvas and fill it with white color
                        gc.clearRect(0, 0, width, height); // Clear the previous content
                        gc.setFill(Color.WHITE); // Set background color
                        gc.fillRect(0, 0, width, height); // Fill with white color

                    } catch (NumberFormatException e) {
                        showErrorDialog("Invalid height input. Please enter a valid number.");
                    }
                });

            } catch (NumberFormatException e) {
                showErrorDialog("Invalid width input. Please enter a valid number.");
            }
        });
    }

    private void showErrorDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void fillCanvasColor(Canvas canvas, ColorPicker colorPicker) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        Color selectedColor = colorPicker.getValue();
        gc.setFill(selectedColor);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }
}



