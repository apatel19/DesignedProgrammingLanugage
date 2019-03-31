class PrityPrinter {

    //For testing purpose
    public void p(Lexeme tree) {
        if (tree == null) return;
        tree.display(tree);
        p(tree.left);
        p(tree.right);
    }

    //Main PP class
    public void print(Lexeme tree) {
        if (tree == null) return;
        while(tree != null) {
           if (tree.left.type == "VARIABLEDEF" || tree.left.type == "DEFINE") {
                varDef(tree.left);
           } else if (tree.left.type == "FUNCTIONDEFINITION") {
                funcDef(tree.left);
           } else if (tree.left.type == "IDFUNCCALL") {
                funcCall(tree.left);
           }
            System.out.println("");
            tree = tree.right;
        }
    }
    public void varDef(Lexeme tree) {
        if (tree == null) return;
        //System.out.println("Tree: " + tree.type); //For testing
        switch(tree.type){
            case "VARIABLEDEF": System.out.printf("var "); break;
            case "VARIABLE": System.out.printf("%s",tree.string); break;
            case "INTEGER": System.out.printf("%d", tree.integer); break;
            case "STRING": System.out.printf(" \"%s\"", tree.string); break;
            case "REAL": System.out.printf("%f", tree.real); break;
            case "BOOLEAN": System.out.printf("%b",tree.bool); break;
            case "ASSIGN": System.out.printf("="); break;
            case "IDFUNCCALL": funcCall(tree); return;
            case "COMMA": System.out.printf(","); break;
            case "SEMICOLON": System.out.printf(";"); break;
            case "ARRAYDEF": System.out.printf("["); break;
            case "DOT": varDef(tree.left);
                        System.out.printf("."); 
                        varDef(tree.right);
                        return;
            case "PLUS": varDef(tree.left); 
                         System.out.printf("+");
                         varDef(tree.right);
                         return;
            case "TIMES": expList(tree.left); 
                         System.out.printf("*");
                         expList(tree.right);
                         return;
            case "CBRACKET": System.out.printf("]"); break;
            case "OBRACKET": System.out.printf("["); break;
            case "VBAR": System.out.printf("|"); break;
            case "PUSH_DOWN": System.out.printf("push_down");
        }
        varDef(tree.left);
        varDef(tree.right);
    }

    public void funcDef(Lexeme tree) {
        if (tree == null) return;
        switch(tree.type) {
            case "FUNCTIONDEFINITION":System.out.printf("function "); break;
            case "VARIABLEDEF": varDef(tree); return;
            case "DEFINE": varDef(tree); return; //added
            case "VARIABLE":System.out.printf("%s ",tree.string); break;
            case "INTEGER": System.out.printf("%d", tree.integer); break;
            case "STRING": System.out.printf(" \"%s\"", tree.string); break;
            case "REAL": System.out.printf("%f", tree.real); break;
            case "BOOLEAN": System.out.printf("%b",tree.bool); break;
            case "OPAREN":System.out.printf("("); break;
            case "CPAREN":System.out.printf(")"); break;
            case "ASSIGN": funcDef(tree.left);
                           System.out.printf("=");
                           funcDef(tree.right);
                           return;
            case "DOT": varDef(tree.left);
                           varDef(tree.right);
                           return;
            case "BLOCK": System.out.printf("{"); break;
            case "IFSTATEMENT": System.out.printf("if ("); break;
            case "WHILE": System.out.printf("while ("); break;
            case "LESSTHAN": System.out.printf("<"); break;
            case "ELSE": System.out.printf("else"); break;
            case "CBRACE": System.out.printf("}"); break;
            case "RETURN": System.out.printf("return "); break;
            case "SEMICOLON": System.out.printf(";"); break;
            case "PRINT": System.out.printf("print("); break;
            case "PRINTLN": System.out.printf("println("); break;
            case "DOUBLEEQUALTO":
                            funcDef(tree.left);
                            System.out.printf("==");
                            funcDef(tree.right);
                            // System.out.printf(")");
                            return;
            case "GREATERTHAN": 
                            funcDef(tree.left);
                            System.out.printf(">"); 
                            funcDef(tree.right);
                            // System.out.printf(")");
                            return;
            case "LESSEQUALTO": 
                            funcDef(tree.left);
                            System.out.printf("<="); 
                            funcDef(tree.right);
                            // System.out.printf(")");
                            return;
            case "GREATEREQUALTO": 
                            funcDef(tree.left);
                            System.out.printf(">="); 
                            funcDef(tree.right);
                            // System.out.printf(")");
                            return;
            case "DIVIDE":
                        funcDef(tree.left);
                        System.out.printf("/");
                        funcDef(tree.right);
                        //System.out.printf(")");
                        return;
            case "PLUS": funcDef(tree.left); 
                         System.out.printf("+");
                         funcDef(tree.right);
                         //System.out.printf(";");
                         return;
            case "TIMES": expList(tree.left); 
                         System.out.printf("*");
                         expList(tree.right);
                         return;
            case "PARAMLIST": expList(tree); return;
            case "EXPLIST": expList(tree); return;
            case "IDFUNCCALL": funcCall(tree); return;
        }
            funcDef(tree.left);
            funcDef(tree.right);
    }

    public void expList(Lexeme tree) {
        if (tree == null) {return;}
        switch(tree.type) {
            case "SEMICOLON": System.out.printf(";"); break;
            case "COMMA": System.out.printf(","); break;
            case "CPAREN": System.out.printf(")"); break;
            case "VARIABLE":System.out.printf("%s",tree.string); break;
            case "INTEGER": System.out.printf("%d", tree.integer); break;
            case "STRING": System.out.printf(" \"%s\"", tree.string); break;
            case "REAL": System.out.printf("%lf", tree.real); break;
            case "BOOLEAN": System.out.printf("%b",tree.bool); break;
            case "PLUS": expList(tree.left); 
                         System.out.printf("+");
                         expList(tree.right);
                         return;
            case "TIMES": expList(tree.left); 
                         System.out.printf("*");
                         expList(tree.right);
                         return;
            case "IDFUNCCALL": funcCall(tree); return;
        }
        //tree.display(tree);
        expList(tree.left);
        expList(tree.right);
    }

    public void funcCall(Lexeme tree){
        if (tree == null) return;
        switch (tree.type) {
            case "VARIABLE":System.out.printf("%s",tree.string); break;
            case "OPAREN": System.out.printf("("); break;
            case "CPAREN": System.out.printf(")"); break;
            case "EXPLIST": expList(tree); return;
            case "SEMICOLON": System.out.printf(";"); break;
            case "DOT":  System.out.printf(".");
        }
        funcCall(tree.left);
        funcCall(tree.right);
    }

    public static void main(String[] args) {
        try {
            recognizer reco = new recognizer();
            Lexeme tree = reco.parse(args[0]);
            PrityPrinter p = new PrityPrinter();
            //p.p(tree); //For testing purpose
            p.print(tree);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}