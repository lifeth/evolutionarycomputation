package unalcol.search.selection;

import unalcol.random.integer.IntRoulette;
import unalcol.search.Goal;
import unalcol.search.solution.Solution;

public class Roulette<T> implements Selection<T> {

	public IntRoulette generator(Solution<T>[] x) {
		double[] p = new double[x.length];
		String gName = Goal.class.getName();
		double sum = 0;

		for (int i = 0; i < p.length; i++) {
			p[i] = (double) x[i].info(gName);
			sum += p[i];
		}

		for (int i = 0; i < p.length; i++) {
			p[i] /= sum;
		}

		return new IntRoulette(p);
	}

	@Override
	public int[] apply(int n, Solution<T>[] x) {
		
		IntRoulette g = generator(x);
		return  g.generate(n);
	}

	@Override
	public int choose_one(Solution<T>[] x) {
		IntRoulette g = generator(x);
		return g.next();
	}

}
