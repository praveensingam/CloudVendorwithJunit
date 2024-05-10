package com.sp.service;

import java.util.List;

import com.sp.model.CloudVendor;

public interface CloudVendorService {

	String addCloudVendor(CloudVendor cloudVendor);

	CloudVendor getVendorById(Integer vendorId);

	String deleteVendorById(Integer vendorId);

	String updateById(Integer vendorId, CloudVendor cloudVendor);

	List<CloudVendor> findByName(String vendorName);

	List<CloudVendor> findAllVendors();

}
