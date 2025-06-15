SRC_DIR=src/main/java
BIN_DIR=out
MAIN_CLASS=myrpal

SOURCES := \$(shell find \$(SRC_DIR) -name "*.java")

# Default target
all: compile

# Compile all Java files into BIN_DIR
compile:
	mkdir -p $(BIN_DIR)
	javac -d $(BIN_DIR) $(SOURCES)

# Run with input file only
run: compile
	@echo "Usage: make runfile FILE=filename"
	@echo "Usage: make runast FILE=filename"
	@echo "Usage: make runst FILE=filename"

runfile:
	java -cp $(BIN_DIR) $(MAIN_CLASS) $(FILE)

# Run with -ast switch
runast:
	java -cp $(BIN_DIR) $(MAIN_CLASS) -ast $(FILE)

# Run with -st switch
runst:
	java -cp $(BIN_DIR) $(MAIN_CLASS) -st $(FILE)

# Clean up compiled files
clean:
	rm -rf $(BIN_DIR)

.PHONY: all compile run runfile runast clean
