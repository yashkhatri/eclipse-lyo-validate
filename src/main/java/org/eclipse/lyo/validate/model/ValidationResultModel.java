package org.eclipse.lyo.validate.model;

import java.util.List;

public class ValidationResultModel {

	private long totalNumberOfResources;
	private long validResourceCount;
	private long invalidResourceCount;
	
	private List<ResourceModel> validResources;
	private List<ResourceModel> invalidResources;
	
	public long getTotalNumberOfResources() {
		return totalNumberOfResources;
	}
	public void setTotalNumberOfResources(long totalNumberOfResources) {
		this.totalNumberOfResources = totalNumberOfResources;
	}
	public long getValidResourceCount() {
		return validResourceCount;
	}
	public long getInvalidResourceCount() {
		return invalidResourceCount;
	}
	public List<ResourceModel> getValidResources() {
		return validResources;
	}
	public List<ResourceModel> getInvalidResources() {
		return invalidResources;
	}
	public void setValidCount(long validCount) {
		this.validResourceCount = validCount;
	}
	public void setInvalidCount(long invalidCount) {
		this.invalidResourceCount = invalidCount;
	}
	public void setValidResources
	(List<ResourceModel> validResources2) {
		this.validResources = validResources2;
	}
	public void setInvalidResources(List<ResourceModel> validResources2) {
		this.invalidResources = validResources2;
	}
	
	@Override
	public String toString() {
		return "ValidationResultModel [totalNumberOfResources=" + totalNumberOfResources + ", validResourceCount="
				+ validResourceCount + ", invalidResourceCount=" + invalidResourceCount + ", validResources="
				+ validResources + ", invalidResources=" + invalidResources + "]";
	}
}
