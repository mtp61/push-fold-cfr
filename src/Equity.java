import java.util.Arrays;

public class Equity {
	// 0 for tie 1 for hand1 win 2 for hand2 win
	public static int simulateHand(Hand hand1, Hand hand2) {
		assert(!Hand.overlap(hand1, hand2)); // TODO I don't think this is working...
		int[] hand_cards = { hand1.card1, hand1.card2, hand2.card1, hand2.card2 };

		// get community cards
		int[] deck = Cards.getShuffledDeck();
		int[] community_cards = new int[5];
		int counter = 0;
		int next_card = -1;
		boolean in_hands;
		for (int i = 0; i < 5; ++i) {
			in_hands = true;
			while (in_hands) {
				// check if next card in deck is in hand_cards
				in_hands = false;
				next_card = deck[counter];
				for (int hand_card : hand_cards) {
					if (next_card == hand_card) {
						++counter;
						in_hands = true;
						break;
					}
				}
			}
			community_cards[i] = next_card;
			++counter;
		}
		
		int[] cards1 = new int[7];
		int[] cards2 = new int[7];
		
		cards1[0] = hand1.card1;
		cards1[1] = hand1.card2;
		cards2[0] = hand2.card1;
		cards2[1] = hand2.card2;
		
		for (int i = 0; i < 5; ++i) {
			cards1[i + 2] = community_cards[i];
			cards2[i + 2] = community_cards[i];
		}
		
		int score1 = cardsScore(cards1);
		int score2 = cardsScore(cards2);
		
		if (score1 > score2) {
			return 1;
		} else if (score2 > score1) {
			return 2;
		} else {
			return 0;
		}
	}
	
