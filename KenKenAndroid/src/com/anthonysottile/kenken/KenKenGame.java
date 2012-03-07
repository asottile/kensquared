package com.anthonysottile.kenken;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.anthonysottile.kenken.cages.CageGenerator;
import com.anthonysottile.kenken.cages.ICage;

import android.graphics.Point;

public class KenKenGame {
	
	private Date gameStartTime;
	public Date getGameStartTime() {
		return this.gameStartTime;
	}

	public void PenalizeGameStartTime(int milliseconds) {
		this.gameStartTime.setTime(this.gameStartTime.getTime() - milliseconds);
	}
	
	private LatinSquare latinSquare;
	public LatinSquare getLatinSquare() {
		return this.latinSquare;
	}

	private boolean[][] cageSquareOccupied;
	
	private List<ICage> cages;
	public List<ICage> getCages() {
		return this.cages;
	}
	
	private UserSquare[][] userSquares;
	public UserSquare[][] getUserSquares() {
		return this.userSquares;
	}
	
	public boolean squareIsOffBoard(Point p) {
		int order = this.latinSquare.getOrder();
		
		return
				p.x >= order ||
				p.y >= order ||
				p.x < 0 ||
				p.y < 0;
	}
	
	public boolean squareIsValid(Point p) {
		if(this.squareIsOffBoard(p)) {
			return false;
		}
		
		return !this.cageSquareOccupied[p.x][p.y];
	}

	public void setOccupied(Point p) {
		this.cageSquareOccupied[p.x][p.y] = true; 
	}
	
	public KenKenGame(int order) {
		this.latinSquare = new LatinSquare(order);
		
		this.cageSquareOccupied = new boolean[order][];
		this.userSquares = new UserSquare[order][];
		for(int i = 0; i < order; i += 1) {
			this.cageSquareOccupied[i] = new boolean[order];
			this.userSquares[i] = new UserSquare[order];
			
			for(int j = 0; j < order; j += 1) {
				this.cageSquareOccupied[i][j] = false;
				this.userSquares[i][j] = new UserSquare(i, j, order);
			}
		}
		
		this.cages = new ArrayList<ICage>();

		CageGenerator.Generate(this);
		
		this.gameStartTime = new Date();
	}
}
