all: Lexeme.class Lexer.class recognizer.class Environment.class PrityPrinter.class Evaluator.class
	chmod 755 pp
	chmod 755 eval
	chmod 755 run

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

pp:	PrityPrinter.java Lexer.java Lexeme.java recognizer.java Environment.java
	javac PrityPrinter.java

PrityPrinter.class: PrityPrinter.java Lexeme.java Lexer.java recognizer.java Environment.java
	javac PrityPrinter.java

Evaluator.class: Evaluator.java PrityPrinter.java Lexeme.java Lexer.java recognizer.java Environment.java
	javac Evaluator.java

eval: PrityPrinter.java Lexer.java Lexeme.java recognizer.java Environment.java
	javac PrityPrinter.java

error1 :
	@echo error1.apps contents:
	@cat error1.apps

error1x :
	./eval error1.apps
	@echo 

error2 :
	@echo error2.apps contents:
	@cat error2.apps

error2x : 
	./eval error2.apps
	@echo

error3 :
	@echo error3.apps contents:
	@cat error3.apps

error3x :
	./eval error3.apps
	@echo

error4 :
	@echo error4.apps contents:
	@cat error4.apps

error4x :
	./eval error4.apps
	@echo

error5 :
	@echo error5.apps contents:
	@cat error5.apps

error5x :
	./eval error5.apps
	@echo

arrays :
	@echo arrays.apps content:
	@cat arrays.apps

arraysx :
	./eval arrays.apps
	@echo

conditionals :
	@echo conditionals.apps content:
	@cat conditionals.apps

conditionalsx :
	./eval conditionals.apps
	@echo

iteration :
	@echo iteration.apps contents:
	@cat iteration.apps

iterationx :
	./eval iteration.apps
	@echo

recursion :
	@echo recursion.apps contents:
	@cat recursion.apps

recursionx :
	./eval recursion.apps
	@echo

functions :
	@echo functions.apps contents:
	@cat functions.apps

functionsx :
	./eval functions.apps
	@echo

problem :
	@echo intSum.apps contents: Summing all ints found in file int.txt
	@cat intSum.apps

problemx :
	./run int.txt
	@echo

lambda :
	@echo couldn't get lambda working!! :(
	@echo

lambdax :
	@echo not implemented.

objects :
	@echo coudln't get objects working!! :(

objectsx :
	@echo not implemented

clean:
	rm -f *.class