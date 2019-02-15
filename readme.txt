Author: Apurva Patel
Project: Designed Programming language

Scanner: reads input from command line which is a file name.


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
    and, or, true, false, print, println, return, NOT

To define new variable:
    var i = 0;       && this defines integer i and sets value to 0.
    var i = "0";     && this defines string i and sets value to string 0

For function:
    function main (void) {
        && function body
        return;
    }
    && to call function
    main();

    && To print
    print("APURVA");

    && IF_ELSE
    if (a) {
        &&do this
    } else {
        &&do this
    }

for While and if else:
    var i = 0;
    while (i < 0) {
        if (i == 0) {
            && do this
        } else {
            && do this
            break
        } 
    }



/*** RECOGNIZER ***//

Outputs:
    test1.apps: VALID
    test2.apps: VALID
    test3.apps: VALID
    test4.apps: VALID
    test5.apps: "SYNTAX ERROR Line 18: received UNKNOWN, expected VARIABLE"
            ADDED COMMENTS HOW TO CORRECT.


    /**How to compile**/

    "make" : Compiles all module
    "make recognizer.class": compile recognizer and needed module

    To run:
   
    "recognizer test1.apps" : runs recognizer on test1.apps file 
    "make run" : runs recognizer on all the files

    "make clean": deletes all .class files

/** Environment **/

    -- holding information about which variables are in scope and what there values are.

    To Compile:
        make      :  Compiles java files
        make run  :  runs Environment file.
