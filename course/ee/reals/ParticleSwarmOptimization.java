package ee.reals;

import unalcol.descriptors.Descriptors;
import unalcol.io.Write;
import unalcol.optimization.OptimizationFunction;
import unalcol.optimization.OptimizationGoal;
import unalcol.optimization.method.AdaptOperatorOptimizationFactory;
import unalcol.optimization.method.OptimizationFactory;
import unalcol.optimization.real.HyperCube;
import unalcol.optimization.real.mutation.IntensityMutation;
import unalcol.optimization.real.mutation.OneFifthRule;
import unalcol.optimization.real.mutation.PermutationPick;
import unalcol.optimization.real.mutation.PickComponents;
import unalcol.optimization.real.testbed.Rastrigin;
import unalcol.random.real.DoubleGenerator;
import unalcol.random.real.SimplestSymmetricPowerLawGenerator;
import unalcol.reflect.tag.TaggedObject;
import unalcol.search.Goal;
import unalcol.search.local.LocalSearch;
import unalcol.search.solution.SolutionDescriptors;
import unalcol.search.solution.SolutionWrite;
import unalcol.search.space.Space;
import unalcol.types.real.array.DoubleArray;
import unalcol.types.real.array.DoubleArrayPlainWrite;

public class ParticleSwarmOptimization {
	
	
	public static double real(){
		// Search Space definition
		int DIM = 10;
		double[] min = DoubleArray.create(DIM, -5.12);
		double[] max = DoubleArray.create(DIM, 5.12);
    	Space<double[]> space = new HyperCube( min, max );
    	
    	// Optimization Function
    	OptimizationFunction<double[]> function = new Rastrigin();		
        Goal<double[], Double> goal = new OptimizationGoal<double[]>(function); // minimizing, add the parameter false if maximizing   	
    	
    	// Variation definition
    	DoubleGenerator random = new SimplestSymmetricPowerLawGenerator(); // It can be set to Gaussian or other symmetric number generator (centered in zero)

    	//new StandardGaussianGenerator();
    	PickComponents pick = new PermutationPick(6); // It can be set to null if the mutation operator is applied to every component of the solution array
    	IntensityMutation variation = new IntensityMutation( 0.2, random, pick );
        
        // Search method
        int MAXITERS = 100000;
        boolean neutral = true; // Accepts movements when having same function value
        boolean adapt_operator = false; //
        LocalSearch<double[],Double> search;
        
        if( adapt_operator ){
        	OneFifthRule adapt = new OneFifthRule(100, 0.9); // One Fifth rule for adapting the mutation parameter
        	AdaptOperatorOptimizationFactory<double[],Double> factory = new AdaptOperatorOptimizationFactory<double[],Double>();
        	search = factory.hill_climbing( variation, adapt, neutral, MAXITERS );
        }else{
        	OptimizationFactory<double[]> factory = new OptimizationFactory<double[]>();
        	search = factory.hill_climbing( variation, neutral, MAXITERS );
        }
        // Tracking the goal evaluations
        SolutionDescriptors<double[]> desc = new SolutionDescriptors<double[]>();
        Descriptors.set(TaggedObject.class, desc);
        DoubleArrayPlainWrite write = new DoubleArrayPlainWrite(false);
        Write.set(double[].class, write);
        SolutionWrite<double[]> w_desc = new SolutionWrite<double[]>(true);
        Write.set(TaggedObject.class, w_desc);
        
        //ConsoleTracer tracer = new ConsoleTracer();       
        //Tracer.addTracer(goal, tracer);  // Uncomment if you want to trace the function evaluations
        //Tracer.addTracer(search, tracer); // Uncomment if you want to trace the hill-climbing algorithm
        
        // Apply the search method
        TaggedObject<double[]> solution = search.solve(space, goal);
        
        System.out.println(solution.info(Goal.class.getName()));	
        
        return (double) solution.info(Goal.class.getName());
	}
    
}