	private static int cardsScore(int[] cards) {		
		// get the count for ranks
		int[] rank_counts = new int[13];
		for (int card : cards) {
			++rank_counts[card / 10];
		}
		
		// get the count for suits
		int[] suit_counts = new int[4];
		for (int card : cards) {
			++suit_counts[card % 10];
		}
		
		int counter;
		
		// check for flush
		boolean is_flush = false;
		int flush_suit = -1;
		int[] flush_ranks = new int[13];
		counter = 0;
		for (int count : suit_counts) {
			if (count >= 5) {
				is_flush = true;
				flush_suit = counter;
				
				// get the ranks of the flush cards
				for (int card : cards) {
					if (card % 10 == flush_suit) {
						flush_ranks[card / 10] = 1;
					}
				}
				
				break;
			}
			++counter;
		}
		
		// check for straight
		boolean is_straight = false;
		int cur_run = 0;
		int max_run = 0;
		int max_run_top = -1;
		boolean last_card_in_run = true;
		counter = 0;
		for (int count : rank_counts) {
			if (last_card_in_run) {
				if (count > 0) {
					++cur_run;
					if (cur_run > max_run) {
						max_run = cur_run;
						max_run_top = counter;
					}
				} else {
					cur_run = 0;
					last_card_in_run = false;
				}
			} else {
				if (count > 0) {
					cur_run = 1;
					last_card_in_run = true;
				}
			}
			++counter;
		}
		if (max_run >= 5) {
			is_straight = true;
		}
		
		// TODO check for straight and flush
		boolean straight_flush = false;
		
		// pairs, threes, quads
		int num_pairs = 0;
		int num_threes = 0;
		boolean quads = false;
		for (int count : rank_counts) {
			if (count == 2) {
				++num_pairs;
			} else if (count == 3) {
				++num_threes;
			} else if (count == 4) {
				quads = true;
			}
		}
		
		// get hand type
		int hand_type;
		int[] hand_ranks = new int[5];
		if (straight_flush) {
			hand_type = 8;
			
			// TODO
		} else if (quads) {
			hand_type = 7;
			
			// get the quads
			for (int i = 12; i >= 0; --i) {
				if (rank_counts[i] == 4) {
					hand_ranks[0] = i;
					hand_ranks[1] = i;
					hand_ranks[2] = i;
					hand_ranks[3] = i;
					break;
				}
			}
			
			// get the last card
			for (int i = 12; i >= 0; --i) {
				if (rank_counts[i] == 1) {
					hand_ranks[4] = i;
					break;
				}
			}
		} else if ((num_threes == 1 && num_pairs > 0) || num_threes == 2) {
			hand_type = 6;
			
			// get the three of a kind
			int three_index = -1;
			for (int i = 12; i >= 0; --i) {
				if (rank_counts[i] == 3) {
					hand_ranks[0] = i;
					hand_ranks[1] = i;
					hand_ranks[2] = i;
					three_index = i;
					break;
				}
			}

			// get the pair
			for (int i = 12; i >= 0; --i) {
				if (i != three_index && rank_counts[i] >= 2) {
					hand_ranks[3] = i;
					hand_ranks[4] = i;
					break;
				}
			}
		} else if (is_flush) {
			hand_type = 5;
			
			// get the top ranks
			int j = 0;
			for (int i = 12; i >= 0; --i) {
				if (flush_ranks[i] == 1) {
					hand_ranks[j] = i;
					++j;
					if (j == 5) {
						break;
					}
				}
			}
		} else if (is_straight) {
			hand_type = 4;
			
			for (int i = 0; i < 5; ++i) {
				hand_ranks[i] = max_run_top - i;
			}
			
		} else if (num_threes > 0) {
			hand_type = 3;
			
			// get the three of a kind
			for (int i = 12; i >= 0; --i) {
				if (rank_counts[i] == 3) {
					hand_ranks[0] = i;
					hand_ranks[1] = i;
					hand_ranks[2] = i;
					break;
				}
			}
			
			// get the other two cards
			int j = 3;
			for (int i = 12; i >= 0; --i) {
				if (rank_counts[i] == 1) {
					hand_ranks[j] = i;
					++j;
					if (j == 5) {
						break;
					}
				}
			}
		} else if (num_pairs > 1) {
			hand_type = 2;
			
			// get the first pair
			int first_pair_index = -1;
			for (int i = 12; i >= 0; --i) {
				if (rank_counts[i] == 2) {
					hand_ranks[0] = i;
					hand_ranks[1] = i;
					first_pair_index = i;
					break;
				}
			}

			// get the second pair
			for (int i = first_pair_index - 1; i >= 0; --i) {
				if (rank_counts[i] == 2) {
					hand_ranks[2] = i;
					hand_ranks[3] = i;
					break;
				}
			}
			
			// get the last card
			for (int i = 12; i >= 0; --i) {
				if (rank_counts[i] == 1) {
					hand_ranks[4] = i;
					break;
				}
			}
		} else if (num_pairs == 1) {
			hand_type = 1;
			
			// get the pair
			for (int i = 12; i >= 0; --i) {
				if (rank_counts[i] == 2) {
					hand_ranks[0] = i;
					hand_ranks[1] = i;
					break;
				}
			}
			
			// get the other 3 cards
			int j = 2;
			for (int i = 12; i >= 0; --i) {
				if (rank_counts[i] == 1) {
					hand_ranks[j] = i;
					++j;
					if (j == 5) {
						break;
					}
				}
			}
		} else {
			hand_type = 0;
			
			int j = 0;
			for (int i = 12; i >= 0; --i) {
				if (rank_counts[i] == 1) {
					hand_ranks[j] = i;
					++j;
					if (j == 5) {
						break;
					}
				}
			}
		}
		
		/*
		System.out.println(hand_type);
		System.out.println(Arrays.toString(hand_ranks));
		for (int card : cards) {
			System.out.print(Cards.numToCard(card) + " ");
		}
		System.out.println();
		*/
		
		int hand_score = 0;
		int power = 1;
		for (int i = 4; i >= 0; --i) {
			hand_score += power * hand_ranks[i];
			power *= 13;
		}
		hand_score += power * hand_type;
		
		return hand_score;
	}
}
