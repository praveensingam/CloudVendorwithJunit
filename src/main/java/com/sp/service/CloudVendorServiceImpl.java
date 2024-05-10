package com.sp.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.sp.dao.CloudVendorRepo;
import com.sp.model.CloudVendor;

import jakarta.persistence.EntityNotFoundException;

@Service
public class CloudVendorServiceImpl implements CloudVendorService {

	// @Autowired
	CloudVendorRepo vendorRepo;

	public CloudVendorServiceImpl(CloudVendorRepo cloudVendorRepo) {
		this.vendorRepo = cloudVendorRepo;
	}

	@Override
	public String addCloudVendor(CloudVendor cloudVendor) {
        
		if (cloudVendor == null) {
			throw new IllegalArgumentException("Cloud vendor should not be nul");
		}

		if (vendorRepo.existsByMobile(cloudVendor.getMobile())) {
			return "Vendor with the same mobile number already exists";
		}
		try {
			vendorRepo.save(cloudVendor);
			return "Vendor details saved successfully";
		} catch (Exception e) {
			return "Failed to save Vendor Details";
		}
	}

	@Override
	public CloudVendor getVendorById(Integer vendorId) {

//		Optional<CloudVendor> ops = vendorRepo.findById(vendorId);
//		
//		if(ops.isEmpty())
//			return new CloudVendor();
//		else
//		return ops.get();

		if (vendorId == null) {
			throw new IllegalArgumentException("VendorId should not be null");
		}
		return vendorRepo.findById(vendorId).orElse(null);
	}

	@Override
	public String deleteVendorById(Integer vendorId) {

		if (vendorId == null) {
			throw new IllegalArgumentException("VendorId should not be null");
		}

		// Check if vendorId exist in the database
		if (!vendorRepo.existsById(vendorId))
			return "vendor does not exist with Id " + vendorId;

		// Delete the vendor Information
		vendorRepo.deleteById(vendorId);

		return "Vendor info deleted existing with vendorId " + vendorId;

	}

	@Override
	public String updateById(Integer vendorId, CloudVendor cloudVendor) {

		if (vendorId == null || cloudVendor == null) {
			return "Invalid input: vendorId or cloudVendor is null";
		}

		if (isMobileDuplicated(vendorId, cloudVendor)) {
			return "Mobile number can not be duplicated";
		}

		Optional<CloudVendor> vendor = vendorRepo.findById(vendorId);

		if (vendor.isPresent()) {

			CloudVendor existingVendor = vendor.get();
			existingVendor.setVendorName(cloudVendor.getVendorName());
			existingVendor.setVendorDetails(cloudVendor.getVendorDetails());
			existingVendor.setMobile(cloudVendor.getMobile());

			vendorRepo.save(existingVendor);

			return "Vendor details updated successfully";
		} else
			return "Vendor does not exist with given vendorId";
	}

	private boolean isMobileDuplicated(Integer vendorId, CloudVendor cloudVendor) {

		CloudVendor existingVendor = vendorRepo.findByMobile(cloudVendor.getMobile());

		if (existingVendor != null)

			return !vendorId.equals(existingVendor.getVendorId());

		else

			return false;

	}

	@Override
	public List<CloudVendor> findByName(String vendorName) {

		if (vendorName.trim().equalsIgnoreCase("null") || vendorName.isBlank()) {
			throw new IllegalArgumentException("Vendor name should not be null or blank");
		}

		List<CloudVendor> cloudVendor = vendorRepo.findByVendorNameIgnoreCase(vendorName);

		if (!cloudVendor.isEmpty())
			return cloudVendor;
		else
			throw new EntityNotFoundException("No Entity was present with given vendorName");
	}

	@Override
	public List<CloudVendor> findAllVendors() {
		return vendorRepo.findAll();
	}
}
