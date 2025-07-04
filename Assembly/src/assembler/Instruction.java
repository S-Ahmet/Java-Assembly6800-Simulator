package assembler;

public class Instruction {
    public int opcode;
    public int length;

    public Instruction(int opcode, int length) {
        this.opcode = opcode;
        this.length = length;
    }
}
