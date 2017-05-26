package ga.epimarks;

import unalcol.evolution.ga.GAVariation;
import unalcol.optimization.OptimizationFunction;
import unalcol.optimization.OptimizationGoal;
import unalcol.search.Goal;
import unalcol.search.RealQualityGoal;
import unalcol.search.population.Population;
import unalcol.search.selection.Elitism;
import unalcol.search.selection.Selection;
import unalcol.search.selection.Tournament;
import unalcol.search.solution.Solution;
import unalcol.search.space.Space;
import unalcol.sort.Order;
import unalcol.types.real.array.DoubleArray;

public class GeneticAlgorithmTest {
	

	public static void calculate() {
		int n = 30;
		double avg = 0;
		double x[] = new double[n];
		
		for (int i = 0; i < n; i++) {
			//x[i] = maxOnes();
			System.out.println(""+i+" :"+x[i]);
			avg += x[i];
		}
		
		avg /= n;
		DoubleArray.merge(x);
		double median = x[n/2];
		
		System.out.println(avg +" # "+median);
		
		double vavg = 0.0;
		double vmedian = 0.0;
		
		for (int i = 0; i < n; i++) {
			vavg += (x[i] - avg) * (x[i] - avg);
			vmedian += (x[i] - median) * (x[i] - median);
		}
		
		double savg= Math.sqrt(vavg/(n-1));
		double smedian = Math.sqrt(vmedian/(n-1));
		
		System.out.println("Average: " +avg + " +/- " +savg);
		System.out.println("Median: " +median + " +/- " +smedian);
	}

	
	public static Solution<MarkedBitArray> pick(Population<MarkedBitArray> pop) {
		String gName = Goal.class.getName();
		@SuppressWarnings("unchecked")
		RealQualityGoal<MarkedBitArray> goal = (RealQualityGoal<MarkedBitArray>)pop.data(gName);
		Order<Double> order = goal.order();
		int k=0;
		double q = (Double)pop.get(0).info(gName);
		for( int i=1; i<pop.size(); i++ ){
			double qi = (Double)pop.get(i).info(gName);
			if( order.compare(q, qi) < 0 ){
				k=i;
				q=qi;
			}
		}
		return pop.get(k);
	}

	public static void task(int run) {
		
		// Search Space definition
		Space<MarkedBitArray> space = new ga.epimarks.BinarySpace(100);

		// Optimization Function
		OptimizationFunction<MarkedBitArray> function = new ga.epimarks.MaxOnes();
		Goal<MarkedBitArray, Double> goal = new OptimizationGoal<MarkedBitArray>(function, false);
		MarkedBitArray[] pop = new MarkedBitArray[100];

		//Initial Population
		for (int i = 0; i < 100; i++) {
			pop[i] = space.pick();
		}

		@SuppressWarnings("unchecked")
		Solution<MarkedBitArray>[] solution = new Solution[pop.length];
		
		// Aptitude Calculation txt
		for (int j = 0; j < pop.length; j++) {
			solution[j] = new Solution<MarkedBitArray>(pop[j], goal);
		}
				
		//Parent selection
		//Selection<MarkedBitArray> selection = new Elitism<MarkedBitArray>(0.2, 0.8);
		Selection<MarkedBitArray> selection = new Tournament<MarkedBitArray>(4);
		
		//Operators
		ga.epimarks.XOver xover = new ga.epimarks.XOver();
		ga.epimarks.BitMutation mutation = new ga.epimarks.BitMutation();
		
		@SuppressWarnings({ "unchecked", "rawtypes" })
		GAVariation<MarkedBitArray> variation = new GAVariation( selection,  mutation, xover, 1.0); 
		
		String gName = Goal.class.getName();
		
		Population<MarkedBitArray> newPop = new Population<MarkedBitArray>(solution);
		//newPop.set( gName, goal );
		
        for (int i = 1; i <= 100; i++) {
        	
		    Object data = newPop.data(gName);
		    
			newPop =
	    			new Population<MarkedBitArray>(variation.apply(space, newPop.object()));
	    	newPop.set( gName, data);

			//Select the best of the population in the current it
	    	//bestByIte[run][i- 1] = (double) pick(newPop).info(gName);
		}	
	}
	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws Exception {
		
		int DIM = 100;
		int POP_SIZE = 10;

		Space<MarkedBitArray> space = new ga.epimarks.BinarySpace(DIM);

		// Optimization Function
		OptimizationFunction<MarkedBitArray> function = new ga.epimarks.MaxOnes();
		Goal<MarkedBitArray, Double> goal = new OptimizationGoal<MarkedBitArray>(function, false);
		MarkedBitArray[] pop = new MarkedBitArray[POP_SIZE];

		//Initial Population
		for (int i = 0; i < POP_SIZE; i++) {
			pop[i] = space.pick();
		}

		Solution<MarkedBitArray>[] solution = new Solution[pop.length];
		
		// Aptitude Calculation txt
		for (int j = 0; j < pop.length; j++) {
			solution[j] = new Solution<MarkedBitArray>(pop[j], goal);
			System.out.println(pop[j] + "  :" + solution[j].info(Goal.class.getName()));
		}
		
		//Applying Selection
		Selection<MarkedBitArray> selection = new Elitism<MarkedBitArray>(1.0, 0);

		Solution<MarkedBitArray>[] parents = selection.pick(2, solution);
		
		System.out.println("\nParents");
		
		System.out.println(parents[0].object() + "  :" + parents[0].info(Goal.class.getName()));
		
		System.out.println(parents[1].object() + "  :" + parents[1].info(Goal.class.getName()));

		//Applying XOver
		ga.epimarks.XOver xover = new ga.epimarks.XOver();
		MarkedBitArray[] children = xover.apply(parents[0].object(), parents[1].object());
		
		System.out.println("\nXover children");
	
		Solution<MarkedBitArray>[] s =  new Solution[2];
		s[0] = new Solution<MarkedBitArray>(children[0], goal);
		s[1] = new Solution<MarkedBitArray>(children[1], goal);
		
		System.out.println(children[0] + "  :" + s[0].info(Goal.class.getName()));
		
		System.out.println(children[1] + "  :" + s[1].info(Goal.class.getName()));
		
		//Applying Mutation
		ga.epimarks.BitMutation mutation = new ga.epimarks.BitMutation();
		children[0] = mutation.apply(children[0]);
		children[1] = mutation.apply(children[1]);
		
		Solution<MarkedBitArray>[] s2 =  new Solution[2];
		s2[0] = new Solution<MarkedBitArray>(children[0], goal);
		s2[1] = new Solution<MarkedBitArray>(children[1], goal);
		
		System.out.println("\nMutation children");
		
		System.out.println(children[0] + "  :" + s2[0].info(Goal.class.getName()));
		
		System.out.println(children[1] + "  :" + s2[1].info(Goal.class.getName()));
	}
}
