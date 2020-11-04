
public class Hand {
	public int card1;
	public int card2;
	
	public int card1_rank;
	public int card1_suit;
	public int card2_rank;
	public int card2_suit;
	public boolean suited;

	public Hand(int card1, int card2) {
		if (card1 > card2) {
			this.card1 = card1;
			this.card2 = card2;
		} else {
			this.card1 = card2;
			this.card2 = card1;
		}
		
		this.card1_rank = this.card1 / 10;
		this.card1_suit = this.card1 % 10;
		this.card2_rank = this.card2 / 10;
		this.card2_suit = this.card2 % 10;
		this.suited = card1_suit == card2_suit;
	}
	
	@Override
	public String toString() {
		return Character.toString(Cards.char_ranks[card1_rank])
				+ Character.toString(Cards.char_ranks[card2_rank])
				+ (suited ? "s" : "");
	}
	
	// returns true if there is overlap between the two hands
	public static boolean overlap(Hand hand1, Hand hand2) {
		int[] cards = { hand1.card1, hand1.card2, hand2.card1, hand2.card2 };
		
		for (int i = 0; i < 3; ++i) {
			for (int j = i + 1; j < 4; ++j) {
				if (cards[i] == cards[j]) {
					return true;
				}
			}
		}		
		
		return false;
	}
}
