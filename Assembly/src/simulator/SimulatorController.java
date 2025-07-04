package simulator;

import java.util.HashMap;
import java.util.Map;

public class SimulatorController {

    private int pc = 0xC000;
    private int a = 0;
    private int b = 0;
    private int x = 0;
    private final Map<Integer, Integer> memory = new HashMap<>();

    private String[] instructions;
    private int currentLine = 0;

    private int stepCount = 0;
    private final int MAX_STEPS = 100;

    public void load(String machineCode) {
        this.instructions = machineCode.split("\\n");
        this.pc = 0xC000;
        this.currentLine = 0;
        this.a = 0;
        this.b = 0;
        this.x = 0;
        this.memory.clear();
        this.stepCount = 0;
    }

    public boolean hasNextStep() {
        return instructions != null && currentLine < instructions.length;
    }

    public String step() {
        if (stepCount++ > MAX_STEPS) {
            return "Simülasyon sonlandırıldı (sonsuz döngü algılandı).";
        }

        if (!hasNextStep()) return "Simülasyon tamamlandı.";

        String line = instructions[currentLine].trim();
        if (line.isEmpty()) {
            currentLine++;
            pc++;
            return "Boş satır geçildi.";
        }

        String[] bytes = line.split("\\s+");
        String opcode = bytes[0].toUpperCase();
        String log = "PC: $" + String.format("%04X", pc) + " → ";

        switch (opcode) {
            case "86": // LDAA #imm
                a = getHexOperand(bytes, 1);
                log += "LDAA → A = " + a;
                pc += 2;
                break;

            case "C6": // LDAB #imm
                b = getHexOperand(bytes, 1);
                log += "LDAB → B = " + b;
                pc += 2;
                break;

            case "8B": // ADDA #imm
                a = (a + getHexOperand(bytes, 1)) & 0xFF;
                log += "ADDA → A = " + a;
                pc += 2;
                break;

            case "5C": // INCB
                b = (b + 1) & 0xFF;
                log += "INCB → B = " + b;
                pc += 1;
                break;
            case "96": // LDAA direct
                int addr96 = getHexOperand(bytes, 1);
                a = memory.getOrDefault(addr96, 0);
                log += "LDAA → A = " + a;
                pc += 2;
                break;case "9B": // ADDA direct
                int addr9B = getHexOperand(bytes, 1);
                a = (a + memory.getOrDefault(addr9B, 0)) & 0xFF;
                log += "ADDA → A = " + a;
                pc += 2;
                break;
            case "90": // SUBA direct
                int addr90 = getHexOperand(bytes, 1);
                a = (a - memory.getOrDefault(addr90, 0)) & 0xFF;
                log += "SUBA → A = " + a;
                pc += 2;
                break;


            case "97": // STAA direct
                int addrStaa = getHexOperand(bytes, 1);
                memory.put(addrStaa, a);
                log += "STAA → [$" + String.format("%02X", addrStaa) + "] = " + a;
                pc += 2;
                break;

            case "A7": // STAA offset,X
                int offsetA = getHexOperand(bytes, 1);
                memory.put((x + offsetA) & 0xFFFF, a);
                log += "STAA,X → [$" + String.format("%04X", (x + offsetA)) + "] = " + a;
                pc += 2;
                break;

            case "E7": // STAB offset,X
                int offsetB = getHexOperand(bytes, 1);
                memory.put((x + offsetB) & 0xFFFF, b);
                log += "STAB,X → [$" + String.format("%04X", (x + offsetB)) + "] = " + b;
                pc += 2;
                break;

            case "E6": // LDAB offset,X
                int addrLd = (x + getHexOperand(bytes, 1)) & 0xFFFF;
                b = memory.getOrDefault(addrLd, 0);
                log += "LDAB,X → B = " + b;
                pc += 2;
                break;

            case "4C": // INCA
                a = (a + 1) & 0xFF;
                log += "INCA → A = " + a;
                pc += 1;
                break;

            case "08": // INX
                x = (x + 1) & 0xFFFF;
                log += "INX → X = " + x;
                pc += 1;
                break;

            case "09": // DEX
                x = (x - 1) & 0xFFFF;
                log += "DEX → X = " + x;
                pc += 1;
                break;

            case "CE": // LDX #imm16
                x = ((getHexOperand(bytes, 1) << 8) | getHexOperand(bytes, 2)) & 0xFFFF;
                log += "LDX → X = $" + String.format("%04X", x);
                pc += 3;
                break;

            case "7E": // JMP addr (offset-based)
                int offset = getHexOperand(bytes, 1);
                pc = 0xC000 + offset;
                currentLine = findInstructionLineByPC(pc);
                log += "JMP → PC = $" + String.format("%04X", pc);
                return log;

            case "20": // BRA offset
                pc = (pc + 2 + getSignedByte(bytes[1])) & 0xFFFF;
                currentLine = findInstructionLineByPC(pc);
                log += "BRA → PC = $" + String.format("%04X", pc);
                return log;

            case "26": // BNE offset
                if (b != 0) {
                    pc = (pc + 2 + getSignedByte(bytes[1])) & 0xFFFF;
                    currentLine = findInstructionLineByPC(pc);
                    log += "BNE true → $" + String.format("%04X", pc);
                    return log;
                } else {
                    pc += 2;
                    log += "BNE false → devam";
                }
                break;

            case "27": // BEQ offset
                if (b == 0) {
                    pc = (pc + 2 + getSignedByte(bytes[1])) & 0xFFFF;
                    currentLine = findInstructionLineByPC(pc);
                    log += "BEQ true → $" + String.format("%04X", pc);
                    return log;
                } else {
                    pc += 2;
                    log += "BEQ false → devam";
                }
                break;

            case "BD": // JSR addr
                pc = getHexOperand(bytes, 1);
                currentLine = findInstructionLineByPC(pc);
                log += "JSR → PC = $" + String.format("%04X", pc);
                return log;

            case "39": // RTS
                pc += 1;
                log += "RTS → dönüş yapıldı";
                break;

            case "11": // CBA
                log += "CBA → A - B = " + (a - b);
                pc += 1;
                break;
            // ... mevcut switch bloğuna aşağıdakileri EKLE ...

            case "A6": // LDAA offset,X
                int addrA6 = (x + getHexOperand(bytes, 1)) & 0xFFFF;
                a = memory.getOrDefault(addrA6, 0);
                log += "LDAA,X → A = " + a;
                pc += 2;
                break;

            case "84": // ANDA #imm
                a = a & getHexOperand(bytes, 1);
                log += "ANDA → A = " + a;
                pc += 2;
                break;

            case "44": // LSRA
                a = (a >> 1) & 0xFF;
                log += "LSRA → A = " + a;
                pc += 1;
                break;

            case "2B": // BMI offset
                if ((a & 0x80) != 0) {
                    pc = (pc + 2 + getSignedByte(bytes[1])) & 0xFFFF;
                    currentLine = findInstructionLineByPC(pc);
                    log += "BMI true → $" + String.format("%04X", pc);
                    return log;
                } else {
                    pc += 2;
                    log += "BMI false → devam";
                }
                break;


            case "48": // ASLA
                a = (a << 1) & 0xFF;
                log += "ASLA → A = " + a;
                pc += 1;
                break;

            case "58": // ASLB
                b = (b << 1) & 0xFF;
                log += "ASLB → B = " + b;
                pc += 1;
                break;

            case "1B": // ABA
                a = (a + b) & 0xFF;
                log += "ABA → A = " + a;
                pc += 1;
                break;

            case "10": // SBA
                a = (a - b) & 0xFF;
                log += "SBA → A = " + a;
                pc += 1;
                break;

            case "16": // TAB
                b = a;
                log += "TAB → B = " + b;
                pc += 1;
                break;

            case "17": // TBA
                a = b;
                log += "TBA → A = " + a;
                pc += 1;
                break;

            case "0D": // SEC
                log += "SEC → (simüle edilmedi)";
                pc += 1;
                break;

            case "0C": // CLC
                log += "CLC → (simüle edilmedi)";
                pc += 1;
                break;

            case "01": // NOP
                log += "NOP → işlem yok";
                pc += 1;
                break;

            default:
                log += "Bilinmeyen opcode: " + opcode;
                pc += bytes.length;
                break;
        }

        currentLine++;
        return log;
    }

