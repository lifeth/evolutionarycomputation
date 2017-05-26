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
		// Tracer.trace(this, pop, step);
		String gName = Goal.class.getName();
		while (terminationCondition.evaluate(pop)) {
			pop = step.apply(pop, space);

			// Stop condition
			if ((double) pop.get(0).info(gName) == 1) {
				break;
			}
			// Tracer.trace(this, pop, step); Double
		}
		return pop;
	}

}
