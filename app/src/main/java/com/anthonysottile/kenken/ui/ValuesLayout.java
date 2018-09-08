package com.anthonysottile.kenken.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.anthonysottile.kenken.ui.CustomButton.CheckChangedEvent;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.EventObject;
import java.util.List;
import java.util.Set;

public class ValuesLayout extends LinearLayout {

    private static final LayoutParams buttonsLayoutParams =
            new LinearLayout.LayoutParams(
                    30,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    0.5f
            );

    public class ValueEvent extends EventObject {
        private final int value;

        public int getValue() {
            return this.value;
        }

        public ValueEvent(Object sender, int value) {
            super(sender);

            this.value = value;
        }
    }

    public interface ValueChangedListener extends EventListener {
        void onValueChanged(ValueEvent event);
    }

    private final List<ValueChangedListener> valueChangedListeners =
            new ArrayList<ValueChangedListener>();

    public void AddValueChangedListener(ValueChangedListener listener) {
        this.valueChangedListeners.add(listener);
    }

    private void triggerValueChanged(int value) {
        ValueEvent event = new ValueEvent(this, value);

        for (ValueChangedListener listener : this.valueChangedListeners) {
            listener.onValueChanged(event);
        }
    }

    private CustomButton[] valueButtons = null;
    private CustomButton selectedButton = null;

    private final CustomButton.CheckChangedListener checkChangedListener =
            event -> {
                CustomButton valueButton = (CustomButton) event.getSource();

                if (valueButton.getChecked()) {
                    // Button became checked
                    if (ValuesLayout.this.selectedButton != null) {
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
            };

    public void SetDisabled(Set<Integer> disabled) {
        // First enable all the buttons
        for (CustomButton valueButton : this.valueButtons) {
            valueButton.setEnabled(true);
        }

        for (int x : disabled) {
            this.valueButtons[x - 1].setEnabled(false);
        }
    }

    public void SetDisabled() {
        for (CustomButton valueButton : this.valueButtons) {
            valueButton.setEnabled(false);
        }
    }

    /**
     * Sets the value of this control.  Note, this does not bubble an event
     * as it should only be called when selecting a square.
     *
     * @param value The value to set.
     */
    public void SetValue(int value) {
        // Do not trigger events as this should only be called from
        //  the setup/tear-down of a square being clicked.

        // Uncheck the current selected button if it is checked
        if (this.selectedButton != null) {
            this.selectedButton.setCheckedNoTrigger(false);
            this.selectedButton = null;
        }

        // If the value is not the "uncheck" value then set the check
        if (value != 0) {
            this.selectedButton = this.valueButtons[value - 1];
            this.selectedButton.setCheckedNoTrigger(true);
        }
    }

    public void Clear() {
        if (this.valueButtons != null) {
            this.removeAllViews();

            for (CustomButton valueButton : this.valueButtons) {
                valueButton.ClearCheckChangedListeners();
            }

            this.valueButtons = null;
        }
    }

    /**
     * Setup method for when a new game is started.
     *
     * @param gameSize The size of the game.
     */
    public void NewGame(int gameSize) {
        this.Clear();

        this.valueButtons = new CustomButton[gameSize];
        for (int i = 0; i < gameSize; i += 1) {
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

        // Set the first and last buttons to be curved (left and right).
        this.valueButtons[0].setHasLeftCurve(true);
        this.valueButtons[this.valueButtons.length - 1].setHasRightCurve(true);
    }

    public ValuesLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.setPadding(5, 15, 5, 15);
    }
}
