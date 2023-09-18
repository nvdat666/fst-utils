package com.example.fstutils;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.*;

public class PopupChooseFieldsController {
    private String idToGet;
    ToggleGroup toggleGroup = new ToggleGroup();
    javafx.collections.ObservableList<javafx.scene.Node> children;

    private Map<String, LinkedList> findJsonArrayInJsonObject(LinkedHashMap jsonObject, Map<String, LinkedList> map) {
        Set<String> setFields = jsonObject.keySet();
        for (String field : setFields) {
            Object obj = jsonObject.get(field);
            RadioButton radioButton = new RadioButton(field);
            String id = UUID.randomUUID().toString();
            radioButton.setId(id);
            radioButton.setToggleGroup(toggleGroup);
            if (obj instanceof LinkedList) {
                map.put(id, (LinkedList) obj);
            } else if (obj instanceof LinkedHashMap) {
                map.putAll(findJsonArrayInJsonObject((LinkedHashMap) obj, map));
                radioButton.setDisable(true);
            } else {
                radioButton.setDisable(true);
            }
            children.add(radioButton);
        }
        return map;
    }

    Map<String, LinkedList> showStage(LinkedHashMap jsonObject) {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);

        VBox root = new VBox(10);
        root.setPadding(new Insets(20, 20, 20, 20));

        children = root.getChildren();
        Scene scene = new Scene(root);

        Text headerPopup = new Text("Please choose a field (must be array) to convert to excel");
        headerPopup.setStyle("-fx-font-weight: bold");
        children.add(headerPopup);

        Map<String, LinkedList> rs = findJsonArrayInJsonObject(jsonObject, new LinkedHashMap<>());


//        Set<String> setFields = jsonObject.keySet();
//        for (String field : setFields) {
//            Object obj = jsonObject.get(field);
//            RadioButton radioButton = new RadioButton(field);
//            radioButton.setToggleGroup(toggleGroup);
//            if (!(obj instanceof JSONArray)) {
//                radioButton.setDisable(true);
//            } else if (obj instanceof JSONObject) {
//                JSONObject childJson = (JSONObject) obj;
//            }
//            children.add(radioButton);
//        }

        Button submit = new Button("Submit");
        submit.setOnAction(e -> {
            RadioButton checkedRadioButton = (RadioButton) toggleGroup.getSelectedToggle(); // Cast object to radio button
            if (checkedRadioButton != null) {
                idToGet = checkedRadioButton.getId();
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
        return rs;
    }

    String getIdToGet() {
        return idToGet;
    }
}