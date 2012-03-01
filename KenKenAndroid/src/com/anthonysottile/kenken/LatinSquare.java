package com.anthonysottile.kenken;

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
}
