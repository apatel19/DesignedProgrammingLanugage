
/*
    Author: Apurva Patel
    Scanner Class: Scan the given input file.
*/

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class scanner {
     Lexer lexer;
     String token;
     Lexeme lexeme;

    public void getFileInfo (String f) {
        try {
            lexer = new Lexer(f);
            lexeme = lexer.lex();
        } catch (Exception e) {
            e.printStackTrace();
        }

        token = lexeme.type;  
        while (token != "END_OF_INPUT"){
            lexeme.display(lexeme);
            try {
                lexeme = lexer.lex();
            } catch (Exception e) {
                e.printStackTrace(); 
            }
            token = lexeme.type;
        }
        lexeme.display(lexeme);
    }  

    public static void main(String[] args) {
        scanner scan = new scanner();
        scan.getFileInfo(args[0]);
    }

}