package com.anthonysottile.kenken;

public class ValueSetEventArgs {
	
	private int x;
	public int getX() {
		return this.x;
	}
	
	private int y;
	public int getY() {
		return this.y;
	}
	
	private int value;
	public int getValue() {
		return this.value;
	}
	
	public ValueSetEventArgs(int x, int y, int value) {
		this.x = x;
		this.y = y;
		this.value = value;
	}
}
