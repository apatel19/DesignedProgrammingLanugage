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
        //System.out.println("OLD: " + old.type + " | Current: " + currentLexeme.type );
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
        } else if (type.equals("END_OF_INPUT")) {
            System.out.println("VALID");
            System.exit(0);
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

    public boolean varDefPending () {
        return check("SEMICOLON");
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

    public boolean unaryPending() {
        return idDefPending() || check("STRING") || numericPending() || check("CHAR")
            || check("MINUS") || check("OPAREN") || functionCallPending() || check("NULL")
            || check("TRUE") || check("FALSE") || check("PRINT") || check("RETURN") || check("NOT");
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
            Lexeme tree = match("FUNCTIONDEF");
            tree.left = match("VARIABLE");
            tree.right = funcDef();
            Lexeme temp = new Lexeme("FUNCTIONDEF", null, tree);
            tree = temp;
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
        if (check("VARIABLEDEF")){
            match("VARIABLEDEF");
            if (check("VARIABLE")){
               Lexeme a = match("VARIABLE");
                if (check("SEMICOLON")){
                    //return new Lexeme("VARIABLEDEF", a, match("SEMICOLON"));
                    return match("SEMICOLON");
                } else if (check("ASSIGN")){
                    match("ASSIGN");
                    Lexeme b = expr();
                    Lexeme c = match("SEMICOLON");
                    return new Lexeme("VARIABLEDEF", b, c);
                }
            }
        }
        Lexeme c = match("VARIABLE");

        if (check("ASSIGN")){
            match("ASSIGN");
            Lexeme a = optExprList();
            Lexeme b = match("SEMICOLON");
            return new Lexeme("VARIABLEDEF",a, b);
        } 
        Lexeme d = funcCall();
        match("SEMICOLON");
        return new Lexeme ("FUNCTIONCALL", c, d);
    }

    public Lexeme funcDef() throws Exception {
       // match("FUNCTIONDEF");
       // Lexeme a = match("VARIABLE");
       //tree.left = match("VARIABLE");
        Lexeme a = match("OPAREN");
        Lexeme b =  optExprList(); //optParamList();
        Lexeme c = match("CPAREN");
        Lexeme d = block();
        return new Lexeme("FUNCDEF", a, new Lexeme("GLUE", b, new Lexeme("GLUE", c,
                new Lexeme("GLUE", d, null))));
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
            match("COMMA");
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
            match("COMMA");
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
        return tree;
    }

    public Lexeme funcCall() throws Exception {
        // match("OPAREN");
        // Lexeme b = optExprList();
        // match("CPAREN");
        //return b;
        Lexeme a = match("OPAREN");
        Lexeme b = optExprList();
        Lexeme c = match("CPAREN");
        return new Lexeme("FUNCTIONCALL", a, new Lexeme("GLUE", b, new Lexeme("GLUE", c, null)));
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
            tree = match("TRUE");
        } 
        else if (check("FALSE")) {
            tree = match("FALSE");
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
            Lexeme a = expr();
            match("CBRACKET");
            tree = new Lexeme("ARRAYDEF", a, null);
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
            match("CPAREN");
            tree.right = null;
        }
        else if (check("PRINTLN")) {
            tree = match("PRINTLN");
            match("OPAREN");
            tree.left = expressionList();
            match("CPAREN");
            tree.right = null;
        }
        else if (check("NOT")) {
            tree = match("NOT");
            tree.left = null;
            tree.right = unary();
        }
        else if (check("RETURN")){
            tree = match("RETURN");
            tree.left = optExprList();
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
            return new Lexeme("IDFUNCCALL", a, b);
        }
        else if (check("DOT")) {
            Lexeme b = match("DOT");
            b.left = a;
            b.right = idDef();
            return b;
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
        Lexeme b = null;
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
            match("SEMICOLON");
        }
        else if (functionDefinitionPending()){
            Lexeme tree = match("FUNCTIONDEF");
            tree.left = match("VARIABLE");
            tree.right = funcDef();
            Lexeme temp = new Lexeme("FUNCTIONDEF", null, tree);
            tree = temp;
            a = tree;
            //a = funcDef();
        } else if (varDefinitionPending()){
            a = varDef();
        } else {
            System.out.println("Error at line: " + currentLexeme.line);
            System.exit(1);
        }
        return new Lexeme("STATEMENT", a, null);
    }

    public Lexeme ifStatement() throws Exception {
        match("IF");
        match("OPAREN");
        Lexeme a = expr();
        match("CPAREN");
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
        match("CPAREN");
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

    public static void main(String[] args) {
        recognizer reco = new recognizer();
        Lexeme lexeme;
        try {
            lexeme = reco.parse(args[0]);
        } catch (Exception e) {
            e.printStackTrace();
        }
           
    }

}