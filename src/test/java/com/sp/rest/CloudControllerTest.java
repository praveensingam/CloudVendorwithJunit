package com.sp.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sp.model.CloudVendor;
import com.sp.service.CloudVendorService;

import jakarta.persistence.EntityNotFoundException;

@WebMvcTest(CloudController.class)
class CloudControllerTest {

	//To generate Jacoco report first add jacoco plugin in pom.xml and then run maven build with "clean test" goal.
	//After build got succeed then run maven build with "jacoco:report" goal. then open index.html(present in generated report) with web browser.
	
	//To work with sonar qube first d/I sq, then start startsonar.bat present in bin folder. Then we can open sonar interface using Localhost:9000 .
	//Then generate security code use it to pass as maven goal in application (refer text files)
	
	@Autowired
	private MockMvc mockMvc; // mock implementation for the Spring MVC infrastructure.

	@MockBean
	CloudVendorService vendorService;

	CloudVendor vendorFound;

	CloudVendor cloudVendor2;

	List<CloudVendor> vendorsList;

	@BeforeEach
	void setUp() {
		vendorFound = new CloudVendor(1, "looser", "None", 9999888770l);
		cloudVendor2 = new CloudVendor(2, "lazy", "Some", 9999888771l);
		vendorsList = new ArrayList<CloudVendor>();

		vendorsList.add(vendorFound);
		vendorsList.add(cloudVendor2);
	}

	@AfterEach
	void tearDown() {

	}

	@Test
	void addVendor_Success() throws Exception {

		when(vendorService.addCloudVendor(cloudVendor2)).thenReturn("Vendor details saved successfully");

		this.mockMvc
				.perform(post("/add-vendor").accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON)
						.content(asJsonString(cloudVendor2)))// passing the data
				.andDo(print()).andExpect(status().isCreated())
				.andExpect(content().string("Vendor details saved successfully"));

		verify(vendorService, times(1)).addCloudVendor(cloudVendor2);
	}

	@Test
	void addVendor_Failed() throws Exception {

		when(vendorService.addCloudVendor(cloudVendor2)).thenReturn("Failed to save Vendor Details");

		this.mockMvc
				.perform(post("/add-vendor").accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON)
						.content(asJsonString(cloudVendor2)))
				.andDo(print()).andExpect(status().isExpectationFailed())
				.andExpect(content().string("Failed to save Vendor Details"));

	}
	
	@Test
	void addVendor_mobileNoDuplicated() throws Exception {
		
		when(vendorService.addCloudVendor(cloudVendor2)).thenReturn("Vendor with the same mobile number already exists");
		
		this.mockMvc.perform(post("/add-vendor").accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON).content(asJsonString(cloudVendor2)))
		.andDo(print()).andExpect(status().isConflict())
		.andExpect(content().string("Vendor with the same mobile number already exists"));
		
	}