    private int getSignedByte(String hex) {
        int val = Integer.parseInt(hex, 16);
        return (val > 127) ? val - 256 : val;
    }

    private int getHexOperand(String[] arr, int index) {
        return index < arr.length ? Integer.parseInt(arr[index], 16) : 0;
    }

    private int findInstructionLineByPC(int targetAddr) {
        int org = 0xC000;
        int curr = org;
        for (int i = 0; i < instructions.length; i++) {
            String line = instructions[i].trim();
            if (line.isEmpty()) continue;
            int len = line.split("\\s+").length;
            if (curr == targetAddr) return i;
            curr += len;
        }
        return instructions.length;
    }

    public int getA() { return a; }
    public int getB() { return b; }
    public int getX() { return x; }
    public int getPC() { return pc; }
    public Map<Integer, Integer> getMemory() { return memory; }
    public String[] getInstructions() { return instructions; }
    // Belleği belirli aralıkta ekrana yazdırmak için
    public void dumpFullMemory() {
        System.out.println("=== FULL RAM DUMP ===");
        for (int base = 0x0000; base <= 0x00F0; base += 0x10) {
            System.out.printf("%04X:", base);
            for (int i = 0; i < 16; i++) {
                int addr = base + i;
                int val = memory.getOrDefault(addr, 0);
                System.out.printf(" %02X", val);
            }
            System.out.println();
        }
    }


}
