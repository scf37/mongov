package ru.scf37.mongov.dao;

import java.util.Date;

import org.bson.types.ObjectId;

import com.mongodb.util.JSON;

public class IdUtil {
	public static String getType(Object id) {
		return id.getClass().getSimpleName();
	}
	
	public static Object makeId(String stringId, String type) {
		if (type.equals("ObjectId")) {
			return new ObjectId(stringId);
		}
		
		if (type.equals("Integer")) {
			return Integer.parseInt(stringId);
		}
		
		if (type.equals("Long")) {
			return Long.parseLong(stringId);
		}
		
		if (type.equals("Date")) {
			return Date.parse(stringId);
		}
		
		if (type.equals("String")) {
			return stringId;
		}
		
		if (type.equals("DBObject")) {
			return JSON.parse(stringId);
		}
		
		throw new RuntimeException("Unknown id '" + stringId + "' of type '" + type + "'");
	}
}
