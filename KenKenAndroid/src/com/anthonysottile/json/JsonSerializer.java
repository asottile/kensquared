package com.anthonysottile.json;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.Queue;

import org.json.JSONObject;

public class JsonSerializer {

	private class QueueItem {
		public JSONObject parentJson;
		public Object parentObject;
		public Field field;
		public QueueItem(JSONObject parentJson, Object parentObject, Field field) {
			this.parentJson = parentJson;
			this.parentObject = parentObject;
			this.field = field;
		}
	}
	
	public JSONObject Serialize(Object object) {
		
		Queue<QueueItem> queue = new LinkedList<QueueItem>();
		
		Class<?> reflectedClass = object.getClass();
		Field[] fields = reflectedClass.getDeclaredFields();
		JSONObject json = new JSONObject();
		
		for(int i = 0; i < fields.length; i += 1) {
			queue.add(new QueueItem(json, object, fields[i]));
		}
		
		while(!queue.isEmpty()) {
			QueueItem item = queue.remove();
			JSONObject j = item.parentJson;
			Object o = item.parentObject;
			Field f = item.field;
			
			try {
				Object child = f.get(o);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		
		return json;
	}
}
