package com.anthonysottile.kenken.ui

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.LinearLayout
import java.util.*

class ValuesLayout(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {

    private val valueChangedListeners = ArrayList<(Int) -> Unit>()

    private var valueButtons: Array<CustomButton>? = null
    private var selectedButton: CustomButton? = null

    private fun checkChangedListener(valueButton: CustomButton) {
        if (valueButton.checked) {
            // Button became checked
            if (this@ValuesLayout.selectedButton != null) {
                this@ValuesLayout.selectedButton!!.setCheckedNoTrigger(false)
                this@ValuesLayout.selectedButton = null
            }

            this@ValuesLayout.selectedButton = valueButton
            this@ValuesLayout.triggerValueChanged(valueButton.value)

        } else {
            // Button became unchecked
            this@ValuesLayout.selectedButton = null
            this@ValuesLayout.triggerValueChanged(0)
        }
    }

    fun addValueChangedListener(listener: (Int) -> Unit) {
        this.valueChangedListeners.add(listener)
    }

    private fun triggerValueChanged(value: Int) {
        for (listener in this.valueChangedListeners) {
            listener(value)
        }
    }

    fun setDisabled(disabled: Set<Int>) {
        // First enable all the buttons
        for (valueButton in this.valueButtons!!) {
            valueButton.uiEnabled = true
        }

        for (x in disabled) {
            this.valueButtons!![x - 1].uiEnabled = false
        }
    }

    fun setDisabled() {
        for (valueButton in this.valueButtons!!) {
            valueButton.uiEnabled = false
        }
    }

    /**
     * Sets the value of this control.  Note, this does not bubble an event
     * as it should only be called when selecting a square.
     *
     * @param value The value to set.
     */
    fun setValue(value: Int) {
        // Do not trigger events as this should only be called from
        //  the setup/tear-down of a square being clicked.

        // Uncheck the current selected button if it is checked
        if (this.selectedButton != null) {
            this.selectedButton!!.setCheckedNoTrigger(false)
            this.selectedButton = null
        }

        // If the value is not the "uncheck" value then set the check
        if (value != 0) {
            this.selectedButton = this.valueButtons!![value - 1]
            this.selectedButton!!.setCheckedNoTrigger(true)
        }
    }

    fun clear() {
        if (this.valueButtons != null) {
            this.removeAllViews()
            this.valueButtons = null
        }
    }

    fun newGame(gameSize: Int) {
        this.clear()

        this.valueButtons = Array(gameSize) { i ->
            val button = CustomButton(
                    this.context,
                    true,
                    false,
                    i + 1,
                    Integer.toString(i + 1, 10),
                    i == 0,
                    i == gameSize - 1,
                    true
            )

            button.addCheckChangedListener(this::checkChangedListener)

            this@ValuesLayout.addView(button, ValuesLayout.buttonsLayoutParams)

            return@Array button
        }
    }

    init {
        this.setPadding(5, 15, 5, 15)
    }

    companion object {
        private val buttonsLayoutParams = LinearLayout.LayoutParams(
                30,
                ViewGroup.LayoutParams.MATCH_PARENT,
                0.5f
        )
    }
}
