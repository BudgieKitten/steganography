package ca.budgiekitten.steganography;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

import javafx.scene.image.ImageView;
import javafx.scene.image.Image;

import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;

import javafx.geometry.Insets;
import javafx.geometry.Pos;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import java.util.Scanner;

public class GUI extends Application {
    private File imageFile;
    private Image image;
    private ImageView imageView;
    private BufferedReader msg;
    private File imgOutputLocation;
    private File txtOutputLocation;

    // User's convenience only and should not be used for boolean checking
    private String imgPreviousChosenDir = ""; // Remember the initial or previous directory that user chose img
    private String txtPreviousChosenDir = ""; // Remember the initial or previous directory that user chose txt

    public void start(Stage stage) {
        // Main
        BorderPane pane = new BorderPane();

        /*
        Top pane
        Choose embed or extract data
         */
        HBox paneTop = new HBox(2);
        paneTop.setAlignment(Pos.CENTER);

        Button btEmbedData = new Button("Embed data");
        btEmbedData.setPadding(new Insets(10, 40, 10, 40));
        btEmbedData.setAlignment(Pos.CENTER);
        Button btExtractData = new Button("Extract data");
        btExtractData.setPadding(new Insets(10, 40, 10, 40));
        btExtractData.setAlignment(Pos.CENTER);

        paneTop.getChildren().addAll(btEmbedData, btExtractData);
        pane.setTop(paneTop);

        /*
        Center pane
         */
        // Default is Embed Data mode
        btEmbedData.setStyle("-fx-background-color: transparent;");
        VBox paneCenterDefault = getEmbedDataGUI(pane, stage);
        pane.setCenter(paneCenterDefault);

        /*
        Changing the GUI to fit whether Embed Data or Extract data mode is chosen
         */
        btEmbedData.setOnAction(e -> {
            // Differentiate which mode is chosen
            btEmbedData.setStyle("-fx-background-color: transparent;");
            btExtractData.setStyle("");

            // Clear all resources stored in instance variables from previous use (if exist)
            imageFile = null;
            image = null;
            imageView = null;
            msg = null;
            imgOutputLocation = null;

            // Create GUI for embedding data
            VBox paneCenter = getEmbedDataGUI(pane, stage);
            pane.getChildren().remove(1);
            pane.setCenter(paneCenter);
        });

        btExtractData.setOnAction(e -> {
            // Differentiate which mode is chosen
            btEmbedData.setStyle("");
            btExtractData.setStyle("-fx-background-color: transparent;");

            // Clear all resources stored in instance variables from previous use (if exist)
            imageFile = null;
            image = null;
            imageView = null;
            msg = null;
            imgOutputLocation = null;

            // Create GUI for extracting data
            VBox paneCenter = getExtractDataGUI(pane, stage);
            pane.getChildren().remove(1);
            pane.setCenter(paneCenter);
        });

        Scene scene = new Scene(pane, 600, 650);
        stage.setScene(scene);
        stage.setTitle("Steganography");
        stage.setResizable(false);
        stage.show();

    }

