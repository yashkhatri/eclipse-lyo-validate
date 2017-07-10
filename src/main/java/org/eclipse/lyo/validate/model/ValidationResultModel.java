package org.eclipse.lyo.validate.model;

import java.util.List;

/**
 * The Class ValidationResultModel.
 * @author Yash Khatri (yash.s.khatri@gmail.com)
 */
public class ValidationResultModel {

	/** The total number of resources. */
	private long totalNumberOfResources;
	
	/** The valid resource count. */
	private long validResourceCount;
	
	/** The invalid resource count. */
	private long invalidResourceCount;
	
	/** The valid resources. */
	private List<ResourceModel> validResources;
	
	/** The invalid resources. */
	private List<ResourceModel> invalidResources;
	
	/**
	 * Gets the total number of resources.
	 *
	 * @return the total number of resources
	 */
	public long getTotalNumberOfResources() {
		return totalNumberOfResources;
	}
	
	/**
	 * Sets the total number of resources.
	 *
	 * @param totalNumberOfResources the new total number of resources
	 */
	public void setTotalNumberOfResources(long totalNumberOfResources) {
		this.totalNumberOfResources = totalNumberOfResources;
	}
	
	/**
	 * Gets the valid resource count.
	 *
	 * @return the valid resource count
	 */
	public long getValidResourceCount() {
		return validResourceCount;
	}
	
	/**
	 * Gets the invalid resource count.
	 *
	 * @return the invalid resource count
	 */
	public long getInvalidResourceCount() {
		return invalidResourceCount;
	}
	
	/**
	 * Gets the valid resources.
	 *
	 * @return the valid resources
	 */
	public List<ResourceModel> getValidResources() {
		return validResources;
	}
	
	/**
	 * Gets the invalid resources.
	 *
	 * @return the invalid resources
	 */
	public List<ResourceModel> getInvalidResources() {
		return invalidResources;
	}
	
	/**
	 * Sets the valid count.
	 *
	 * @param validCount the new valid count
	 */
	public void setValidCount(long validCount) {
		this.validResourceCount = validCount;
	}
	
	/**
	 * Sets the invalid count.
	 *
	 * @param invalidCount the new invalid count
	 */
	public void setInvalidCount(long invalidCount) {
		this.invalidResourceCount = invalidCount;
	}
	
	/**
	 * Sets the valid resources.
	 *
	 * @param validResources2 the new valid resources
	 */
	public void setValidResources
	(List<ResourceModel> validResources2) {
		this.validResources = validResources2;
	}
	
	/**
	 * Sets the invalid resources.
	 *
	 * @param validResources2 the new invalid resources
	 */
	public void setInvalidResources(List<ResourceModel> validResources2) {
		this.invalidResources = validResources2;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ValidationResultModel [totalNumberOfResources=" + totalNumberOfResources + ", validResourceCount="
				+ validResourceCount + ", invalidResourceCount=" + invalidResourceCount + ", validResources="
				+ validResources + ", invalidResources=" + invalidResources + "]";
	}
}
