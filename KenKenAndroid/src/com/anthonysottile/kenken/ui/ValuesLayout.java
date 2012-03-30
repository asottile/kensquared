package com.anthonysottile.kenken.ui;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.EventObject;
import java.util.List;

import com.anthonysottile.kenken.ui.CustomButton.CheckChangedEvent;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class ValuesLayout extends LinearLayout {
	
	private static final LayoutParams buttonsLayoutParams =
		new LinearLayout.LayoutParams(
			30,
			ViewGroup.LayoutParams.FILL_PARENT,
			0.5f
		);
	
	//#region Events
	
	public class ValueEvent extends EventObject {
		
		private static final long serialVersionUID = 4677958792610955100L;
		
		private int value;
		public int getValue() {
			return this.value;
		}
		
		public ValueEvent(Object sender, int value) {
			super(sender);
			
			this.value = value;
		}
	}
	
	public interface ValueChangedListener extends EventListener {
		public void onValueChanged(ValueEvent event);
	}
	
	private List<ValueChangedListener> valueChangedListeners =
		new ArrayList<ValueChangedListener>();
	public void AddValueChangedListener(ValueChangedListener listener) {
		this.valueChangedListeners.add(listener);
	}
	public void RemoveValueChangedListener(ValueChangedListener listener) {
		this.valueChangedListeners.remove(listener);
	}
	private void triggerValueChanged(int value) {
		ValueEvent event = new ValueEvent(this, value);
		int size = this.valueChangedListeners.size();
		for(int i = 0; i < size; i += 1) {
			this.valueChangedListeners.get(i).onValueChanged(event);
		}
	}
	
	//#endregion
	
	private CustomButton[] valueButtons = null;
	private CustomButton selectedButton = null;
	
	private final CustomButton.CheckChangedListener checkChangedListener =
		new CustomButton.CheckChangedListener() {
			public void onCheckChanged(CheckChangedEvent event) {
				CustomButton valueButton = (CustomButton)event.getSource();
				
				if(valueButton.getChecked()) {
					
					// Button became checked
					if(ValuesLayout.this.selectedButton != null) {
						ValuesLayout.this.selectedButton.setCheckedNoTrigger(false);
						ValuesLayout.this.selectedButton = null;
					}
					
					ValuesLayout.this.selectedButton = valueButton;
					ValuesLayout.this.triggerValueChanged(valueButton.getValue());
					
				} else {
					
					// Button became unchecked
					ValuesLayout.this.selectedButton = null;
					ValuesLayout.this.triggerValueChanged(0);
				}
			}
		};
		
	public void SetDisabled(List<Integer> disabled) {
		// First enable all the buttons
		for(int i = 0; i < valueButtons.length; i += 1) {
			this.valueButtons[i].setEnabled(true);
		}
		
		// Then disable the guys that we are supposed to
		int disabledSize = disabled.size();
		for(int i = 0; i < disabledSize; i += 1) {
			this.valueButtons[disabled.get(i) - 1].setEnabled(false);
		}
	}
	
	public void SetDisabled() {
		// Disable all of the buttons.
		for(int i = 0; i < this.valueButtons.length; i += 1) {
			this.valueButtons[i].setEnabled(false);
		}
	}
	
	public void SetValue(int value) {

		// Do not trigger events as this should only be called from
		//  the setup/tear-down of a square being clicked.
		
		// Uncheck the current selected button if it is checked
		if(this.selectedButton != null) {
			this.selectedButton.setCheckedNoTrigger(false);
			this.selectedButton = null;
		}
		
		// If the value is not the "uncheck" value then set the check
		if(value != 0) {
			this.selectedButton = this.valueButtons[value - 1];
			this.selectedButton.setCheckedNoTrigger(true);
		}
	}
	
	public void TrySetValue(int value) {
		// Only set the value if it is enabled
		// Also when checking or unchecking the target button,
		//  trigger the check events.
		if(this.valueButtons[value - 1].getEnabled()) {
			if(this.valueButtons[value - 1].getChecked()) {
				// This button is currently selected
				// Uncheck it and set the currentSelectedButton to null
				this.selectedButton.setChecked(false);
				this.selectedButton = null;
			} else {
				
				// Uncheck the current selected button if it is checked
				if(this.selectedButton != null) {
					this.selectedButton.setCheckedNoTrigger(false);
					this.selectedButton = null;
				}
				
				this.selectedButton = this.valueButtons[value - 1];
				this.selectedButton.setChecked(true);
			}
		}
	}
	
	public void Clear() {
		if(this.valueButtons != null) {
			this.removeAllViews();
			
			for(int i = 0; i < this.valueButtons.length; i += 1) {
				this.valueButtons[i].ClearCheckChangedListeners();
			}
			
			this.valueButtons = null;
		}
	}
	
	public void NewGame(int order) {
		this.Clear();

		this.valueButtons = new CustomButton[order];
		for(int i = 0; i < order; i += 1) {
			this.valueButtons[i] = new CustomButton(this.getContext());
			this.valueButtons[i].setEnabled(true);
			this.valueButtons[i].setHasLeftCurve(false);
			this.valueButtons[i].setHasRightCurve(false);
			this.valueButtons[i].setIsCheckable(true);
			this.valueButtons[i].setCheckedNoTrigger(false);
			this.valueButtons[i].setValue(i + 1);
			this.valueButtons[i].setText(Integer.toString(i + 1, 10));
			this.valueButtons[i].AddCheckChangedListener(this.checkChangedListener);
			
			this.addView(this.valueButtons[i], ValuesLayout.buttonsLayoutParams);
		}
		
		this.valueButtons[0].setHasLeftCurve(true);
		this.valueButtons[this.valueButtons.length - 1].setHasRightCurve(true);
	}
	
	public ValuesLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		this.setPadding(5, 15, 5, 15);
	}
}
