package pg.tree;

import unalcol.search.variation.Variation_1_1;

public class NodeMutation extends Variation_1_1<Node>{
	
	public String[] codes = new String[]{"-9", "-8", "-7", "-6", "-5", "-4", "-3", "-2", "-1", "0", "1","2","3", "4","5", "6", "7", "8", "9", "X", "Y","+", "*"};
	
	@Override
	public Node apply(Node gen){
		gen = gen.clone(null);
		int n = gen.weigth();
		int m = (int)(Math.random()*n);
		Node mutate = gen.get(m);
		int x = (int)(Math.random()*codes.length);
		mutate.operator = codes[x];
		
		return gen;
	}
	
}
