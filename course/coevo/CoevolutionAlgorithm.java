package coevo;

import java.io.FileWriter;

import unalcol.optimization.OptimizationFunction;
import unalcol.optimization.OptimizationGoal;
import unalcol.optimization.binary.BinarySpace;
import unalcol.optimization.binary.BitMutation;
import unalcol.optimization.binary.XOver;
import unalcol.search.Goal;
import unalcol.search.selection.Elitism;
import unalcol.search.selection.Selection;
import unalcol.search.selection.Tournament;
import unalcol.search.solution.Solution;
import unalcol.search.space.Space;
import unalcol.types.collection.bitarray.BitArray;
import unalcol.types.real.array.DoubleArray;

public class CoevolutionAlgorithm {
	public static int RUN = 0;
	public static int MAXITERS = 10000;
	public static int DIM = 10;
	public static int POPSIZE = 30;
	public static double prayBestByIte[][] = new double[30][MAXITERS];
	public static double predatorBestByIte[][] = new double[30][MAXITERS];
	public static double bestByIte[][];
	public static String gName = Goal.class.getName();


	public void evolve() {

		// Search Space definition
		Space<BitArray> space = new BinarySpace(DIM);

		// Optimization Function Predator
		OptimizationFunction<BitArray> functionPredator = new PredatorFitness();
		Goal<BitArray, Double> goalPredator = new OptimizationGoal<BitArray>(functionPredator, true);
		BitArray[] predators = new BitArray[POPSIZE];

		// Optimization Function Pray
		OptimizationFunction<BitArray> functionPray = new PrayFitness();
		Goal<BitArray, Double> goalPray = new OptimizationGoal<BitArray>(functionPray, false);
		BitArray[] pray = new BitArray[POPSIZE];

		// Initial Population
		for (int i = 0; i < POPSIZE; i++) {
			predators[i] = space.pick();
			pray[i] = space.pick();
		}

		@SuppressWarnings("unchecked")
		Solution<BitArray>[] solutionPredator = new Solution[predators.length];
		@SuppressWarnings("unchecked")
		Solution<BitArray>[] solutionPray = new Solution[pray.length];

		// Aptitude Calculation
		for (int j = 0; j < POPSIZE; j++) {
			solutionPredator[j] = new Solution<BitArray>(predators[j], goalPredator);
			solutionPray[j] = new Solution<BitArray>(pray[j], goalPray);
		}
		
		//print(solutionPredator);
		//System.out.println("=================================");
		//print(solutionPray);

		// Population selection
		Selection<BitArray> selection = new Elitism<BitArray>(1.0, 0.0);
		// Parent selection
		Selection<BitArray> pselection = new Tournament<BitArray>(4);

		// Variation definition
		BitMutation mutation = new BitMutation();
		XOver xover = new XOver();

		for (int i = 0; i < MAXITERS; i++) {

			Evaluate.calculateAverage(solutionPredator, true);
			Evaluate.calculateAverage(solutionPray, false);

			//System.out.println("===============Iteration " + i + "=================");

			// Replacement: generational
			solutionPredator = newGeneration(pselection, selection, xover, mutation, goalPredator, solutionPredator);
			CoevolutionAlgorithm.predatorBestByIte[CoevolutionAlgorithm.RUN][i] = (double)  solutionPredator[0].info(gName);
			solutionPray = newGeneration(pselection, selection, xover, mutation, goalPray, solutionPray);
			CoevolutionAlgorithm.prayBestByIte[CoevolutionAlgorithm.RUN][i] = (double)  solutionPray[0].info(gName);

			//print(solutionPredator);
			//System.out.println("=====================================================");
			//print(solutionPray);
		}
	}
	
