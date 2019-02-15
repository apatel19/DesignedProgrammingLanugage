/*
    Author: Apurva Patel
    Lexeme Class: Has what type of data and its value stored in this created object class. Also display the given 
                  Lexeme object.
*/

import java.util.ArrayList;


class Lexeme {

    String type;
    String string;
    int integer;
    double real;
    char c;
    int line;
    boolean bool;

    Lexeme left;
    Lexeme right;

    ArrayList<Lexeme> arrVal;


    public Lexeme (String t){
        type = t;
    } 

    public Lexeme(String t, Lexeme l, Lexeme r) {
        type = t;
        left = l;
        right = r;
    }

    public Lexeme (String t, String str){
        type = t;
        string = str;
    }

    public Lexeme(String t, ArrayList<Lexeme> arr) {
        type = t;
        arrVal = arr;
    }

    public Lexeme (String t, int integer){
        type = t;
        this.integer = integer;
    }

    public Lexeme (String t, double real){
        type = t;
        this.real = real;
    } 

    public Lexeme (String t, char c){
        type = t;
        this.c = c;
    }

    public Lexeme (String t, String str, int line){
        type = t;
        string = str;
        this.line = line;
    }

    public Lexeme (String t, boolean b) {
        type = t;
        bool = b;
    }

    public boolean checkValue(Lexeme lexeme) {
        if (lexeme.type.equals("INTEGER")) {
            return true;
        }
        else if (lexeme.type.equals("REAL")) {
            return true;
        }
        else if (lexeme.type.equals("STRING")){
           return true;
        } 
        else if (lexeme.type.equals("VARIABLE")){
            return true;
        }
        else if (lexeme.type.equals("BOOLEAN")) {
            return true;
        }
        else if (lexeme.type.equals("EMPTY")) {
            return true;
        }
        else if (lexeme.type.equals("UNKNOWN")) {
            System.out.println("Found UNKNOWN type: " + lexeme.string +" at line: " + lexeme.line);
            System.exit(1);
        }
            return false;
    }

    public void display (Lexeme lexeme) {

        if (lexeme.type.equals("INTEGER")) {
            System.out.print("INTEGER " + lexeme.integer);
        }
        else if (lexeme.type.equals("REAL")) {
            System.out.print("REAL " + lexeme.real);
        }
        else if (lexeme.type.equals("STRING")){
            System.out.print("STRING " + lexeme.string);
        } 
        else if (lexeme.type.equals("VARIABLE")){
            System.out.print("VARIABLE " + lexeme.string);
        }
        else if (lexeme.type.equals("BOOLEAN")) {
            System.out.print("BOOLEAN " + lexeme.bool);
        }
        else if (lexeme.type.equals("EMPTY")) {
            System.out.print("empty");
        }
        else if (lexeme.type.equals("VARIABLEDEF")) {
            System.out.print("VARIABLEDEF");
        } 
        else if (lexeme.type.equals("UNKNOWN")) {
            System.out.print("Found UNKNOWN type: " + lexeme.string +" at line: " + lexeme.line);
            System.exit(1);
        }
        else {
            System.out.print(lexeme.type);
        }

    }

}