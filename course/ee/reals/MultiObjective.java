
import org.moeaframework.algorithm.SPEA2;
import org.moeaframework.core.Initialization;
import org.moeaframework.core.Population;
import org.moeaframework.core.Solution;
import org.moeaframework.core.Variation;
import org.moeaframework.core.operator.RandomInitialization;
import org.moeaframework.core.spi.OperatorFactory;
import org.moeaframework.core.variable.RealVariable;
import org.moeaframework.problem.AbstractProblem;
import org.moeaframework.util.TypedProperties;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author eduarc
 */
public class MultiObjective extends AbstractProblem {

    public static final double LOWER_BOUND = -5;
    public static final double UPPER_BOUND = +5;
    public static final double A = 10;

    public MultiObjective() {
        super(10, 2);
    }

    @Override
    public void evaluate(Solution solution) {

        rastrigin(solution);
        styblinski_tang(solution);
    }

    public double rastrigin(Solution sol) {

        double y = A * numberOfVariables;

        for (int i = 0; i < numberOfVariables; i++) {
            RealVariable v = (RealVariable) sol.getVariable(i);
            double x = v.getValue();
            y += x * x - A * Math.cos(2 * Math.PI * x);
        }
        sol.setObjective(0, y);
        return y;
    }

    public double styblinski_tang(Solution sol) {

        double y = 0;

        for (int i = 0; i < numberOfVariables; i++) {
            RealVariable v = (RealVariable) sol.getVariable(i);
            double x = v.getValue();
            y += x * x * x * x - 16 * x * x + 5 * x;
        }
        sol.setObjective(1, y / 2.0);
        return y;
    }

    @Override
    public Solution newSolution() {

        Solution sol = new Solution(numberOfVariables, 2);

        for (int i = 0; i < numberOfVariables; i++) {
            RealVariable v = new RealVariable(LOWER_BOUND, UPPER_BOUND);
            v.randomize();
            sol.setVariable(i, v);
        }
        return sol;
    }

    public static void main(String args[]) {

        MultiObjective problem = new MultiObjective();
        TypedProperties prop = new TypedProperties();
        prop.setInt("populationSize", 100);
        prop.setInt("offspringSize", 100);
        prop.setInt("k", 1);
        int populationSize = (int) prop.getDouble("populationSize", 100);
        int offspringSize = (int) prop.getDouble("offspringSize", 100);
        int k = (int) prop.getDouble("k", 1);

        Initialization initialization = new RandomInitialization(problem, populationSize);
        Solution[] pop = initialization.initialize();
        Population popu = new Population(pop);

        for (Solution s : popu) {
            problem.evaluate(s);
        }
        Variation variation = OperatorFactory.getInstance().getVariation(null, prop, problem);
        SPEA2 sol = new SPEA2(problem, initialization, variation, offspringSize, k);
        int iters = 10000;

        for (int i = 0; i < iters; i++) {
            sol.step();
        }
        Population result = sol.getPopulation();
        System.out.println("Objective 1\tObjective 2");

        for (Solution solution : result) {
            System.out.format("%.4f\t%.4f%n", solution.getObjective(0), solution.getObjective(1));
        }
        System.out.println("NON DOMINATED");
        result = sol.getResult();
        System.out.println("Objective 1\tObjective 2");

        for (Solution solution : result) {
            System.out.format("%.4f\t%.4f%n", solution.getObjective(0), solution.getObjective(1));
        }
        for (Solution solution : result) {
            for (int i = 0; i < solution.getNumberOfVariables(); i++) {
                RealVariable v = (RealVariable) solution.getVariable(i);
                System.out.format("%4f ", v.getValue());
            }
            System.out.println();
        }
    }
}
