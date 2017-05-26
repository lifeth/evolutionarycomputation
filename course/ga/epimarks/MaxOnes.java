package ga.epimarks;

import unalcol.optimization.OptimizationFunction;

public class MaxOnes extends OptimizationFunction<MarkedBitArray> {

	/**
	 * Evaluate the max ones OptimizationFunction function over the binary array
	 * given
	 * 
	 * @param x
	 *            Binary Array to be evaluated
	 * @return the OptimizationFunction function over the binary array
	 */
	@Override
	public Double apply(MarkedBitArray x) {
		double f = 0.0;
		for (int i = 0; i < x.size(); i++) {
			
			if( (x.get(i) && !x.getMark(i)) || (!x.get(i) && x.getMark(i))){
				f++;		       
			}
		}
		return f;
	}

}
