package pg.tree;

import unalcol.search.variation.Variation_2_2;

public class NodeXOver extends Variation_2_2<Node> {

	@Override
	public Node[] apply(Node gen1, Node gen2) {
		int n1, n2, m1, m2;
		Node child1_1 = gen1.clone(null);
		Node child2_1 = gen2.clone(null);
		Node child1_2 = gen1.clone(null);
		Node child2_2 = gen2.clone(null);

		n1 = child1_1.weigth();
		n2 = child2_1.weigth();

		m1 = (int) (Math.random() * n1);
		m2 = (int) (Math.random() * n2);

		Node node1_1 = child1_1.get(m1);
		Node node2_1= child2_1.get(m2);

		n1 = child1_2.weigth();
		n2 = child2_2.weigth();

		m1 = (int) (Math.random() * n1);
		m2 = (int) (Math.random() * n2);

		Node node1_2 = child1_2.get(m1);
		Node node2_2 = child2_2.get(m2);

		node1_2.left = node2_1.left;
		node1_2.right = node2_1.right;
		node1_2.operator = node2_1.operator;
		
		node2_2.left = node1_1.left;
		node2_2.right = node1_1.right;
		node2_2.operator = node1_1.operator;
		
		return new Node[] { child1_2, child2_2 };
	}
}
