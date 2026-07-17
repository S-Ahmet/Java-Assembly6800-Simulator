# Java Assembly 6800 Simulator

A Java-based simulator for the **Motorola 6800 (M6800)** Assembly language. This project provides an environment to load, interpret, and execute M6800 Assembly programs while visualizing CPU registers and memory states.

## Features

* Execute Motorola 6800 Assembly instructions
* Assembly code parsing and execution
* CPU register simulation
* Memory management
* Step-by-step execution
* Continuous program execution
* User-friendly graphical interface
* Register and memory visualization

## Technologies

* Java
* Java Swing
* Object-Oriented Programming (OOP)

## CPU Components

The simulator models the following CPU registers:

* Accumulator A
* Accumulator B
* Index Register (X)
* Stack Pointer (SP)
* Program Counter (PC)
* Condition Code Register (CC)

## Project Structure

```
Java-Assembly6800-Simulator/
│
├── src/
│   ├── parser/
│   ├── cpu/
│   ├── memory/
│   ├── instructions/
│   ├── gui/
│   └── ...
│
├── examples/
├── screenshots/
└── README.md
```

## Getting Started

1. Clone the repository:

```bash
git clone https://github.com/S-Ahmet/Java-Assembly6800-Simulator.git
```

2. Open the project in your preferred Java IDE (IntelliJ IDEA, Eclipse, or NetBeans).

3. Build and run the project.

## Usage

1. Load or write an M6800 Assembly program.
2. Assemble the code.
3. Execute it using **Run** or **Step** mode.
4. Observe changes in registers, memory, and execution flow.

## Future Improvements

* More M6800 instruction support
* Breakpoints
* Memory editor
* Better error reporting
* Assembly syntax highlighting
* Import/Export Assembly files

## Screenshots

You can add screenshots of the simulator interface here.

```
screenshots/main-window.png
```

## Author

**Sadık Ahmet Karabulut**

GitHub: https://github.com/S-Ahmet

---

This project was developed for educational purposes to better understand the architecture and instruction set of the Motorola 6800 processor through simulation.
