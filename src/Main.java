import java.util.Arrays;

public class Main {
	public static void main(String args[]) {
		int[] deck = Cards.getShuffledDeck();
		Hand hand = new Hand(deck[0], deck[1]);
		//System.out.println(hand);
		
		Range r = new Range();
		r.emptyRange();
		
		// set range
		r.updateRange(0, 1);
		r.updateRange(1, 1);
		r.updateRange(2, 1);
		r.updateRange(3, 1);
		r.updateRange(13, 1);
		r.updateRange(14, 1);
		r.updateRange(15, 1);

		//r.printRange();
		
		
		Hand h1 = new Hand(120, 111);
		Hand h2 = new Hand(51, 51);
		
		System.out.println(Cards.numToCard(h1.card1) + " " + Cards.numToCard(h1.card2));
		System.out.println(Cards.numToCard(h2.card1) + " " + Cards.numToCard(h2.card2));
		
		int[] results = new int[3];
		
		
		int n_iter = 1000000;
		
		for (int i = 0; i < n_iter; ++i) {
			++results[Equity.simulateHand(h1, h2)];
		}
		
		System.out.println(Arrays.toString(results));
	}
}