//	@Test
//	void addVendor_Exception_WhenNullCloudVendor() throws Exception {
//		
//		CloudVendor vendor=null;
//		when(vendorService.addCloudVendor(vendor)).thenThrow(new IllegalArgumentException("Cloud vendor should not be null"));
//		
//		this.mockMvc.perform(post("/add-vendor").accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON)
//				.content(asJsonString(vendor)))
//		.andDo(print())
//		.andExpect(status().isBadRequest())
//		.andExpect(content().string("Cloud vendor should not be null"));
//		
//	} 
	
	//mockMvc  allows to stimulates (work as client to send request Ex: browser, postman) HTTP requests and interact with controllers

	// Helper method to convert Object to JSON string
	private String asJsonString(CloudVendor cloudVendor2) {

		try {
			return new ObjectMapper().writeValueAsString(cloudVendor2);
			// when we use writeValueAsString from the ObjectMapper class,
			// it serializes the cloudVendor2 object into a JSON string representation
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}

	@Test
	void updateVenodrInfo_success() throws Exception {

		Integer vendorId = 2;

		when(vendorService.updateById(vendorId, cloudVendor2)).thenReturn("Vendor details updated successfully");

		this.mockMvc
				.perform(put("/update-vendor/{vendorId}", vendorId).accept(MediaType.APPLICATION_JSON)
						.contentType(MediaType.APPLICATION_JSON).content(asJsonString(cloudVendor2)))
				.andDo(print()).andExpect(status().isOk())
				.andExpect(content().string("Vendor details updated successfully"));

		verify(vendorService, times(1)).updateById(vendorId, cloudVendor2);
	}

	@Test
	void updateVendor_FialedToUpdate() throws Exception {

		Integer vendorId = 3;

		when(vendorService.updateById(vendorId, cloudVendor2)).thenReturn("Vendor does not exist with given vendorId");

		this.mockMvc
				.perform(put("/update-vendor/{vendorId}", vendorId).accept(MediaType.APPLICATION_JSON)
						.contentType(MediaType.APPLICATION_JSON).content(asJsonString(cloudVendor2)))
				.andDo(print()).andExpect(status().isBadRequest())
				.andExpect(content().string("Vendor does not exist with given vendorId"));
	}

	@Test
	void getVendorInfoById_Found() throws Exception {

		Integer vendorId = 1;

		when(vendorService.getVendorById(vendorId)).thenReturn(vendorFound);

		this.mockMvc.perform(get("/get_vendor/{vendorId}", vendorId)).andDo(print()).andExpect(status().isFound())
				.andExpect(jsonPath("$.Message").value("Available Info of Vendor with Id " + vendorId))
				// .andExpect(jsonPath("$.HttpStatus").value(HttpStatus.FOUND.name()))
				.andExpect(jsonPath("$.Data").value(vendorFound));

		verify(vendorService, times(1)).getVendorById(1);
	}

	@Test
	void getVendorInfoById_NotFound() throws Exception {

		Integer vendorId = 3;
		when(vendorService.getVendorById(vendorId)).thenReturn(null);

		this.mockMvc.perform(get("/get_vendor/{vendorId}", vendorId)).andDo(print()).andExpect(status().isNotFound())
				.andExpect(jsonPath("$.Message").value("Vendor not found with Id " + vendorId))
				.andExpect(jsonPath("$.HttpStatus").value(HttpStatus.NOT_FOUND.name()))
				.andExpect(jsonPath("$.Data").doesNotExist());
	}// Here even though ResponseEntity does not contain strict JSON object
		// structure,
		// but it contains a structure that resembles JSON. As a result, you can still
		// use JSONPath expressions
		// to assert values within the response body.

	@Test
	void deleteVendorInfoById_Success() throws Exception {

		Integer vendorId = 1;

		String response = "Vendor info deleted existing with vendorId " + vendorId;

		when(vendorService.deleteVendorById(vendorId)).thenReturn(response);

		this.mockMvc.perform(delete("/delete-info/{vendorId}", vendorId)).andDo(print()).andExpect(status().isOk())
				.andExpect(content().string(response));
	}// Here as ResponseEntity(in controller) does not contain JSON like structured
		// data we can not use JSON path expressions.
		// Here ResponseEntity contains raw String and status fields.

	@Test
	void deleteVendorInfoById_failed() throws Exception {

		Integer vendorId = 3;

		String response = "vendor does not exist with Id " + vendorId;

		when(vendorService.deleteVendorById(vendorId)).thenReturn(response);

		this.mockMvc.perform(delete("/delete-info/{vendorId}", vendorId)).andDo(print())
				.andExpect(content().string(response)).andExpect(status().isNotFound());
	}

//	@Test
//	void deleteVendorInfoById_NullVendorId_ThrowException() throws Exception {
//
//		Integer vendorId = null;
//
//		String message = "VendorId should not be null";
//
//		when(vendorService.deleteVendorById(null)).thenThrow(new IllegalArgumentException(message));
//
//		// doThrow(new
//		// IllegalArgumentException(message)).when(vendorService).deleteVendorById(null);
//
//		mockMvc.perform(delete("/delete-info/{vendorId}", vendorId)).andDo(print()).andExpect(status().isBadRequest())
//				.andExpect(content().string(message));
//
//		verify(vendorService).deleteVendorById(vendorId);
//	}

	// we can expect the controller to return a 400 Bad Request status when such
	// exceptions(illegalargu...) occur

	@Test
	void getAllVendors_Success() throws Exception {

		when(vendorService.findAllVendors()).thenReturn(vendorsList);

		// String expectedJson = new ObjectMapper().writeValueAsString(vendorsList);

		this.mockMvc.perform(get("/findAll")).andDo(print()).andExpect(status().isFound())
				.andExpect(jsonPath("$.Message").value("List of Vendors Available"))
				.andExpect(jsonPath("$.Data").isArray());

		for (int i = 0; i < vendorsList.size(); i++) {
			CloudVendor vendor = vendorsList.get(i);

			this.mockMvc.perform(get("/findAll")) // [%d]: This part is a placeholder for a number (%d). It will be
													// replaced by the index i
					.andExpect(jsonPath(String.format("$.Data[%d].vendorId ", i)).value(vendor.getVendorId()))
					.andExpect(jsonPath(String.format("$.Data[%d].vendorName", i)).value(vendor.getVendorName()))
					.andExpect(jsonPath(String.format("$.Data[%d].vendorDetails", i)).value(vendor.getVendorDetails()))
					.andExpect(jsonPath(String.format("$.Data[%d].mobile", i)).value(vendor.getMobile()));
		}
		// for example, if i is 0, the resulting JSONPath expression would be
		// $.Data[0].vendorId, indicating that
		// we want to access the vendorId field of the first element in the "Data" array
		// of the JSON response. Similarly,
		// for i equal to 1, it would be $.Data[1].vendorId, and so on,

		//but it is causing more number of calls to controller method unnecessarily ( follow findVendorInfoByName_Found()for efficiency)
		verify(vendorService, atLeastOnce()).findAllVendors();

	}

	@Test
	void getAllVendors_Empty() throws Exception {

		when(vendorService.findAllVendors()).thenReturn(Collections.emptyList());

		this.mockMvc.perform(get("/findAll")).andDo(print()).andExpect(status().isNotFound())
				.andExpect(jsonPath("$.Message").value("Vendors are not available"))
				.andExpect(jsonPath("$.Data").isEmpty());

		verify(vendorService, times(1)).findAllVendors();
	}

	@Test
	void findVendorInfoByName_Found() throws Exception {

		String vendorName = "looser";
		List<CloudVendor> vendorsList2 = Collections.singletonList(vendorFound);

		when(vendorService.findByName(vendorName)).thenReturn(vendorsList2);

		MvcResult result =this.mockMvc.perform(get("/findByName/{vendorName}", vendorName)).andDo(print()).andExpect(status().isFound())
				.andExpect(jsonPath("$.Message").value("Vendor Info Fetched")).andReturn();
		// .andExpect(jsonPath("$.Data").value(vendorsList));

		String content =result.getResponse().getContentAsString();
		//When you receive an HTTP response, it's usually in the form of a string. 
		//This string can represent data in various formats such as JSON, XML, HTML, plain text, 
		JSONObject jsonResponse = new JSONObject(content);
		// By converting the response string into a JSONObject, you gain access to methods and properties
		//that simplify data extraction.
		JSONArray data = jsonResponse.getJSONArray("Data");
		//In many API responses, the actual data you're interested in is often encapsulated within a specific key,
		//such as "Data" in our example. To access this data, we need to first parse the response into a JSONObject 
		//and then retrieve the JSONArray associated with the "Data" key 
		
		//Once we pass List<object> data to responsebuilder it converts it into jsonformat before sneding to client. it is the
		//process that converts data into jsonarry so we are retrieving it.
		
		assertEquals(vendorsList2.size(), data.length());
		
		for (int i = 0; i < vendorsList2.size(); i++) {
			JSONObject jsonVendor =data.getJSONObject(i);
			CloudVendor vendor = vendorsList2.get(i);
			
			assertEquals(vendor.getVendorId(), jsonVendor.getInt("vendorId"));
			assertEquals(vendor.getVendorName(), jsonVendor.getString("vendorName"));
			assertEquals(vendor.getMobile(), jsonVendor.getLong("mobile"));
			assertEquals(vendor.getVendorDetails(), jsonVendor.getString("vendorDetails"));
		}
        //verify method invocation
		verify(vendorService, atLeastOnce()).findByName(vendorName);
	}

	@Test
	void findVendorInfoByName_NotFound() throws Exception {

		String vendorName = "Praveen";

		when(vendorService.findByName(vendorName))
				.thenThrow(new EntityNotFoundException("No Entity was present with given vendorName"));

		this.mockMvc.perform(get("/findByName/{vendorName}", vendorName)).andDo(print())
				.andExpect(status().isNotFound())
				.andExpect(content().string("No Entity was present with given vendorName"));
	}

//	@Test
//	void findVendorInfoByName_vendorNameNull_Exception() throws Exception {
//
//		String vendorName = "";
//
//		when(vendorService.findByName(vendorName))
//				.thenThrow(new IllegalArgumentException("Vendor name should not be null or empty"));
//
//		//// get("/findByName/{vendorName}", vendorName) or ("/findByName/")
//		this.mockMvc.perform(MockMvcRequestBuilders.get("/findByName/").param("vendorName", "")
//						.accept(MediaType.APPLICATION_JSON))
//				.andDo(print()).andExpect(status().isBadRequest())
//				.andExpect(content().string("Vendor name should not be null or empty"));
//	}

	@Test
	void findVendorInfoByName_UnknownException() throws Exception {

		when(vendorService.findByName(anyString())).thenThrow(new RuntimeException("Unexpeted Exception occured"));

		this.mockMvc
				.perform(MockMvcRequestBuilders.get("/findByName/{vendorName}", "someone")
						.accept(MediaType.APPLICATION_JSON))
				.andDo(print()).andExpect(status().isExpectationFailed())
				.andExpect(content().string("Failed to fetch Vendor Details"));

		verify(vendorService, times(1)).findByName(anyString());
	}

}
