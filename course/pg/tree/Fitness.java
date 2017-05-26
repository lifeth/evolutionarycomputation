package pg.tree;

import unalcol.optimization.OptimizationFunction;

public class Fitness extends OptimizationFunction<Node>{

	@Override
	public Double apply(Node node) {
		double f = 0;
		
		for (int i = 0; i < TreeTest.samples.length; i++) {
			int sample[] = TreeTest.samples[i];
			int result = node.evaluate(sample[0], sample[1]);
			f+= Math.abs(result - sample[2]);
		}
		
		return f;
	}

}
