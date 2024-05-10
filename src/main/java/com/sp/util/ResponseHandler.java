package com.sp.util;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ResponseHandler {// can say as Utility class ( collection of helper methods which are static in general)
	//No need of instantiation, so no non static variables
	//private constructor and final class are characteristics
	
	private ResponseHandler() {// To prevent accidental instantiation
		
	}

	public static ResponseEntity<Object> responseBuilder(String message, HttpStatus httpStatus, Object data) {

		Map<String, Object> response = new HashMap<>();

		response.put("Data", data);
		response.put("Message", message);
		response.put("HttpStatus", httpStatus.name());

		return new ResponseEntity<>(response, httpStatus);

	}
   //This structure resembles JSON in that it consists of key-value pairs, which is a characteristic of JSON.
	//However, it's important to note that this structure is not a strict JSON object.
	
	// JSON has specific rules for formatting, such as using double quotes for strings, using colons to separate keys and values,
	//and using curly braces to encapsulate objects.
	
//	The structure returned by ResponseEntity<Object> may not adhere to these rules exactly, but it serves 
//	the purpose of conveying data in a structured format that can be easily serialized into JSON by the framework.
}
