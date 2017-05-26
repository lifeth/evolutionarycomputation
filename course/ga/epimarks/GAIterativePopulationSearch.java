package ga.epimarks;

import unalcol.algorithm.iterative.ForLoopCondition;
import unalcol.math.logic.Predicate;
import unalcol.search.population.IterativePopulationSearch;
import unalcol.search.population.Population;
import unalcol.search.population.PopulationSearch;
import unalcol.search.solution.Solution;
import unalcol.search.space.Space;

public class GAIterativePopulationSearch<T,R> extends IterativePopulationSearch<T, R>{

	public GAIterativePopulationSearch(PopulationSearch<T, R> step, Predicate<Population<T>> tC) {
		super(step, tC);
	}
	
	@Override
	public Population<T> apply(Population<T> pop, Space<T> space) {
    	//Tracer.trace(this, pop, step);
    	ForLoopCondition< Population<T> > it = (ForLoopCondition< Population<T> >) terminationCondition;
		Solution<T>[] bestSolutions = (Solution<T>[])tagged_array(it.getEnd());

        while( this.terminationCondition.evaluate(pop) ){
            pop = step.apply(pop, space);
            
			//Select the best of the population in the current it
            bestSolutions[it.getIter()] = pick(pop);
        	//Tracer.trace(this, pop, step);
        }
        
        //Best solutions by iteration
        return new Population<T>(bestSolutions);
	}

}
