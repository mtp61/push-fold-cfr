import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

public class CFR {	
	private static int niter = 10000000;
	
	private static int stack_size = 20;
	
	public static void main(String args[]) {
		double[][] cum_regrets_push = new double[169][2];  // the first and second indices on axis-2 are for doing the action and not doing it
		double[][] cum_regrets_call = new double[169][2];
		double[] strategy_sum_push = new double[169];
		double[] strategy_sum_call = new double[169];
		
		// load the hashmap
		HashMap<Integer, ArrayList<Float>> hashmap = EquityFileGenerator.loadEquityFile();
		Random rand = new Random();
		
		// main loop
		double[] regret_strategy_push, regret_strategy_call;
		int index_push, index_call;
		boolean action_push, action_call;
		double prob_push, prob_call, equity_push, equity_call, regret;
		for (int iter = 0; iter < niter; ++iter) {
			// generate the two strategies
			regret_strategy_push = getStrategy(cum_regrets_push);
			regret_strategy_call = getStrategy(cum_regrets_call);
			
			// add to strategy sums
			for (int i = 0; i < 169; ++i) {
				strategy_sum_push[i] += regret_strategy_push[i];
				strategy_sum_call[i] += regret_strategy_call[i];
			}
			
			/*
			// testing
			boolean problem = false;
			int pindex = -1;
			for (int i = 0; i < 169; ++i) {
				if (regret_strategy_push[i] < 0) {
					problem = true;
					pindex = i;
					break;
				}
			}
			if (problem) {
				System.out.println(regret_strategy_push[pindex]);
				System.out.println(Arrays.toString(cum_regrets_push[pindex]));
				return;
			}
			*/		
			
			// TODO double loop structure?
			
			// deal cards to the players
			int[] deck = Cards.getShuffledDeck();
			index_push = Range.cardsToIndex(deck[0], deck[1]);
			index_call = Range.cardsToIndex(deck[2], deck[3]);
			
			// get actions TODO this is inefficent, don't need to calc the entire strat. 
			prob_push = regret_strategy_push[index_push];
			prob_call = regret_strategy_call[index_call];
			action_push = rand.nextDouble() < prob_push;  // TODO move these into the if statements
			action_call = rand.nextDouble() < prob_call;
			
			equity_push = (double) hashmap.get(1000 * index_push + index_call).get(0);  // TODO handle ties
			equity_call = 1 - equity_push;
			
			// TODO do we even need to simulate?
			
			if (action_push) {
				if (action_call) {  // push and call
					// pusher utility is equity * (stacksize + .5) - (1 - equity) * (stack_size - .5)
					// other option is fold which is 0
					regret = equity_push * (stack_size + .5) - (1 - equity_push) * (stack_size - .5);
					cum_regrets_push[index_push][1] += regret;
					
					// caller utility is equity * (stack_size + 1) - (1 - equity) * (stack_size - 1)
					// other option is fold which is 0
					regret = equity_call * (stack_size + 1) - (1 - equity_call) * (stack_size - 1);
					cum_regrets_call[index_call][1] += regret;					
				} else {  // push and fold
					// pusher utility is 1.5
					// other option is fold which is 0
					regret = 1.5;
					cum_regrets_push[index_push][1] += regret;					

					// caller utility is 0
					// other option is call which is equity * (stack_size + .5) - (1 - equity) * (stack_size - 1)
					regret = -(equity_call * (stack_size + .5) - (1 - equity_call) * (stack_size - 1));
					cum_regrets_call[index_call][0] += regret;					
				}
			} else {  // no push
				// pusher utility is 0
				// other option is push which is equity * (stacksize + .5) - (1 - equity) * (stack_size - .5)
				regret = -(equity_push * (stack_size + .5) - (1 - equity_push) * (stack_size - .5));
				cum_regrets_push[index_push][0] += regret;					
				
				// caller doesn't act		
			}
			
			//System.out.println((action_push ? "push" : "no push") + ", " + (action_call ? "called" : "no call"));
		}
		
		// compute the final strategy
		float[] strategy_push = new float[169];
		float[] strategy_call = new float[169];
		for (int i = 0; i < 169; ++i) {
			strategy_push[i] = (float) (strategy_sum_push[i] / niter);
			strategy_call[i] = (float) (strategy_sum_call[i] / niter);
		}
		
		// generate ranges
		Range range_push = new Range();
		Range range_call = new Range();
		range_push.setRange(strategy_push);
		range_call.setRange(strategy_call);
		
		// print the ranges
		System.out.println("push range");
		range_push.printRangeBinary();
		System.out.println("\ncall range");
		range_call.printRangeBinary();
		
		// save the strategy to disc
		range_push.writeRange("push_range.txt");
		range_call.writeRange("call_range.txt");
	}
	
	private static double[] getStrategy(double[][] cum_regrets) {		
		double[] strategy = new double[169];
		
		//double[] regret_sum = new double[169];
		
		double regret_sum;
		for (int i = 0; i < 169; ++i) {
			regret_sum = 0;
			regret_sum += cum_regrets[i][0] > 0 ? cum_regrets[i][0] : 0;
			regret_sum += cum_regrets[i][1] > 0 ? cum_regrets[i][1] : 0;
			
			if (regret_sum > 0) {
				strategy[i] = cum_regrets[i][0] > 0 ? cum_regrets[i][0] / regret_sum : 0;
			} else {
				strategy[i] = .5;
			}
		}
		
		return strategy;
	}
}
