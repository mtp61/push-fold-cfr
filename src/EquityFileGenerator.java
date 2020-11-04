import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

public class EquityFileGenerator {
	private static int niter = 10000;
	private static Random rand = new Random();

	public static void main(String args[]) {		
		// make a hashmap to store the data
		HashMap<Integer, ArrayList<Float>> hashmap = new HashMap<Integer, ArrayList<Float>>();
		
		// generate the equities
		int hand_int;
		int rank1_1, rank1_2, rank2_1, rank2_2;
		//int card1_1, card1_2, card2_1, card2_2;
		boolean suited1, suited2;
		boolean bad_hand;
		int[] hand1 = null, hand2 = null;
		ArrayList<Integer> results;
		ArrayList<Float> results_f;
		for (int i = 0; i < 169; ++i) {
			for (int j = 0; j < 169; ++j) {
				// make sure we haven't already generated this
				if (hashmap.containsKey(1000 * j + i)) {
					continue;
				}
				
				hand_int = 1000 * i + j;

				rank1_1 = 12 - (i % 13);
				rank1_2 = 12 - (i / 13);
				rank2_1 = 12 - (j % 13);
				rank2_2 = 12 - (j / 13);
				suited1 = (i % 13) > (i / 13);
				suited2 = (j % 13) > (j / 13);
				
				results  = new ArrayList<Integer>();
				results.add(0);
				results.add(0);
				results.add(0);
				
				for (int iter = 0; iter < niter; ++iter) {
					// generate two compatible hands 
					bad_hand = true;
					while (bad_hand) {
						hand1 = generateHand(rank1_1, rank1_2, suited1);
						hand2 = generateHand(rank2_1, rank2_2, suited2);
						
						//System.out.println(Arrays.toString(hand1) + " " + Arrays.toString(hand2));
						
						// check for overlap
						bad_hand = false;
						int[] cards = { hand1[0], hand1[1], hand2[0], hand2[1] };
						for (int k = 0; k < 3; ++k) {
							for (int l = k + 1; l < 4; ++l) {
								if (cards[k] == cards[l]) {
									bad_hand = true;
								}
							}
						}
					}
					
					int result = Equity.simulateHand(new Hand(hand1[0], hand1[1]), new Hand(hand2[0], hand2[1]));
			
					results.set(result, results.get(result) + 1);					
				}
								
				// set the hashmap
				results_f = new ArrayList<Float>();
				results_f.add((float) results.get(1) / niter);
				results_f.add((float) results.get(2) / niter);
				results_f.add((float) results.get(0) / niter);
				
				hashmap.put(hand_int, results_f);
				
				System.out.println((new Hand(hand1[0], hand1[1])).toString() + " v " + (new Hand(hand2[0], hand2[1])).toString() + ": " + results_f);
			}
		}
		
		// make sure every possible input is in the map
		for (int i = 0; i < 169; ++i) {
			for (int j = 0; j < 169; ++j) {
				hand_int = 1000 * i + j;
				if (!hashmap.containsKey(hand_int)) {
					hashmap.put(hand_int, hashmap.get(1000 * j + i));
				}
			}
		}
		
		// write to file
		dumpEquityFile(hashmap);
	}
	
	private static int[] generateHand(int rank1, int rank2, boolean suited) {
		int[] hand = new int[2];
		if (suited) {  // suited
			int suit = rand.nextInt(4);
			hand[0] = 10 * rank1 + suit;
			hand[1] = 10 * rank2 + suit;
		} else if (rank1 == rank2){  // pair
			int suit1 = rand.nextInt(4);
			int suit2 = rand.nextInt(3);
			
			if (suit2 >= suit1) {
				++suit2;
			}
			
			hand[0] = 10 * rank1 + suit1;
			hand[1] = 10 * rank2 + suit2;
		} else {  // not suited
			int suit1 = rand.nextInt(4);
			int suit2 = rand.nextInt(4);
			
			hand[0] = 10 * rank1 + suit1;
			hand[1] = 10 * rank2 + suit2;
		}
		
		return hand;
	}
	
	public static void dumpEquityFile(HashMap<Integer, ArrayList<Float>> hashmap) {
		try {
			FileWriter fw = new FileWriter("equities.txt");
					
			for (Map.Entry<Integer, ArrayList<Float>> entry : hashmap.entrySet()) {
				ArrayList<Float> results = entry.getValue();
				fw.write(String.format("%d %f %f %f", entry.getKey(), results.get(0), results.get(1), results.get(2)) + "\n");
			}
			
			fw.close();
		} catch (IOException e) {
			System.out.println("FileWriter Error");
		}
	}
	
	public static HashMap<Integer, ArrayList<Float>> loadEquityFile() {
		HashMap<Integer, ArrayList<Float>> hashmap = new HashMap<Integer, ArrayList<Float>>();
		
		Scanner scanner;
		String line;
		int key;
		ArrayList<Float> value;
		try {
			BufferedReader reader = new BufferedReader(new FileReader("equities.txt"));
			
			line = reader.readLine();
			while (line != null) {
				scanner = new Scanner(line);
				
				key = scanner.nextInt();
				
				value = new ArrayList<Float>();
				value.add(scanner.nextFloat());
				value.add(scanner.nextFloat());
				value.add(scanner.nextFloat());
				
				hashmap.put(key, value);
				
				line = reader.readLine();
			}
			
			reader.close();
		} catch (IOException e) {
			System.out.println("FileReader error");
		}
		
		return hashmap;
	}
}
