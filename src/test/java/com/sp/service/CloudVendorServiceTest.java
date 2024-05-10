package com.sp.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.OptimisticLockingFailureException;

import com.sp.dao.CloudVendorRepo;
import com.sp.model.CloudVendor;

import jakarta.persistence.EntityNotFoundException;

@SpringBootTest
class CloudVendorServiceTest {

	@Mock
	private CloudVendorRepo cloudVendorRepo;
	@InjectMocks // can not be used on interfaces.
	private CloudVendorServiceImpl cloudVendorService;
	AutoCloseable autoCloseable;
	CloudVendor cloudVendor;
	CloudVendor cloudVendor2;

	List<CloudVendor> vendorList = null;

	@BeforeEach
	void setUp() {

		autoCloseable = MockitoAnnotations.openMocks(this);
		cloudVendor = new CloudVendor(1, "Praveen", "AWS", 9876543210l);
		cloudVendor2 = new CloudVendor(2, "Praveen", "GCP", 8796453210l);
		// cloudVendorService = new CloudVendorServiceImpl(cloudVendorRepo);
		vendorList = new ArrayList<>();

		vendorList.add(cloudVendor);
		vendorList.add(cloudVendor2);

		// Ensure that the mock interactions do not rely on the previous test case (as
		// sometimes modifications may happen to mock object in previous test cases)
		//reset(cloudVendorRepo); // Resetting mock for test case independence
		
		//Not necessary as we are using  .openMocks()

	}

	@AfterEach
	void tearDown() throws Exception {
		autoCloseable.close();
	}

	@Test
	void addCloudVendor_success() {
		
		long mobile = 9876543210l;

		when(cloudVendorRepo.existsByMobile(mobile)).thenReturn(false);

		when(cloudVendorRepo.save(cloudVendor)).thenReturn(cloudVendor);

		String result = cloudVendorService.addCloudVendor(cloudVendor);

		assertEquals("Vendor details saved successfully", result);

		verify(cloudVendorRepo, times(1)).save(cloudVendor);
//		statement is saying: "Verify that the save() method of "
//	     + "the cloudVendorRepo mock object was called exactly once with the "
//	     + "cloudVendor object as an argument during the test execution."
	}

	@Test
	void addCloudVendor_MobileExists_DuplicationMsg() {

		long mobile = 9876543210l;
		
		when(cloudVendorRepo.existsByMobile(mobile)).thenReturn(true);

//		String result = cloudVendorService.addCloudVendor(cloudVendor);
//
//		assertEquals("Vendor with the same vendorId already exists", result);
		
		String response = cloudVendorService.addCloudVendor(cloudVendor);
		
		assertEquals("Vendor with the same mobile number already exists", response);
		
		verify(cloudVendorRepo, never()).save(cloudVendor);
	}

	@Test
	void addCloudVendor_WhenNull_ThrowsException() {

		assertThrows(IllegalArgumentException.class, () -> {
			cloudVendorService.addCloudVendor(null);
		});

		verify(cloudVendorRepo, never()).save(null);
	}
	
	@Test
	void addCloudVendor_UnExpectedException() {
		
		when(cloudVendorRepo.save(cloudVendor))
		.thenThrow(new OptimisticLockingFailureException("Concurrency conflict: Data has been modified by another transaction. Please refresh and retry"));
		
		assertEquals(new Exception("Failed to save Vendor Details").getMessage(),cloudVendorService.addCloudVendor(cloudVendor));
		
		verify(cloudVendorRepo, times(1)).save(cloudVendor);
	}

	@Test // success
	void cloudVendorGetById_ReturnsValidVendor_WhenExists() {//can use anyInt() instead of cloudvendor.getVendorId()
		
		when(cloudVendorRepo.findById(cloudVendor.getVendorId())).thenReturn(Optional.ofNullable(cloudVendor));

		CloudVendor actual = cloudVendorService.getVendorById(cloudVendor.getVendorId());

		assertEquals(cloudVendor, actual);
		
		verify(cloudVendorRepo, times(1)).findById(cloudVendor.getVendorId());

	}

	@Test // failure
	void cloudVendorGetById_Empty() {

		when(cloudVendorRepo.findById(cloudVendor.getVendorId())).thenReturn(Optional.ofNullable(null));

		CloudVendor actual = cloudVendorService.getVendorById(cloudVendor.getVendorId());

		assertNull(actual);
		
		verify(cloudVendorRepo, times(1)).findById(cloudVendor.getVendorId());

	}
	@Test
	void cloudVendorGetById_NullException() {
		
		assertThrows(IllegalArgumentException.class,()->cloudVendorService.getVendorById(null));
		
		verify(cloudVendorRepo, never()).findById(null);
	}

	@Test // null check
	void deleteVendorById_ThrowsException_WhenIdIsNull() {
		
		assertThrows(IllegalArgumentException.class, () -> cloudVendorService.deleteVendorById(null));

		verify(cloudVendorRepo, never()).deleteById(null);
	}

	@Test // failure
	void deleteVendorById_vendorNotExistById() {

		Integer vendorId = 3;
		
		when(cloudVendorRepo.existsById(vendorId)).thenReturn(false);

		assertThat(cloudVendorService.deleteVendorById(vendorId)).contains("vendor does not exist with Id ");

		verify(cloudVendorRepo, never()).deleteById(vendorId);
	}

