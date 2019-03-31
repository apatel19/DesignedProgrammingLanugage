Author: Apurva Patel
Project: Designed Programming language
Class: 403 Programming Language
Language name: apps

&& File Info.
Grammar.txt: Contaitns grammar for my language
Lexer : Identify Data types. --Lexeme
Scanner: reads input from command line which is a file name.
recognizer.java: making sure written source code satisfy the grammar rules. If found syntax error. Returns error info and line number found error at.
Evaluator: Evaluates identified source code. If found sematic error. Throws erroe.
Enviornment: Stores Evaluated data in enviornment. Creates table for storing data. Global, Local.

&&Language Helper

Comments:
    For single line commnets use: &&
    For block comment:
        &^ This
            is 
            A 
            Block Comment.
        ^&

Reserved Key words:
    var, function, while, if, else, break, continue, struct
    and, or, true, false, print, println, return, NOT, Operators

To define new variable:
    var i = 0;       && this defines integer i and sets value to 0.
    var j = "0";     && this defines string i and sets value to string 0
    var k;           && Declare i;
    i = 1;           && Update value

For function:
    function main (value, str, func) {
        && function body

        value; && will return value        
    }
    && to call function
    main(10, "String", func);

    && To print
    print("APURVA");

    && IF_ELSE
    if (a) {
        &&do this
    } else {
        &&do this
    }

For While and if else:
    var i = 0;
    while (i < 0) {
        if (i == 0) {
            && do this
        } else {
            && do this
            break
        } 
    }

For Arrays: Implemeted built in: push_down, edit_at, access at index.
    var array1 = []; && defines an array
    var array2 = [10, 20, 30]; && defines array with 10, 20, 30;

    var val = array2|0|; && return value at array2 index 0
    array1.push_down(1000, 2000); && adds value 1000, 2000 to an array one. Accept any number of arguments.
    array1.edit_at(1, 11111); && adds value 11111 at index 1

For recursion:
    function fib(n) {
    if (n <= 1) {
       n;
    }
    else {
		 fib(n - 1) + fib(n - 2);
    }
}

Allows functions to return function:

function topLevel() {
    function doSubtraction(x, y) {
        var temp = x - y;
        temp;
    }
    doSubtraction;
}
var top = topLevel();
println(top(100,200)); && does substraction


Allows Integer File Operations: 
    var open = openFileForReading(arg1); && arg1 can be name of input integer text file, "int.txt" or if wants to give input from command line then write arg1.
    var intVal = readIntegerFromFile(open); && return a read integer from file.
    var check = endOfFile(openFile); && returns String value "END" for reached end of file and "NOTEND" for stil has integer in file to read.
    && Look intSum.apps for Explanation

Lambda Functions: Not implemented.
Objects: Not Implemented.

Scrips:
    ./recognizer input.apps     && runs recognizer on input.apps to find if there are any syntacx erro.
    ./eval input.apps           && Evaluates the given file written in "apps" Language.
    ./run integer.txt           && runs intSum.apps and sum all the integers found in input fole integer.txt.

Make File Commands:
    make              && Compiles all java file
    make clean        && Removes .class files

    Responds to this commands accodingly.   - x files executed
        make error1             make arrays                 make problem
        make erroe1x            make arraysx                make problemx
        make error2             make conditionals           make lambds
        make erroe2x            make conditionalsx          make lambdax
        make error3             make iteration              make objects
        make erroe3x            make iterationx             make objectsx
        make error4             make recursion
        make erroe4x            make recursionx
        make error5             make functions
        make error5x            make functionsx

