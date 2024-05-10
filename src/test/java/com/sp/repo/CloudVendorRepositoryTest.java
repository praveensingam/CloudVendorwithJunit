package com.sp.repo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import com.sp.dao.CloudVendorRepo;
import com.sp.model.CloudVendor;

@SpringBootTest
//@DataJpaTest // It will consider embedded database(h2) by default
class CloudVendorRepositoryTest {
//JUnit5 test classes and methods should have default package visibility.
	
	/*
	 * This is because test classes and methods are typically meant to be used
	 * within the same package as the class being tested. Declaring them as public
	 * could potentially expose them to other packages, which may not be necessary
	 * and could lead to unintentional usage or coupling between packages.
	 */
	
	CloudVendor cloudVendor = null;
	
	List<CloudVendor> vendorsList = new ArrayList<>();

	@Mock
	CloudVendorRepo cloudVendorRepo;

	@BeforeEach
	void setUp() {
		cloudVendor = new CloudVendor(1, "Someone", "AWS", 7298439280l);
       
		vendorsList.add(cloudVendor);
		
	}

	@AfterEach
	void tearDown() {
		cloudVendor = null;
		
		vendorsList.clear();
		
		Mockito.reset(cloudVendorRepo);
		// cloudVendorRepo.deleteAll();
	}

	@Test // Test case Success
	@DisplayName("Test FindByName Success")
	void findByVendorName_Found() {
		
		String vendorName = "Someone";
		//List<CloudVendor> vendorsList = cloudVendorRepo.findByVendorName("jeff bezos");
		
		when(cloudVendorRepo.findByVendorNameIgnoreCase(vendorName)).thenReturn(vendorsList);
		
		assertEquals(vendorsList, cloudVendorRepo.findByVendorNameIgnoreCase(vendorName));

		assertThat(vendorsList.get(0).getVendorId()).isEqualTo(cloudVendor.getVendorId());

		assertThat(vendorsList.get(0).getVendorName()).isEqualTo(cloudVendor.getVendorName());

	}

	@Test // Negative testing
	@DisplayName("Test FindByName Failure")
	void findByVendorName_NotFound() {

		String vendorName = "Something";
		//List<CloudVendor> vendorList = cloudVendorRepo.findByVendorName("Praveen");
		
		when(cloudVendorRepo.findByVendorNameIgnoreCase(vendorName)).thenReturn(Collections.emptyList());
		
		assertTrue(cloudVendorRepo.findByVendorNameIgnoreCase(vendorName).isEmpty());

		verify(cloudVendorRepo, times(1)).findByVendorNameIgnoreCase(vendorName);

	}

	@Test
	void findByVendorName_CaseSensitivity() {
		
		String vendorName = "someone";
		
		when(cloudVendorRepo.findByVendorNameIgnoreCase(vendorName)).thenReturn(vendorsList);

		assertEquals(vendorsList, cloudVendorRepo.findByVendorNameIgnoreCase(vendorName));
	
	}

	@Test
	void existsByMobile_Found() {

		Long mobileNo = 7298439280l;

		when(cloudVendorRepo.existsByMobile(mobileNo)).thenReturn(true);

		assertTrue(cloudVendorRepo.existsByMobile(mobileNo));

		verify(cloudVendorRepo, times(1)).existsByMobile(mobileNo);

	}

	@Test
	void existsByMobile_NotFound() {

		Long mobileNo = 7718439280l;

		when(cloudVendorRepo.existsByMobile(mobileNo)).thenReturn(false);

		assertFalse(cloudVendorRepo.existsByMobile(mobileNo));

		verify(cloudVendorRepo, times(1)).existsByMobile(mobileNo);

	}

	@Test
	void findByMobile_Found() {

		Long mobileNo = 7298439280l;

		when(cloudVendorRepo.findByMobile(mobileNo)).thenReturn(cloudVendor);

		assertEquals(cloudVendor, cloudVendorRepo.findByMobile(mobileNo));

		verify(cloudVendorRepo, times(1)).findByMobile(mobileNo);
	}

	@Test
	void findByMobile_NotFound() {

		Long mobileNo = 7898439280l;

		when(cloudVendorRepo.findByMobile(mobileNo)).thenReturn(null);

		assertEquals(null, cloudVendorRepo.findByMobile(mobileNo));

		verify(cloudVendorRepo, times(1)).findByMobile(mobileNo);
	}
}
