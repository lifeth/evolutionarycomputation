package funico;

import funico.interpreter.Equation;

/*
 * FunicoApp.java
 * -Xms512m -Xmx1024m
 * -Xms32m -Xmx32m
 */

import funico.interpreter.Evaluator;
import funico.interpreter.Example;
import funico.interpreter.ExampleException;
import funico.interpreter.Extractor;
import funico.interpreter.GoalException;
import funico.interpreter.ProgramException;
import funico.language.LexicalException;
import funico.language.SyntacticalException;

/**
 * The main class of the application.
 */
public class FunicoApp {

    public FunicoApp() {
    }

    /**
     * Main method launching the application.
     */
    public static void main(String[] args) {
        try {
            InduceProgram.extractor = new Extractor(
                "geq(0,1) = false; geq(0,0) = true; geq(1,0) = true; geq(1,1) = true; geq(1,2) = false; geq(2,1) = true; geq(2,5) = false; geq(5,2) = true; geq(3,3) = true");
            System.out.println(InduceProgram.extractor.getTableMainFunctors());
            System.out.println(InduceProgram.extractor.getTableFunctors());
            System.out.println(InduceProgram.extractor.getTableTerminals());
            System.out.println(InduceProgram.extractor.getTableVariables());            
            System.out.println(InduceProgram.extractor.getTableTerminalsWithType());
            System.out.println();
            System.out.println(Evaluator.evalue(
                    "mod3(0) = 0; mod3(1) = 1; mod3(2) = 2; mod3(s(s(s(X)))) = mod3(X)",
                    "mod3(5)"));
            System.out.println(Evaluator.evalue(
                    "even(0) = true; even(1) = false; even(s(s(X))) = even(X)",
                    "even(5)"));
            System.out.println(Evaluator.evalue(
                    "sum(0,X) = X; sum(s(X),Y) = s(sum(X,Y))",
                    "sum(5,3)"));
            
            System.out.println(Evaluator.evalue(
                    "min(A,B) = 2","min(2,5)"));
            System.out.println();

            InduceProgram.example = new Example("geq(false,false) = false \n geq(0,0) = true\n geq(1,0) = true; geq(1,1) = true; geq(1,2) = false; geq(2,1) = true; geq(2,5) = false; geq(5,2) = true; geq(3,3) = true");
           // for (Equation eq : example.getListEquations()) {
            	//System.out.println(InduceProgram.example.getListEquations().getFirst().getLhs().getListChildren().getFirst().getType());
            //	System.out.println(InduceProgram.example .getListEquations().getFirst().getLhs().getListChildren().getLast().getType());
              // System.out.println("Allowed: "+new Equation("geq(false,false) = false").getTypeAllowed());

		//	}
            /*
           // System.out.println(example.getListEquations().size());*/
            
	        // System.out.println(new Equation("3 = true; geq(A,3) = s(1); s(2) = true; s(s(B)) = s(1)").repair());

	         /*Goal goal = new Goal("geq(0,1)\n geq(0,0); geq(1,0); geq(1,1); geq(1,2); geq(2,1); geq(2,5); geq(5,2); geq(3,3)");
	         //System.out.println(goal.getListGoals().get);
*/
           // Random random = new Random();
            //System.out.println(InduceProgram.extractor.generateRandomTerm(2, random.nextDouble()));
            System.out.println(InduceProgram.extractor.generateRandomEquation(1, unalcol.random.Random.nextDouble()));

        } catch (ExampleException | ProgramException | GoalException | LexicalException | SyntacticalException ex) {
            System.out.println(ex);
        } 
    }
}