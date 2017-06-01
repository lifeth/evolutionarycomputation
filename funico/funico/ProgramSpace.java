package funico;

import funico.interpreter.Equation;
import funico.interpreter.Program;
import unalcol.random.Random;
import unalcol.random.integer.IntUniform;
import unalcol.search.population.Population;
import unalcol.search.space.Space;

public class ProgramSpace extends Space<Program> {

	private int maxNumberEqs;
	private Population<Program> programs;

	public ProgramSpace(int ne, Population<Program> inducedPrograms) {
		this.maxNumberEqs = ne;
		this.programs = inducedPrograms;
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
	public Program[] pick(int n) {

		Program[] v = (Program[]) new Program[n];

		if (this.programs != null) {

			for (int i = 0; i < n ; i++)
				v[i] = this.programs.get(i).object();

			/*for (int i = n / 2; i < n; i++)
				v[i] = pick();*/

			return v;
		}

		for (int i = 0; i < n; i++) {
			v[i] = pick();
		}

		return v;
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
