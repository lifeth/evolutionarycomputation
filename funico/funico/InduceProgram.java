package funico;

import funico.interpreter.Example;
import funico.interpreter.Extractor;
import funico.interpreter.Program;
import unalcol.evolution.EAFactory;
import unalcol.optimization.OptimizationFunction;
import unalcol.optimization.OptimizationGoal;
import unalcol.search.Goal;
import unalcol.search.population.Population;
import unalcol.search.population.PopulationSearch;
import unalcol.search.selection.Roulette;
import unalcol.search.selection.Selection;
import unalcol.search.space.Space;
import unalcol.search.variation.Variation_1_1;
import unalcol.search.variation.Variation_2_2;

public class InduceProgram {

	public static Example example;
	public static Extractor extractor;
	public static int maxNumEquations;
	public static int numberTerms;
	public static Population<Program> inducedPrograms;
	public static String gName = Goal.class.getName();

	public InduceProgram() {
	}

	public InduceProgram(String examples, int neq, int terms) {
		init(examples, neq, terms);
	}

	public void init(String examples, int neq, int terms) {
		try {
			InduceProgram.extractor = new Extractor(examples);
			InduceProgram.example = new Example(examples);
			InduceProgram.maxNumEquations = neq;
			InduceProgram.numberTerms = terms;

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean evolve() {

		int popzize = 500;
		int iterations = 100;

		// Search Space definition
		Space<Program> space = new ProgramSpace(InduceProgram.maxNumEquations);

		// Optimization Function
		OptimizationFunction<Program> function = new Fitness();
		Goal<Program, Double> goal = new OptimizationGoal<Program>(function, false);
		// Variation definition
		Variation_1_1<Program> mutation = new ProgramMutation();
		Variation_2_2<Program> xover = new ProgramXOver();

		// Parent selection
		// Selection<Program> pselection = new Tournament<Program>(4);
		Selection<Program> pselection = new Roulette<Program>();

		// Search method
		EAFactory<Program> factory = new EAFactory<Program>();
		PopulationSearch<Program, Double> search = factory.geneticProgramming(popzize, pselection, mutation, xover, 1.0,
				iterations);

		// Apply the search method
		InduceProgram.inducedPrograms = search.solve(space, goal, null);

		return InduceProgram.inducedPrograms.get(0).object().getCovering() == 1;
	}
}
