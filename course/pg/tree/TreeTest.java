package pg.tree;

import unalcol.optimization.OptimizationFunction;
import unalcol.optimization.OptimizationGoal;
import unalcol.random.integer.IntUniform;
import unalcol.search.Goal;
import unalcol.search.selection.Elitism;
import unalcol.search.selection.Roulette;
import unalcol.search.selection.Selection;
import unalcol.search.solution.Solution;

public class TreeTest {
	public static int [][] samples = new int [10][3]; 

	public static void test(){
		
		generateSamples();
		
    	System.out.println("=====================================");
		Node node = new Node(null, 2);
		Node node2 = new Node(null, 2);
		node.printExpression();
		System.out.println(" Evaluate: " + node.evaluate(5, 5));
		System.out.println("=====================================");
		node2.printExpression();
		System.out.println(" Evaluate 2: " + node2.evaluate(5, 5));

		System.out.println("=====================================");
		NodeXOver nxo = new NodeXOver();
		Node[] children = nxo.apply(node, node2);
		children[0].printExpression();
		System.out.println(" Evaluate child 1: " + children[0].evaluate(5, 5));
		System.out.println("=====================================");
		children[1].printExpression();
		System.out.println(" Evaluate child 2: " + children[1].evaluate(5, 5));
        
		System.out.println("=====================================");
		NodeMutation nm = new NodeMutation();
		children[0] = nm.apply(children[0]);
		children[0].printExpression();
		System.out.println(" Evaluate after mutation child 1: " + children[0].evaluate(5, 5));
		System.out.println("=====================================");
		children[1] = nm.apply(children[1]);
		children[1].printExpression();
		System.out.println(" Evaluate after mutation child 2: " + children[1].evaluate(5, 5));
	}



	public static void generateSamples() {
		// f(x,y) = x^2 + 3xy - 4
		for (int i = 0; i < 10; i++) {
			int x = new IntUniform(-9, 9).generate();
			int y = new IntUniform(-9, 9).generate();
			int sample[] = {x, y, ((x * x) + (3 * x * y) - 4)};
			samples[i] = sample;	
		}
		
		for (int i = 0; i < 10; i++) {
			int[] sample = samples[i];
			System.out.println("Sample "+i+": "+"("+sample[0]+","+sample[1]+","+sample[2]+")");
		}
	}

	public static void evolve() {
		
		generateSamples();

		int popzize = 100;
		int iteration = 1000;

		// Optimization Function
		OptimizationFunction<Node> function = new Fitness();
		Goal<Node, Double> goal = new OptimizationGoal<Node>(function, true);
		Node[] pop = new Node[popzize];

		// Initial Population
		for (int i = 0; i < popzize; i++) {
			pop[i] = new Node(null, 2);
		}

		@SuppressWarnings("unchecked")
		Solution<Node>[] solution = new Solution[pop.length];

		// Aptitude Calculation 
		for (int j = 0; j < pop.length; j++) {
			solution[j] = new Solution<Node>(pop[j], goal);
		}

		// Population selection
		Selection<Node> selection = new Elitism<Node>(1.0, 0.0);
		// Parent selection
		//Selection<Node> pselection = new Tournament<Node>(4);
		Selection<Node> pselection = new Roulette<Node>();

		// Operators
		NodeXOver xover = new NodeXOver();
		NodeMutation mutation = new NodeMutation();

		String gName = Goal.class.getName();

		for (int i = 0; i < iteration; i++) {
			
			System.out.println("===============Iteration "+ i+"=================");

			@SuppressWarnings("unchecked")
			Solution<Node>[] s2 = new Solution[popzize*2];
			
			for (int j = 0; j < popzize; j+=2) {
				
				// Selection
				Solution<Node>[] parents = pselection.pick(2, solution);
				System.out.println("Selected Parents: "+parents[0].info(gName) +"---"+parents[1].info(gName));
				
				// Xover
				Node[] children = xover.apply(parents[0].object(), parents[1].object());
				
				// Mutation
				children[0] = mutation.apply(children[0]);
				children[1] = mutation.apply(children[1]);
				
				s2[j] = new Solution<Node>(children[0], goal);
				s2[j+1] = new Solution<Node>(children[1], goal);
			}
			
			System.arraycopy(solution, 0, s2, s2.length/2, popzize);
		
			//Replacement selecting best 10 
			solution = selection.pick(popzize, s2);		
			System.out.println("=====================================");
			
			for (int j = 0; j < solution.length; j++) {
				solution[j].object().printExpression();
				System.out.println(" : " + solution[j].info(gName));
			}
		}
	}
	
	public static void main(String[] args) {
		//test();
		evolve();
	}
}
