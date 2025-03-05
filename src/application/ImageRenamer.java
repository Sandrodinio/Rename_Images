package application;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;

public class ImageRenamer extends Application {
    private static final MyLogger LOGGER = new MyLogger("errors.log");
    private TextField formatField;
    private FlowPane previewPane;
    private File selectedFolder;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        formatField = new TextField("ImageFormat");
        formatField.setPromptText("Enter filename format");

        Button openButton = new Button("Open Folder");
        Button renameButton = new Button("Rename");
        openButton.setOnAction(e -> openFolder(primaryStage));
        renameButton.setOnAction(e -> {
            if (selectedFolder != null) {
                try {
                    renameImages(selectedFolder);
                    displayConfirmation();
                    displayPreview(selectedFolder);
                } catch (ImageRenamingException ex) {
                    showErrorDialog(ex.getMessage());
                    LOGGER.log("Error renaming images", ex);
                }
            }
        });

        previewPane = new FlowPane();
        previewPane.setHgap(10);
        previewPane.setVgap(10);

        VBox topContainer = new VBox(formatField, openButton, renameButton);

        BorderPane root = new BorderPane();
        root.setTop(topContainer);
        root.setCenter(previewPane);

        Scene scene = new Scene(root, 600, 400);
        primaryStage.setTitle("Image Renamer");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void openFolder(Stage stage) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Open Image Folder");
        selectedFolder = directoryChooser.showDialog(stage);
        if (selectedFolder != null) {
            displayPreview(selectedFolder);
        }
    }

    private void renameImages(File folder) throws ImageRenamingException {
        File[] files = folder.listFiles((dir, name) -> name.matches(".*\\.(png|jpg|jpeg|gif|bmp)"));
        if (files != null) {
            Arrays.sort(files);
            String baseName = formatField.getText();
            for (int i = 0; i < files.length; i++) {
                String fileName = files[i].getName();
                int lastDotIndex = fileName.lastIndexOf('.');
                String extension = (lastDotIndex != -1) ? fileName.substring(lastDotIndex) : "";

                String newName = String.format("%03d_%s%s", i + 1, baseName, extension);
                File newFile = new File(folder, newName);
                if (!files[i].equals(newFile)) {
                    try {
                        Files.move(files[i].toPath(), newFile.toPath());
                    } catch (IOException e) {
                        LOGGER.log("Error renaming images", e);
                        throw new ImageRenamingException("Error renaming file: " + files[i].getName(), e);
                    }
                }
            }
        }
    }

    private void displayPreview(File folder) {
        previewPane.getChildren().clear();
        File[] files = folder.listFiles((dir, name) -> name.matches(".*\\.(png|jpg|jpeg|gif|bmp)"));
        if (files != null) {
            for (File file : files) {
                VBox imageContainer = new VBox();
                Image image = new Image(file.toURI().toString(), 100, 100, true, true);
                ImageView imageView = new ImageView(image);
                Label fileNameLabel = new Label(file.getName());
                imageContainer.getChildren().addAll(imageView, fileNameLabel);
                imageContainer.setPadding(new Insets(5));
                previewPane.getChildren().add(imageContainer);
            }
        }
    }

    private void displayConfirmation() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Renaming Complete");
        alert.setHeaderText(null);
        alert.setContentText("The images have been successfully renamed.");
        alert.showAndWait();
    }

    private void showErrorDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}