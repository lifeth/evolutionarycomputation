package ga.epimarks;

import java.util.HashMap;
import java.util.Map;

import unalcol.optimization.OptimizationFunction;

public class Deceptive extends OptimizationFunction<MarkedBitArray> {

	protected static Map<String, Integer> order = new HashMap<>();

	public Deceptive() {
		/*
		 * String Value String Value 
		 * 000 28 		100 14 
		 * 001 26 		101 0 
		 * 010 22 		110 0 
		 * 011	0 		111 30 
		 * The Order-Three deceptive problem
		 */
		Deceptive.order.put("000", 28);
		Deceptive.order.put("001", 26);
		Deceptive.order.put("010", 22);
		Deceptive.order.put("011", 0);
		Deceptive.order.put("100", 14);
		Deceptive.order.put("101", 0);
		Deceptive.order.put("110", 0);
		Deceptive.order.put("111", 30);
	}

	public int getValue(MarkedBitArray x, int pos) {

		if (x.get(pos) && x.getMark(pos))
			return 0;

		if (x.get(pos) && !x.getMark(pos))
			return 1;

		if (!x.get(pos) && x.getMark(pos))
			return 1;

		return 0;
	}

	@Override
	public Double apply(MarkedBitArray x) {
		double f = 0.0;

		for (int i = 0; i < x.size(); i += 3) {
			 f += order.get(getValue(x, i) + "" + getValue(x, i + 1) + "" + getValue(x, i + 2));
			f += order.get((x.get(i) ? 1 : 0) + "" + (x.get(i + 1) ? 1 : 0) + "" + (x.get(i + 2) ? 1 : 0));
		}
		return f;
	}
}
