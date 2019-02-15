all: Lexeme.class Lexer.class scanner recognizer.class Environment.class
	chmod 755 environment

Lexeme.class: Lexeme.java
	javac Lexeme.java

Lexer.class: Lexer.java
	javac Lexer.java

scanner: scanner.java recognizer.java Lexer.java Lexeme.java
	javac scanner.java

recognizer.class : recognizer.java scanner.java Lexeme.java Lexer.java
	javac recognizer.java

Environment.class: Environment.java scanner.java Lexeme.java Lexer.java
	javac Environment.java

clean:
	rm -f *.class

test:
	javac Environment.java
	./environment

run: test