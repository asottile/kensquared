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
	public void setSign(Sign sign) {
		this.sign = sign;
	}
	
	private int number;
	public int getNumber() {
		return this.number;
	}
	public void setNumber(int number) {
		this.number = number;
	}
	
	@Override
	public String toString()
    {
        return this.number + " " + SignNumber.toSignString(this.sign);
    }
}
