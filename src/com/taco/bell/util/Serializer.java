package com.taco.bell.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


public class Serializer {
	private static Gson gson = new GsonBuilder().serializeNulls().create();;

	public static Object deserialize(String object, Class classType) {
		try {
			return gson.fromJson(object,classType);
		} catch( Exception e ) {
			return null;
		}
	}


	public static String serialize(Object object) {
		try {
			//return xstream.toXML(object);
			return gson.toJson(object);
		} catch( Exception e ) {
			e.printStackTrace();
			return null;
		}
	}

}
