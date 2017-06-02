package funico;

import unalcol.math.logic.Predicate;
import unalcol.search.Goal;
import unalcol.search.population.IterativePopulationSearch;
import unalcol.search.population.Population;
import unalcol.search.population.PopulationSearch;
import unalcol.search.space.Space;

public class GPIterativePopulationSearch<T, R> extends IterativePopulationSearch<T, R> {

	public GPIterativePopulationSearch(PopulationSearch<T, R> step, Predicate<Population<T>> tC) {
		super(step, tC);
	}

	@Override
	public Population<T> apply(Population<T> pop, Space<T> space) {

		String gName = Goal.class.getName();
		while (terminationCondition.evaluate(pop)) {
			pop = step.apply(pop, space);
			
			for (int j = 0; j < pop.size(); j++) {
				 System.out.println("Equation " + j + ": " +
				 pop.get(j).object().toString() + ": " +
				 pop.get(j).info(gName));
			}
			
			// Stop condition
			if ((double) pop.get(0).info(gName) == 1 || InduceProgram.stop == true) {
				break;
			}
		}
		return pop;
	}

}
