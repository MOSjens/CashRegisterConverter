package com.jensreinhart;


import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;


public class CashRegisterConverter extends Application
{
    private File selectedFile;

    /**
     * UI objects
     */
    Button openButton;
    Button processButton;
    Button closeButton;
    Text path;

    public static void main( String[] args )
    {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("Cash Register Converter");
        // Layout
        BorderPane border = new BorderPane();

        // File chooser
        FileChooser fileChooser = new FileChooser();

        // Buttons
        HBox hbox = new HBox();
        hbox.setPadding(new Insets(15, 12, 15, 12));
        hbox.setSpacing(10);

        openButton = new Button("CSV Datei Öffnen");
        openButton.setPrefSize(150, 20);
        openButton.setOnAction(e -> {
            selectedFile = fileChooser.showOpenDialog(stage);
            if (selectedFile != null) {
                path.setText(selectedFile.getAbsolutePath());
            }
        });

        processButton = new Button("XML Dateien Erstellen");
        processButton.setPrefSize(150, 20);
        processButton.setOnAction(e -> {
            if (selectedFile != null) {
                processCsv();
            } else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Keine Datei ausgewählt!");
                alert.setHeaderText(null);
                alert.setContentText("Bitte wählen Sie eine CSV Datei aus!");
                alert.showAndWait();
            }
        });

        closeButton = new Button("Schließen");
        closeButton.setPrefSize(150, 20);
        closeButton.setOnAction(e -> {
            stage.close();
        });

        hbox.getChildren().addAll(openButton, processButton, closeButton);
        border.setBottom(hbox);

        // Text
        VBox vbox = new VBox();
        vbox.setPadding(new Insets(10));
        vbox.setSpacing(8);

        Text title = new Text("CSV in Lexware import XML umwandeln");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        Text pathHeading = new Text("Ausgewählte Datei:");
        pathHeading.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
        path = new Text("Noch keine Datei ausgewählt!");
        path.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        path.setWrappingWidth(400);


        vbox.getChildren().addAll(title, pathHeading, path);
        border.setCenter(vbox);

        Scene scene = new Scene(border, 500,300);
        stage.setScene(scene);
        stage.show();
    }

    private void processCsv(){
        Order cashOrder;
        Order ecOrder;

        try {
            CsvParser csvParser = new CsvParser(selectedFile);
            // TODO check for correctness of file -> alert if not correct (e.g. String comparison of first row)

            cashOrder = csvParser.parsCashOrder();
            ecOrder = csvParser.parsEcOrder();

            // save to xml
            XmlCreator.saveOrderToXml(cashOrder, "BAR");
            XmlCreator.saveOrderToXml(ecOrder, "EC");

            alertDone();

        } catch (Exception e ) {
            alertException(e);
        }


    }

    private void alertDone(){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Fertig");
        alert.setHeaderText(null);
        alert.setContentText("Die Konvertierung ist abgeschlossen und die Dateien wurden erstellt");

        alert.showAndWait();
    }

    private void alertException (Exception e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Fehler!");
        alert.setHeaderText("Ein Fehler ist aufgetreten!");
        alert.setContentText("Fehlertext:");

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);

        TextArea textArea = new TextArea(sw.toString());
        textArea.setEditable(false);
        textArea.setWrapText(true);

        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(textArea, 0, 0); // 0?

        // Set expandable Exception into the dialog pane.
        alert.getDialogPane().setExpandableContent(expContent);

        alert.showAndWait();
    }
}
