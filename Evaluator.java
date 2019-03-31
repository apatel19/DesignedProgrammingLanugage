import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Evaluator {

    private Lexeme global;
    private Environment e;
    
    private String inputFileName;

    public Evaluator() {
        e = new Environment();
        global = e.create();
    }

    private Lexeme eval(Lexeme tree, Lexeme env) {
        if (tree == null) {
            return null;
        }
        // System.out.println("->: " + tree.type); //For testing purpose
        switch (tree.type) {
        // self-evaluating
        case "READER": return tree;
        case "STRING":
            return tree;
        case "BOOLEAN":
            return tree;
        case "SEMICOLON":
            return tree;
        case "VARIABLE":
            return e.lookup(tree, env);
        case "DOLLARSIGN":
            return tree;
        case "INTEGER":
            return tree;
        case "REAL":
            return tree;
        case "EMPTY":
            return tree;

        // operators
        case "DOUBLEEQUALTO":
            return evalSimpleOp(tree, env);
        case "NOTEQUALTO":
            return evalSimpleOp(tree, env);
        case "GREATEREQUALTO":
            return evalSimpleOp(tree, env);
        case "LESSEQUALTO":
            return evalSimpleOp(tree, env);
        case "GREATERTHAN":
            return evalSimpleOp(tree, env);
        case "LESSTHAN":
            return evalSimpleOp(tree, env);
        case "PLUS":
            return evalSimpleOp(tree, env);
        case "MINUS":
            return evalSimpleOp(tree, env);
        case "TIMES":
            return evalSimpleOp(tree, env);
        case "DIVIDE":
            return evalSimpleOp(tree, env);
        case "MOD":
            return evalSimpleOp(tree, env);
        case "EXP":
            return evalSimpleOp(tree, env);
        case "ASSIGN":
            return evalAssign(tree, env);

        // builtin functions
        case "PRINT":
            return evalPrint(tree, env);
        case "PRINTLN":
            return evalPrintLn(tree, env);
         
            
        case "PROGRAM":
            return evalProgram(tree, env);
        case "VARIABLEDEF":
            return evalVarDef(tree, env);
        case "DEFINE":
            return evalVarDef(tree, env);
        case "FUNCTIONDEFINITION":
            return evalFuncDef(tree, env);
        case "FUNCTIONDEF":
            return evalFuncDef(tree, env);
        case "IDFUNCCALL":
            return evalFuncCall(tree, env);
        case "EXPLIST":
            return evalExpList(tree, env);
        case "ARRAYDEF":
            return evalArrayDef(tree, env);
        case "BLOCK":
            return evalBlock(tree, env);
        case "DOT":
            return evalDot(tree, env);

        // statements
        case "STATEMENTLIST":
            return evalStatementList(tree, env);
        case "STATEMENT":
            return evalStatement(tree, env);
        case "IFSTATEMENT":
            return evalIfStatement(tree, env);
        case "OPTELSE":
            return evalOptElse(tree, env);
        case "WHILE":
            return evalWhile(tree, env);
        // default: System.out.println("...IGNORE" + tree.type); break; //For testing purpose
        }
        return tree;
    }

    private Lexeme evalSimpleOp(Lexeme t, Lexeme env) {
        switch (t.type) {
        case "PLUS":
            return evalPlus(t, env);
        case "MINUS":
            return evalMinus(t, env);
        case "TIMES":
            return evalTimes(t, env);
        case "DIVIDE":
            return evalDivide(t, env);
        case "EXP":
            return evalExp(t, env);
        case "MOD":
            return evalMod(t, env);
        case "DOUBLEEQUALTO":
            return evalEqualTo(t, env);
        case "NOTEQUALTO":
            return evalNotEqualTo(t, env);
        case "GREATEREQUALTO":
            return evalGthanEqualTo(t, env);
        case "LESSEQUALTO":
            return evalLthanEqualTo(t, env);
        case "GREATERTHAN":
            return evalGreaterThan(t, env);
        case "LESSTHAN":
            return evalLessThan(t, env);
        default:
            return null;
        }
    }

    private Lexeme evalPrint(Lexeme t, Lexeme env) {
        Lexeme eargs = evalExpList(t.left, env);

        while (eargs != null) {
            if (eargs.left != null) {
                eargs.display(eargs.left);
            }
            eargs = eargs.right;
        }
        return null;
    }

    public Lexeme evalPrintLn(Lexeme t, Lexeme env) {
        Lexeme eargs = evalExpList(t.left, env);
        while (eargs != null) {
            if (eargs.left != null) {
                eargs.display(eargs.left);
            }
            eargs = eargs.right;
        }
        System.out.println(); //Prints new Line
        return null;
    }

    //Handling opening file
    private Lexeme evalOpenFile(Lexeme t, Lexeme env) {
        String fileName = t.right.left.right.left.right.left.left.string;

        //If file has text file coded in or reading it from terminal.
        if (fileName.equals("arg1")) {
            fileName = inputFileName;
        }
        Lexeme temp = null;
        try {
            Scanner reader = new Scanner(new File(fileName));
            temp = new Lexeme(fileName, reader);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return temp;
    }

    //Handling reading from file
    private Lexeme evalReadFile(Lexeme t, Lexeme env) {
        String varname = t.right.left.right.left.right.left.left.string;
        Lexeme lookUp = e.lookup(new Lexeme ("VARIABLE", varname), env);
        Integer x = lookUp.reader.nextInt();
        Lexeme intLexeme = new Lexeme("INTEGER", x);
        return intLexeme;
    }

    //Handling if we are at end of file
    private Lexeme evalEndFile(Lexeme t, Lexeme env) {
        String varname = t.right.left.right.left.right.left.left.string;
        Lexeme lookUp = e.lookup(new Lexeme ("VARIABLE", varname), env);
        boolean check = lookUp.reader.hasNextInt();
        Lexeme boolLexeme = null;
        if(check) {
            boolLexeme = new Lexeme("STRING", "NOTEND");
        }
        else { boolLexeme = new Lexeme("STRING", "END");}
        return boolLexeme;
    }
    
    private Lexeme evalDot(Lexeme t, Lexeme env) {
        if (t.right != null && t.right.type.equals("ADDTOARRAY")){
            return evalAddToArray(t, env);
        } else if (t.right != null && t.right.type.equals("EDITARRAY")){
            return evalEditArray(t, env);
        }
        Lexeme object = eval(t.left, env);
        return eval(t.right, e.extend(object.left, object.right.left, env));
    }

    private Lexeme evalExpList(Lexeme t, Lexeme env) {
        Lexeme result = new Lexeme("EVALEDARGLIST");
        Lexeme ptr = result;
        while (t != null) {
            Lexeme val = eval(t.left, env);
            ptr.left = val;
            t = t.right;
            if(t != null) {
                ptr.right = new Lexeme("EVALEDARGLIST");
                ptr = ptr.right;
            }
        }
        return result;
    }

    private Lexeme evalPlus(Lexeme t, Lexeme env) {
        Lexeme left = eval(t.left, env);
        Lexeme right = eval(t.right, env);
        if (left.type.equals("INTEGER") && right.type.equals("INTEGER"))
            return new Lexeme("INTEGER", left.integer + right.integer);
        else if (left.type.equals("INTEGER") && right.type.equals("REAL"))
            return new Lexeme("REAL", left.integer + right.integer);
        else if (left.type.equals("REAL") && right.type.equals("INTEGER"))
            return new Lexeme("REAL", left.real + right.integer);
        else if (left.type.equals("REAL") && right.type.equals("REAL")){
            return new Lexeme("REAL", left.real + right.real);
        } 
        System.out.printf("Sematic Error: invalid Operation %s + %s.\n", t.left.type, t.right.type);
        System.out.println("INVALID");
        System.exit(1);
        return null;
    }

     private Lexeme evalMinus(Lexeme t, Lexeme env) {
        Lexeme left = eval(t.left, env);
        Lexeme right = eval(t.right, env);
        if (left.type.equals("INTEGER") && right.type.equals("INTEGER"))
            return new Lexeme("INTEGER", left.integer - right.integer);
        else if (left.type.equals("INTEGER") && right.type.equals("REAL"))
            return new Lexeme("REAL", left.integer - right.real);
        else if (left.type.equals("REAL") && right.type.equals("INTEGER"))
            return new Lexeme("REAL", left.real - right.integer);
        else if (left.type.equals("REAL") && right.type.equals("REAL")){
            return new Lexeme("REAL", left.real - right.real);
        } 
        System.out.printf("Sematic Error: invalid Operation %s - %s.\n", t.left.type, t.right.type);
        System.out.println("INVALID");
        System.exit(1);
        return null;
    }

    private Lexeme evalTimes(Lexeme t, Lexeme env) {
        Lexeme left = eval(t.left, env);
        Lexeme right = eval(t.right, env);
        if (left.type.equals("INTEGER") && right.type.equals("INTEGER"))
            return new Lexeme("INTEGER", left.integer * right.integer);
        else if (left.type.equals("INTEGER") && right.type.equals("REAL"))
            return new Lexeme("REAL", left.integer * right.real);
        else if (left.type.equals("REAL") && right.type.equals("INTEGER"))
            return new Lexeme("REAL", left.real * right.integer);
        else if (left.type.equals("REAL") && right.type.equals("REAL")){
            return new Lexeme("REAL", left.real * right.real);
        } 
        System.out.printf("Sematic Error: invalid Operation %s * %s.\n", t.left.type, t.right.type);
        System.out.println("INVALID");
        System.exit(1);
        return null;
    }

    private Lexeme evalDivide(Lexeme t, Lexeme env) {
        Lexeme left = eval(t.left, env);
        Lexeme right = eval(t.right, env);
        if (left.type.equals("INTEGER") && right.type.equals("INTEGER"))
            return new Lexeme("INTEGER", left.integer / right.integer);
        else if (left.type.equals("INTEGER") && right.type.equals("REAL"))
            return new Lexeme("REAL", left.integer / right.real);
        else if (left.type.equals("REAL") && right.type.equals("INTEGER"))
            return new Lexeme("REAL", left.real / right.integer);
        else if (left.type.equals("REAL") && right.type.equals("REAL")){
                return new Lexeme("REAL", left.real / right.real);
        } 
        System.out.printf("Sematic Error: invalid Operation %s / %s.\n", t.left.type, t.right.type);
        System.out.println("INVALID");
        System.exit(1);
        return null;
    }

    private Lexeme evalExp(Lexeme t, Lexeme env) {
        Lexeme left = eval(t.left, env);
        Lexeme right = eval(t.right, env);
        if (left.type.equals("INTEGER") && right.type.equals("INTEGER"))
            return new Lexeme("REAL", Math.pow((double) left.integer, (double) right.integer));
        else if (left.type.equals("INTEGER") && right.type.equals("REAL"))
            return new Lexeme("REAL", Math.pow(left.integer, right.real));
        else if (left.type.equals("REAL") && right.type.equals("INTEGER"))
            return new Lexeme("REAL", Math.pow(left.real, right.integer));
        else
            return new Lexeme("REAL", Math.pow(left.real, right.real));
    }

    private Lexeme evalMod(Lexeme t, Lexeme env) {
        Lexeme left = eval(t.left, env);
        Lexeme right = eval(t.right, env);
        return new Lexeme("INTEGER", left.integer % right.integer);
    }

    private Lexeme evalAssign(Lexeme t, Lexeme env) {
        Lexeme value = eval(t.right, env);

        if (t.left.type.equals("VARIABLE")) {
            e.update(t.left, value, env);
        }
        else if (t.left.type.equals("DOT")) {
            Lexeme object = eval(t.left.left, env);
            e.update(t.left.right, value, object);
        }

        return value;
    }

    private Lexeme evalEqualTo(Lexeme t, Lexeme env) {
        Lexeme left = eval(t.left, env);
        Lexeme right = eval(t.right, env);
        if (left.type.equals("INTEGER") && right.type.equals("INTEGER"))
            return new Lexeme("BOOLEAN", left.integer == right.integer);
        else if (left.type.equals("INTEGER") && right.type.equals("REAL"))
            return new Lexeme("BOOLEAN", left.integer == right.real);
        else if (left.type.equals("REAL") && right.type.equals("INTEGER"))
            return new Lexeme("BOOLEAN", left.real == right.integer);
        else if (left.type.equals("REAL") && right.type.equals("REAL"))
            return new Lexeme("BOOLEAN", left.real == right.real);
        else if (left.type.equals("STRING") && right.type.equals("STRING"))
            return new Lexeme("BOOLEAN", left.string.equals(right.string));
        else if (left.type.equals("BOOLEAN") && right.type.equals("BOOLEAN"))
            return new Lexeme("BOOLEAN", left.bool == right.bool);
        else if (left.type.equals("EMPTY") && !right.type.equals("EMPTY"))
            return new Lexeme("BOOLEAN", false);
        else if (!left.type.equals("EMPTY") && right.type.equals("EMPTY"))
            return new Lexeme("BOOLEAN", false);
        else if (left.type.equals("EMPTY") && right.type.equals("EMPTY"))
            return new Lexeme("BOOLEAN", true);
        else {
            System.out.printf("\nFatal error in Evaluator.java: Type mismatch, attempting to compare %s with %s\n", left.type, right.type);
            System.exit(1);
            return null;
        }
    }

    private Lexeme evalNotEqualTo(Lexeme t, Lexeme env) {
        Lexeme left = eval(t.left, env);
        Lexeme right = eval(t.right, env);
        if (left.type.equals("INTEGER") && right.type.equals("INTEGER"))
            return new Lexeme("BOOLEAN", left.integer != right.integer);
        else if (left.type.equals("INTEGER") && right.type.equals("REAL"))
            return new Lexeme("BOOLEAN", left.integer != right.real);
        else if (left.type.equals("REAL") && right.type.equals("INTEGER"))
            return new Lexeme("BOOLEAN", left.real != right.integer);
        else if (left.type.equals("REAL") && right.type.equals("REAL"))
            return new Lexeme("BOOLEAN", left.real != right.real);
        else if (left.type.equals("STRING") && right.type.equals("STRING"))
            return new Lexeme("BOOLEAN", !left.string.equals(right.string));
        else if (left.type.equals("BOOLEAN") && right.type.equals("BOOLEAN"))
            return new Lexeme("BOOLEAN", left.bool != right.bool);
        else if (left.type.equals("EMPTY") && !right.type.equals("EMPTY"))
            return new Lexeme("BOOLEAN", true);
        else if (!left.type.equals("EMPTY") && right.type.equals("EMPTY"))
            return new Lexeme("BOOLEAN", true);
        else if (left.type.equals("EMPTY") && right.type.equals("EMPTY"))
            return new Lexeme("BOOLEAN", false);
        else {
            System.out.printf("\nFatal error in Evaluator.java: Type mismatch, attempting to compare %s with %s\n", left.type, right.type);
            System.exit(1);
            return null;
        }
    }

    private Lexeme evalGthanEqualTo(Lexeme t, Lexeme env) {
        Lexeme left = eval(t.left, env);
        Lexeme right = eval(t.right, env);
        if (left.type.equals("INTEGER") && right.type.equals("INTEGER"))
            return new Lexeme("BOOLEAN", left.integer >= right.integer);
        else if (left.type.equals("INTEGER") && right.type.equals("REAL"))
            return new Lexeme("BOOLEAN", left.integer >= right.real);
        else if (left.type.equals("REAL") && right.type.equals("INTEGER"))
            return new Lexeme("BOOLEAN", left.real >= right.integer);
        else if (left.type.equals("REAL") && right.type.equals("REAL"))
            return new Lexeme("BOOLEAN", left.real >= right.real);
        else {
            System.out.printf("\nFatal error in Evaluator.java: Type mismatch, attempting to compare %s with %s\n", left.type, right.type);
            System.exit(1);
            return null;
        }
    }

    private Lexeme evalLthanEqualTo(Lexeme t, Lexeme env) {
        Lexeme left = eval(t.left, env);
        Lexeme right = eval(t.right, env);
        if (left.type.equals("INTEGER") && right.type.equals("INTEGER"))
            return new Lexeme("BOOLEAN", left.integer <= right.integer);
        else if (left.type.equals("INTEGER") && right.type.equals("REAL"))
            return new Lexeme("BOOLEAN", left.integer <= right.real);
        else if (left.type.equals("REAL") && right.type.equals("INTEGER"))
            return new Lexeme("BOOLEAN", left.real <= right.integer);
        else if (left.type.equals("REAL") && right.type.equals("REAL"))
            return new Lexeme("BOOLEAN", left.real <= right.real);
        else {
            System.out.printf("\nFatal error in Evaluator.java: Type mismatch, attempting to compare %s with %s\n", left.type, right.type);
            System.exit(1);
            return null;
        }
    }

    private Lexeme evalLessThan(Lexeme t, Lexeme env) {
        Lexeme left = eval(t.left, env);
        Lexeme right = eval(t.right, env);
        if (left.type.equals("INTEGER") && right.type.equals("INTEGER"))
            return new Lexeme("BOOLEAN", left.integer < right.integer);
        else if (left.type.equals("INTEGER") && right.type.equals("REAL"))
            return new Lexeme("BOOLEAN", left.integer < right.real);
        else if (left.type.equals("REAL") && right.type.equals("INTEGER"))
            return new Lexeme("BOOLEAN", left.real < right.integer);
        else
            return new Lexeme("BOOLEAN", left.real < right.real);
    }

    private Lexeme evalGreaterThan(Lexeme t, Lexeme env) {
        Lexeme left = eval(t.left, env);
        Lexeme right = eval(t.right, env);
        if (left.type.equals("INTEGER") && right.type.equals("INTEGER"))
            return new Lexeme("BOOLEAN", left.integer > right.integer);
        else if (left.type.equals("INTEGER") && right.type.equals("REAL"))
            return new Lexeme("BOOLEAN", left.integer > right.real);
        else if (left.type.equals("REAL") && right.type.equals("INTEGER"))
            return new Lexeme("BOOLEAN", left.real > right.integer);
        else
            return new Lexeme("BOOLEAN", left.real > right.real);
    }

    private Lexeme evalStatementList(Lexeme t, Lexeme env) {
        Lexeme val = null;
        while (t != null) {
            val = eval(t.left, env);
            t = t.right;
        }
        return val;
    }

    private Lexeme evalStatement(Lexeme t, Lexeme env) {
        return eval(t.left, env);
    }

    private Lexeme evalWhile(Lexeme t, Lexeme env) {
        Lexeme result = null;
        while(eval(t.left, env).bool) {
            result = eval(t.right, env);
        }
        return result;
    }

    private Lexeme evalIfStatement(Lexeme t, Lexeme env) {
        if(eval(t.left, env).bool) {
            return eval(t.right.left, env);
        }
        else {
            return eval(t.right.right.left, env);
        }
    }

    private Lexeme evalOptElse(Lexeme t, Lexeme env) {
        return eval(t.right, env);
    }

    private Lexeme evalBlock(Lexeme t, Lexeme env) {
        Lexeme result = null;

        while (t != null) {
            result = eval(t.left, env);
            t = t.right;
        }
        return result;
    }

    private Lexeme evalProgram(Lexeme t, Lexeme env) {
        while (t != null) {
            eval(t.left, env);
            t = t.right;
        }
        return null;
    }

    private Lexeme evalVarDef(Lexeme t, Lexeme env) {
        if (t.type.equals("VARIABLEDEF")){
            if ( (t.right.left.left != null) && t.right.left.left.type.equals("ARRAYACCESS")){
                Lexeme var = t.right.left;
                Lexeme result = evalArrAccess(t.right.left.left, env, var);
                Lexeme val = eval(result, env);
                e.insert(t.left, val, env);
                return null;
            } else if (t.right.type.equals("OPENFILE")){
                Lexeme val = evalOpenFile(t, env);
                Lexeme varName = new Lexeme ("VARIABLE", t.left.string);
                e.insert(varName, val, env);
                // Lexeme lookUp = e.lookup(varName, env);              //For testing purposes 
                // System.out.println("DISPLAY:");
                // lookUp.display(lookUp);
            } else if (t.right.type.equals("READFILE")){
                Lexeme val = evalReadFile(t, env);
                Lexeme varName = new Lexeme ("VARIABLE", t.left.string);
                e.insert(varName, val, env);
            } else if (t.right.type.equals("ENDFILE")){
                Lexeme val = evalEndFile(t, env);
                Lexeme varName = new Lexeme ("VARIABLE", t.left.string);
                e.insert(varName, val, env);
                // Lexeme lookUp = e.lookup(varName, env);              //For testing purposes
                // System.out.println("DISPLAY:--> ");
                // lookUp.display(lookUp);
            }
            else {
                Lexeme val = eval(t.right.left, env);
                e.insert(t.left, val, env);
            }
        } else {
            Lexeme valUpdate = eval(t.right.left.left, env);
            e.update(t.left, valUpdate, env);
        }
        return null;
    }
    private Lexeme evalFuncDef(Lexeme tree, Lexeme env) {
        // Lexeme closure = new Lexeme("CLOSURE", env, tree);
        // e.insert(tree.left, closure, env);
        // return null;
        if (!tree.type.equals("FUNCTIONDEFINITION") && tree.left == null) {
            //System.out.println("LAMBDA CALL HIT");
            return evalLambda(tree, env);
        }
        Lexeme closure = new Lexeme("CLOSURE", env, tree);
        e.insert(tree.right.left, closure, env);
        return null;
    }

    private Lexeme evalFuncCall(Lexeme t, Lexeme env) {
        Lexeme closure = eval(t.left, env); //get function definition environment
        Lexeme args = t.right.left.right.left;  //Coming from func call
        Lexeme params = closure.right.right.right.right.left;
        Lexeme body = closure.right.right.right.right.right.right.left;
        Lexeme senv = closure.left;
        Lexeme eargs = evalExpList(args, env);
        Lexeme xenv = e.extend(params, eargs, senv);

        //variable that points to this xenv
        e.insert(new Lexeme("ID", "mug"), xenv, xenv);
        return eval(body, xenv);
    }

    private Lexeme evalArrayDef(Lexeme t, Lexeme env) {
        Lexeme arr = new Lexeme("EVALEDARRAY");
        
        Lexeme evaledArgs = eval(t.left, env);
        ArrayList<Lexeme> ptrs = new ArrayList<>();

        Lexeme cursor = evaledArgs;

        while(cursor != null) {
            ptrs.add(cursor.left);
            cursor = cursor.right;
        }

        arr.left = evaledArgs;
        arr.right = null;
        arr.arrVal = ptrs;
        return arr;
    }

    private Lexeme evalArrAccess(Lexeme t, Lexeme env, Lexeme var) {
        Lexeme arr = eval(var, env);
        Lexeme index = eval(t.right, env);
        Lexeme result = arr.arrVal.get(index.integer);
        return result;
    }

    public Lexeme evalAddToArray(Lexeme t, Lexeme env){
        Lexeme arr = eval(t.left, env);
        Lexeme evaledArgs = eval(t.right.right.right.right.left, env);
        Lexeme cursor = evaledArgs;
        while(cursor != null) {
            arr.arrVal.add(cursor.left);
            cursor = cursor.right;
        }
        return arr;
    }

    public Lexeme evalEditArray(Lexeme t, Lexeme env){
        Lexeme arr = eval(t.left, env);
        Lexeme evaledArgs = eval(t.right.right.right.right.left, env);
        Lexeme cursor = evaledArgs;
        Integer at = cursor.left.integer;
        Lexeme val = cursor.right.left;
        arr.arrVal.add(at, val);
        return null;
    }



    private Lexeme evalLambda(Lexeme tree, Lexeme env) {
        //System.out.println("LAMBDA ARGS: " + tree.left.type);
        Lexeme args = tree.left;
        Lexeme params = tree.right.left;
        Lexeme body = tree.right.right;
        Lexeme local = e.extend(env, params, args);
        return eval(body, local);
        //return null;
    }

    void runEval(Lexeme tree) {
        eval(tree, global);
    }    

    void runEval1(Lexeme tree, String file) {
        inputFileName = file;
        eval(tree, global);
    }

    public static void main(String[] args) throws Exception {
         Evaluator evalObject;
        recognizer parseObject;
    
        evalObject = new Evaluator();
        parseObject = new recognizer();
        if (args.length < 2) {
            evalObject.runEval(parseObject.parse(args[0]));
        }
        else {
            evalObject.runEval1(parseObject.parse(args[0]), args[1]);
        }
    }

}
