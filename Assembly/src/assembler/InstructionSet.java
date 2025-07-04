package assembler;

import java.util.HashMap;
import java.util.Map;

public class InstructionSet {

    public static class Instruction {
        public final String opcode;
        public final String mode;
        public final int length;

        public Instruction(String opcode, String mode, int length) {
            this.opcode = opcode;
            this.mode = mode;
            this.length = length;
        }
    }

    private static final Map<String, Instruction> instructionMap = new HashMap<>();

    static {
        // Immediate Addressing
        instructionMap.put("LDAA_IMM", new Instruction("86", "IMM", 2));
        instructionMap.put("LDAB_IMM", new Instruction("C6", "IMM", 2));
        instructionMap.put("ADDA_IMM", new Instruction("8B", "IMM", 2));
        instructionMap.put("ADDB_IMM", new Instruction("CB", "IMM", 2));
        instructionMap.put("SUBA_IMM", new Instruction("80", "IMM", 2));
        instructionMap.put("SUBB_IMM", new Instruction("C0", "IMM", 2));
        instructionMap.put("CMPA_IMM", new Instruction("81", "IMM", 2));
        instructionMap.put("CMPB_IMM", new Instruction("C1", "IMM", 2));
        instructionMap.put("ANDA_IMM", new Instruction("84", "IMM", 2));
        instructionMap.put("LDX_IMM", new Instruction("CE", "IMM16", 3));

        // Direct Addressing
        instructionMap.put("LDAA_DIR", new Instruction("96", "DIR", 2));
        instructionMap.put("LDAB_DIR", new Instruction("D6", "DIR", 2));
        instructionMap.put("STAA_DIR", new Instruction("97", "DIR", 2));
        instructionMap.put("STAB_DIR", new Instruction("D7", "DIR", 2));
        instructionMap.put("LDX_DIR", new Instruction("DE", "DIR", 2));
        instructionMap.put("STX_DIR", new Instruction("DF", "DIR", 2));
        instructionMap.put("CMPA_DIR", new Instruction("91", "DIR", 2));
        instructionMap.put("CMPB_DIR", new Instruction("D1", "DIR", 2));
        instructionMap.put("ANDA_DIR", new Instruction("94", "DIR", 2));
        instructionMap.put("ADDA_DIR", new Instruction("9B", "DIR", 2));
        instructionMap.put("SUBA_DIR", new Instruction("90", "DIR", 2));

        // Indexed Addressing
        instructionMap.put("LDAA_IDX", new Instruction("A6", "IDX", 2));
        instructionMap.put("LDAB_IDX", new Instruction("E6", "IDX", 2));
        instructionMap.put("STAA_IDX", new Instruction("A7", "IDX", 2));
        instructionMap.put("STAB_IDX", new Instruction("E7", "IDX", 2));
        instructionMap.put("LDX_IDX", new Instruction("EE", "IDX", 2));
        instructionMap.put("STX_IDX", new Instruction("EF", "IDX", 2));

        // Extended Addressing
        instructionMap.put("LDAA_EXT", new Instruction("B6", "EXT", 3));
        instructionMap.put("LDAB_EXT", new Instruction("F6", "EXT", 3));
        instructionMap.put("STAA_EXT", new Instruction("B7", "EXT", 3));
        instructionMap.put("STAB_EXT", new Instruction("F7", "EXT", 3));
        instructionMap.put("CMPA_EXT", new Instruction("B1", "EXT", 3));
        instructionMap.put("CMPB_EXT", new Instruction("F1", "EXT", 3));
        instructionMap.put("ANDA_EXT", new Instruction("B4", "EXT", 3));
        instructionMap.put("ADDA_EXT", new Instruction("BB", "EXT", 3));
        instructionMap.put("SUBA_EXT", new Instruction("B0", "EXT", 3));
        instructionMap.put("JMP_EXT", new Instruction("7E", "EXT", 3));
        instructionMap.put("JSR_EXT", new Instruction("BD", "EXT", 3));

        // Relative Addressing (Branch)
        instructionMap.put("BRA_REL", new Instruction("20", "REL", 2));
        instructionMap.put("BNE_REL", new Instruction("26", "REL", 2));
        instructionMap.put("BEQ_REL", new Instruction("27", "REL", 2));
        instructionMap.put("BMI_REL", new Instruction("2B", "REL", 2));
        instructionMap.put("BPL_REL", new Instruction("2A", "REL", 2));
        instructionMap.put("BSR_REL", new Instruction("8D", "REL", 2));

        // Inherent Addressing
        instructionMap.put("INCA_INH", new Instruction("4C", "INH", 1));
        instructionMap.put("DECA_INH", new Instruction("4A", "INH", 1));
        instructionMap.put("INCB_INH", new Instruction("5C", "INH", 1));
        instructionMap.put("DECB_INH", new Instruction("5A", "INH", 1));
        instructionMap.put("CLRA_INH", new Instruction("4F", "INH", 1));
        instructionMap.put("CLRB_INH", new Instruction("5F", "INH", 1));
        instructionMap.put("NOP_INH", new Instruction("01", "INH", 1));
        instructionMap.put("RTS_INH", new Instruction("39", "INH", 1));
        instructionMap.put("CBA_INH", new Instruction("11", "INH", 1));
        instructionMap.put("ABA_INH", new Instruction("1B", "INH", 1));
        instructionMap.put("SBA_INH", new Instruction("10", "INH", 1));
        instructionMap.put("TAB_INH", new Instruction("16", "INH", 1));
        instructionMap.put("TBA_INH", new Instruction("17", "INH", 1));
        instructionMap.put("DEX_INH", new Instruction("09", "INH", 1));
        instructionMap.put("INX_INH", new Instruction("08", "INH", 1));
        instructionMap.put("SEC_INH", new Instruction("0D", "INH", 1));
        instructionMap.put("CLC_INH", new Instruction("0C", "INH", 1));
        instructionMap.put("ASLA_INH", new Instruction("48", "INH", 1)); // 🔥 ASLA EKLENDİ

        // Shift & Rotate
        instructionMap.put("LSRA_INH", new Instruction("44", "INH", 1));
        instructionMap.put("LSRB_INH", new Instruction("54", "INH", 1));
        instructionMap.put("ASRA_INH", new Instruction("47", "INH", 1));
        instructionMap.put("ASRB_INH", new Instruction("57", "INH", 1));
        instructionMap.put("ROLA_INH", new Instruction("49", "INH", 1));
        instructionMap.put("ROLB_INH", new Instruction("59", "INH", 1));
        instructionMap.put("RORA_INH", new Instruction("46", "INH", 1));
        instructionMap.put("RORB_INH", new Instruction("56", "INH", 1));
    }

    public static Instruction getInstruction(String mnemonic, String operand) {
        if (mnemonic == null) return null;
        mnemonic = mnemonic.toUpperCase();

        String modeKey = determineModeKey(operand);
        String fullKey = mnemonic + "_" + modeKey;

        return instructionMap.get(fullKey);
    }

    private static String determineModeKey(String operand) {
        if (operand == null || operand.isEmpty()) return "INH";
        operand = operand.trim().toUpperCase().replace("*", "");

        if (operand.startsWith("#")) {
            String value = operand.substring(1);
            if (value.startsWith("$")) value = value.substring(1);
            if (value.length() > 2) return "IMM16";
            return "IMM";
        }

        if (operand.endsWith(",X")) return "IDX";
        if (operand.matches("\\$[0-9A-F]{4}|[0-9]{4}")) return "EXT";
        if (operand.matches("-?\\d+") || operand.matches("[A-Z_][A-Z0-9_]*")) return "REL";

        return "DIR";
    }
}