	public Solution<BitArray>[] newGeneration(Selection<BitArray> pselection, Selection<BitArray> selection, XOver xover, 
			BitMutation mutation, Goal<BitArray, Double> goal, Solution<BitArray>[] currentPop){
		
		@SuppressWarnings("unchecked")
		Solution<BitArray>[] solution = new Solution[currentPop.length * 2];

		for (int j = 0; j < currentPop.length; j += 2) {

			// Selection
			Solution<BitArray>[] parents = pselection.pick(2, currentPop);
			
			// Xover
			BitArray[] children = xover.apply(parents[0].object(), parents[1].object());

			// Mutation
			children[0] = mutation.apply(children[0]);
			children[1] = mutation.apply(children[1]);

			solution[j] = new Solution<BitArray>(children[0], goal);
			solution[j + 1] = new Solution<BitArray>(children[1], goal);
		}
		
		System.arraycopy(currentPop, 0, solution, solution.length/2, POPSIZE);
		
		//Replacement selecting best
		return selection.pick(POPSIZE, solution);	
	}
	
	public void print(Solution<BitArray>[] solution){
		for (int j = 0; j < solution.length; j++) {
			System.out.println(solution[j].object() + " : " + solution[j].info(gName));
		}
	}

	public void calculate(String csv, String txt, boolean max) throws Exception {

		int n = 30;
		double x[] = new double[n];
		double avg[] = new double[MAXITERS];
		double median[] = new double[MAXITERS];
		double best[] = new double[MAXITERS];
		double worst[] = new double[MAXITERS];

		double vavg;
		double vmedian;
		double savg;
		double smedian;

		FileWriter plot = new FileWriter("/Users/lifeth/Google Drive UNAL/UNACIONAL/COMPUTACION EVOLUTIVA/"+csv);
		FileWriter stats = new FileWriter(
				"/Users/lifeth/Google Drive UNAL/UNACIONAL/COMPUTACION EVOLUTIVA/"+txt);

		// Write matrix
		print(stats, n);

		// Statistics
		for (int j = 0; j < MAXITERS; j++) {

			for (int i = 0; i < n; i++) {
				x[i] = bestByIte[i][j];
				avg[j] += x[i];
			}

			avg[j] /= n;
			DoubleArray.merge(x);
			median[j] = ((n % 2) == 0 ? (x[n / 2] + x[n / 2 - 1]) / 2.0 : x[n / 2]);
			if(max){
				best[j] = x[n - 1];
				worst[j] = x[0];
			}
			else{
				best[j] = x[0];
				worst[j] = x[n - 1];
			}
		}

		plot.write("Iteration,\t FBest,\t FMedian,\t FWorst,\t DeStand" + "\n");

		for (int j = 0; j < MAXITERS; j++) {

			vavg = 0;
			vmedian = 0;

			for (int i = 0; i < n; i++) {
				vavg += (bestByIte[i][j] - avg[j]) * (bestByIte[i][j] - avg[j]);
				vmedian += (bestByIte[i][j] - median[j]) * (bestByIte[i][j] - median[j]);
			}

			savg = Math.sqrt(vavg / (n - 1));
			smedian = Math.sqrt(vmedian / (n - 1));

			plot.write(j + ",\t" + best[j] + ",\t" + median[j] + ",\t" + worst[j] + ",\t" + smedian + "\n");
			stats.write(j + " Average: " + avg[j] + " +/- " + savg + "\n");
			stats.write(j + " Median: " + median[j] + " +/- " + smedian + "\n");
		}

		plot.close();
		stats.close();
	}

	public void print(FileWriter w, int runs) throws Exception {

		for (int i = 0; i < runs; i++) {
			w.write("\n");

			for (int j = 0; j < MAXITERS; j++) {
				w.write(bestByIte[i][j] + " ");
			}
		}
		w.write("\n\n");
	}

	public static void main(String[] args) throws Exception {
		
		CoevolutionAlgorithm e = new CoevolutionAlgorithm();
		
		for (int i = 0; i < 30; i++) {
			e.evolve();
			RUN++;
		}
		
		bestByIte = prayBestByIte;
		e.calculate("PlotPray.csv", "StatiscticsPray.txt", true);
		
		bestByIte = predatorBestByIte;
		e.calculate("PlotPredator.csv", "StatiscticsPredator.txt", false);
	}
}
