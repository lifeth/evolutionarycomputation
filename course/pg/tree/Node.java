package pg.tree;

import unalcol.random.Random;
import unalcol.random.integer.IntUniform;

public class Node {
	Node left;
	String operator;
	Node right;
	Node parent;
	static String var[] = { "X", "Y", "N" };

	public Node(Node parent, int h) {
		this.parent = parent;

		if (h == 0) {
			int x = (int) (Math.random() * var.length);
			this.operator = var[x].charAt(0) == 'N' ? "" + new IntUniform(-9, 9).generate() : var[x]; // variable
																										// or
																										// number
			left = right = null;
		} else {
			this.operator = Random.nextBool() ? "+" : "*"; // + or *;
			this.left = new Node(this, h - 1);
			this.right = new Node(this, h - 1);
		}
	}

	public Node(Node parent, String operator) {
		this.operator = operator;
		this.left = this.right = null;
		this.parent = parent;
	}

	public Node(Node parent, String operator, Node left, Node right) {
		this.operator = operator;
		this.left = left;
		this.right = right;
		this.parent = parent;
	}

	public boolean isLeaf() {
		return this.left == null;
	}

	public Node clone(Node parent) {
		Node n = new Node(parent, operator);

		if (!this.isLeaf()) {
			n.left = left.clone(n);
			n.right = right.clone(n);
		}

		return n;
	}

	public int weigth() {

		if (isLeaf())
			return 1;

		return 1 + left.weigth() + right.weigth();
	}

	public Node get(int index) {
		if (index == 0)
			return this;

		index--;

		if (this.isLeaf())
			return this;

		int wLeft = left.weigth();
		if (index < wLeft) {
			return left.get(index);
		} else {
			return right.get(index);
		}
	}

	public int evaluate(int x, int y) {

		switch (operator.charAt(0)) {
		case '*':
			//TODO no se debe multiplicar si no tiene hijos, usar reparacion del arbol al mutar y cruzar para evitarlo
			return this.isLeaf() ? (x * y) : (left.evaluate(x, y) * right.evaluate(x, y));

		case '+':
			//TODO
			return this.isLeaf() ? (x * y) : (left.evaluate(x, y) + right.evaluate(x, y));

		case 'X':
			return x;

		case 'Y':
			return y;
		default:
			return Integer.parseInt(operator);
		}
	}

	public void printExpression() {
		print(this);
	}
	
	public void print(Node node){
		
		if(node != null){
			 
			if (!(Character.isLetterOrDigit(node.operator.charAt(0)) || node.operator.charAt(0) == '-')) {
				System.out.print("(");
				print(node.left);
			}
	
			System.out.print(node.operator);
	
			if (!(Character.isLetterOrDigit(node.operator.charAt(0)) || node.operator.charAt(0) == '-')) {
				print(node.right);
				System.out.print(")");
			}
		}
	}
}