SRC_DIR = src
BIN_DIR = bin
MAIN_CLASS = gui.MainWindow
DATA_DIR = data

SOURCES = $(shell find $(SRC_DIR) -name "*.java")

all: compile

compile:
	mkdir -p $(BIN_DIR)
	javac -d $(BIN_DIR) -sourcepath $(SRC_DIR) $(SOURCES)

run: compile
	java -cp $(BIN_DIR) $(MAIN_CLASS) $(DATA_DIR)

clean:
	rm -rf $(BIN_DIR)

.PHONY: all compile run clean
