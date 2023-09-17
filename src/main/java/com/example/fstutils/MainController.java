package com.example.fstutils;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.fxmisc.richtext.InlineCssTextArea;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class MainController {
    @FXML
    private Label fileName;
    @FXML
    private Label type;
    @FXML
    private Label size;
    @FXML
    private Label labelDirectory;
    @FXML
    private InlineCssTextArea outputLogArea;
    @FXML
    private Button buttonSelectFile;
    @FXML
    private Button buttonConvert;
    @FXML
    private Button buttonChooseDirectory;
    @FXML
    private ProgressBar progressBar;
    private String pathDirectoryToSave;
    private File file;

    @FXML
    public void initialize() {
        clearInformationFile();
        File initialDirectory = new File(System.getProperty("user.home") + File.separator + "Downloads");
        labelDirectory.setText(initialDirectory.getAbsolutePath());
        pathDirectoryToSave = initialDirectory.getAbsolutePath();
    }

    public void showLog(String text) {
        Platform.runLater(() -> {
            outputLogArea.append(" * " + text + "\n", "-fx-fill: black;");
            outputLogArea.moveTo(outputLogArea.getLength());
            outputLogArea.requestFollowCaret();
        });

//        outputLogArea.requestFocus();
    }

    public void showError(String text) {
        Platform.runLater(() -> outputLogArea.append("- " + text + "\n", "-fx-fill: red;"));

    }

    public void showSuccess(String text) {
        Platform.runLater(() -> outputLogArea.append("- " + text + "\n", "-fx-fill: blue;"));

    }

    private void clearInformationFile() {
        if (this.file != null) {
            showLog("File " + fileName.getText() + " has been removed");
        }
        this.file = null;
        fileName.setText("");
        type.setText("");
        size.setText("");
    }

    @FXML
    protected void onSelectFileClick(ActionEvent e) {

        Stage stage = (Stage) buttonSelectFile.getScene().getWindow();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose file..");
        fileChooser.setInitialDirectory(
                new File(System.getProperty("user.home") + File.separator + "Downloads")
        );
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Text Files", "*.json", "*.txt"));

        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            this.file = file;
            String fileNameStr = file.getName();
            fileName.setText(fileNameStr);
            type.setText(Utils.getExtensionByStringHandling(fileNameStr).orElse(""));

            long bytes = file.length();
            double kilobytes = ((double) bytes / 1024);
            size.setText(String.format("%.2f KB", kilobytes));
            showLog("File " + fileNameStr + " has been chosen");
            buttonConvert.setDisable(false);
        } else {
            buttonConvert.setDisable(true);
            clearInformationFile();
        }
    }

    @FXML
    protected void onSelectDirectoryClick(ActionEvent e) {

        Stage stage = (Stage) buttonChooseDirectory.getScene().getWindow();


        DirectoryChooser directoryChooser = new DirectoryChooser();

        // Set the Dialog title
        directoryChooser.setTitle("Select a Directory");

        // Set initial directory
        File initialDirectory = new File(System.getProperty("user.home") + File.separator + "Downloads");
        directoryChooser.setInitialDirectory(initialDirectory);

        File selectedDirectory = directoryChooser.showDialog(stage);

        if (selectedDirectory != null) {
            labelDirectory.setText(selectedDirectory.getAbsolutePath());
            pathDirectoryToSave = selectedDirectory.getAbsolutePath();
        } else {
            labelDirectory.setText("No directory selected.");
            pathDirectoryToSave = null;
        }
    }


    @FXML
    protected void onClickButtonConvert(ActionEvent e) {
        File file = this.file;

        if (!validateDirectorySaveFile(pathDirectoryToSave)) {
            return;
        }
        if (!validateFile(file)) {
            return;
        }

        Path path = file.toPath();
        String contentFromFile;

        try {
            contentFromFile = Files.readString(path);
        } catch (IOException ex) {
            showError("Read content of file fail!");
            return;
        }

        if (contentFromFile == null || contentFromFile.isEmpty() || contentFromFile.isBlank()) {
            showError("Content of file is empty!");
            return;
        }
        contentFromFile = contentFromFile.strip();

        JSONParser parser = new JSONParser();
        Object obj;
        try {
            obj = parser.parse(contentFromFile);
        } catch (ParseException ex) {
            showError("Parse json file error!");
            return;
        }

        JSONArray jsonArrayToConvert;
        if (obj instanceof JSONArray) {
            jsonArrayToConvert = (JSONArray) obj;
            if (jsonArrayToConvert.isEmpty()) {
                showError("JsonArray is empty!");
                return;
            }

        } else if (obj instanceof JSONObject) {
            JSONObject jsonObject = (JSONObject) obj;

            PopupChooseFieldsController wc = new PopupChooseFieldsController();
            wc.showStage(jsonObject);
            String fieldToConvert = wc.getData();

            if (fieldToConvert == null || fieldToConvert.isEmpty() || fieldToConvert.isBlank()) {
                showError("No field has been choose!");
                return;
            }
            jsonArrayToConvert = (JSONArray) jsonObject.get(fieldToConvert);
        } else {
            showError("Data not instance of jsonArray or jsonObject!");
            return;
        }

        if (jsonArrayToConvert.isEmpty()) {
            showError("Array is empty. No data to convert!");
            return;
        }

        outputLogArea.clear();
        String fileNameExcel = file.getName().replaceAll(".json", ".xlsx");

        writeObjects2ExcelFile(jsonArrayToConvert, pathDirectoryToSave + "\\" + fileNameExcel);

    }

    private boolean validateFile(File file) {
        boolean isValidFile = true;
        if (Objects.isNull(file)) {
            showError("Please choose a file first!");
            return false;
        }
        String extension = Utils.getExtensionByStringHandling(file.getName()).orElse(null);

        if (!List.of("json", "txt").contains(extension)) {
            isValidFile = false;
            showError("File extension not support!");
        }

        long bytes = file.length();
        if (bytes <= 0) {
            isValidFile = false;
            showError("File size is invalid!");
        }
        return isValidFile;
    }

    private boolean validateDirectorySaveFile(String pathDirectoryToSave) {
        if (pathDirectoryToSave == null || pathDirectoryToSave.isEmpty() || pathDirectoryToSave.isBlank()) {
            showError("Save to directory not valid!");
            return false;
        }

        Path path = Paths.get(pathDirectoryToSave);
        if (!Files.exists(path)) {
            showError("Save to directory not exist!");
            return false;
        }
        return true;
    }

    private void writeObjects2ExcelFile(JSONArray jsonArray, String filePath) {

        // Create a background Task
        Task<String> task = new Task<>() {
            @Override
            protected String call() throws Exception {
                try {
                    int progress = 0;
                    updateProgress(progress++, 10);

                    JSONObject firstObj = (JSONObject) jsonArray.get(0);
                    Set<String> header = firstObj.keySet();

                    String[] COLUMNs = new String[header.size()];
                    header.toArray(COLUMNs);
                    showLog("Creating excel file..");
                    Workbook workbook = new XSSFWorkbook();
                    updateProgress(progress++, 10);


                    CreationHelper createHelper = workbook.getCreationHelper();
                    showLog("Creating sheet 'result'..");
                    Sheet sheet = workbook.createSheet("result");
                    updateProgress(progress++, 10);

                    Font headerFont = workbook.createFont();
                    headerFont.setBold(true);
                    headerFont.setColor(IndexedColors.BLUE.getIndex());

                    CellStyle headerCellStyle = workbook.createCellStyle();
                    headerCellStyle.setFont(headerFont);

                    // Header
                    showLog("Creating column header..");
                    Row headerRow = sheet.createRow(0);
                    for (int col = 0; col < COLUMNs.length; col++) {
                        Cell cell = headerRow.createCell(col);
                        cell.setCellValue(COLUMNs[col]);
                        cell.setCellStyle(headerCellStyle);
                    }
                    updateProgress(progress++, 10);

                    // CellStyle for Age
                    CellStyle ageCellStyle = workbook.createCellStyle();
                    ageCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("#"));

                    int rowIdx = 1;
                    showLog("Creating row data..");
                    int total = jsonArray.size() / 6;
                    for (int i = 0; i < jsonArray.size(); i++) {
                        if (i % total == 0) {
                            if (i != 0) {
//                                showLog("Created " + i + " of " + jsonArray.size() + " row");
                                updateProgress(progress++, 10);
                            }
                        }
                        
                        JSONObject component = (JSONObject) jsonArray.get(i);
                        Row row = sheet.createRow(rowIdx++);

                        for (int j = 0; j < COLUMNs.length; j++) {
                            row.createCell(j).setCellValue(String.valueOf(component.get(COLUMNs[j])));
                        }
                    }
//                    updateProgress(progress++, 10);


                    showLog("Saving file to directory..");
                    FileOutputStream fileOut = new FileOutputStream(filePath);
                    workbook.write(fileOut);
                    fileOut.close();
                    workbook.close();
                    updateProgress(progress++, 10);
                    showLog("File has been saved to '" + filePath + "'.");

                    return filePath;
                } catch (Exception e) {
                    updateProgress(0, 10);
                    throw e;
                }

            }
        };

        // This method allows us to handle any Exceptions thrown by the task
        task.setOnFailed(wse -> {
            showError(wse.getSource().getException().getMessage());
            wse.getSource().getException().printStackTrace();
        });

        // If the task completed successfully, perform other updates here
        task.setOnSucceeded(wse -> {
            showSuccess("Convert json successfully!");

        });

        // Before starting our task, we need to bind our UI values to the properties on the task
        progressBar.progressProperty().bind(task.progressProperty());
//        outputLogArea.textProperty().bind(task.messageProperty());

        // Now, start the task on a background thread
        new Thread(task).start();
    }

}