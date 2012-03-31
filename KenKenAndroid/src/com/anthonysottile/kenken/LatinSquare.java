package com.anthonysottile.kenken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class LatinSquare {

	private int[][] values;
	public int[][] getValues() {
		return this.values;
	}
	
	private int order;
	public int getOrder() {
		return this.order;
	}
	
	public LatinSquare(int order) {
		this.order = order;
		
		// Initialize the numbers array
		//  but not the inner arrays. Those get made later.
		this.values = new int[order][];
		
        NumberPicker picker = new NumberPicker(order);

        // Retrieve the base list
        int[] baseList = new int[order];
        for (int i = 0; i < order; i += 1)
        {
            baseList[i] = picker.GetNext();
        }

        // Generate the rows
        // Rows is indexed [row][column]
        int[][] rows = new int[order][];
        picker.Reset();
        for (int i = 0; i < order; i += 1)
        {
            int[] list = new int[order];
            int shift = picker.GetNext() - 1;
            for (int j = 0; j < order; j += 1)
            {
                // To shift the index correctly
                int index = (j + shift) % order;
                list[index] = baseList[j];
            }

            rows[i] = list;
        }

        // Rotate the square and "shuffle" rows
        // Columns is indexed [column][row]
        int[][] columns = new int[order][];
        for (int i = 0; i < order; i += 1)
        {
            int[] list = new int[order];
            for (int j = 0; j < order; j += 1)
            {
                list[j] = rows[j][i];
            }
            columns[i] = list;
        }

        // Swap a column with its next column
        // This needs to be a single swap with a column next to it
        //  otherwise the original ordering property is restored :(
        picker.Reset();
        int columnIndex = picker.GetNext() - 1;
        int columnIndexNext = (columnIndex + 1) % order;

        int[] tempColumn = columns[columnIndex];
        columns[columnIndex] = columns[columnIndexNext];
        columns[columnIndexNext] = tempColumn;

        // Rotate the square once more and "shuffle" rows
        picker.Reset();
        for (int i = 0; i < order; i += 1)
        {
            int row = picker.GetNext() - 1;
            int[] list = new int[order];
            for (int j = 0; j < order; j += 1)
            {
                list[j] = columns[j][row];
            }
            this.values[i] = list;
        }
	}
	
	// #region JSON Serialization
	
	private static final String orderProperty = "Order";
	private static final String valuesProperty = "Values";
	
	public JSONObject ToJson() {
		JSONObject json = new JSONObject();
		
		try {
			json.put(orderProperty, this.order);
			
			JSONArray outerArray = new JSONArray();
			
			for (int i = 0; i < this.values.length; i += 1) {
				JSONArray innerArray = new JSONArray();
				
				for (int j = 0; j < this.values[i].length; j += 1) {
					innerArray.put(j, this.values[i][j]);
				}
				
				outerArray.put(i, innerArray);
			}
			
			json.put(valuesProperty, outerArray);
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return json;
	}
	
	public LatinSquare(JSONObject json) {
		
		try {
			this.order = json.getInt(orderProperty);
			
			JSONArray outerArray = json.getJSONArray(valuesProperty);
			
			this.values = new int[outerArray.length()][];
			for (int i = 0; i < this.values.length; i += 1) {
				
				JSONArray innerArray = outerArray.getJSONArray(i);
				this.values[i] = new int[this.values.length];
				
				for (int j = 0; j < this.values[i].length; j += 1) {
					this.values[i][j] = innerArray.getInt(j);
				}
			}
			
		} catch(JSONException e) {
			e.printStackTrace();
		}
	}
	
	// #endregion
	
}
