package simulator;

public class InstructionExecutor {

    private final CpuState state;

    public InstructionExecutor(CpuState state) {
        this.state = state;
    }

    public void execute(String opcode, String operand) {
        switch (opcode) {
            case "86": // LDAA #
                state.registerA = Integer.parseInt(operand, 16);
                break;
            case "C6": // LDAB #
                state.registerB = Integer.parseInt(operand, 16);
                break;
            case "4C": // INCA
                state.registerA = (state.registerA + 1) & 0xFF;
                break;
            case "5C": // INCB
                state.registerB = (state.registerB + 1) & 0xFF;
                break;
            case "8B": // ADDA #
                state.registerA = (state.registerA + Integer.parseInt(operand, 16)) & 0xFF;
                break;
            case "CB": // ADDB #
                state.registerB = (state.registerB + Integer.parseInt(operand, 16)) & 0xFF;
                break;
            default:
                System.out.println("Simülasyon için tanımsız opcode: " + opcode);
        }

        state.programCounter++;
    }
}
