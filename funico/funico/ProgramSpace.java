package funico;

import funico.interpreter.Equation;
import funico.interpreter.Program;
import unalcol.random.Random;
import unalcol.random.integer.IntUniform;
import unalcol.search.space.Space;

public class ProgramSpace extends Space<Program> {

	private int maxNumberEqs;

	public ProgramSpace(int ne) {
		this.maxNumberEqs = ne;
	}

	@Override
	public boolean feasible(Program x) {
		return true;
	}

	@Override
	public double feasibility(Program x) {
		return 1;
	}

	@Override
	public Program repair(Program x) {

		Equation e = x.getListEquations().get(x.getEqPos());
		e.repair();

		return x;
	}

	public int getNumberEquations() {
		return new IntUniform(1, this.maxNumberEqs + 1).generate();
	}

	public String generateEquations() {

		StringBuilder sb = new StringBuilder();
		int ne = this.getNumberEquations();

		for (int i = 0; i < ne; i++) {
			sb.append(InduceProgram.extractor.generateRandomEquation(2, Random.nextDouble()));
			sb.append("; ");
		}

		return sb.toString();
	}

	@Override
	public Program pick() {
		try {

			return new Program(this.generateEquations());

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

}
