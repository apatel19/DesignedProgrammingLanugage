class recognizer {
    
    Lexer l;
    Lexeme currentLexeme;
    Lexeme output;

    public boolean check(String type) {
        return currentLexeme.type.equals(type);
    }

    public Lexeme advance() throws Exception {
        Lexeme old = currentLexeme;
        currentLexeme = l.lex();
        //System.out.println("OLD: " + old.type + " | Current: " + currentLexeme.type ); // for testing
        return old;
    }

    public Lexeme match(String type) throws Exception {
        matchNoAdvance(type);
        return advance();
    }

    public void matchNoAdvance(String type) {
        if (type.equals("INVALID")) {
            System.out.printf("SYNTAX ERROR Line %d: invalid character %s. Typo? \n", l.line, currentLexeme.type);
            System.out.println("INVALID");
            System.exit(1);
        }
        else if (!check(type)) {
            System.out.printf("SYNTAX ERROR ");
            System.out.printf("Line %d: received %s, expected %s.\n", l.line, currentLexeme.type, type);
            System.out.println("INVALID");
            System.exit(1);
        }
    }
    // Pending 

    public boolean programPending() {
        return definitionPending();
    }

    public boolean definitionPending() {
        return varDefinitionPending() || functionDefinitionPending() || structDefinitionPending();
    }

    public boolean structDefinitionPending() {
        return check("STRUCT");
    }

    public boolean varDefinitionPending() {
        return check("VARIABLEDEF") || check("VARIABLE");
    }

    public boolean functionDefinitionPending() {
        return check("FUNCTIONDEF");
    }

    public boolean functionCallPending() {
        return check("OPAREN");
    }

    public boolean paramListPending() {
        return check("VARIABLE");
    }

    public boolean expressionListPending() {
        return exprPending();
    }    

    public boolean exprPending() {
        return unaryPending();
    }

    public boolean numericPending() {
        return check("INTEGER") || check("REAL");
    }

    public boolean idDefPending() {
        return check("VARIABLE");
    }

    public boolean arrayDefPending() {
        return check("OBRACKET");
    }

    public boolean unaryPending() {
        return idDefPending() || check("STRING") || numericPending() || arrayDefPending() || check("CHAR")
            || check("MINUS") || check("OPAREN") || functionCallPending() || check("NULL")
            || check("TRUE") || check("FALSE") || check("PRINT") || check("PRINTLN") || check("RETURN") || check("NOT") || check("VBAR")
            || check("OPEN_FILE_FOR_READING") || check("AT_FILE_END") || check("CLOSE_FILE") || check("READ_INTEGER_FROM_FILE");
    }

    public boolean operatorPending() {
        return check("ASSIGN") || check("PLUS") || check("MINUS") || check("TIMES")
               || check("DIVIDE") || check("MOD") || check("EXP") || check("NOT") 
               || check("GREATEREQUALTO") || check("GREATERTHAN") || check("LESSEQUALTO")
               || check("LESSTHAN") || check("DOLLARSIGN") || check("DOT") || check("NOTEQUALTO")
               || check("EXCLAMATION") || check("AND") || check("OR") || check("DOUBLEEQUALTO");
    }

    public boolean statementListPending(){
        return statementPending();
    }

    public boolean statementPending(){
        return whileLoopPending() || ifStatementPending() || exprPending() || varDefinitionPending() || functionDefinitionPending();
    }
    
     public boolean ifStatementPending() {
        return check("IF");
    }

    public boolean whileLoopPending() {
        return check("WHILE");
    }

    //Implementing Grammer rules

     public Lexeme program() throws Exception {
        Lexeme a = definition();
        Lexeme b = null;
        if (programPending()) {
            b = program();
        }
        return new Lexeme("PROGRAM", a, b);
    }

     public Lexeme definition() throws Exception {
        if (varDefinitionPending()) {
            return varDef();
        }
        else if (functionDefinitionPending()) {
            // match("FUNCTIONDEF");
            // Lexeme temp = new Lexeme("FUNCTIONDEF", match("VARIABLE"), funcDef());
            // Lexeme tree = temp;
            // return tree;
            Lexeme tree = match("FUNCTIONDEF");
            if (check("VARIABLE")){
                tree.left = match("VARIABLE");
            }
            tree.right = funcDef();

            if (tree.left != null) {
                Lexeme temp = new Lexeme ("FUNCTIONDEFINITION", null, tree);
                tree = temp;
                return tree;
            } 
            return tree;
        } 
        else if (idDefPending()) {
            Lexeme tree = new Lexeme("DEFINITION");
            tree.left = idDef();
            tree.right = match("SEMICOLON");
            return tree;
        }
        return structDef();
    }

    public Lexeme structDef() throws Exception {
       match("STRUCT");
       Lexeme a = match("VARIABLE");
       Lexeme b = block();
       return new Lexeme ("STRUCTDEF", a, b);
    }
    public Lexeme varDef() throws Exception {
        if(check("VARIABLEDEF")){
            match("VARIABLEDEF");
            Lexeme variable = match("VARIABLE");
            if (check("SEMICOLON")){
                Lexeme semicolon = match("SEMICOLON");
                Lexeme temp = new Lexeme("DECLARE", variable, null);
                return new Lexeme ("VARIABLEDEF", temp, semicolon);
            }
            else if (check("ASSIGN")){
                variable.left = match("ASSIGN");
                Lexeme valueVariable = expr();
                if (valueVariable != null && valueVariable.left != null && valueVariable.left.string != null){
                    if (valueVariable.left.string.equals("openFileForReading")){
                        Lexeme semicolon = match("SEMICOLON");
                        Lexeme temp = new Lexeme("OPENFILE", valueVariable, semicolon);
                        return new Lexeme("VARIABLEDEF", variable, temp);
                    } else if (valueVariable.left.string.equals("readIntegerFromFile")){
                        Lexeme semicolon = match("SEMICOLON");
                        Lexeme temp = new Lexeme("READFILE", valueVariable, semicolon);
                        return new Lexeme("VARIABLEDEF", variable, temp);
                    } else if (valueVariable.left.string.equals("endOfFile")){
                        Lexeme semicolon = match("SEMICOLON");
                        Lexeme temp = new Lexeme("ENDFILE", valueVariable, semicolon);
                        return new Lexeme("VARIABLEDEF", variable, temp);
                    }
                }
                Lexeme semicolon = match("SEMICOLON");
                Lexeme temp = new Lexeme ("TEMP", valueVariable, semicolon);
                return new Lexeme("VARIABLEDEF", variable, temp);
            }
        }
        Lexeme variable = match("VARIABLE");
            if (check("SEMICOLON")){
                Lexeme semicolon = match("SEMICOLON");
                Lexeme temp = new Lexeme("DECLARE", variable, null);
                return new Lexeme ("DEFINE", temp, semicolon);
            }
            else if (check("ASSIGN")){
                variable.left = match("ASSIGN");
                Lexeme valueVariable = expr();//match("VARIABLE")
                Lexeme semicolon = match("SEMICOLON");
                Lexeme temp = new Lexeme ("TEMP", valueVariable, semicolon);
                return new Lexeme("DEFINE", variable, temp);
            } else if (check("OPAREN")){
                Lexeme call = funcCall();
                if (check("DOT")){
                    Lexeme dot = dotFunc();
                    Lexeme temp = new Lexeme ("TEMP", dot, match("SEMICOLON"));
                    Lexeme temp1 = new Lexeme ("TEMP", variable, call);
                    return new Lexeme("IDFUNCCALL", temp1, temp);
                } else {
                    Lexeme temp = new Lexeme ("GLUE", call, match("SEMICOLON"));
                    return new Lexeme("IDFUNCCALL", variable, temp);
                }
                
            } else if (check("DOT")) {
                Lexeme temp = new Lexeme ("TEMP", dotFunc(), match("SEMICOLON"));
                return new Lexeme("DOT", variable, temp);
            }
        return null;
    }

    public Lexeme dotFunc() throws Exception {
        Lexeme a = match("DOT");
        if (check("PUSH_DOWN")){
           Lexeme func = match("PUSH_DOWN");
           func.right = funcCall();
           return new Lexeme("ADDTOARRAY", a, func);
        } else if (check("EDIT_AT")){
           Lexeme func = match("EDIT_AT");
           func.right = funcCall();
           return new Lexeme("EDITARRAY", a, func);
        }
        Lexeme func = match("VARIABLE");
        func.right = funcCall();
        return new Lexeme("DOTFUNC", a, func);
    }

    public Lexeme arrayAccess() throws Exception {
        Lexeme a = match("VBAR");
        Lexeme b = match("INTEGER");
        b.right = match("VBAR");
        return new Lexeme ("ARRAYACCESS", a, b);
    }

    public Lexeme funcDef() throws Exception {
        Lexeme a = match("OPAREN");
        Lexeme b = optParamList();
        Lexeme c = match("CPAREN");
        Lexeme d = block();
        return new Lexeme ("FUNCDEF", a, new Lexeme ("GLUE", b, new Lexeme("GLUE", c, new Lexeme("GLUE", d, null))));
    }

    public Lexeme optParamList() throws Exception {
        if (paramListPending()) {
            return paramList();
        }
        return null;
    }

    public Lexeme paramList() throws Exception {
        Lexeme a = match("VARIABLE");
        Lexeme b = null;
        if (check("COMMA")) {
            a.right = match("COMMA");
            b = paramList();
        }
        return new Lexeme("PARAMLIST", a, b);
    }

    public Lexeme optExprList() throws Exception {
        if (expressionListPending()) {
            return expressionList();
        }
        return null;
    }

    public Lexeme expressionList() throws Exception {
        Lexeme a = expr();
        Lexeme b = null;
        if (check("COMMA")) {
            a.right = match("COMMA");
            b = expressionList();
        }
        return new Lexeme("EXPLIST", a, b);
    }
    
    public Lexeme expr() throws Exception {
        Lexeme tree = unary();
        if (operatorPending()) {
            Lexeme temp = operator();
            temp.left = tree;
            temp.right = expr();
            tree = temp;
        }
        else if (check("VBAR")){
            Lexeme temp = arrayAccess();
            tree.left = temp;
        }
        return tree;
    }

    public Lexeme funcCall() throws Exception {
        Lexeme a = match("OPAREN");
        Lexeme b = optExprList();
        Lexeme c = match("CPAREN");
        return new Lexeme("IDFUNCCALL", a, new Lexeme("GLUE", b, new Lexeme("GLUE", c, null)));
    }

    public Lexeme numeric() throws Exception {
        if (check("INTEGER")) {
            Lexeme a = match("INTEGER");
            return a;
        }
        else {
            Lexeme a = match("REAL");
            return a;
        }
    }

    public Lexeme unary() throws Exception {
        Lexeme tree;
        if (idDefPending()) {
            tree = idDef();
        }
        else if (check("STRING")) {
            tree = match("STRING");
            tree.string = tree.string.substring(1, tree.string.length() - 1);
        }
        else if (check("TRUE")) {
            match("TRUE");
            tree = new Lexeme ("STRING", "TRUE");
        } 
        else if (check("FALSE")) {
            match("FALSE");
            tree = new Lexeme ("STRING", "FALSE");
        }
        else if (check("INTEGER") || check("REAL")) {
            tree = numeric();
        } 
        else if (check("MINUS")){
            tree = match("MINUS");
            tree.type = "NEGATIVE";
            tree.left = null;
            tree.right = unary();
        } 
        else if (check("OBRACKET")) {
            match("OBRACKET");
            Lexeme a = optExprList();
            Lexeme b = match("CBRACKET");
            tree = new Lexeme("ARRAYDEF", a, b);
        }
        else if (check("VARIABLE")){
            Lexeme b = match("VARIABLE");
            Lexeme a = funcCall();
            tree = new Lexeme("IDFUNCCALL", b, a);
        }
        else if (check("NULL")){
            tree = match("NULL");
        }
        else if (check("EMPTY")) {
            tree = match("EMPTY");
        }
        else if (check("PRINT")) {
            tree = match("PRINT");
            match("OPAREN");
            tree.left = optExprList();
            tree.right = match("CPAREN");
        }
        else if (check("PRINTLN")) {
            tree = match("PRINTLN");
            match("OPAREN");
            tree.left = expressionList();
            tree.right = match("CPAREN");;
        }
        else if (check("NOT")) {
            tree = match("NOT");
            tree.left = null;
            tree.right = unary();
        }
        else if (check("RETURN")){
            tree = match("RETURN");
            tree.left = optExprList();
          //  tree.right = new Lexeme ("SEMICOLON"); changed
        }
        ///ADDED
        else if(check("DOT")){
            tree = dotFunc();
            tree.right = match("SEMICOLON");
        }
        else {
            tree = match("INVALID");
        }
        return tree;
    }

     public Lexeme idDef() throws Exception {
        Lexeme a = match("VARIABLE");
        if (check("OPAREN")) {
            Lexeme b = funcCall();
            Lexeme temp = new Lexeme ("GLUE", b, null); //Added on Mar 29th
            return new Lexeme("IDFUNCCALL", a, temp); //CHanging on Mar28th
        }
        else if (check("DOT")) {
            return new Lexeme("DOT", a, dotFunc());    
        }
        return a;
    }

    public Lexeme operator() throws Exception {
        if (check("ASSIGN")) {
            Lexeme a = match("ASSIGN");
            if (check("ASSIGN")) {
                match("ASSIGN");
                return new Lexeme("DOUBLEEQUALTO");
            }
            return a;
        }
        else if (check("DOUBLEEQUALTO")){
            return match("DOUBLEEQUALTO");
        }
        else if (check("GREATERTHAN")) {
            Lexeme a = match("GREATERTHAN");
            if (check("EQUALTO")) {
                match("EQUALTO");
                return new Lexeme("GREATEREQUALTO");
            }
            return a;
        } else if (check("GREATEREQUALTO")) {
            return match("GREATEREQUALTO");  
        } 
        else if (check("LESSEQUALTO")){
           return match("LESSEQUALTO");
        }
        else if (check("LESSTHAN")) {
            Lexeme a = match("LESSTHAN");
            if (check("EQUALTO")) {
                match("EQUALTO");
                return new Lexeme("LESSEQUALTO");
            }
            return a;
        }
        else if (check("NOT")) {
            match("NOT");
            match("EQUALTO");
            return new Lexeme("NOTEQUALTO");
        } 
        else if (check("EXCLAMATION")) {
            Lexeme a = match("EXCLAMATION");
            if (check("ASSIGN")){
                match("ASSIGN");
                return new Lexeme("NOTEQUALTO");
            }
            return a;
        }
        else if (check("NOTEQUALTO")){
            return match("NOTEQUALTO");
        }
        else if (check("PLUS")) {
            return match("PLUS");
        }
        else if (check("TIMES")) {
            return match("TIMES");
        }
        else if (check("MINUS")) {
            return match("MINUS");
        }
        else if (check("DIVIDE")) {
            return match("DIVIDE");
        }
        else if (check("MOD")) {
            return match("MOD");
        }
        else if (check("EXP")) {
            return match("EXP");
        }
        else if (check("AND")) {
            return match("AND");
        } 
        else if (check("DOT")){
            return match("DOT");
        } 
        else if (check("DOLLARSIGN")){
            return match("DOLLARSIGN");
        }
        else {
            return match("OR");
        }
    }

    public Lexeme block() throws Exception {
        match("OBRACE");
        Lexeme a = optStatementList();
        match("CBRACE");
        return new Lexeme("BLOCK", a, null);
    }

    public Lexeme optStatementList() throws Exception {
        if (statementListPending()) {
            return statementList();
        }
        return null;
    }

    public Lexeme statementList() throws Exception {
        Lexeme a = statement();
        Lexeme b = null;
        if (statementListPending()) {
            b = statementList();
        }
        return new Lexeme("STATEMENTLIST", a, b);
    }

    public Lexeme statement() throws Exception {
        Lexeme a = null;
        if (whileLoopPending()) {
            a =  whileLoop();
        }
        else if (ifStatementPending()) {
            a = ifStatement();
        }
        else if (exprPending()) {
            a = expr();
            return new Lexeme("STATEMENT", a, match("SEMICOLON"));
        }
        else if (definitionPending()){
            a = definition();
            return new Lexeme ("STATEMENT", a, null);
        }
        else {
            System.out.println("Error at line: " + currentLexeme.line);
            System.exit(1);
        }
        return new Lexeme("STATEMENT", a, null);
    }

    public Lexeme ifStatement() throws Exception {
        match("IF");
        match("OPAREN");
        Lexeme a = expr();
        a.right.right = match("CPAREN");
        Lexeme b = block();
        Lexeme c = optElse();
        return new Lexeme("IFSTATEMENT", a, new Lexeme("GLUE", b,
                new Lexeme("GLUE", c, null)));
    }

    public Lexeme optElse() throws Exception {
        if (check("ELSE")) {
           Lexeme a = match("ELSE");
            if (check("IF")){
                return new Lexeme ("ELSEIF",a,ifStatement());
            }
            return new Lexeme("OPTELSE", a, block());
        }
        else {
            return null;
        }
    }

    public Lexeme whileLoop() throws Exception {
        match("WHILE");
        match("OPAREN");
        Lexeme a = expr();
        a.right.right = match("CPAREN"); 
        Lexeme b = block();
        return new Lexeme("WHILE", a, b);
    }

    public Lexeme parse(String f) throws Exception {
        l = new Lexer(f);
        currentLexeme = l.lex();
        output = program();
        match("END_OF_INPUT");
        return output;
    }

    String intFile = "";
    public Lexeme parseWithFile (String f, String f2) throws Exception {
        l = new Lexer(f);
        intFile = f2;
        currentLexeme = l.lex();
        output = program();
        match("END_OF_INPUT");
        return output;
    }

    public static void main(String[] args) {
        recognizer reco = new recognizer();
        try {
            reco.parse(args[0]);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}