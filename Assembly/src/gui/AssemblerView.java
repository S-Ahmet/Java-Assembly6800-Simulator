package gui;

import assembler.CodeGenerator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import model.TranslationRow;
import simulator.SimulatorController;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

public class AssemblerView extends BorderPane {

    private TextArea assemblyInput;
    private TextArea machineOutput;
    private TableView<TranslationRow> translationTable;
    private ObservableList<TranslationRow> tableData = FXCollections.observableArrayList();

    private Label aReg, bReg, pcReg;
    private TextArea simLog;
    private SimulatorController simulator = new SimulatorController();

    public AssemblerView() {
        setupLayout();
    }

    private void setupLayout() {
        assemblyInput = new TextArea();
        assemblyInput.setFont(Font.font("Courier New", 14));
        assemblyInput.setPromptText("Motorola 6800 assembly kodunuzu buraya yazınız...");

        machineOutput = new TextArea();
        machineOutput.setFont(Font.font("Courier New", 14));
        machineOutput.setEditable(false);

        Button translateButton = new Button("Çevir");
        translateButton.setOnAction(e -> translateCode());

        Button simulateButton = new Button("Simüle Et");
        simulateButton.setOnAction(e -> simulateStep());

        Button runAllButton = new Button("Tümünü Çalıştır");
        runAllButton.setOnAction(e -> runAllSteps());

        Button resetButton = new Button("Resetle");
        resetButton.setOnAction(e -> {
            simulator.load(machineOutput.getText());
            simLog.clear();
            simLog.appendText("Simülasyon resetlendi.\n");
            aReg.setText("A: 0");
            bReg.setText("B: 0");
            pcReg.setText("PC: $C000");
        });

        HBox buttonBox = new HBox(10, translateButton, simulateButton, runAllButton, resetButton);
        buttonBox.setPadding(new Insets(10));

        translationTable = new TableView<>();
        TableColumn<TranslationRow, String> colLine = new TableColumn<>("Satır");
        colLine.setCellValueFactory(new PropertyValueFactory<>("lineNumber"));
        TableColumn<TranslationRow, String> colAsm = new TableColumn<>("Assembly");
        colAsm.setCellValueFactory(new PropertyValueFactory<>("assemblyCode"));
        TableColumn<TranslationRow, String> colObj = new TableColumn<>("Makine Kodu");
        colObj.setCellValueFactory(new PropertyValueFactory<>("objectCode"));
        translationTable.getColumns().addAll(colLine, colAsm, colObj);
        translationTable.setItems(tableData);
        translationTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        translationTable.setRowFactory(tv -> {
            TableRow<TranslationRow> row = new TableRow<>() {
                @Override
                protected void updateItem(TranslationRow item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || empty) {
                        setStyle("");
                    } else if (item.isError()) {
                        setStyle("-fx-background-color: #ffcccc;");
                    } else {
                        setStyle("");
                    }
                }
            };

            row.setOnMouseClicked(event -> {
                TranslationRow item = row.getItem();
                if (item != null && item.isError()) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Hata Detayı");
                    alert.setHeaderText("Satır " + item.getLineNumber());
                    alert.setContentText(item.getObjectCode());
                    alert.showAndWait();
                }
            });

            return row;
        });

        aReg = new Label("A: 0");
        bReg = new Label("B: 0");
        pcReg = new Label("PC: $C000");

        simLog = new TextArea();
        simLog.setEditable(false);
        simLog.setPrefHeight(150);
        simLog.setFont(Font.font("Courier New", 12));

        VBox simPanel = new VBox(5, aReg, bReg, pcReg, new Label("Simülasyon Log:"), simLog);
        simPanel.setPadding(new Insets(10));

        VBox outputBox = new VBox(5,
                new Label("Makine Kodu Çıktısı:"), machineOutput,
                new Label("Satır-Satır Dönüşüm Tablosu:"), translationTable,
                buttonBox,
                simPanel
        );
        outputBox.setPadding(new Insets(10));

        this.setLeft(assemblyInput);
        this.setCenter(outputBox);
        this.setPadding(new Insets(10));
    }

    private void translateCode() {
        String input = assemblyInput.getText();
        String[] lines = input.split("\n");

        List<String> sourceLines = new ArrayList<>(Arrays.asList(lines));
        CodeGenerator generator = new CodeGenerator(sourceLines);
        generator.firstPass();
        generator.secondPass();

        List<String> outputLines = generator.getOutputLines();

        StringBuilder rawOutput = new StringBuilder();
        tableData.clear();

        for (int i = 0; i < outputLines.size(); i++) {
            String asmLine = i < sourceLines.size() ? sourceLines.get(i).trim() : "";
            String obj = outputLines.get(i);
            rawOutput.append(obj).append("\n");

            boolean isError = obj.contains("HATA");
            tableData.add(new TranslationRow(String.valueOf(i + 1), asmLine, obj, isError));
        }

        machineOutput.setText(formatAsMemoryDump(outputLines, generator.getOrigin()));
        simulator.load(rawOutput.toString());
        simLog.clear();
        simLog.appendText("Simülasyon başlatıldı.\n");
    }

    private String formatAsMemoryDump(List<String> outputLines, int base) {
        List<Integer> bytes = new ArrayList<>();
        for (String line : outputLines) {
            if (line.isBlank() || line.contains("HATA")) continue;

            String[] hexParts = line.trim().split("\\s+");
            for (String h : hexParts) {
                try {
                    bytes.add(Integer.parseInt(h, 16));
                } catch (NumberFormatException ignored) {}
            }
        }

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < bytes.size(); i += 16) {
            builder.append(String.format("%04X: ", base + i));
            for (int j = 0; j < 16; j++) {
                if (i + j < bytes.size()) {
                    builder.append(String.format("%02X ", bytes.get(i + j)));
                } else {
                    builder.append("   ");
                }
            }
            builder.append("\n");
        }

        return builder.toString();
    }

    private void simulateStep() {
        if (!simulator.hasNextStep()) {
            simLog.appendText("Simülasyon tamamlandı.\n");
            return;
        }

        String log = simulator.step();
        simLog.appendText(log + "\n");

        aReg.setText("A: " + simulator.getA());
        bReg.setText("B: " + simulator.getB());
        pcReg.setText("PC: $" + String.format("%04X", simulator.getPC()));
    }

    private void runAllSteps() {
        while (simulator.hasNextStep()) {
            String log = simulator.step();
            simLog.appendText(log + "\n");
        }

        aReg.setText("A: " + simulator.getA());
        bReg.setText("B: " + simulator.getB());
        pcReg.setText("PC: $" + String.format("%04X", simulator.getPC()));
    }

}
