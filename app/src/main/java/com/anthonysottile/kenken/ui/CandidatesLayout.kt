package com.anthonysottile.kenken.ui

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import java.util.*

class CandidatesLayout(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {

    private val candidateAddedListeners = ArrayList<(Int) -> Unit>()

    private val candidateRemovedListeners = ArrayList<(Int) -> Unit>()

    private var candidates: Array<CustomButton>? = null

    fun addCandidateAddedListener(listener: (Int) -> Unit) {
        this.candidateAddedListeners.add(listener)
    }

    private fun triggerCandidateAdded(candidate: Int) {
        for (listener in this.candidateAddedListeners) {
            listener(candidate)
        }
    }

    fun addCandidateRemovedListener(listener: (Int) -> Unit) {
        this.candidateRemovedListeners.add(listener)
    }

    private fun triggerCandidateRemoved(candidate: Int) {
        for (listener in this.candidateRemovedListeners) {
            listener(candidate)
        }
    }

    private fun populateAllClicked(unused: CustomButton) {
        for (candidate in this.candidates!!) {
            if (candidate.uiEnabled) {
                candidate.checked = true
            }
        }
    }

    private fun clearAllClicked(unused: CustomButton) {
        for (candidate in this.candidates!!) {
            if (candidate.uiEnabled) {
                candidate.checked = false
            }
        }
    }

    private fun checkChangedListener(button: CustomButton) {
        if (button.checked) {
            this@CandidatesLayout.triggerCandidateAdded(button.value)
        } else {
            this@CandidatesLayout.triggerCandidateRemoved(button.value)
        }
    }

    /**
     * Sets the values of the candidates control.  The values are passed in
     * as booleans representing the checked state of each candidate.
     * Note: this does not trigger any events as this should only be
     * called from ui setting of a square.
     *
     * @param values The checked state of each candidate.
     */
    fun setValues(values: BooleanArray) {
        for ((i, value) in values.withIndex()) {
            this.candidates!![i].setCheckedNoTrigger(value)
        }
    }

    fun setDisabled() {
        for (candidate in this.candidates!!) {
            candidate.uiEnabled = false
        }
    }

    /**
     * Sets the specific candidate buttons to disabled.
     *
     * @param disabled The candidate buttons to disabled.
     */
    fun setDisabled(disabled: Set<Int>) {
        for (candidate in this.candidates!!) {
            candidate.uiEnabled = true
        }

        for (x in disabled) {
            this.candidates!![x - 1].uiEnabled = false
        }
    }

    /**
     * Setup method for when a new game is started.
     *
     * @param gameSize The size of the game.
     */
    fun newGame(gameSize: Int) {
        this.clear()

        // Candidates layout... Add the + and - buttons
        val plusButton = CustomButton(
                this.context,
                true,
                true,
                0,
                "+",
                true,
                true,
                false
        )
        plusButton.addClickListener(this::populateAllClicked)

        val minusButton = CustomButton(
                this.context,
                true,
                true,
                0,
                "-",
                true,
                true,
                false
        )
        minusButton.addClickListener(this::clearAllClicked)

        this.addView(plusButton, CandidatesLayout.allNoneLayoutParams)
        this.addView(TextView(this.context), 5, ViewGroup.LayoutParams.MATCH_PARENT)
        this.addView(minusButton, CandidatesLayout.allNoneLayoutParams)
        this.addView(TextView(this.context), 5, ViewGroup.LayoutParams.MATCH_PARENT)

        this.candidates = Array(gameSize) { i ->
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

            this.addView(button, CandidatesLayout.buttonsLayoutParams)
            return@Array button
        }
    }

    /**
     * Clears the candidates control removing all ui elements.
     */
    fun clear() {
        if (this.candidates != null) {
            this.removeAllViews()
            this.candidates = null
        }
    }

    init {

        this.setPadding(5, 15, 5, 15)
    }

    companion object {
        private val allNoneLayoutParams = LinearLayout.LayoutParams(
                30,
                ViewGroup.LayoutParams.MATCH_PARENT,
                0.3f
        )

        private val buttonsLayoutParams = LinearLayout.LayoutParams(
                30,
                ViewGroup.LayoutParams.MATCH_PARENT,
                0.5f
        )
    }
}
