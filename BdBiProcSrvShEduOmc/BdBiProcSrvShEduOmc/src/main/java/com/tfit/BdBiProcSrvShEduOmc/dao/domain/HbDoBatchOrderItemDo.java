package com.tfit.BdBiProcSrvShEduOmc.dao.domain;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class HbDoBatchOrderItemDo {
	Long doBatchOrderItemID;
	Long OriginalClientAppID;
	Long BatchOrderID;
	Integer DomainOrderEntityTypeID;
	Long DomainOrderID;
	Integer ProviderEntityTypeID;
	Long ProviderEntityID;
	Integer CustomerEntityTypeID;
	Long CustomerEntityID;
	Long DomainProjectID;
	Long OrderPersonID;
	Integer DomainPolicyEntityTypeID;
	Long DomainPolicyID;
	Integer RackSalesPolicyTypeID;
	Long SalesRackPolicyID;
	Integer PayModeID;
	Integer StatusID;
	Long CustomizedStatusID;
	Short isFulfilled;
	Short isClosed;
	Short isForceClosedByBatchOrder;
	Short isInvalid;
	Short isCancelled;
	Integer GoodsManifestEntityTypeID;
	Long GoodsManifestID;
	Integer CurrencyTypeID;
	BigDecimal Price;
	Integer DistributionNoteEntityTypeID;
	String DistributionNoteIDs;
	Timestamp ExpectedConsumeDatetime;
	Long CreatorID;
	Integer CreatorEntityTypeID;
	Timestamp CreatedDatetime;
	Long LastModifierID;
	Integer LastModifierEntityTypeID;
	Timestamp LastModifiedDatetime;
	Short isDeleted;
	Integer DeletedByEntityTypeID;
	Long DeletedByEntityID;
	Timestamp DeletedDatetime;
	Short isMock;
	Short isTombStone;
	
	public Long getDoBatchOrderItemID() {
		return doBatchOrderItemID;
	}
	public void setDoBatchOrderItemID(Long doBatchOrderItemID) {
		this.doBatchOrderItemID = doBatchOrderItemID;
	}
	public Long getOriginalClientAppID() {
		return OriginalClientAppID;
	}
	public void setOriginalClientAppID(Long originalClientAppID) {
		OriginalClientAppID = originalClientAppID;
	}
	public Long getBatchOrderID() {
		return BatchOrderID;
	}
	public void setBatchOrderID(Long batchOrderID) {
		BatchOrderID = batchOrderID;
	}
	public Integer getDomainOrderEntityTypeID() {
		return DomainOrderEntityTypeID;
	}
	public void setDomainOrderEntityTypeID(Integer domainOrderEntityTypeID) {
		DomainOrderEntityTypeID = domainOrderEntityTypeID;
	}
	public Long getDomainOrderID() {
		return DomainOrderID;
	}
	public void setDomainOrderID(Long domainOrderID) {
		DomainOrderID = domainOrderID;
	}
	public Integer getProviderEntityTypeID() {
		return ProviderEntityTypeID;
	}
	public void setProviderEntityTypeID(Integer providerEntityTypeID) {
		ProviderEntityTypeID = providerEntityTypeID;
	}
	public Long getProviderEntityID() {
		return ProviderEntityID;
	}
	public void setProviderEntityID(Long providerEntityID) {
		ProviderEntityID = providerEntityID;
	}
	public Integer getCustomerEntityTypeID() {
		return CustomerEntityTypeID;
	}
	public void setCustomerEntityTypeID(Integer customerEntityTypeID) {
		CustomerEntityTypeID = customerEntityTypeID;
	}
	public Long getCustomerEntityID() {
		return CustomerEntityID;
	}
	public void setCustomerEntityID(Long customerEntityID) {
		CustomerEntityID = customerEntityID;
	}
	public Long getDomainProjectID() {
		return DomainProjectID;
	}
	public void setDomainProjectID(Long domainProjectID) {
		DomainProjectID = domainProjectID;
	}
	public Long getOrderPersonID() {
		return OrderPersonID;
	}
	public void setOrderPersonID(Long orderPersonID) {
		OrderPersonID = orderPersonID;
	}
	public Integer getDomainPolicyEntityTypeID() {
		return DomainPolicyEntityTypeID;
	}
	public void setDomainPolicyEntityTypeID(Integer domainPolicyEntityTypeID) {
		DomainPolicyEntityTypeID = domainPolicyEntityTypeID;
	}
	public Long getDomainPolicyID() {
		return DomainPolicyID;
	}
	public void setDomainPolicyID(Long domainPolicyID) {
		DomainPolicyID = domainPolicyID;
	}
	public Integer getRackSalesPolicyTypeID() {
		return RackSalesPolicyTypeID;
	}
	public void setRackSalesPolicyTypeID(Integer rackSalesPolicyTypeID) {
		RackSalesPolicyTypeID = rackSalesPolicyTypeID;
	}
	public Long getSalesRackPolicyID() {
		return SalesRackPolicyID;
	}
	public void setSalesRackPolicyID(Long salesRackPolicyID) {
		SalesRackPolicyID = salesRackPolicyID;
	}
	public Integer getPayModeID() {
		return PayModeID;
	}
	public void setPayModeID(Integer payModeID) {
		PayModeID = payModeID;
	}
	public Integer getStatusID() {
		return StatusID;
	}
	public void setStatusID(Integer statusID) {
		StatusID = statusID;
	}
	public Long getCustomizedStatusID() {
		return CustomizedStatusID;
	}
	public void setCustomizedStatusID(Long customizedStatusID) {
		CustomizedStatusID = customizedStatusID;
	}
	public Short getIsFulfilled() {
		return isFulfilled;
	}
	public void setIsFulfilled(Short isFulfilled) {
		this.isFulfilled = isFulfilled;
	}
	public Short getIsClosed() {
		return isClosed;
	}
	public void setIsClosed(Short isClosed) {
		this.isClosed = isClosed;
	}
	public Short getIsForceClosedByBatchOrder() {
		return isForceClosedByBatchOrder;
	}
	public void setIsForceClosedByBatchOrder(Short isForceClosedByBatchOrder) {
		this.isForceClosedByBatchOrder = isForceClosedByBatchOrder;
	}
	public Short getIsInvalid() {
		return isInvalid;
	}
	public void setIsInvalid(Short isInvalid) {
		this.isInvalid = isInvalid;
	}
	public Short getIsCancelled() {
		return isCancelled;
	}
	public void setIsCancelled(Short isCancelled) {
		this.isCancelled = isCancelled;
	}
	public Integer getGoodsManifestEntityTypeID() {
		return GoodsManifestEntityTypeID;
	}
	public void setGoodsManifestEntityTypeID(Integer goodsManifestEntityTypeID) {
		GoodsManifestEntityTypeID = goodsManifestEntityTypeID;
	}
	public Long getGoodsManifestID() {
		return GoodsManifestID;
	}
	public void setGoodsManifestID(Long goodsManifestID) {
		GoodsManifestID = goodsManifestID;
	}
	public Integer getCurrencyTypeID() {
		return CurrencyTypeID;
	}
	public void setCurrencyTypeID(Integer currencyTypeID) {
		CurrencyTypeID = currencyTypeID;
	}
	public BigDecimal getPrice() {
		return Price;
	}
	public void setPrice(BigDecimal price) {
		Price = price;
	}
	public Integer getDistributionNoteEntityTypeID() {
		return DistributionNoteEntityTypeID;
	}
	public void setDistributionNoteEntityTypeID(Integer distributionNoteEntityTypeID) {
		DistributionNoteEntityTypeID = distributionNoteEntityTypeID;
	}
	public String getDistributionNoteIDs() {
		return DistributionNoteIDs;
	}
	public void setDistributionNoteIDs(String distributionNoteIDs) {
		DistributionNoteIDs = distributionNoteIDs;
	}
	public Timestamp getExpectedConsumeDatetime() {
		return ExpectedConsumeDatetime;
	}
	public void setExpectedConsumeDatetime(Timestamp expectedConsumeDatetime) {
		ExpectedConsumeDatetime = expectedConsumeDatetime;
	}
	public Long getCreatorID() {
		return CreatorID;
	}
	public void setCreatorID(Long creatorID) {
		CreatorID = creatorID;
	}
	public Integer getCreatorEntityTypeID() {
		return CreatorEntityTypeID;
	}
	public void setCreatorEntityTypeID(Integer creatorEntityTypeID) {
		CreatorEntityTypeID = creatorEntityTypeID;
	}
	public Timestamp getCreatedDatetime() {
		return CreatedDatetime;
	}
	public void setCreatedDatetime(Timestamp createdDatetime) {
		CreatedDatetime = createdDatetime;
	}
	public Long getLastModifierID() {
		return LastModifierID;
	}
	public void setLastModifierID(Long lastModifierID) {
		LastModifierID = lastModifierID;
	}
	public Integer getLastModifierEntityTypeID() {
		return LastModifierEntityTypeID;
	}
	public void setLastModifierEntityTypeID(Integer lastModifierEntityTypeID) {
		LastModifierEntityTypeID = lastModifierEntityTypeID;
	}
	public Timestamp getLastModifiedDatetime() {
		return LastModifiedDatetime;
	}
	public void setLastModifiedDatetime(Timestamp lastModifiedDatetime) {
		LastModifiedDatetime = lastModifiedDatetime;
	}
	public Short getIsDeleted() {
		return isDeleted;
	}
	public void setIsDeleted(Short isDeleted) {
		this.isDeleted = isDeleted;
	}
	public Integer getDeletedByEntityTypeID() {
		return DeletedByEntityTypeID;
	}
	public void setDeletedByEntityTypeID(Integer deletedByEntityTypeID) {
		DeletedByEntityTypeID = deletedByEntityTypeID;
	}
	public Long getDeletedByEntityID() {
		return DeletedByEntityID;
	}
	public void setDeletedByEntityID(Long deletedByEntityID) {
		DeletedByEntityID = deletedByEntityID;
	}
	public Timestamp getDeletedDatetime() {
		return DeletedDatetime;
	}
	public void setDeletedDatetime(Timestamp deletedDatetime) {
		DeletedDatetime = deletedDatetime;
	}
	public Short getIsMock() {
		return isMock;
	}
	public void setIsMock(Short isMock) {
		this.isMock = isMock;
	}
	public Short getIsTombStone() {
		return isTombStone;
	}
	public void setIsTombStone(Short isTombStone) {
		this.isTombStone = isTombStone;
	}
}
