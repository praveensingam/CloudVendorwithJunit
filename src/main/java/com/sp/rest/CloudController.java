package com.sp.rest;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sp.model.CloudVendor;
import com.sp.service.CloudVendorService;
import com.sp.util.ResponseHandler;

import jakarta.persistence.EntityNotFoundException;

/*Even though the HttpResponse is created within the controller method, it still goes through
the Spring MVC framework for processing and handling before being sent to the client. This allows 
the framework to handle tasks such as content negotiation, serialization, and setting response headers 
according to the configuration and requirements of the application.*/

@RestController
public class CloudController {

	@Autowired
	CloudVendorService vendorService;

	Logger logger = LoggerFactory.getLogger(CloudController.class);
	
	@PostMapping("/add-vendor")
	public ResponseEntity<String> addVendor(@RequestBody CloudVendor cloudVendor) {
		
		logger.info("Provided Vendor Info to addVendor method is "+cloudVendor);

		String msg = vendorService.addCloudVendor(cloudVendor);
		
		logger.info("Response received from service class to addVendor method is : "+msg);

		if (msg.contains("saved successfully")) {
			return new ResponseEntity<>(msg, HttpStatus.CREATED);
		} else if (msg.contains("Failed to save")) {
			return new ResponseEntity<>(msg, HttpStatus.EXPECTATION_FAILED);
		} else if (msg.contains("same mobile number already exists")) {
			return new ResponseEntity<>(msg, HttpStatus.CONFLICT);
		} else {
			return new ResponseEntity<>(msg, HttpStatus.BAD_REQUEST);
		}
	}
	// An HttpResponse: refers to the response sent by a server to fulfill an Http
	// request
	// made by a client.

	// HttPResponse = Status + Headers + Body

	// while every response in a REST API is indeed an HTTP response, the format of
	// the response body (e.g., JSON, XML, custom format) is determined by
	// application logic and requirements, and it's not inherently part of the HTTP
	// response itself.
	// Serialization: involves converting the object's state (its fields,
	// properties, and values) into a format that can be easily reconstructed later.
	// This typically involves converting the object into a byte stream or a
	// text-based format such as JSON or XML.

	// If we return object/message directly also it will get serialized
	// automatically but By returning JSON, you have more control over the structure
	// and content of
	// the response data, allowing you to include additional metadata or customize
	// the response as needed.

	@GetMapping("/get_vendor/{vendorId}")
	public ResponseEntity<Object> getVendorInfoById(@PathVariable Integer vendorId) {

		logger.info("Provided vendorId to getVendorInfoById method is : "+vendorId);
		
		CloudVendor vendor = vendorService.getVendorById(vendorId);
		
		logger.info("Search results for provided vendorId in getVendorInfoById method is : "+vendor);

		if (vendor != null) {
			return ResponseHandler.responseBuilder("Available Info of Vendor with Id " + vendorId, HttpStatus.FOUND,
					vendor);
		} else
			return ResponseHandler.responseBuilder("Vendor not found with Id " + vendorId, HttpStatus.NOT_FOUND, null);

	}

	@DeleteMapping("/delete-info/{vendorId}")
	public ResponseEntity<String> deleteVendorInfo(@PathVariable Integer vendorId) {

        logger.info("Provided vendorId to deleteVendorInfo method is : "+vendorId);
		// vendorId = null; //null, indicating that it doesn't point to any valid object
		// in memory (can initialize only wrapper classes with null, not primitive data
		// types)
		try {
			String response = vendorService.deleteVendorById(vendorId);
            logger.info("Response recieved from service class to deletevendorInfo method is "+response);
			HttpStatus status = response.startsWith("Vendor info deleted") ? HttpStatus.OK : HttpStatus.NOT_FOUND;

			return new ResponseEntity<>(response, status);
			// ResponseEntity is a powerful and versatile class in Spring MVC that allows
			// you to construct and customize
			// HTTP responses with fine-grained control over the status code, headers, and
			// body.
		} catch (IllegalArgumentException e) {
			logger.error("Excception caught in DeleteVendorInfo method is "+e.getMessage());
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}

	@PutMapping("/update-vendor/{vendorId}")
	public ResponseEntity<Object> updateVendorInfo(@PathVariable Integer vendorId,
			@RequestBody CloudVendor cloudVendor) {

		logger.info("Provided VendorId and Vendor Details to updateVendorInfo method are : "+ vendorId +" & "+cloudVendor);
		
		String msg = vendorService.updateById(vendorId, cloudVendor);
		
		logger.info("Response recieved in UpdateVendorInfo method from service class is : "+msg);

		HttpStatus status = msg.startsWith("Vendor details updated") ? HttpStatus.OK : HttpStatus.BAD_REQUEST;

		return new ResponseEntity<>(msg, status);

	}

//			Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
//
//			if (responseBody != null && responseBody.containsKey("Data")) {
//				ObjectMapper objectMapper = new ObjectMapper();
//
//				vendor = objectMapper.convertValue(responseBody.get("Data"), CloudVendor.class);
//
//				System.out.println(vendor);
//			}
//		}
//
//		String msg = null;
//
//		if (vendor.equals(new CloudVendor())) {
//			System.out.println(vendor);
//			return "Failed to update vendor info";
//		} else {
//			System.out.println(vendor);
//			msg = vendorService.updateById(cloudVendor);
//			return msg;
//		}

	@GetMapping(value = { "/findByName", "/findByName/{vendorName}" })
	public ResponseEntity<Object> findVendorInfoByName(
			@PathVariable(value = "vendorName", required = false) String vendorName) {
		
		logger.info("Provided vendor name to findVendorInfoByName method is : "+vendorName);

		List<CloudVendor> vendorFound = null;
		try {
			vendorFound = vendorService.findByName(vendorName);
			logger.info("vendor found using findVendorInfoByName method is : "+vendorFound);
			return ResponseHandler.responseBuilder("Vendor Info Fetched", HttpStatus.FOUND, vendorFound);
		} catch (IllegalArgumentException e) {
			logger.error("Exception caught in findVendorInfoByName method is : "+e);
			return new ResponseEntity<>("Vendor name should not be null or blank", HttpStatus.BAD_REQUEST);
		} catch (EntityNotFoundException e) {
			logger.error("Exception caught in findVendorInfoByName method is : "+e);
			return new ResponseEntity<>("No Entity was present with given vendorName", HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			logger.error("Exception caught in findVendorInfoByName method is : "+e);
			return new ResponseEntity<>("Failed to fetch Vendor Details", HttpStatus.EXPECTATION_FAILED);
		}

	}

	@GetMapping("/findAll")
	public ResponseEntity<Object> getAllVendors() {

		List<CloudVendor> vendorsList = vendorService.findAllVendors();
		
		logger.info("VendorsList found using getAllVendors method is : "+vendorsList);

		if (!vendorsList.isEmpty()) {
			return ResponseHandler.responseBuilder("List of Vendors Available", HttpStatus.FOUND, vendorsList);
		} else {
			return ResponseHandler.responseBuilder("Vendors are not available", HttpStatus.NOT_FOUND, vendorsList);
		}
	}
}
