package com.anthonysottile.kenken;

public class SignNumber {
	
	private static String toSignString(Sign sign) {
		switch(sign) {
			case Add:
				return "+";
    		case Subtract:
    			return "-";
    		case Multiply:
    			return "*";
    		case Divide:
    			return "/";
    		case None:
			default:
    			return "";
		}
	}
	
	private Sign sign;
	public Sign getSign() {
		return this.sign;
	}
	
	private int number;
	public int getNumber() {
		return this.number;
	}
	
	@Override
	public String toString() {
        return this.number + " " + SignNumber.toSignString(this.sign);
    }
	
	public SignNumber(Sign sign, int number) {
		this.sign = sign;
		this.number = number;
	}
}
