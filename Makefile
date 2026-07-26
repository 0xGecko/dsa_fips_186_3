# Makefile pour le projet DSA

# Variables
JAVAC = javac
JAVA = java
SRC_DIR = src
BIN_DIR = bin
SRC = $(SRC_DIR)/DSA.java
CLASS = DSA

# Cible par défaut
all: compile run

# Compilation (dépose les .class dans le dossier bin)
compile:
	$(JAVAC) -d $(BIN_DIR) $(SRC)

# Exécution (indique à Java d'aller chercher dans le dossier bin)
run: compile
	$(JAVA) -cp $(BIN_DIR) $(CLASS)

# Nettoyage
clean:
	rm -rf $(BIN_DIR)/*.class