package com.anthonysottile.kenken;

import org.json.JSONException;
import org.json.JSONObject;

public class SignNumber {

	private Sign sign;
	public Sign getSign() {
		return this.sign;
	}

	private int number;
	public int getNumber() {
		return this.number;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.number);
		sb.append(' ');
		sb.append(this.sign.toString());

		return sb.toString();    }

	public SignNumber(Sign sign, int number) {
		this.sign = sign;
		this.number = number;
	}

	// #region JSON Serialization

	private static final String signProperty = "Sign";
	private static final String numberProperty = "Number";

	public SignNumber(JSONObject json) {

		try {
			this.sign = Sign.toSign(json.getInt(SignNumber.signProperty));
			this.number = json.getInt(SignNumber.numberProperty);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public JSONObject ToJson() {
		JSONObject json = new JSONObject();

		try {
			json.put(SignNumber.signProperty, this.sign.getIntValue());
			json.put(SignNumber.numberProperty, this.number);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return json;
	}

	// #endregion
}
