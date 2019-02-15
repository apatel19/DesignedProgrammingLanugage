class envTest {
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

        //Printing full table
        e.printEnv(env1, false);      
    }
}