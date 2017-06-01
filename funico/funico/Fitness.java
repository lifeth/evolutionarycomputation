package funico;

import funico.interpreter.Equation;
import funico.interpreter.Evaluator;
import funico.interpreter.Program;
import unalcol.optimization.OptimizationFunction;

public class Fitness extends OptimizationFunction<Program> {

	@Override
	public Double apply(Program program) {
		double f = 0;
		double totalExamples = InduceProgram.example.getListEquations().size();
		try {

			for (Equation eq : InduceProgram.example.getListEquations()) {

				f += (Evaluator.evalue(program.toString(), eq.getLhs().toString(), Evaluator.INITIAL_MAX_STEP, 100))
						.equals(eq.getRhs().toString()) ? 1 : 0;
			}
		} catch (Exception e) {
			f += 0;
		}

		f /= totalExamples;

		program.setCovering(f);

		return f;
	}
}