    private VBox getEmbedDataGUI(BorderPane pane, Stage stage) {
        /*
        Center pane for Embed Data mode
        Choose image and txt file and deal with steganography
         */
        VBox paneCenter = new VBox(10);

        // Browse image
        HBox paneBrowseImage = new HBox(3);
        paneBrowseImage.setAlignment(Pos.CENTER);

        Label lbChooseImage = new Label("Choose an image (.png, .bmp, .jpg)");
        Button btChooseImage = new Button("Browse");
        btChooseImage.setPadding(new Insets(5, 10, 5, 10));

        paneBrowseImage.getChildren().addAll(lbChooseImage, btChooseImage);
        paneCenter.getChildren().add(paneBrowseImage);
        pane.setCenter(paneCenter);

        // Display image
        StackPane paneDisplayImage = new StackPane();

        btChooseImage.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open Resource File");
            if (!this.imgPreviousChosenDir.isEmpty()) {
                // Get previous chosen dir
                fileChooser.setInitialDirectory(new File(imgPreviousChosenDir));
            }
            fileChooser.getExtensionFilters().addAll(
                    new ExtensionFilter("Image Files", "*.png", "*.jpg", "*.bmp", "*.jpeg")
            );

            try {
                this.imageFile = fileChooser.showOpenDialog(stage);

                this.image = new Image(this.imageFile.toURI().toString());
                this.imageView = new ImageView(this.image);

                String fileLocation = this.imageFile.toString();
                this.imgPreviousChosenDir = fileLocation.substring(0, fileLocation.lastIndexOf("\\"));

                /*
                Check if width - height > 500 to adjust for image size
                Appropriate for image with big width
                No support for image with big height since it can exceed the screen
                 */
                boolean checkSizeWidth = this.image.getWidth() - this.image.getHeight() > 500;

                /*
                Check if width - height > 500 to adjust for image size
                Appropriate for image with big width
                 */
                boolean checkSizeHeight = this.image.getHeight() - this.image.getWidth() > 500;

                if (checkSizeWidth) {
                    this.imageView.setFitWidth(450);
                    this.imageView.setFitHeight(250);
                } else if (checkSizeHeight) {
                    this.imageView.setFitWidth(250);
                    this.imageView.setFitHeight(350);
                    this.imageView.setPreserveRatio(true);
                } else {
                    this.imageView.setFitWidth(250);
                    this.imageView.setFitHeight(250);
                }

                if (paneDisplayImage.getChildren().isEmpty()) {
                    paneDisplayImage.getChildren().add(this.imageView);
                    paneCenter.getChildren().add(1, paneDisplayImage);
                } else {
                    // Clear current chosen image in order to add new image
                    paneDisplayImage.getChildren().clear();

                    paneDisplayImage.getChildren().add(this.imageView);
                }

            } catch (NullPointerException ex) {
                // User closed window
                this.imageFile = null;

                this.image = null;
                this.imageView = null;

                // Clear the image since previous image was not chosen
                if (!paneDisplayImage.getChildren().isEmpty()) {
                    paneDisplayImage.getChildren().clear();
                    paneCenter.getChildren().remove(1);
                } else {
                    // Do nothing since no image was selected yet so no need to clear anything
                }
            }

        });

        // Choose message AND display location
        VBox paneChooseDisplayMsg = new VBox(5);

        // Choose message
        HBox paneChooseMsg = new HBox(5);
        Label lbSecretMsg = new Label("Choose message (.txt)");
        Button btChooseMsg = new Button("Browse");
        btChooseMsg.setPadding(new Insets(5, 10, 5, 10));

        paneChooseMsg.getChildren().addAll(lbSecretMsg, btChooseMsg);
        paneChooseDisplayMsg.getChildren().add(paneChooseMsg);
        paneCenter.getChildren().add(paneChooseDisplayMsg);

        // Display location
        TextField tfLocation = new TextField();
        tfLocation.setEditable(false);
        tfLocation.setPromptText("Location");
        tfLocation.setPrefColumnCount(20);
        paneChooseDisplayMsg.getChildren().add(tfLocation);

        btChooseMsg.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open Resource File");
            if (!this.txtPreviousChosenDir.isEmpty()) {
                // Get previous chosen dir
                fileChooser.setInitialDirectory(new File(this.txtPreviousChosenDir));
            }
            fileChooser.getExtensionFilters().addAll(
                    new ExtensionFilter("Text Files", "*.txt")
            );

            try {
                File selectedFile = fileChooser.showOpenDialog(stage);
                this.msg = new BufferedReader(new FileReader(selectedFile));
                tfLocation.setText(selectedFile.toString());

                String fileLocation = selectedFile.toString();
                this.txtPreviousChosenDir = fileLocation.substring(0, fileLocation.lastIndexOf("\\"));
            } catch (NullPointerException ex) {
                // User close window
                // Clear msg and file location since previous file was not chosen
                this.msg = null;
                tfLocation.setText("");
            } catch (IOException ex) {
                // File not found
            }
        });

        /*
        Output location
         */
        VBox paneOutput = new VBox(5);

        // Choose output location
        HBox paneOutputLocation = new HBox(5);
        Label lbOutputLocation = new Label("Output location (.png, .bmp, .jpg)");
        Button btChooseOutput = new Button("Browse");
        btChooseOutput.setPadding(new Insets(5, 10, 5, 10));

        paneOutputLocation.getChildren().addAll(lbOutputLocation, btChooseOutput);
        paneOutput.getChildren().add(paneOutputLocation);
        paneCenter.getChildren().add(paneOutput);

        // Display output location
        TextField tfOutputLocation = new TextField();
        tfOutputLocation.setEditable(false);
        tfOutputLocation.setPromptText("Location");
        tfOutputLocation.setPrefColumnCount(20);
        paneOutput.getChildren().add(tfOutputLocation);

        btChooseOutput.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Output location");

            if (!this.imgPreviousChosenDir.isEmpty()) {
                // Get previous chosen dir
                fileChooser.setInitialDirectory(new File(this.imgPreviousChosenDir));
            }

            String imgFile = "";
            String imgExtension = "";
            String imgName = "";

            try {
                // Use information previous chosen dir and file to set up the secret file
                imgFile = this.imageFile.toString();
                imgExtension = imgFile.substring(imgFile.lastIndexOf("."));
                imgName = imgFile.substring(imgFile.lastIndexOf("\\") + 1);

                fileChooser.setInitialFileName(
                        imgName.substring(0, imgName.lastIndexOf(imgExtension)) + "-output"
                );
            } catch (NullPointerException ex) {
                // Image file is not yet chosen and return null
                // Use default name
                fileChooser.setInitialFileName("output");
            }

            FileChooser.ExtensionFilter[] extensionFilter = {
                    new ExtensionFilter("PNG", "*.png"),
                    new ExtensionFilter("JPG", "*.jpg"),
                    new ExtensionFilter("BMP", "*.bmp")
            };

            if (imgExtension.isEmpty() || imgExtension.equals(".png")) {
                fileChooser.getExtensionFilters().addAll(
                        extensionFilter[0],
                        extensionFilter[1],
                        extensionFilter[2]
                );
            } else if (imgExtension.equals(".jpg") || imgExtension.equals(".jpeg")) {
                fileChooser.getExtensionFilters().addAll(
                        extensionFilter[1],
                        extensionFilter[0],
                        extensionFilter[2]
                );
            } else if (imgExtension.equals(".bmp")) {
                fileChooser.getExtensionFilters().addAll(
                        extensionFilter[2],
                        extensionFilter[0],
                        extensionFilter[1]
                );
            }

            try {
                File selectedFile = fileChooser.showSaveDialog(stage);
                this.imgOutputLocation = new File(selectedFile.toString());
                tfOutputLocation.setText(selectedFile.toString());
            } catch (NullPointerException ex) {
                // User closed window
                this.imgOutputLocation = null;
                tfOutputLocation.setText("");
            }

        });

        // Embed button
        Button btEmbed = new Button("Embed");
        btEmbed.setPadding(new Insets(5, 10, 5, 10));
        // Align button to the right
        HBox paneBtEmbed = new HBox();
        paneBtEmbed.setPadding(new Insets(5, 5, 5, 5));
        Pane paneEmpty = new Pane();
        HBox.setHgrow(paneEmpty, Priority.ALWAYS);

        paneBtEmbed.getChildren().addAll(paneEmpty, btEmbed);
        paneCenter.getChildren().add(paneBtEmbed);

        /*
        Dealing with event.
        Begin embedding. BtEmbed clicked
         */
        btEmbed.setOnAction(e -> {
            if (this.imageFile != null
                    && this.msg != null
                    && this.imgOutputLocation != null) {

                try {
                    Embed.embed(this.imageFile, this.msg, this.imgOutputLocation);

                    // Clear current selected files to avoid overriding
                    this.imageFile = null;
                    this.msg = null;
                    this.imgOutputLocation = null;

                    //Display successful message
                    String successfulMsg = "Successfully embedded the message!" +
                            "\nTo embed new message, re choose all 3 of them.";
                    Text txtEmbedSuccessful = new Text(successfulMsg);
                    txtEmbedSuccessful.setFont(new Font(20));
                    pane.setBottom(txtEmbedSuccessful);
                } catch (IOException ex) {
                    // Nothing will happen
                }

            } else if (this.imageFile == null
                    || this.msg == null
                    || this.imgOutputLocation == null){
                //Display missing components message
                VBox paneMissingComponents = new VBox(5);
                Text txtMissing;

                if (this.imageFile == null) {
                    txtMissing = new Text("Image to embed not chosen");
                    txtMissing.setFont(new Font(20));
                    paneMissingComponents.getChildren().add(txtMissing);
                }

                if (this.msg == null) {
                    txtMissing = new Text("Secret text to embed not chosen");
                    txtMissing.setFont(new Font(20));
                    paneMissingComponents.getChildren().add(txtMissing);
                }

                if (this.imgOutputLocation == null) {
                    txtMissing = new Text("Output location not chosen");
                    txtMissing.setFont(new Font(20));
                    paneMissingComponents.getChildren().add(txtMissing);
                }

                pane.setBottom(paneMissingComponents);

            }

        });

        return paneCenter;

    }

    private VBox getExtractDataGUI(BorderPane pane, Stage stage) {
        /*
        Center pane for Extract Data mode
        Choose image to extract message from it
         */
        VBox paneCenter = new VBox(10);

        // Browse image
        HBox paneBrowseImage = new HBox(3);
        paneBrowseImage.setAlignment(Pos.CENTER);

        Label lbChooseImage = new Label("Choose an image (.png, .bmp, .jpg)");
        Button btChooseImage = new Button("Browse");
        btChooseImage.setPadding(new Insets(5, 10, 5, 10));

        paneBrowseImage.getChildren().addAll(lbChooseImage, btChooseImage);
        paneCenter.getChildren().add(paneBrowseImage);
        pane.setCenter(paneCenter);

        // Display image
        StackPane paneDisplayImage = new StackPane();

        btChooseImage.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open Resource File");
            if (!this.imgPreviousChosenDir.isEmpty()) {
                // Get previous chosen dir
                fileChooser.setInitialDirectory(new File(imgPreviousChosenDir));
            }
            fileChooser.getExtensionFilters().addAll(
                    new ExtensionFilter("Image Files", "*.png", "*.jpg", "*.bmp", "*.jpeg")
            );

            try {
                this.imageFile = fileChooser.showOpenDialog(stage);

                this.image = new Image(this.imageFile.toURI().toString());
                this.imageView = new ImageView(this.image);

                String fileLocation = this.imageFile.toString();
                this.imgPreviousChosenDir = fileLocation.substring(0, fileLocation.lastIndexOf("\\"));

                /*
                Check if width - height > 500 to adjust for image size
                Appropriate for image with big width
                No support for image with big height since it can exceed the screen
                 */
                boolean checkSizeWidth = this.image.getWidth() - this.image.getHeight() > 500;

                /*
                Check if width - height > 500 to adjust for image size
                Appropriate for image with big width
                 */
                boolean checkSizeHeight = this.image.getHeight() - this.image.getWidth() > 500;

                if (checkSizeWidth) {
                    this.imageView.setFitWidth(450);
                    this.imageView.setFitHeight(250);
                } else if (checkSizeHeight) {
                    this.imageView.setFitWidth(250);
                    this.imageView.setFitHeight(350);
                    this.imageView.setPreserveRatio(true);
                } else {
                    this.imageView.setFitWidth(250);
                    this.imageView.setFitHeight(250);
                }

                if (paneDisplayImage.getChildren().isEmpty()) {
                    paneDisplayImage.getChildren().add(this.imageView);
                    paneCenter.getChildren().add(1, paneDisplayImage);
                } else {
                    // Clear current chosen image in order to add new image
                    paneDisplayImage.getChildren().clear();

                    paneDisplayImage.getChildren().add(this.imageView);
                }

            } catch (NullPointerException ex) {
                // User closed window
                this.imageFile = null;

                this.image = null;
                this.imageView = null;

                // Clear the image since previous image was not chosen
                if (!paneDisplayImage.getChildren().isEmpty()) {
                    paneDisplayImage.getChildren().clear();
                    paneCenter.getChildren().remove(1);
                } else {
                    // Do nothing since no image was selected yet so no need to clear anything
                }
            }

        });

        /*
        Output location
         */
        VBox paneOutput = new VBox(5);

        // Choose output location
        HBox paneOutputLocation = new HBox(5);
        Label lbOutputLocation = new Label("Output location (.txt)");
        Button btChooseOutput = new Button("Browse");
        btChooseOutput.setPadding(new Insets(5, 10, 5, 10));

        paneOutputLocation.getChildren().addAll(lbOutputLocation, btChooseOutput);
        paneOutput.getChildren().add(paneOutputLocation);
        paneCenter.getChildren().add(paneOutput);

        // Display output location
        TextField tfOutputLocation = new TextField();
        tfOutputLocation.setEditable(false);
        tfOutputLocation.setPromptText("Location");
        tfOutputLocation.setPrefColumnCount(20);
        paneOutput.getChildren().add(tfOutputLocation);

        btChooseOutput.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Output location");
            fileChooser.setInitialFileName("extract");
            if (!this.imgPreviousChosenDir.isEmpty()) {
                // Get previous chosen dir
                fileChooser.setInitialDirectory(new File(this.imgPreviousChosenDir));
            }
            fileChooser.getExtensionFilters().add(
                    new ExtensionFilter("Text Files", "*.txt")
            );

            try {
                File selectedFile = fileChooser.showSaveDialog(stage);
                this.txtOutputLocation = new File(selectedFile.toString());
                tfOutputLocation.setText(selectedFile.toString());
            } catch (NullPointerException ex) {
                // User closed window
                this.txtOutputLocation = null;
                tfOutputLocation.setText("");
            }

        });

        // Extract button
        Button btExtract = new Button("Extract");
        btExtract.setPadding(new Insets(5, 10, 5, 10));
        // Align button to the right
        HBox paneBtExtract = new HBox();
        paneBtExtract.setPadding(new Insets(5, 5, 5, 5));
        Pane paneEmpty = new Pane();
        HBox.setHgrow(paneEmpty, Priority.ALWAYS);

        paneBtExtract.getChildren().addAll(paneEmpty, btExtract);
        paneCenter.getChildren().add(paneBtExtract);

        /*
        Dealing with event.
        Begin embedding. BtEmbed clicked
         */
        btExtract.setOnAction(e -> {
            if (this.imageFile != null
                    && this.txtOutputLocation != null) {

                try {
                    String initialText = Extract.extract(this.imageFile, this.txtOutputLocation);

                    // Clear current selected files to avoid overriding
                    this.imageFile = null;
                    this.txtOutputLocation = null;

                    // Pane for displaying successful message
                    VBox paneSuccessfulMsg = new VBox(10);

                    // Display successful message
                    String successfulMsg = "Successfully extracted the message to a text file!" +
                            "\nTo extract new message, re choose all 2 of them." +
                            "\nFirst 60 characters:";
                    Text txtExtractSuccessful = new Text(successfulMsg);
                    txtExtractSuccessful.setFont(new Font(15));

                    // Display initial msg
                    Text txtInitialMsg = new Text(initialText);
                    txtInitialMsg.setFont(new Font(20));

                    paneSuccessfulMsg.getChildren().addAll(txtExtractSuccessful, txtInitialMsg);
                    pane.setBottom(paneSuccessfulMsg);
                } catch (IOException ex) {
                    // Nothing will happen
                }

            } else if (this.imageFile == null
                    || this.txtOutputLocation == null){
                //Display missing components message
                VBox paneMissingComponents = new VBox(5);
                Text txtMissing;

                if (this.imageFile == null) {
                    txtMissing = new Text("Image to extract not chosen");
                    txtMissing.setFont(new Font(20));
                    paneMissingComponents.getChildren().add(txtMissing);
                }

                if (this.txtOutputLocation == null) {
                    txtMissing = new Text("Output location not chosen");
                    txtMissing.setFont(new Font(20));
                    paneMissingComponents.getChildren().add(txtMissing);
                }

                pane.setBottom(paneMissingComponents);

            }

        });

        return paneCenter;

    }

}
