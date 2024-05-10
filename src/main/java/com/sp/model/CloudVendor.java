package com.sp.model;

import java.io.Serializable;
import java.util.Objects;

import jakarta.annotation.Nonnull;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table(name = "Cloud_vendor_Info")
public class CloudVendor implements Serializable{
  
	@Id
	@SequenceGenerator(name="vendor_id_seq",sequenceName="vendor_id_seq",allocationSize = 1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="vendor_id_seq")
	private Integer vendorId;
	private String vendorName;
	private String vendorDetails;
	@Column(unique=true)
	@Nonnull
	private Long mobile;
	public CloudVendor() {
		super();
	}
	public CloudVendor(Integer vendorId,String vendorName, String vendorDetails, Long mobile) {
		super();
		this.vendorId = vendorId;
		this.vendorName = vendorName;
		this.vendorDetails = vendorDetails;
		this.mobile = mobile;
	}
	public CloudVendor(String vendorName, String vendorDetails, long mobile) {
		this.vendorName = vendorName;
		this.vendorDetails = vendorDetails;
		this.mobile = mobile;
	}
	@Override
	public String toString() {
		return "CloudVendor [vendorId=" + vendorId + ", vendorName=" + vendorName + ", vendorDetails=" + vendorDetails
				+ ", mobile=" + mobile + "]";
	}
	public Integer getVendorId() {
		return vendorId;
	}
	public void setVendorId(Integer vendorId) {
		this.vendorId = vendorId;
	}
	public String getVendorName() {
		return vendorName;
	}
	public void setVendorName(String vendorName) {
		this.vendorName = vendorName;
	}
	public String getVendorDetails() {
		return vendorDetails;
	}
	public void setVendorDetails(String vendorDetails) {
		this.vendorDetails = vendorDetails;
	}
	public Long getMobile() {
		return mobile;
	}
	public void setMobile(Long mobile) {
		this.mobile = mobile;
	}
	@Override
	public int hashCode() {
		return Objects.hash(mobile, vendorDetails, vendorId, vendorName);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CloudVendor other = (CloudVendor) obj;
		return Objects.equals(mobile, other.mobile) && Objects.equals(vendorDetails, other.vendorDetails)
				&& Objects.equals(vendorId, other.vendorId) && Objects.equals(vendorName, other.vendorName);
	}
}
