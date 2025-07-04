package assembler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CodeGenerator {

    private final List<String> sourceLines;
    private final List<String> outputLines = new ArrayList<>();
    private final Map<String, Integer> labelAddressMap = new HashMap<>();
    private int origin = 0x0000;

    public CodeGenerator(List<String> sourceLines) {
        this.sourceLines = sourceLines;
    }

    public void setOrigin(int origin) {
        this.origin = origin;
    }

    public int getOrigin() {
        return origin;
    }

    public void firstPass() {
        int currentAddress = origin;

        for (String line : sourceLines) {
            String trimmed = line.trim();
            if (trimmed.isEmpty()) {
                outputLines.add("");
                continue;
            }

            if (trimmed.toUpperCase().startsWith("ORG")) {
                try {
                    origin = parseOperand(trimmed.split("\\s+")[1]);
                    currentAddress = origin;
                    outputLines.add("");
                } catch (Exception e) {
                    outputLines.add("HATA: ORG çözümlenemedi");
                }
                continue;
            }

            if (trimmed.equalsIgnoreCase("END") || trimmed.equalsIgnoreCase(".END")) {
                outputLines.add("");
                continue;
            }

            String[] parts = trimmed.split("\\s+", 3);
            String labelOrMnemonic = parts[0].toUpperCase();
            String mnemonic = labelOrMnemonic;
            String operand = parts.length > 1 ? parts[1].trim() : null;

            if (!isMnemonicValid(labelOrMnemonic, operand)) {
                labelAddressMap.put(labelOrMnemonic, currentAddress);
                if (parts.length == 3) {
                    mnemonic = parts[1].toUpperCase();
                    operand = parts[2].trim();
                } else if (parts.length == 2) {
                    mnemonic = parts[1].toUpperCase();
                    operand = null;
                } else {
                    outputLines.add("");
                    continue;
                }
            }

            InstructionSet.Instruction instr = InstructionSet.getInstruction(mnemonic, operand);
            currentAddress += (instr != null) ? instr.length : 1;
            outputLines.add("");
        }
    }

    public void secondPass() {
        int currentAddress = origin;

        for (int i = 0; i < sourceLines.size(); i++) {
            String line = sourceLines.get(i).trim();
            if (line.isEmpty()) {
                outputLines.set(i, "");
                continue;
            }

            if (line.toUpperCase().startsWith("ORG")) {
                try {
                    origin = parseOperand(line.split("\\s+")[1]);
                    currentAddress = origin;
                } catch (Exception e) {
                    outputLines.set(i, "HATA: ORG çözümlenemedi");
                }
                continue;
            }

            if (line.equalsIgnoreCase("END") || line.equalsIgnoreCase(".END")) {
                outputLines.set(i, "");
                continue;
            }

            String[] parts = line.split("\\s+", 3);
            String labelOrMnemonic = parts[0].toUpperCase();
            String mnemonic = labelOrMnemonic;
            String operand = parts.length > 1 ? parts[1].trim() : null;

            if (!isMnemonicValid(labelOrMnemonic, operand)) {
                if (parts.length < 2) {
                    outputLines.set(i, "");
                    continue;
                }
                mnemonic = parts[1].toUpperCase();
                operand = parts.length > 2 ? parts[2].trim() : null;
            }

            if (mnemonic.equalsIgnoreCase("END") || mnemonic.equalsIgnoreCase(".END")) {
                outputLines.set(i, "");
                continue;
            }

            InstructionSet.Instruction instr = InstructionSet.getInstruction(mnemonic, operand);
            if (instr == null) {
                outputLines.set(i, "HATA: Geçersiz komut -> " + mnemonic);
                continue;
            }

            try {
                StringBuilder obj = new StringBuilder(instr.opcode);

                switch (instr.mode) {
                    case "IMM" -> {
                        int value = parseOperand(operand);
                        obj.append(" ").append(String.format("%02X", value));
                    }
                    case "IMM16" -> {
                        int value = parseOperand(operand);
                        obj.append(" ").append(String.format("%02X %02X", (value >> 8) & 0xFF, value & 0xFF));
                    }
                    case "DIR" -> {
                        int addr = resolveAddress(operand);
                        obj.append(" ").append(String.format("%02X", addr & 0xFF));
                    }
                    case "IDX" -> {
                        int offset = parseOperand(operand.toUpperCase().replace(",X", "").trim());
                        obj.append(" ").append(String.format("%02X", offset & 0xFF));
                    }
                    case "EXT" -> {
                        int addr = resolveAddress(operand);
                        obj.append(" ").append(String.format("%02X %02X", (addr >> 8) & 0xFF, addr & 0xFF));
                    }
                    case "REL" -> {
                        int target = resolveAddress(operand);
                        int offset = target - (currentAddress + 2);
                        if (offset < -128 || offset > 127) {
                            throw new IllegalArgumentException("Offset aralık dışında: " + offset);
                        }
                        obj.append(" ").append(String.format("%02X", offset & 0xFF));
                    }
                    default -> {
                        // INH (inherent) mode
                    }
                }

                outputLines.set(i, obj.toString());
                currentAddress += instr.length;

            } catch (Exception e) {
                outputLines.set(i, "HATA: Operand çözülemedi -> " + operand);
            }
        }
    }

    private boolean isMnemonicValid(String mnemonic, String operand) {
        return InstructionSet.getInstruction(mnemonic, operand) != null;
    }

    private int parseOperand(String operand) {
        if (operand == null) return 0;

        operand = operand.trim().replace("*", "");

        if (operand.startsWith("#")) operand = operand.substring(1);
        if (operand.startsWith("$")) operand = "0x" + operand.substring(1);

        return Integer.decode(operand);
    }

    private int resolveAddress(String operand) {
        if (operand == null) return 0;
        if (labelAddressMap.containsKey(operand)) return labelAddressMap.get(operand);
        if (operand.startsWith("$")) operand = "0x" + operand.substring(1);
        return Integer.decode(operand);
    }

    public List<String> getOutputLines() {
        return outputLines;
    }
}
