package com.anthonysottile.kenken.ui;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.EventObject;
import java.util.List;

import com.anthonysottile.kenken.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CandidatesLayout extends LinearLayout {

	public class CandidateEvent extends EventObject {
		
		private static final long serialVersionUID = -5538360989234256662L;
		private int candidate = 0;
		public int getCandidate() {
			return this.candidate;
		}
		
		public CandidateEvent(Object object, int candidate) {
			super(object);
			this.candidate = candidate;
		}
	}
	
	public interface CandidateAddedListener extends EventListener {
		public void onCandidateAdded(CandidateEvent event);
	}
	public interface CandidateRemovedListener extends EventListener {
		public void onCandidateRemoved(CandidateEvent event);
	}
	
	private List<CandidateAddedListener> candidateAddedListeners =
			new ArrayList<CandidateAddedListener>();
	public void AddCandidateAddedListener(CandidateAddedListener listener) {
		this.candidateAddedListeners.add(listener);
	}
	public void RemoveCandidateAddedListener(CandidateAddedListener listener) {
		this.candidateAddedListeners.remove(listener);
	}
	private void triggerCandidateAdded(int candidate) {
		CandidateEvent event = new CandidateEvent(this, candidate);
		
		int size = this.candidateAddedListeners.size();
		for(int i = 0; i < size; i += 1) {
			this.candidateAddedListeners.get(i).onCandidateAdded(event);
		}
	}
	
	private List<CandidateRemovedListener> candidateRemovedListeners =
			new ArrayList<CandidateRemovedListener>();
	public void AddCandidateRemovedListener(CandidateRemovedListener listener) {
		this.candidateRemovedListeners.add(listener);
	}
	public void RemoveCandidateRemovedListener(CandidateRemovedListener listener) {
		this.candidateRemovedListeners.remove(listener);
	}
	private void triggerCandidateRemoved(int candidate) {
		CandidateEvent event = new CandidateEvent(this, candidate);
		
		int size = this.candidateRemovedListeners.size();
		for(int i = 0; i < size; i += 1) {
			this.candidateRemovedListeners.get(i).onCandidateRemoved(event);
		}
	}
	
	
	private CustomButton plusButton = null;
	private CustomButton minusButton = null;
	private CustomButton[] candidates = null;
	
	private void populateAllClicked() {
		for(int i = 0; i < this.candidates.length; i += 1) {
			if(this.candidates[i].getEnabled() && !this.candidates[i].getChecked()) {
				this.candidates[i].setChecked(true);
			}
		}
	}
	
	private void clearAllClicked() {
		for(int i = 0; i < this.candidates.length; i += 1) {
			if(this.candidates[i].getEnabled() && this.candidates[i].getChecked()) {
				this.candidates[i].setChecked(false);
			}
		}
	}
	
	private void handleCheckChanged(CustomButton button) {
		if(button.getChecked()) {
			this.triggerCandidateAdded(button.getValue());
		} else {
			this.triggerCandidateRemoved(button.getValue());
		}
	}
	
	public void SetValues(boolean[] values) {
		for(int i = 0; i < values.length; i += 1) {
			this.candidates[i].setCheckedNoTrigger(values[i]);
		}
	}
	
	public void TrySetValue(int value) {
		if(this.candidates != null && this.candidates[value - 1].getEnabled()) {
			this.candidates[value - 1].toggleChecked();
		}
	}
	
	public void SetDisabled() {
		if(this.candidates != null) {
			for(int i = 0; i < this.candidates.length; i += 1) {
				this.candidates[i].setEnabled(false);
			}
		}
	}
	
	public void SetDisabled(List<Integer> disabled) {

		for(int i = 0; i < this.candidates.length; i += 1) {
			this.candidates[i].setEnabled(true);
		}
		
		int disabledSize = disabled.size();
		for(int i = 0; i < disabledSize; i += 1) {
			int indexToDisable = disabled.get(i) - 1;
			this.candidates[indexToDisable].setEnabled(false);
		}
	}
	
	public void NewGame(int order) {
		this.Clear();

		final CandidatesLayout self = this;
		
		plusButton.setEnabled(true);
		minusButton.setEnabled(true);
		
		this.candidates = new CustomButton[order];
		for(int i = 0; i < order; i += 1) {
			this.candidates[i] = new CustomButton(this.getContext());
			this.candidates[i].setEnabled(true);
			this.candidates[i].setHasLeftCurve(false);
			this.candidates[i].setHasRightCurve(false);
			this.candidates[i].setIsCheckable(true);
			this.candidates[i].setCheckedNoTrigger(false);
			this.candidates[i].setValue(i + 1);
			this.candidates[i].setText(((Integer)(i + 1)).toString());
			this.candidates[i].AddCheckChangedListener(
				new CustomButton.CheckChangedListener() {
					public void onCheckChanged(CustomButton.CheckChangedEvent event) {
						self.handleCheckChanged((CustomButton)event.getSource());
					}
				}
			);
			this.addView(this.candidates[i], 30, ViewGroup.LayoutParams.FILL_PARENT);
		}
	}
	
	public void Clear() {
		// Remove candidate views if they are there
		if(this.candidates != null) {
			this.removeViews(4, this.candidates.length);
			
			for(int i = 0; i < candidates.length; i += 1) {
				this.candidates[i].ClearCheckChangedListeners();
			}
		}
		
		plusButton.setEnabled(false);
		minusButton.setEnabled(false);
	}
	
	public CandidatesLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		this.setPadding(5, 5, 5, 5);
		
		final CandidatesLayout self = this;
		
		// Candidates layout... Add the + and - buttons
		plusButton = new CustomButton(context);
		plusButton.setEnabled(false);
		plusButton.setHasLeftCurve(true);
		plusButton.setHasRightCurve(true);
		plusButton.setIsCheckable(false);
		plusButton.setCheckedNoTrigger(true);
		plusButton.setText(context.getString(R.string.plus));
		plusButton.AddClickListener(new CustomButton.ClickListener() {
			public void onClick(EventObject event) {
				self.populateAllClicked();
			}
		});

		minusButton = new CustomButton(context);
		minusButton.setEnabled(false);
		minusButton.setHasLeftCurve(true);
		minusButton.setHasRightCurve(true);
		minusButton.setIsCheckable(false);
		minusButton.setCheckedNoTrigger(true);
		minusButton.setText(context.getString(R.string.minus));
		minusButton.AddClickListener(new CustomButton.ClickListener() {
			public void onClick(EventObject event) {
				self.clearAllClicked();
			}
		});
		
		this.addView(plusButton, 30, ViewGroup.LayoutParams.FILL_PARENT);
		this.addView(new TextView(context), 5, ViewGroup.LayoutParams.FILL_PARENT);
		this.addView(minusButton, 30, ViewGroup.LayoutParams.FILL_PARENT);
		this.addView(new TextView(context), 5, ViewGroup.LayoutParams.FILL_PARENT);
	}
}
