/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package funico.interpreter;

import funico.InduceProgram;
import funico.SetTheory;
import funico.language.FunicoConstants;
import funico.language.LexicalException;
import funico.language.Parser;
import funico.language.SyntacticalException;
import funico.language.Term;
import unalcol.random.Random;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

/**
 *
 * @author Camiku
 */
public final class Equation implements funico.language.FunicoConstants {

	private Term root;
	private int maxLength = 0;

	public Equation(Term term) {
		this.root = term;
		maxLength = toString().length() - 2;
	}

	public Equation(Equation e) {
		this(e.getRoot().clone());
	}

	public Equation(Term lhs, Term rhs) {
		this.root = new Term("equal", EQUAL, null);
		this.root.addChild(lhs);
		this.root.addChild(rhs);
		maxLength = toString().length() - 2;
	}

	public Equation(String text) throws LexicalException, SyntacticalException, ExampleException {
		this(Parser.parsing(text).getFirst());
	}

	public Term getRoot() {
		return root;
	}

	public void setRoot(Term root) {
		this.root = root;
	}

	/**
	 * Get the value of lhs
	 *
	 * @return the value of lhs
	 */
	public Term getLhs() {
		return this.getRoot().getChild(0);
	}

	/**
	 * Set the value of lhs
	 *
	 * @param lhs
	 *            new value of lhs
	 */
	public void setLhs(Term lhs) {
		this.getRoot().setChild(0, lhs);
	}

	/**
	 * Get the value of rhs
	 *
	 * @return the value of rhs
	 */
	public Term getRhs() {
		return this.getRoot().getChild(1);
	}

	/**
	 * Set the value of rhs
	 *
	 * @param rhs
	 *            new value of rhs
	 */
	public void setRhs(Term rhs) {
		this.getRoot().setChild(1, rhs);
	}

	@Override
	public Equation clone() {
		return new Equation(this);
	}

	@Override
	public String toString() {
		return getRoot().printSyntacticSugar();
	}

	public String print() {
		return getLhs().print() + " = " + getRhs().print();
	}

	public String printWithOutSyntacticalSugar() {
		return getRoot().print();
	}

	public Equation getVariant() {
		return new Equation(this.getRoot().getVariant());
	}

	private int calculateMaxLength() {
		maxLength = getRoot().toString().length() - 2;
		return maxLength;
	}

	public int getMaxLength() {
		return maxLength;
	}

	public Equation renameVar() {
		this.getRoot().renameVar();
		calculateMaxLength();
		return this;
	}

	public int getNumberNodes() {
		return this.getRoot().getNumberNodes();
	}

	public boolean isBasicEquation() {
		return isBasicEquation(getRhs());
	}

	private boolean isBasicEquation(Term term) {
		switch (term.getType()) {
		case FUNCTOR:
			return false;
		case SUCCESSOR:
			return isBasicEquation(term.getChild(0));
		case LIST:
			return isBasicEquation(term.getChild(0)) && isBasicEquation(term.getChild(1));
		case VARIABLE:
		case NULL:
		case NATURAL:
		case TRUE:
		case FALSE:
		case CONSTANT:
		case UNDEFINED:
			return true;
		default:
			System.out.println("Equation neither simple nor composed");
			return true;
		}
	}

	public boolean isValid() {
		return this.getRoot().isEquationProgramTerm();
	}

	public LinkedList<Term> getListMappings() {
		return getListMappings(this);
	}

	public static LinkedList<Term> getListMappings(Equation equa) {
		return getListMappings(equa.getRoot(), equa.getLhs(), null);
	}

	private static LinkedList<Term> getListMappings(Term term, Term mainFunctor, LinkedList<Term> list) {
		switch (term.getType()) {
		case VARIABLE:
		case NULL:
		case TRUE:
		case FALSE:
		case NATURAL:
		case CONSTANT:
		case UNDEFINED:
			return list;
		case SUCCESSOR:
		case EQUAL_SYMBOL:
		case EQUAL:
		case LIST:
		case FUNCTOR:
			if (term.getType() == FUNCTOR && term != mainFunctor) {
				if (list == null) {
					list = new LinkedList<Term>();
				}
				list.add(term);
			}
			for (Iterator<Term> it = term.getListChildren().iterator(); it.hasNext();) {
				list = getListMappings(it.next(), mainFunctor, list);
			}
			return list;
		default:
			System.out.println("Term type no found");
			return null;
		}
	}

	public ArrayList<Term> getListFunctorNArity() {
		return getListFunctorNArity(this.getRoot(), null);
	}

	public static ArrayList<Term> getListFunctorNArity(Equation equa) {
		return getListFunctorNArity(equa.getRoot(), null);
	}

