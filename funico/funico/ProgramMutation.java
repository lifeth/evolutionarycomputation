package funico;

import java.util.ArrayList;

import funico.interpreter.Equation;
import funico.interpreter.Program;
import funico.language.Term;
import unalcol.random.Random;
import unalcol.search.variation.Variation_1_1;

public class ProgramMutation extends Variation_1_1<Program> {

	@Override
	public Program apply(Program gen) {
		gen = gen.clone();
		
		Object[] term = this.getRandomSubtree(gen);
		Term te = generateSubTree();
		
		logging("New subtree: "+te.print());
		logging("Before: " + gen);
		
		gen.setEqPos((int) term[0]);
		
		Term node = (Term) term[1];
		te.setParent(node.getParent());
		int index = node.getParent().getListChildren().indexOf(node);
		node.getParent().setChild(index, te);
		logging("After: " + gen);
		
		//gen.getListEquations().get((int) term[0]).repair();
		
		//logging("After repair: " + gen);
		
		return gen;
	}

	public Term generateSubTree() {

		return InduceProgram.extractor.generateRandomTerm(2, Random.nextDouble());
	}

	public Object[] getRandomSubtree(Program p) {
		int e = (int) (Math.random() * p.getListEquations().size());
		Equation equation = p.getListEquations().get(e);
		ArrayList<Term> subtrees = equation.getListFunctorNArity();
		return new Object[] { e, subtrees.get((int) (Math.random() * subtrees.size())) };
	}
	
	public void logging(String msg){
		//System.out.println(msg);
	}

	public static void main(String[] args) throws Exception {
		//String examples = "geq(0,1) = false; geq(0,0) = true; geq(1,0) = true; geq(1,1) = true; geq(1,2) = false; geq(2,1) = true; geq(2,5) = false; geq(5,2) = true; geq(3,3) = true";
		String examples = "min(0,0) = 0;  min(0,1) = 0; min(1,0) = 0; min(1,1) = 1; min(5,2) = 2; min(2,5) = 2; min(3,3) = 3; min(4,1) = 1; min(1,4) = 1";
		new InduceProgram(examples, 3, 10);
		new ProgramMutation().apply(new Program("min(s(s(min(s(0),min(min(A,A),s(B))))),2) = min(s(0), A)"
				));//"geq(A,s(s(s(geq(s(1),s(5)))))) = g(A,A); geq(s(A),s(B)) = geq(A,s(B)); geq(s(A),B) = true"
	}

}
