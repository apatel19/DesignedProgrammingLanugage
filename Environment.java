
class Environment {
    public Lexeme car(Lexeme env) {
        return env.left;
    }

    public Lexeme cdr(Lexeme env) {
        return env.right;
    }

    public Lexeme cadr(Lexeme env) {
        return env.right.left;
    }

    public Lexeme setCar(Lexeme env, Lexeme val) {
        env.left = val;
        return env.left;
    }

     public boolean sameVariable(Lexeme a, Lexeme b) {
        return a.string.equals(b.string);
    }

     public Lexeme create() {
        return extend(null, null, null);
    }

    public Lexeme lookup(Lexeme variable, Lexeme env) {
        while(env != null){
                Lexeme vars = car(env);
                Lexeme val = car(cdr(env));
                while(vars != null){
                    if(car(vars) != null){
                        Lexeme a = car(vars);
                        Lexeme b = variable;
                        if (sameVariable(a, b)){
                            return (car(val));
                        }
                    }
                    vars = cdr(vars);
                    val = cdr(val);
                }
                env = cdr(cdr(env));
        }
        System.out.printf("Variable %s is undefined\n", variable.string);
        System.exit(1); 
        return null;
    }

    public Lexeme update(Lexeme variable, Lexeme value, Lexeme env) {
        while (env != null) {
            Lexeme vars = car(env);
            Lexeme vals = car(cdr(env));
            while (vars != null) {
                if (car(vars) != null) {
                    Lexeme a = car(vars);
                    Lexeme b = variable;
                    if (sameVariable(a, b)){
                        return setCar(vals, value);
                    }
                }
                vars = cdr(vars);
                vals = cdr(vals);
            }
            env = cdr(cdr(env));
        }

        System.out.printf("Variable %s is undefined\n", variable.string);
        return null;
    }

    public Lexeme insert(Lexeme variable, Lexeme value, Lexeme env) {
        setCar(env, new Lexeme("GLUE", variable, car(env)));
        setCar(cdr(env), new Lexeme("GLUE", value, cadr(env)));
        return value;
    }

    public Lexeme extend(Lexeme variables, Lexeme values, Lexeme env) {
        return new Lexeme("ENV", variables, new Lexeme("VALUES", values, env));
    }

    public void printEnv(Lexeme env, boolean local_flag){
        if(local_flag) {
           System.out.println("The local environment is...");
        }else {
            System.out.println("The full environment is...");
        }
        while(env != null){
                Lexeme l = new Lexeme("null");
                Lexeme vars = car(env);
                Lexeme val = car(cdr(env));
                
                if(car(vars) == null){
                    System.out.println("void\n");
                    env = cdr(cdr(env));
                    if(local_flag != true) {continue;}
                    else {break;}
                }
                while(vars != null){
                    if(car(vars) != null){
                        l.display(car(vars));
                        System.out.print(" = ");
                        if(l.checkValue(car(val))) {
                            l.display(car(val));
                        }
                        else {System.out.println("unassigned");}
                        System.out.println("");
                    }
                    vars = cdr(vars);
                    val = cdr(val);
                }
            if(local_flag != true) {
                env = cdr(cdr(env));
            }
            else {
                break;
            }
        }
    }
    //Testing my Enviornment class
    public static void main(String[] args) {
        //Creating Environment
        Environment e = new Environment();
        System.out.println("Creating new environment...");
        Lexeme env = e.create();
        System.out.println("Created new environment.");

        //Global Insertion
        Lexeme var = new Lexeme("VARIABLE", "X");
        Lexeme value = new Lexeme("INTEGER", 5);
        System.out.println("Inserting var X with value 5 to Global Env...");
        e.insert(var, value, env);
        System.out.println("Inserting var Z with value 100 to Global Env..."); 
        e.insert(new Lexeme("VARIABLE", "Z"), new Lexeme("INTEGER", 100), env);
        System.out.println("Complete Global Insertion.");

        //Extending
        System.out.println("Extending to env..."); 
        Lexeme env1 = e.extend(var, value, env);
        System.out.println("Extension Completed.");

        //Local Insertion
        System.out.println("Inserting var A with value 10 to Local Env1...");
        var = new Lexeme ("VARIABLE", "A");
        value = new Lexeme ("INTEGER",10);
        e.insert(var, value, env1);
        System.out.println("Inserting var B with value 1000 to Local Env1...");
        var = new Lexeme ("VARIABLE", "B");
        value = new Lexeme ("INTEGER",1000);
        e.insert(var, value, env1);
        System.out.println("Complete Local Insertion."); 

        //Printing Local Table.
        e.printEnv(env1, true);

        //Printing Global Table.
        e.printEnv(env1, false);

        //Looking up to global using local scope
        System.out.println("Looking Global value of X using local env1...");
        Lexeme x = e.lookup(new Lexeme("VARIABLE", "X"), env1);
        System.out.print("Value of X is ");
        x.display(x); 
        System.out.println("");


        //Updating values
        System.out.println("Updating value of X to 100000 using local env1...");
        e.update(new Lexeme("VARIABLE", "X"), new Lexeme("INTEGER", 100000), env1);

        //Trying to access local variable in global should print undefined.
        //System.out.println("Trying yo get value for B using global..... | should print undefined ang Exit if you uncommennt line --174");
        //Lexeme b = e.lookup(new Lexeme("VARIABLE", "B"), env);  // printing undefined
       
        //Printing full table
        e.printEnv(env1, false);      

    }

}
