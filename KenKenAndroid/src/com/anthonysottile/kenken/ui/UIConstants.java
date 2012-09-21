package com.anthonysottile.kenken.ui;

import android.graphics.Color;
import android.graphics.Paint;

public final class UIConstants {

	public static final int MinGameSize = 4;
	public static final int MaxGameSize = 9;
	public static final int GameSizes = UIConstants.MaxGameSize - UIConstants.MinGameSize + 1;

	public static final int BorderWidth = 2;

	public static final int StatisticsOneIndent = 20;

	public static final int CandidatesTextColor = Color.rgb(0x77, 0x77, 0x77);

	private static Paint gridColor = null;
	public static Paint GetGridColor() {
		if (UIConstants.gridColor == null) {
			UIConstants.gridColor = new Paint();
			UIConstants.gridColor.setColor(Color.rgb(0xCC, 0xCC, 0xCC));
		}
		return UIConstants.gridColor;
	}

	private static Paint cageColor = null;
	public static Paint GetCageColor() {
		if (UIConstants.cageColor == null) {
			UIConstants.cageColor = new Paint();
			UIConstants.cageColor.setColor(Color.rgb(0x00, 0x00, 0x00));
		}
		return UIConstants.cageColor;
	}

	private static Paint hoveringColor = null;
	public static Paint GetHoveringColor() {
		if (UIConstants.hoveringColor == null) {
			UIConstants.hoveringColor = new Paint();
			UIConstants.hoveringColor.setColor(Color.rgb(0x1e, 0x77, 0xd3));
		}
		return UIConstants.hoveringColor;
	}

	private static Paint selectedColor = null;
	public static Paint GetSelectedColor() {
		if (UIConstants.selectedColor == null) {
			UIConstants.selectedColor = new Paint();
			UIConstants.selectedColor.setColor(Color.rgb(0x00, 0xC0, 0xC0));
		}
		return UIConstants.selectedColor;
	}

	private static Paint backgroundColor = null;
	public static Paint GetBackgroundColor() {
		if (UIConstants.backgroundColor == null) {
			UIConstants.backgroundColor = new Paint();
			UIConstants.backgroundColor.setColor(Color.rgb(0xff, 0xff, 0xff));
		}
		return UIConstants.backgroundColor;
	}

	private static Paint markedIncorrectColor = null;
	public static Paint GetMarkedIncorrectColor() {
		if (UIConstants.markedIncorrectColor == null) {
			UIConstants.markedIncorrectColor = new Paint();
			UIConstants.markedIncorrectColor.setColor(Color.rgb(0xff, 0x66, 0x66));
		}
		return UIConstants.markedIncorrectColor;
	}

	private UIConstants() { }
}
