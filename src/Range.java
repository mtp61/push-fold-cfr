import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

public class Range {
	private float[] range = new float[169];
	
	public Range() {
		fullRange();
	}
	
	public void emptyRange() {
		for (int i = 0; i < 169; ++i) {
			this.range[i] = 0;
		}
	}
	
	public void fullRange() {
		for (int i = 0; i < 169; ++i) {
			this.range[i] = 1;
		}
	}
	
	public void setRange(float[] range) {
		for (int i = 0; i < 169; ++i) {
			this.range[i] = range[i];
		}
	}
	
	public void setRange(double[] range) {
		for (int i = 0; i < 169; ++i) {
			this.range[i] = (float) range[i];
		}
	}
	
	public void printRange() {
		// print top bar
		System.out.print("    ");
		for (int i = 12; i >= 0; --i) {
			System.out.print(Character.toString(Cards.char_ranks[i]) + "     ");
		}
		System.out.println();
		
		// print each row
		for (int i = 12; i >= 0; --i) {
			System.out.print(Character.toString(Cards.char_ranks[i]) + "   ");
			
			for (int j = 0; j < 13; ++j) {
				int index = 13 * (12 - i) + j; 
				System.out.format("%-3.2f  ", this.range[index]);
			}
		
			System.out.println();
		}
	}
	
	public void printRangeBinary(float threshold) {
		// print top bar
		System.out.print("    ");
		for (int i = 12; i >= 0; --i) {
			System.out.print(Character.toString(Cards.char_ranks[i]) + "  ");
		}
		System.out.println();
		
		// print each row
		for (int i = 12; i >= 0; --i) {
			System.out.print(Character.toString(Cards.char_ranks[i]) + "   ");
			
			for (int j = 0; j < 13; ++j) {
				int index = 13 * (12 - i) + j; 
				if (this.range[index] > threshold) {
					System.out.print("X  ");
				} else {
					System.out.print("   ");
				}
			}
		
			System.out.println();
		}
	}
	
	public void printRangeBinary() {
		this.printRangeBinary((float) 0.5);
	}
	
	public void updateRange(int index, float val) {
		assert(val >= 0 && val <= 1);
		
		this.range[index] = val;
	}
	
	public void updateRange(String hand, float val) {
		// TODO make one new method and call updateRange with the hand converted to an int
	}
	
	public void updateRange(Hand hand, float val) {
		updateRange(hand.toString(), val);
	}
	
	public Hand getRandomHand() {
		float range_sum = 0;
		for (float f : this.range) {
			range_sum += f;
		}
		
		Random rand = new Random();
		float rand_num = range_sum * rand.nextFloat();
		
		int running_sum = 0;
		int counter = 0;
		for (float f : this.range) {
			running_sum += f;
			if (running_sum >= rand_num) {
				return indexToHand(counter);
			}
			++counter;
		}
		
		// TODO shouldn't get here..
		counter = 0;
		for (float f : this.range) {
			if (f > 0) {
				return indexToHand(counter);
			}
			++counter;
		}
		
		return indexToHand(0);
	}
	
	private Hand indexToHand(int index) {
		int x = index % 13;
		int y = index / 13;
		int rank1 = 12 - x;
		int rank2 = 12 - y;
		
		Random rand = new Random();
			
		if (x > y) {  // suited
			int suit = rand.nextInt(4);
			
			return new Hand(10 * rank1 + suit, 10 * rank2 + suit);
		} else if (x == y) {  // pair
			int suit1 = rand.nextInt(4);
			int suit2 = rand.nextInt(3);
			
			if (suit2 >= suit1) {
				++suit2;
			}
			
			return new Hand(10 * rank1 + suit1, 10 * rank2 + suit2);

		} else {  // not suited
			int suit1 = rand.nextInt(4);
			int suit2 = rand.nextInt(4);
			
			return new Hand(10 * rank1 + suit1, 10 * rank2 + suit2);
		}
	}
	
	public static int cardsToIndex(int card1, int card2) {
		boolean suited = (card1 % 10) == (card2 % 10);
		int rank1, rank2;
		// higher card rank1
		if (card1 > card2) {
			rank1 = card1 / 10; 
			rank2 = card2 / 10;
		} else {
			rank1 = card2 / 10; 
			rank2 = card1 / 10;
		}
		
		if (rank1 == rank2) {  // pair
			return 13 * (12 - rank1) + (12 - rank1);
		} else if (suited) {  // suited
			return 13 * (12 - rank1) + (12 - rank2);
		} else {  // not suited
			return 13 * (12 - rank2) + (12 - rank1);
		}
	}
	
	public void writeRange(String filename) {
		try {
			FileWriter fw = new FileWriter(filename);
			
			for (int i = 0; i < 169; ++i) {
				fw.write(Float.toString(this.range[i]) + "\n");
			}
			
			fw.close();
		} catch (IOException e) {
			System.out.println("FileWriter Error");
		}
	}
	
	public void loadRange(String filename) {
		String line;
		Scanner scanner;
		try {
			BufferedReader reader = new BufferedReader(new FileReader("equities.txt"));
			
			for (int i = 0; i < 169; ++i) {
				scanner = new Scanner(reader.readLine());
				
				this.range[i] = scanner.nextFloat();
			}
			
			reader.close();
		} catch (IOException e) {
			System.out.println("FileReader error");
		}
	}
}
