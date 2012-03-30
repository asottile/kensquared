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
    
    /**
     * Converts the number passed in to the enumeration type.
     * 
     * @param n The number to convert to the Sign enum.  Must be inclusively between
     *           [Sign.Add.getIntValue(), Sign.None.getIntValue()]
     * @return Returns the Sign enum from the integer.
     */
    public static Sign toSign(int n) {
    	return Sign.signsByInt[n];
    }
    
    private int n;
    /**
     * Returns the integer representation of the enum.
     * 
     * @return The integer representation of the enum.
     */
    public int getIntValue() {
    	return this.n;
    }
    
    private String s;

    /**
     * Returns the string representation of the enum.
     */
    @Override
    public String toString() {
    	return this.s;
    }
    
    private Sign(int n, String s) {
    	this.n = n;
    	this.s = s;
    }
}
