package coevo;

import unalcol.optimization.OptimizationFunction;
import unalcol.types.collection.bitarray.BitArray;

public class PrayFitness extends OptimizationFunction<BitArray> {
	
	@Override
	public Double apply(BitArray x) {
		double f = 0;
		String s = "";
		
		for (int i = 0; i < x.size(); i++) {
			s += x.get(i) ? 1 : 0;
		}
		
		int velocity = Integer.parseInt(s, 2);
	
		f = Math.abs(velocity - Evaluate.averagePredators);
		
		return f;
	}

}
