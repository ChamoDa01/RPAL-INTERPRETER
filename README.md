# RPAL Compiler 

An RPAL (Right-reference Pure Applicative Language) Compiler implemented in Java, built as part of a Programming Languages project.

This compiler takes RPAL source code as input and produces an Abstract Syntax Tree (AST), a Standardized Tree (ST), and further generates control structures for the CSE (Control Stack Environment) Machine.

## 📁 Project Structure

```
RPAL-INTERPRETER/
├── src/
│ └── main/java/ # Source code
│ └── test/java/ # Unit test code
├── out/ # Compiled .class files
├── Makefile # For compiling and running
├── myrpal.exe # Windows executable
```

## ⚙️ How to Build and Run

### Prerequisites

- Java JDK (21 or later)
- `make` utility

### Compile the project

```bash
make
```

### Run the interpreter on a file
```bash
make runfile FILE=path/to/input
```

### Run with -ast/-st to print the AST/ST
```bash
make runast FILE=path/to/input
make runst FILE=path/to/input
```

### Clean the build
```bash
make clean
```


## 🪟 Running on Windows
### 📦 Latest Release
- **For Windows users, a precompiled executable is included.**
- **Windows Executable:** [`myrpal.exe`](./myrpal.exe)
  
### Run
```bash
.\myrpal.exe file_name
```

### To print AST/ST:
```bash
.\myrpal.exe -ast file_name
.\myrpal.exe -st file_name
```

Make sure your system has Java installed (JDK 21 or later) and myrpal.exe is in the same folder as the input file or added to your PATH.
