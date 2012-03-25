package com.anthonysottile.kenken;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.EventObject;
import java.util.List;

public class UserSquare {
	
	private int x;
	public int getX() {
		return this.x;
	}
	
	private int y;
	public int getY() {
		return this.y;
	}
	
	private boolean[] candidates;
	public boolean[] getCandidates() {
		return this.candidates;
	}
	
	private int value = 0;
	public int getValue() {
		return this.value;
	}
	public void setValue(int value) {
		if(this.value != value) {
			this.value = value;
			
			this.triggerValueSetEvent();

			this.triggerChangedEvent();
		}
	}

	public List<Integer> GetIntCandidates() {
		List<Integer> intList = new ArrayList<Integer>();
		for(int i = 0; i < this.candidates.length; i += 1) {
			if(this.candidates[i]) { 
				intList.add(i);
			}
		}
		
		return intList;
	}

	public String GetCandidatesString() {
		
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for(int i = 0; i < this.candidates.length; i += 1) {
			if(this.candidates[i]) {
				if(!first) {
					sb.append(' ');
				}
				sb.append(i + 1);
				first = false;
			}
		}
		
		return sb.toString();
	}
	
	public String GetValueString() {
		return ((Integer)(this.value)).toString();
	}
	
	public void AddCandidate(int value) {
		if(!this.candidates[value - 1]) {
			this.candidates[value - 1] = true;

			this.triggerChangedEvent();
		}
	}
	
	public void RemoveCandidate(int value) {
		if(this.candidates[value - 1]) {
			this.candidates[value - 1] = false;

			this.triggerChangedEvent();
		}	
	}
	
	// #region UserSquareChanged Event
	
	public interface UserSquareChangedListener {
		public void onUserSquareChanged(EventObject event);
	}
	private List<UserSquareChangedListener> changedHandlers =
			new ArrayList<UserSquareChangedListener>();
	public void AddChangedEventHandler(UserSquareChangedListener handler) {
		this.changedHandlers.add(handler);
	}
	public void RemoveChangedEventHandler(UserSquareChangedListener handler) {
		this.changedHandlers.remove(handler);
	}
	private void triggerChangedEvent() {
		EventObject event = new EventObject(this);
		
		int length = this.changedHandlers.size();
		for(int i = 0; i < length; i += 1) {
			this.changedHandlers.get(i).onUserSquareChanged(event);
		}
	}

	// #endregion
	
	// #region ValueSet Event
	
	public class ValueSetEvent extends EventObject {
		
		private static final long serialVersionUID = -2664136343305658114L;
		
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
		
		public ValueSetEvent(Object sender, int x, int y, int value) {
			super(sender);
			
			this.x = x;
			this.y = y;
			this.value = value;
		}
	}
	public interface ValueSetListener extends EventListener {
		public void onValueSet(ValueSetEvent event);
	}
	private List<ValueSetListener> valueSetListeners =
			new ArrayList<ValueSetListener>();
	public void AddValueSetListener(ValueSetListener listener) {
		this.valueSetListeners.add(listener);
	}
	public void RemoveValueSetListener(ValueSetListener listener) {
		this.valueSetListeners.remove(listener);
	}
	public void ClearValueSetListeners() {
		this.valueSetListeners.clear();
	}
	private void triggerValueSetEvent() {
		ValueSetEvent event = new ValueSetEvent(this, this.x, this.y, this.value);
		
		int length = this.valueSetListeners.size();
		for(int i = 0; i < length; i += 1) {
			this.valueSetListeners.get(i).onValueSet(event);
		}
	}
	
	// #endregion
	
	public UserSquare(int x, int y, int order) {
			this.x = x;
			this.y = y;
			this.candidates = new boolean[order];
	}
}
