package model;

import javafx.beans.property.SimpleStringProperty;

public class TranslationRow {
    private final SimpleStringProperty lineNumber;
    private final SimpleStringProperty assemblyCode;
    private final SimpleStringProperty objectCode;
    private final boolean isError;

    public TranslationRow(String lineNumber, String assemblyCode, String objectCode) {
        this(lineNumber, assemblyCode, objectCode, false);
    }

    public TranslationRow(String lineNumber, String assemblyCode, String objectCode, boolean isError) {
        this.lineNumber = new SimpleStringProperty(lineNumber);
        this.assemblyCode = new SimpleStringProperty(assemblyCode);
        this.objectCode = new SimpleStringProperty(objectCode);
        this.isError = isError;
    }

    public String getLineNumber() {
        return lineNumber.get();
    }

    public String getAssemblyCode() {
        return assemblyCode.get();
    }

    public String getObjectCode() {
        return objectCode.get();
    }

    public boolean isError() {
        return isError;
    }
}
