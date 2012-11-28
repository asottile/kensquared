package com.anthonysottile.kenken.ui;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.anthonysottile.kenken.R;

public final class BitmapCache {

	private static Resources resources = null;

	private static Bitmap enabledLeft = null;
	private static Bitmap enabledCenter = null;
	private static Bitmap enabledRight = null;

	private static Bitmap disabledLeft = null;
	private static Bitmap disabledCenter = null;
	private static Bitmap disabledRight = null;

	private static Bitmap selectedLeft = null;
	private static Bitmap selectedCenter = null;
	private static Bitmap selectedRight = null;

	public static Bitmap getEnabledLeft() {
		if (BitmapCache.enabledLeft == null) {
			BitmapCache.enabledLeft =
				BitmapFactory.decodeResource(BitmapCache.resources, R.drawable.left);
		}
		return BitmapCache.enabledLeft;
	}

	public static Bitmap getEnabledCenter() {
		if (BitmapCache.enabledCenter == null) {
			BitmapCache.enabledCenter =
				BitmapFactory.decodeResource(BitmapCache.resources, R.drawable.middle);
		}
		return BitmapCache.enabledCenter;
	}

	public static Bitmap getEnabledRight() {
		if (BitmapCache.enabledRight == null) {
			BitmapCache.enabledRight =
				BitmapFactory.decodeResource(BitmapCache.resources, R.drawable.right);
		}
		return BitmapCache.enabledRight;
	}

	public static Bitmap getDisabledLeft() {
		if (BitmapCache.disabledLeft == null) {
			BitmapCache.disabledLeft =
				BitmapFactory.decodeResource(BitmapCache.resources, R.drawable.left_disabled);
		}
		return BitmapCache.disabledLeft;
	}

	public static Bitmap getDisabledCenter() {
		if (BitmapCache.disabledCenter == null) {
			BitmapCache.disabledCenter =
				BitmapFactory.decodeResource(BitmapCache.resources, R.drawable.middle_disabled);
		}
		return BitmapCache.disabledCenter;
	}

	public static Bitmap getDisabledRight() {
		if (BitmapCache.disabledRight == null) {
			BitmapCache.disabledRight =
				BitmapFactory.decodeResource(BitmapCache.resources, R.drawable.right_disabled);
		}
		return BitmapCache.disabledRight;
	}

	public static Bitmap getSelectedLeft() {
		if (BitmapCache.selectedLeft == null) {
			BitmapCache.selectedLeft =
				BitmapFactory.decodeResource(BitmapCache.resources, R.drawable.left_selected);
		}
		return BitmapCache.selectedLeft;
	}

	public static Bitmap getSelectedCenter() {
		if (BitmapCache.selectedCenter == null) {
			BitmapCache.selectedCenter =
				BitmapFactory.decodeResource(BitmapCache.resources, R.drawable.middle_selected);
		}
		return BitmapCache.selectedCenter;
	}

	public static Bitmap getSelectedRight() {
		if (BitmapCache.selectedRight == null) {
			BitmapCache.selectedRight =
				BitmapFactory.decodeResource(BitmapCache.resources, R.drawable.right_selected);
		}
		return BitmapCache.selectedRight;
	}

	/**
	 * Initialization method to give the cache a reference to resources.
	 *
	 * @param resources Application resources to load bitmaps from.
	 */
	public static void Initialize(Resources resources) {
		BitmapCache.resources = resources;
	}

	private BitmapCache() { }
}
