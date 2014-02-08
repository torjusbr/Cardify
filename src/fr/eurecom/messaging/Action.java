package fr.eurecom.messaging;

public enum Action {
	ADDED_CARD_TO_DECK,
	DREW_FROM_DECK_TO_STACK,
	ADDED_CARD_TO_PUBLIC_ZONE,
	REMOVED_CARD_FROM_PUBLIC_ZONE,
	TURNED_CARD_IN_PUBLIC_ZONE,
	MOVED_CARD_IN_PUBLIC_ZONE,
	GAME_STARTED,
	INITIAL_CARDS,
	REMAINING_DECK,
	ILLEGAL_ACTION,
	ACK,
	REGISTER,
	GAME_INITIALIZED,
	DISCONNECT
}
