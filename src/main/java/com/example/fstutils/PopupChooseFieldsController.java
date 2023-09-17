package com.example.fstutils;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.poi.util.StringUtil;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import org.fxmisc.richtext.InlineCssTextArea;

public class PopupChooseFieldsController {
    private String data;

    void showStage(JSONObject jsonObject) {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        
        VBox root = new VBox(10);
        root.setPadding(new Insets(20, 20, 20, 20));
        
        javafx.collections.ObservableList<javafx.scene.Node> children = root.getChildren();
        Scene scene = new Scene(root);
        
        Text headerPopup = new Text("Please choose a field (must be array) to convert to excel");
        headerPopup.setStyle("-fx-font-weight: bold");
        children.add(headerPopup);
        
        ToggleGroup toggleGroup = new ToggleGroup();
        Set<String> setFields = jsonObject.keySet();
        for (String field : setFields) {
            Object obj  = jsonObject.get(field);
            RadioButton radioButton = new RadioButton(field);
            radioButton.setToggleGroup(toggleGroup);
            if (!(obj instanceof JSONArray)) {
                radioButton.setDisable(true);
            }
            children.add(radioButton);
        }

        Button submit = new Button("Submit");
        submit.setOnAction(e -> {
            RadioButton checkedRadioButton = (RadioButton) toggleGroup.getSelectedToggle(); // Cast object to radio button
            if (checkedRadioButton != null) {
                data = checkedRadioButton.getText();
            }
//            if (data == null || data.isEmpty() || data.isBlank()) {
//                Alert a = new Alert(Alert.AlertType.NONE, "Please choose a field to get array!", ButtonType.OK);
//                a.show();
//            } else {
//                stage.close();
//            }
            stage.close();
        });

        root.getChildren().add(submit);
        stage.setScene(scene);
        stage.showAndWait();
    }

    String getData() {
        return data;
    }
}