	private static ArrayList<Term> getListFunctorNArity(Term term, ArrayList<Term> listF) {
		if (term.getArity() > 0 && term.getType() != EQUAL && term.getType() != EQUAL_SYMBOL) {
			if (listF == null) {
				listF = new ArrayList<Term>();
			}
			listF.add(term);
		}
		if (term.getListChildren() != null) {
			for (Iterator<Term> it = term.getListChildren().iterator(); it.hasNext();) {
				listF = getListFunctorNArity(it.next(), listF);
			}
		}
		return listF;
	}

	public LinkedList<Term> getListSubTermsWithoutEqualAndLhs() {
		LinkedList<Term> list = getLhs().getListSubTerms();
		getRhs().getListSubTerms(list);
		list.remove(getLhs());
		return list;
	}

	public static ArrayList<Term> getListMappingArityOne(Equation equa) {
		return getListMappingArityOne(equa.getRoot(), null);
	}

	private static ArrayList<Term> getListMappingArityOne(Term term, ArrayList<Term> list) {
		switch (term.getType()) {
		case VARIABLE:
		case NULL:
		case TRUE:
		case FALSE:
		case NATURAL:
		case CONSTANT:
		case UNDEFINED:
			return list;
		case EQUAL_SYMBOL:
		case EQUAL:
		case LIST:
		case SUCCESSOR:
		case FUNCTOR:
			if (term.getArity() == 1 && term.getParent() != null && term.getParent().getArity() > 1
					&& term.getParent().getType() != EQUAL_SYMBOL && term.getParent().getType() != EQUAL) {
				if (list == null) {
					list = new ArrayList<Term>();
				}
				list.add(term);
			}
			for (Iterator<Term> it = term.getListChildren().iterator(); it.hasNext();) {
				list = getListMappingArityOne(it.next(), list);
			}
			return list;
		default:
			System.out.println("Term type no found");
			return null;
		}
	}

