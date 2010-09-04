.PHONY = clean

# Debe estar definida la variable $ANTLR_CLASSPATH en la terminal.
CLASSPATH = $$(echo $$ANTLR_CLASSPATH)

# Hay que modificar este valor según el nombre de tu fichero ".g". En este caso sería, Embellecedor.g.
GRAMATICA = plp2

all: main

main: $(GRAMATICA)Lexer.java $(GRAMATICA)Parser.java
	javac -classpath $(CLASSPATH):. *.java

$(GRAMATICA)Lexer.java $(GRAMATICA)Parser.java: $(GRAMATICA).g
	java -classpath $(CLASSPATH):. org.antlr.Tool $(GRAMATICA).g

clean:
	rm -f *.class
	rm -f $(GRAMATICA)Lexer.java $(GRAMATICA)Parser.java $(GRAMATICA).tokens
