package com.anthonysottile.kenken.ui;

import com.anthonysottile.kenken.R;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

public class KenKenStatistics extends Activity {

	private Spinner dropdown = null;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout.LayoutParams rootLayout =
    		new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT
			);
        LinearLayout root = new LinearLayout(this);
        root.setLayoutParams(rootLayout);
        root.setOrientation(LinearLayout.VERTICAL);
        
        TextView statisticsLabel = new TextView(this);
        statisticsLabel.setTextColor(Color.LTGRAY);
        statisticsLabel.setText(this.getString(R.string.statistics));
        statisticsLabel.setTextSize(20);
        root.addView(
    		statisticsLabel,
    		LayoutParams.FILL_PARENT,
    		LayoutParams.WRAP_CONTENT
		);
        
        LinearLayout gameSizeLayout = new LinearLayout(this);
        
        
        this.setContentView(root);
    }
	
}
