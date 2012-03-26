package com.anthonysottile.json;

public @interface JsonSerialize {
	public final String UsePropertyName = "";
	public final String UseToStringOrToJson = "toString";
	
	String propertyName() default UsePropertyName;
	boolean serealizesToPrimitive() default true;
	String serializeMethodName() default UseToStringOrToJson;
}
