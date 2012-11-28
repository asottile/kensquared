package com.anthonysottile.kenken.ui;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.EventObject;
import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.anthonysottile.kenken.R;
import com.anthonysottile.kenken.ui.CustomButton.CheckChangedEvent;

public class CandidatesLayout extends LinearLayout {

	private static final LayoutParams allNoneLayoutParams =
		new LinearLayout.LayoutParams(
			30,
			ViewGroup.LayoutParams.FILL_PARENT,
			0.3f
		);

	private static final LayoutParams buttonsLayoutParams =
		new LinearLayout.LayoutParams(
			30,
			ViewGroup.LayoutParams.FILL_PARENT,
			0.5f
		);

	//#region Events

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

	private final List<CandidateAddedListener> candidateAddedListeners =
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
		for (int i = 0; i < size; i += 1) {
			this.candidateAddedListeners.get(i).onCandidateAdded(event);
		}
	}

	private final List<CandidateRemovedListener> candidateRemovedListeners =
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
		for (int i = 0; i < size; i += 1) {
			this.candidateRemovedListeners.get(i).onCandidateRemoved(event);
		}
	}

	//#endregion

	private CustomButton plusButton = null;
	private CustomButton minusButton = null;
	private CustomButton[] candidates = null;

	private void populateAllClicked() {
		for (int i = 0; i < this.candidates.length; i += 1) {
			if (this.candidates[i].getEnabled() && !this.candidates[i].getChecked()) {
				this.candidates[i].setChecked(true);
			}
		}
	}

	private void clearAllClicked() {
		for (CustomButton candidate : this.candidates) {
			if (candidate.getEnabled() && candidate.getChecked()) {
				candidate.setChecked(false);
			}
		}
	}

	private final CustomButton.CheckChangedListener checkChangedListener =
		new CustomButton.CheckChangedListener() {
			public void onCheckChanged(CheckChangedEvent event) {
				CustomButton button = (CustomButton)event.getSource();
				if (button.getChecked()) {
					CandidatesLayout.this.triggerCandidateAdded(button.getValue());
				} else {
					CandidatesLayout.this.triggerCandidateRemoved(button.getValue());
				}
			}
		};

	/**
	 * Sets the values of the candidates control.  The values are passed in
	 *  as booleans representing the checked state of each candidate.
	 *  Note: this does not trigger any events as this should only be
	 *   called from ui setting of a square.
	 *
	 * @param values The checked state of each candidate.
	 */
	public void SetValues(boolean[] values) {
		for (int i = 0; i < values.length; i += 1) {
			this.candidates[i].setCheckedNoTrigger(values[i]);
		}
	}

	/**
	 * Attempts to set the candidate of the given value.  If it is disabled it
	 *  does nothing.  If it is checked it becomes unchecked.  Note: this triggers
	 *  events as this should only be called from key press.
	 *
	 * @param value The value to attempt to set.
	 */
	public void TrySetValue(int value) {
		if (this.candidates != null && this.candidates[value - 1].getEnabled()) {
			this.candidates[value - 1].toggleChecked();
		}
	}

	/**
	 * Sets all of the candidate buttons to disabled.
	 */
	public void SetDisabled() {
		if (this.candidates != null) {
			for (CustomButton candidate : this.candidates) {
				candidate.setEnabled(false);
			}
		}
	}

	/**
	 * Sets the specific candidate buttons to disabled.
	 *
	 * @param disabled The candidate buttons to disabled.
	 */
	public void SetDisabled(List<Integer> disabled) {

		for (CustomButton candidate : this.candidates) {
			candidate.setEnabled(true);
		}

		int disabledSize = disabled.size();
		for (int i = 0; i < disabledSize; i += 1) {
			int indexToDisable = disabled.get(i) - 1;
			this.candidates[indexToDisable].setEnabled(false);
		}
	}


	/**
	 * Setup method for when a new game is started.
	 *
	 * @param gameSize The size of the game.
	 */
	public void NewGame(int gameSize) {
		this.Clear();

		// Candidates layout... Add the + and - buttons
		this.plusButton = new CustomButton(this.getContext());
		this.plusButton.setEnabled(true);
		this.plusButton.setHasLeftCurve(true);
		this.plusButton.setHasRightCurve(true);
		this.plusButton.setIsCheckable(false);
		this.plusButton.setCheckedNoTrigger(true);
		this.plusButton.setText(this.getContext().getString(R.string.plus));
		this.plusButton.AddClickListener(new CustomButton.ClickListener() {
			public void onClick(EventObject event) {
				CandidatesLayout.this.populateAllClicked();
			}
		});

		this.minusButton = new CustomButton(this.getContext());
		this.minusButton.setEnabled(true);
		this.minusButton.setHasLeftCurve(true);
		this.minusButton.setHasRightCurve(true);
		this.minusButton.setIsCheckable(false);
		this.minusButton.setCheckedNoTrigger(true);
		this.minusButton.setText(this.getContext().getString(R.string.minus));
		this.minusButton.AddClickListener(new CustomButton.ClickListener() {
			public void onClick(EventObject event) {
				CandidatesLayout.this.clearAllClicked();
			}
		});

		this.addView(this.plusButton, CandidatesLayout.allNoneLayoutParams);
		this.addView(new TextView(this.getContext()), 5, ViewGroup.LayoutParams.FILL_PARENT);
		this.addView(this.minusButton, CandidatesLayout.allNoneLayoutParams);
		this.addView(new TextView(this.getContext()), 5, ViewGroup.LayoutParams.FILL_PARENT);

		this.candidates = new CustomButton[gameSize];
		for (int i = 0; i < gameSize; i += 1) {
			this.candidates[i] = new CustomButton(this.getContext());
			this.candidates[i].setEnabled(true);
			this.candidates[i].setHasLeftCurve(false);
			this.candidates[i].setHasRightCurve(false);
			this.candidates[i].setIsCheckable(true);
			this.candidates[i].setCheckedNoTrigger(false);
			this.candidates[i].setValue(i + 1);
			this.candidates[i].setText(Integer.toString(i + 1, 10));
			this.candidates[i].AddCheckChangedListener(this.checkChangedListener);

			this.addView(this.candidates[i], CandidatesLayout.buttonsLayoutParams);
		}

		this.candidates[0].setHasLeftCurve(true);
		this.candidates[this.candidates.length - 1].setHasRightCurve(true);
	}

	/**
	 * Clears the candidates control removing all ui elements.
	 */
	public void Clear() {
		// Remove candidate views if they are there
		if (this.candidates != null) {
			this.removeAllViews();

			for (CustomButton candidate : this.candidates) {
				candidate.ClearCheckChangedListeners();
			}

			this.candidates = null;
		}
	}

	public CandidatesLayout(Context context, AttributeSet attrs) {
		super(context, attrs);

		this.setPadding(5, 15, 5, 15);
	}
}
