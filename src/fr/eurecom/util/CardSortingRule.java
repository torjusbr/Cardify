package fr.eurecom.util;

public enum CardSortingRule {
	S_H_D_C_ACE_HIGH,
	NO_SUIT_ACE_HIGH,
	S_H_D_C_ACE_LOW,
	NO_SUIT_ACE_LOW;
	
	public static CardSortingRule getEnumWithInt(int i) {
		switch (i) {
		case 0:
			return S_H_D_C_ACE_HIGH;
		case 1:
			return NO_SUIT_ACE_HIGH;
		case 2:
			return S_H_D_C_ACE_LOW;
		case 3:
			return NO_SUIT_ACE_LOW;
		default:
			return S_H_D_C_ACE_HIGH;
		}
	}
}
