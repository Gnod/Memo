package com.gnod.memo.tool;

public class StringHelper {

	public static Boolean isNullOrEmpty(String input){
		
		return (input == null || input.equalsIgnoreCase(""));
	}
}
