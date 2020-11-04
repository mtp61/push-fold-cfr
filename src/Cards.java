import java.util.Random;

public class Cards {			  
	public static int[] ranks = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12 };
	public static char[] char_ranks = { '2', '3', '4', '5', '6', '7', '8', '9', 'T', 'J', 'Q', 'K', 'A' };
	public static int[] suits = { 0, 1, 2, 3 };
	public static char[] char_suits = { 'c', 's', 'h', 'd' };  // TODO order
	
	private static Random rand = new Random();
	
	public static int[] getShuffledDeck() {
		int[] deck = new int[52];
		
		// initialize deck
		int counter = 0;
		for (int rank : ranks) {
			for (int suit : suits) {
				deck[counter] = 10 * rank + suit;
				++counter;
			}
		}
		
		shuffle(deck);
		
		return deck;
	}
	
	
	// implements fisher yates shuffle algorithm
	private static void shuffle(int[] deck) {
		for (int i = deck.length - 1; i > 0; --i) {
			int j = rand.nextInt(i + 1);
			
			int temp = deck[i];
			deck[i] = deck[j];
			deck[j] = temp;
		}
	}
	
	public static String numToCard(int num) {
		int rank = num / 10;
		int suit = num % 10;
		return String.format("%c%c", char_ranks[rank], char_suits[suit]);
	}
}
