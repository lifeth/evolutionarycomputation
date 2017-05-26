package ga.epimarks;

import java.io.FileWriter;
import unalcol.evolution.EAFactory;
import unalcol.optimization.OptimizationFunction;
import unalcol.optimization.OptimizationGoal;
import unalcol.search.Goal;
import unalcol.search.population.PopulationSearch;
import unalcol.search.selection.Elitism;
import unalcol.search.selection.Selection;
import unalcol.search.selection.Tournament;
import unalcol.search.solution.Solution;
import unalcol.search.space.Space;
import unalcol.search.variation.Variation_1_1;
import unalcol.types.real.array.DoubleArray;

public class GeneticAlgorithm {

	public static int RUN = 0;
	public static int MAXITERS = 10000;
	public static int DIM = 300;
	public static int POPSIZE = 1000;
	public static double bestByIte[][] = new double[30][MAXITERS];

	public void maxOnes() {
		// Search Space definition
		Space<MarkedBitArray> space = new ga.epimarks.BinarySpace(DIM);

		// Optimization Function
		OptimizationFunction<MarkedBitArray> function = new MaxOnes();
		Goal<MarkedBitArray, Double> goal = new OptimizationGoal<MarkedBitArray>(function, false);
		// Variation definition
		Variation_1_1<MarkedBitArray> mutation = new ga.epimarks.BitMutation();
		ga.epimarks.XOver xover = new ga.epimarks.XOver();

		// Parent Selection
		 //Selection<MarkedBitArray> selection = new Elitism<MarkedBitArray>(0.2, 0.8);
		Selection<MarkedBitArray> selection = new Tournament<MarkedBitArray>(4);

		// Search method
		EAFactory<MarkedBitArray> factory = new EAFactory<MarkedBitArray>();
		PopulationSearch<MarkedBitArray, Double> search = factory.generational_ga(POPSIZE, selection, mutation, xover,
				1.0, MAXITERS);

		// Apply the search method
		String gName = Goal.class.getName();
		 Solution<MarkedBitArray>[] bestSolutions = search.solve(space, goal, null).object();
		 for (int i = 0; i < MAXITERS; i++) {
			 System.out.println(i+" :");
			 System.out.println(bestSolutions[i].object());
			 GeneticAlgorithm.bestByIte[GeneticAlgorithm.RUN][i] = (double) bestSolutions[i].info(gName);			
		}
	}
	
	public void deceptive() {
		// Search Space definition
		Space<MarkedBitArray> space = new ga.epimarks.BinarySpace(DIM);

		// Optimization Function
		OptimizationFunction<MarkedBitArray> function = new Deceptive();
		Goal<MarkedBitArray, Double> goal = new OptimizationGoal<MarkedBitArray>(function, false);
		// Variation definition
		Variation_1_1<MarkedBitArray> mutation = new ga.epimarks.BitMutation();
		ga.epimarks.XOver xover = new ga.epimarks.XOver();

		// Parent Selection
		//Selection<MarkedBitArray> selection = new Elitism<MarkedBitArray>(1.0, 0);
		Selection<MarkedBitArray> selection = new Tournament<MarkedBitArray>(4);

		// Search method
		EAFactory<MarkedBitArray> factory = new EAFactory<MarkedBitArray>();
		PopulationSearch<MarkedBitArray, Double> search = factory.generational_ga(POPSIZE, selection, mutation, xover,
				1.0, MAXITERS);

		// Apply the search method
		String gName = Goal.class.getName();
		 Solution<MarkedBitArray>[] bestSolutions = search.solve(space, goal, null).object();
		 for (int i = 0; i < MAXITERS; i++) {
			 System.out.println(i+": ");
			 System.out.println(bestSolutions[i].object());
			 GeneticAlgorithm.bestByIte[GeneticAlgorithm.RUN][i] = (double) bestSolutions[i].info(gName);			
		}
	}

	public void calculate() throws Exception {

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

		for (int i = 0; i < n; i++) {
			//maxOnes();
			deceptive();
			RUN++;
		}

		FileWriter plot = new FileWriter("/Users/lifeth/Google Drive UNAL/UNACIONAL/COMPUTACION EVOLUTIVA/plot.csv");
		FileWriter stats = new FileWriter("/Users/lifeth/Google Drive UNAL/UNACIONAL/COMPUTACION EVOLUTIVA/Statistics.txt");
		
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
			best[j] = x[n - 1];
			worst[j] = x[0];
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
		new GeneticAlgorithm().calculate();
	}
}