	public Equation repair() {
		try {
			
			this.repairLength();

			if (!InduceProgram.extractor.getTableMainFunctors().contains(this.getLhs().getValue())) {
				this.setLhs(InduceProgram.extractor.generateRandomEquation(2, Random.nextDouble()).getLhs());
			}

			this.repairSuccessor(this.getRoot());

			this.repairRight();

			while (!getLhs().hasVar() && getRhs().hasVar()) {
				getLhs().changeConstantsBySetVar(getRhs().getSetVars());
			}
			if (!SetTheory.isSubSet(getRhs().getSetVars(), getLhs().getSetVars())) {
				getRhs().changeVarByVar(getLhs().getSetVars());
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return this;
	}

	public void repairSuccessor(Term term) {

		switch (term.getType()) {
		case EQUAL:
		case EQUAL_SYMBOL:
			repairSuccessor(term.getListChildren().getFirst());
			repairSuccessor(term.getListChildren().getLast());
			break;

		case FUNCTOR:

			if (term.getValue().equals("s")) {
				// Successor can be a functor, but not the main functor
				repairSuccessor(term.getListChildren().getFirst());

			} else if (InduceProgram.extractor.getTableMainFunctors().contains(term.getValue())
					&& term.getParent().getValue().equals("s")) {
			//	modifyChildren(term, 1);
			} else {
				repairSuccessor(term.getListChildren().getFirst());
				repairSuccessor(term.getListChildren().getLast());
			}

			break;
		case SUCCESSOR:
			repairSuccessor(term.getListChildren().getFirst());
			break;
		case TRUE:
		case FALSE:
			if (term.getParent().getType() == FunicoConstants.SUCCESSOR || term.getParent().getValue().equals("s")) {

				modifyChildren(term, 1);
			}
			break;
		default:
			break;
		}
	}

	public void repairRight() {
		Term resultType = InduceProgram.example.getListEquations().getFirst().getRhs();

		switch (this.getRhs().getType()) {
		case FUNCTOR:
			if (this.getRhs().getValue().equals("s")) {
				if (resultType.getType() != NATURAL && resultType.getType() != SUCCESSOR) {
					modifyChildren(this.getRhs(), Random.nextBool() ? 4 : 3);
				}
			}
			break;
		case NATURAL:
		case SUCCESSOR:
			if (resultType.getType() != NATURAL && resultType.getType() != SUCCESSOR) {
				modifyChildren(this.getRhs(), Random.nextBool() ? 3 : 4);
			}

			break;
		case TRUE:
		case FALSE:
			if (resultType.getType() != TRUE && resultType.getType() != FALSE) {
				modifyChildren(this.getRhs(), Random.nextBool() ? 1 : 2);
			}

			break;

		default:
			break;
		}
	}

	public int getTypeAllowed() {

		Term left = InduceProgram.example.getListEquations().getFirst().getLhs().getListChildren().getFirst();
		Term resultType = InduceProgram.example.getListEquations().getFirst().getRhs();

		switch (left.getType()) {

		case NATURAL:
		case SUCCESSOR:
		case VARIABLE:
			if (resultType.getType() == NATURAL || resultType.getType() == SUCCESSOR) {
				// Allows the same main functor which returns a number, inside
				return 1 + 1;
			}
			// params are numbers/variables but the result of the main functor
			// is not numeric/variable
			return 1;

		case TRUE:
		case FALSE:
			if (resultType.getType() == TRUE || resultType.getType() == FALSE) {
				// Allows the same main functor which returns a boolean, inside
				return 3 + 1;
			}

			// params are booleans but the result of the main functor is not
			// boolean
			return 3;

		default:
			return 0;
		}
	}

	public void repairMainFunctor(Term term) {

		int type = this.getTypeAllowed();

		switch (term.getType()) {
		case EQUAL:
		case EQUAL_SYMBOL:
			repairMainFunctor(term.getListChildren().getFirst());
			repairMainFunctor(term.getListChildren().getLast());
			break;

		case FUNCTOR:

			if (InduceProgram.extractor.getTableMainFunctors().contains(term.getParent().getValue())) {
				if ((type == 1 && !term.getValue().equals("s")) || type == 3) {
					modifyChildren(term, type);
				}

			} else if (InduceProgram.extractor.getTableMainFunctors().contains(term.getValue())) {
				repairMainFunctor(term.getListChildren().getFirst());
				repairMainFunctor(term.getListChildren().getLast());
			}

			break;

		case SUCCESSOR:
		case NATURAL:
		case VARIABLE:
			if (type != 1) {
				modifyChildren(term, 3);
			}
			break;

		case TRUE:
		case FALSE:
			if (InduceProgram.extractor.getTableMainFunctors().contains(term.getParent().getValue()) && type != 3) {
				modifyChildren(term, 1);
			}
			break;
		default:
			break;
		}
	}

	public void modifyChildren(Term term, int type) {
		Term newTerm = null;
		int index = term.getParent().getListChildren().indexOf(term);

		switch (type) {
		case 1:// Numeric or Variable

			String terminal = SetTheory.getRandomElementSet(InduceProgram.extractor.getTableTerminals());

			if (InduceProgram.extractor.getTableTerminalsWithType().get(terminal) == NATURAL) {
				newTerm = SetTheory.generateTreeNumber(Integer.parseInt(terminal));
				newTerm.setParent(term.getParent());
			} else {
				terminal = SetTheory.getRandomElementSet(InduceProgram.extractor.getTableVariables());
				newTerm = new Term(terminal, VARIABLE, term.getParent());
			}
			break;
		case 3:// Boolean

			boolean t = Random.nextBool();
			newTerm = new Term("" + t, (t ? TRUE : FALSE), term.getParent());
			break;

		case 2:// Main Functor Numeric/Variable
		case 4:// Main Functor Boolean

			newTerm = new Term(SetTheory.getRandomElementSet(InduceProgram.extractor.getTableMainFunctors()), FUNCTOR,
					term.getParent());

			int arity = InduceProgram.extractor.getTableFunctors().get(newTerm.getValue());
			for (int i = 0; i < arity; i++) {
				if (type == 4) {
					t = Random.nextBool();
					newTerm.addChild(new Term("" + t, (t ? TRUE : FALSE), newTerm));
				} else {
					terminal = SetTheory.getRandomElementSet(InduceProgram.extractor.getTableTerminals());

					if (InduceProgram.extractor.getTableTerminalsWithType().get(terminal) == NATURAL) {
						Term te = SetTheory.generateTreeNumber(Integer.parseInt(terminal));
						te.setParent(newTerm);
						newTerm.addChild(te);
					} else {
						terminal = SetTheory.getRandomElementSet(InduceProgram.extractor.getTableVariables());
						newTerm.addChild(new Term(terminal, VARIABLE, newTerm));
					}
				}
			}

		default:
			break;
		}

		term.getParent().setChild(index, newTerm);
	}
	
	public void repairLength(){
		if(this.getRoot().getNumberNodes() > InduceProgram.numberTerms){
			this.setRoot(InduceProgram.extractor.generateRandomEquation(2, Random.nextDouble()).getRoot());
		}
	}

	public int calculateFUNICO(HashSet<String> hashCommutativeOperators) {
		return getRoot().calculateFUNICO(hashCommutativeOperators);
	}

}
