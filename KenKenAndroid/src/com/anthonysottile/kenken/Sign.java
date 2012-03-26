package com.anthonysottile.kenken;

public enum Sign {
	
    Add(Sign.addInt, Sign.addString),
    Subtract(Sign.subtractInt, Sign.subtractString),
    Multiply(Sign.multiplyInt, Sign.multiplyString),
    Divide(Sign.divideInt, Sign.divideString),
    None(Sign.noneInt, Sign.noneString);
    
    private static final int addInt = 0;
    private static final int subtractInt = 1;
    private static final int multiplyInt = 2;
    private static final int divideInt = 3;
    private static final int noneInt = 4;
    
    private static final String addString = "+";
    private static final String subtractString = "-";
    private static final String multiplyString = "*";
    private static final String divideString = "/";
    private static final String noneString = "";
    
    private static final Sign[] signsByInt = {
    	Sign.Add,
    	Sign.Subtract,
    	Sign.Multiply,
    	Sign.Divide,
    	Sign.None
    };
    
    public static Sign toSign(int n) {
    	return Sign.signsByInt[n];
    }
    
    private int n;
    public int getIntValue() {
    	return this.n;
    }
    
    private String s;
    
    @Override
    public String toString() {
    	return this.s;
    }
    
    private Sign(int n, String s) {
    	this.n = n;
    	this.s = s;
    }
}
