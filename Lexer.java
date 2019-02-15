
/*
    Author: Apurva Patel
    Lexer Class: Returns the new Lexeme Object for identified data types.
*/

import java.io.FileReader;
import java.io.PushbackReader;
import java.io.BufferedReader;

class Lexer {

    String filename;
    PushbackReader input;
    int r;
    char ch;
    int line;

     // Lexer constructor, takes in a filename
     public Lexer(String f) throws Exception {
        try {
            filename = f;
            input = new PushbackReader(new BufferedReader(new FileReader(filename)));
            line = 1;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void skipWhiteSpace() throws Exception {

        while(ch == '\n' || ch == '\t' || ch == ' ' || ch == '\r') {
            if (ch == '\n') {
                line++;
            }
            r = input.read();
            ch = (char) r;
        }

        //check to see if we have a comment
        if (ch == '&') {
            r = input.read();
            ch = (char) r;

            //check to see if we have a block comment
            if (ch == '^') {
                r = input.read();
                ch = (char) r;

                while (ch != '^') {
                    if (ch == '\n')
                        line++;
                    r = input.read();
                    ch = (char) r;
                }

                r = input.read();
                ch = (char) r;
                if (ch != '&') {
                    System.err.printf("Error. Line %d: Single ^ character.", line);
                    System.exit(1);
                }

                r = input.read();
                ch = (char) r;
                skipWhiteSpace();
            }

            //comment out the rest of the file
            else if (ch == '*') {
                while (r != -1) {
                    r = input.read();
                    ch = (char) r;
                }
            }
            //there is an invalid character
            else if (ch != '&') {

                System.err.printf("Error Line %d: Single ampersand character.", line);
                System.err.println("For single line comment use: &&");
                System.exit(1);

            }

            //we have a one-line comment
            //read to end of the line
            else {
                while (ch != '\n') {
                    r = input.read();
                    ch = (char) r;
                }
                skipWhiteSpace();
            }
        }
    }

    public Lexeme lexString() throws Exception {
        String buffer = "" + ch;
        ch = (char) input.read();

        while (ch != '\"') {

            //check for double quotes within string
            if (ch == '\\') {
                ch = (char) input.read();
            }
            buffer += ch;
            ch = (char) input.read();
        }

        //grab the last quote
        buffer += ch;
        return new Lexeme ("STRING", buffer);
    }

    public Lexeme lexWord() throws Exception {
        String buffer = "" + ch;

        ch = (char) input.read();

        while (Character.isDigit(ch) || Character.isLetter(ch) || ch == '_' || ch == '-' || ch == '?' || ch == '!') {
            buffer += ch;
            ch = (char) input.read();
        }

        input.unread(ch);
        //switch statement for keywords
        if (buffer.equals("var")) {
            return new Lexeme("VARIABLEDEF");
        }
        else if (buffer.equals("function")){
            return new Lexeme("FUNCTIONDEF");
        } 
        else if (buffer.equals("struct")) {
            return new Lexeme("STRUCT");
        }
        else if (buffer.equals("while")) {
            return new Lexeme("WHILE");
        }
        else if (buffer.equals("break")){
            return new Lexeme("BREAK");
        } 
        else if (buffer.equals("continue")){
            return new Lexeme("CONTINUE");
        }
        else if (buffer.equals("if")) {
            return new Lexeme("IF");
        }
        else if (buffer.equals("else")) {
            return new Lexeme("ELSE");
        }
        else if (buffer.equals("and")) {
            return new Lexeme("AND");
        }
        else if (buffer.equals("or")) {
            return new Lexeme("OR");
        }
        else if (buffer.equals("true")) {
            return new Lexeme("BOOLEAN", true);
        }
        else if (buffer.equals("false")) {
            return new Lexeme("BOOLEAN", false);
        }
        else if (buffer.equals("empty")) {
            return new Lexeme("EMPTY");
        }
        else if (buffer.equals("print")) {
            return new Lexeme("PRINT");
        }
        else if (buffer.equals("println")) {
            return new Lexeme("PRINTLN");
        } 
        else if (buffer.equals("return")) {
            return new Lexeme("RETURN");
        }
        else if (buffer.equals("NOT")) {
            return new Lexeme("NOT");
        }
        return new Lexeme ("VARIABLE", buffer);
    }

    public Lexeme lexNumber() throws Exception {
        boolean isReal = false;
        boolean notNumber = false;
        String buffer = "" + ch;
        ch = (char) input.read();

        while (Character.isDigit(ch) || ch == '.') {
            //check if we have a real
            if (ch == '.') {
                isReal = true;
            }
            
            buffer += ch;
            ch = (char) input.read();

            if (Character.isLetter(ch)) {
                buffer += ch;
                notNumber = true;
                break;
            }
        }

        input.unread(ch);

        if(notNumber) {
            return new Lexeme ("UNKNOWN", buffer, line);
        }
        if (isReal) {
            return new Lexeme ("REAL", Double.parseDouble(buffer));
        }
        else {
            return new Lexeme ("INTEGER", Integer.parseInt(buffer));
        }
    }

    public Lexeme lex() throws Exception {
        //read in the first char
        r = input.read();
        ch = (char) r;

        //handle whitespace, if necessary
        skipWhiteSpace();

        if(r == -1) {
            return new Lexeme("END_OF_INPUT");
        }

        switch(ch) {
            //single character tokens
            case '(':
                return new Lexeme("OPAREN");
            case ')':
                return new Lexeme("CPAREN");
            case '[':
                return new Lexeme("OBRACKET");
            case ']':
                return new Lexeme("CBRACKET");
            case '{':
                return new Lexeme("OBRACE");
            case '}':
                return new Lexeme("CBRACE");
            case ',':
                return new Lexeme("COMMA");
            case ';':
                return new Lexeme("SEMICOLON");
            case '=':
                r = input.read();
                ch = (char) r;
                if(ch == '=') return new Lexeme("DOUBLEEQUALTO");
	            else{
                   input.unread(ch);
                   return new Lexeme("ASSIGN");
                  }
            case '+':
                return new Lexeme("PLUS");
            case '-':
                return new Lexeme("MINUS");
            case '*':
                return new Lexeme("TIMES");
            case '/':
                return new Lexeme("DIVIDE");
            case '%':
                return new Lexeme("MOD");
            case '^':
                return new Lexeme("EXP");
            case '~':
                return new Lexeme("NOT");
            case '>':
                r = input.read();
                ch = (char) r;
                if(ch == '=') return new Lexeme("GREATEREQUALTO");
                else{
                    input.unread(ch);
                    return new Lexeme("GREATERTHAN");
                }  
            case '<':
                r = input.read();
                ch = (char) r;
                if(ch == '=') return new Lexeme("LESSEQUALTO");
                else{
                    input.unread(ch);
                    return new Lexeme("LESSTHAN");
                }  
            case '$':
                return new Lexeme("DOLLARSIGN");
            case '.':
                return new Lexeme("DOT");
            case '!':
                r = input.read();
                ch = (char) r;
                if(ch == '=') return new Lexeme("NOTEQUALTO");
                else{
                    input.unread(ch);
                    return new Lexeme("EXCLAMATION");
                }  
            default:
                //multi-character tokens
                //numbers, variables/keywords, and strings
                if (Character.isDigit(ch)) {
                    return lexNumber();
                }
                else if (Character.isLetter(ch)) {
                    return lexWord();
                }
                else if (ch == '\"') {
                    return lexString();
                }
                else {
                    String s = "" + ch;
                    return new Lexeme ("UNKNOWN", s, line);
                }
        }

    }
    
}