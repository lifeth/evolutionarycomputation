package coevo;

import unalcol.search.solution.Solution;
import unalcol.types.collection.bitarray.BitArray;

public class Evaluate {

	public static double averagePredators = 0;
	public static double averagePray = 0;

	public static void calculateAverage(Solution<BitArray>[] pop, boolean specie) {
		double avg = 0;

		String s;

		for (Solution<BitArray> solution : pop) {
			s = "";
			for (int i = 0; i < solution.object().size(); i ++) {
				s += solution.object().get(i) ? 1 : 0;
			}
			int velocity = Integer.parseInt(s, 2);

			avg += velocity;
		}
		
		avg /= pop.length;

		if (specie)
			averagePredators = avg;
		else
			averagePray = avg;
	}
}
