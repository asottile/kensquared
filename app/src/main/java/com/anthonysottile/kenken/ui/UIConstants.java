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

    private UIConstants() {
    }
}
