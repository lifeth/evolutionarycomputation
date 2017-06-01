package funico;

import java.util.ArrayList;

import funico.interpreter.Equation;
import funico.interpreter.Program;
import funico.language.Term;
import unalcol.search.variation.Variation_2_2;

public class ProgramXOver extends Variation_2_2<Program> {

	@Override
	public Program[] apply(Program gen1, Program gen2) {

		return xoverSubtrees(gen1, gen2);
	}

	public Program[] xoverSubtrees(Program gen1, Program gen2) {

		Program child1_1 = gen1.clone();
		Program child2_1 = gen2.clone();
		Program child1_2 = gen1.clone();
		Program child2_2 = gen2.clone();

		Object[] term1 = getRandomSubtree(child1_1, child1_2);
		Object[] term2 = getRandomSubtree(child2_1, child2_2);

		Term term_2_1 = (Term) term2[1];

		logging("Before: " + child1_2);
		Term node = (Term) term1[2];
		logging("Changed by: " + term_2_1);
		node.setValue(term_2_1.getValue());
		node.setType(term_2_1.getType());
		node.setListChildren(term_2_1.getListChildren());
		child1_2.setEqPos((int) term1[0]);
		logging("After: " + child1_2);

		Term term_1_1 = (Term) term1[1];

		logging("Before: " + child2_2);
		node = (Term) term2[2];
		logging("Changed by: " + term_1_1);
		node.setValue(term_1_1.getValue());
		node.setType(term_1_1.getType());
		node.setListChildren(term_1_1.getListChildren());
		child2_2.setEqPos((int) term2[0]);
		logging("After: " + child2_2);

		return new Program[] { child1_2, child2_2 };
	}

	public Object[] getRandomSubtree(Program p1, Program p2) {
		int e = (int) (Math.random() * p1.getListEquations().size());
		Equation eq1 = p1.getListEquations().get(e);
		Equation eq2 = p2.getListEquations().get(e);
		ArrayList<Term> list1 = eq1.getListFunctorNArity();
		int i = list1.size();
		
		i = (int) (Math.random() * i);
		
		return new Object[] { e, list1.get(i), eq2.getListFunctorNArity().get(i) };
	}

	public void logging(String msg) {
		//System.out.println(msg);
	}

	public static void main(String[] args) throws Exception {
		String examples = "geq(0,1) = false; geq(0,0) = true; geq(1,0) = true; geq(1,1) = true; geq(1,2) = false; geq(2,1) = true; geq(2,5) = false; geq(5,2) = true; geq(3,3) = true";
		new InduceProgram(examples, 3, 10);

		new ProgramXOver().apply(
				new Program("geq(A,s(A)) = false; geq(s(s(A)),s(B)) = false; geq(A,B) = true"), new Program(
						"geq(s(s(A)),A) = geq(A,s(A)); geq(s(A),s(B)) = geq(A,s(B)); geq(s(A),B) = true"));
	}
}
