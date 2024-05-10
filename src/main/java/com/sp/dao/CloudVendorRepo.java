package com.sp.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.sp.model.CloudVendor;

public interface CloudVendorRepo extends JpaRepository<CloudVendor, Integer> {

//	@Query("select v from CloudVendor v where lower(v.vendorName)=lower(:vendorName)")
//	List<CloudVendor> findByVendorName(String vendorName);
	
	List<CloudVendor> findByVendorNameIgnoreCase(String vendorName);

	boolean existsByMobile(Long mobile);

	CloudVendor findByMobile(Long mobile);

}