	@Test // success
	void deleteCloudVendorById_DeletesVendor_WhenIdExists() {

		Integer vendorId = 1;
		
		when(cloudVendorRepo.existsById(vendorId)).thenReturn(true);	

		String result =cloudVendorService.deleteVendorById(vendorId);
		
		assertEquals("Vendor info deleted existing with vendorId " + vendorId, result);

		verify(cloudVendorRepo, times(1)).deleteById(vendorId);
	}
	
	@Test // Success
	void updateById_VendorExist_Updated() {

		Integer vendorId =2;
		
		when(cloudVendorRepo.findByMobile(cloudVendor2.getMobile())).thenReturn(cloudVendor2);
		
		when(cloudVendorRepo.findById(vendorId)).thenReturn(Optional.ofNullable(cloudVendor2));

		String response = cloudVendorService.updateById(vendorId, cloudVendor2);

		assertEquals("Vendor details updated successfully", response);

		verify(cloudVendorRepo, times(1)).save(any());
	}

	@Test // failure 8796453210l
	void updateById_VendorNotExistWithId() {

		Integer vendorId = 3;
		
		cloudVendor = new CloudVendor("rich","jio",8796453213l); //for this method only
		
		when(cloudVendorRepo.findByMobile(cloudVendor.getMobile())).thenReturn(null);
		
		when(cloudVendorRepo.findById(vendorId)).thenReturn(Optional.empty());

		String response = cloudVendorService.updateById(vendorId, cloudVendor);

		assertEquals("Vendor does not exist with given vendorId", response);

		verify(cloudVendorRepo).findById(vendorId);//by default checks for method called once
	}

	@Test
	void updateByID_NullCheck_OfVendorId_CloudVendor() {
		
		String result = cloudVendorService.updateById(null, cloudVendor);
		
		assertEquals("Invalid input: vendorId or cloudVendor is null", result);
		
		String response = cloudVendorService.updateById(cloudVendor.getVendorId(), null);
		
		assertEquals("Invalid input: vendorId or cloudVendor is null", response);
		
		verify(cloudVendorRepo,never()).save(any());
		
	}
	@Test
	void updateById_MobileDuplicated() {//9876543210l
		
		cloudVendor2 = new CloudVendor("rich","jio",9876543210l); //for this method only
		
		Integer vendorId =2 ;
		
		when(cloudVendorRepo.findByMobile(cloudVendor2.getMobile())).thenReturn(cloudVendor);
		
		String result = cloudVendorService.updateById(vendorId, cloudVendor2);
		
		assertEquals("Mobile number can not be duplicated", result);
	}
	
	@Test
	void findByName_Found() {

		when(cloudVendorRepo.findByVendorNameIgnoreCase(cloudVendor.getVendorName())).thenReturn(vendorList);

//		when(cloudVendorRepo.findByVendorName(cloudVendor.getVendorName()))
//	    .thenReturn(new ArrayList<>(Collections.singleton(cloudVendor))); //if we do not have list of values to return

		assertEquals(cloudVendorService.findByName(cloudVendor.getVendorName()).get(0).getVendorId(),
				cloudVendor.getVendorId());

		verify(cloudVendorRepo, times(1)).findByVendorNameIgnoreCase(cloudVendor.getVendorName());
	}

	@Test
	void findByName_NotFound_ThrowException() {
		
		String vendorName = "Singam";

		when(cloudVendorRepo.findByVendorNameIgnoreCase(vendorName))
				.thenReturn(Collections.emptyList());

	//	assertThat(cloudVendorService.findByName(vendorName)).isEqualTo(EntityNotFoundException.class);

		assertThrows(EntityNotFoundException.class, ()-> cloudVendorService.findByName(vendorName));
		
		verify(cloudVendorRepo, times(1)).findByVendorNameIgnoreCase(vendorName);
	}
    
	@Test
	void findByName_WhenVendorNameNull_ThrowException() {
		
		assertThrows(IllegalArgumentException.class, ()-> cloudVendorService.findByName(null));
		
		verify(cloudVendorRepo, never()).findByVendorNameIgnoreCase(null);
	}
	
	@Test
	void findByName_WheVendorNameEmpty_ThrowException() {
		
		assertThrows(IllegalArgumentException.class, ()-> cloudVendorService.findByName(""));
		
		verify(cloudVendorRepo, never()).findByVendorNameIgnoreCase("");
		
	}
	
	@Test // success
	void findAllVendors_Found() {

		when(cloudVendorRepo.findAll()).thenReturn(vendorList);

		// assertThat(cloudVendorService.findAllVendors().get(1)).isEqualTo(cloudVendor2);
		// //use it for more specific assertions

		assertEquals(vendorList, cloudVendorService.findAllVendors());

		verify(cloudVendorRepo, times(1)).findAll();
	}

	@Test // failure
	void findAllVendors_NotFound() {

		when(cloudVendorRepo.findAll()).thenReturn(Collections.emptyList());

		//assertNull(cloudVendorService.findAllVendors()); 

		assertTrue(cloudVendorService.findAllVendors().isEmpty());
		
		verify(cloudVendorRepo, times(1)).findAll();
	}
}
