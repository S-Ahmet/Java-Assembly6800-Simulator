package simulator;

import java.util.HashMap;
import java.util.Map;

public class CpuState {
    public int registerA = 0;
    public int registerB = 0;
    public int programCounter = 0;
    public final Map<Integer, Integer> memory = new HashMap<>();

    public void reset() {
        registerA = 0;
        registerB = 0;
        programCounter = 0;
        memory.clear();
    }

    public String toString() {
        return "A: " + registerA + " | B: " + registerB + " | PC: $" + Integer.toHexString(programCounter).toUpperCase();
    }
}
