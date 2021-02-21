package com.tfit.BdBiProcSrvShEduOmc.dao.domain;

import java.sql.Timestamp;

public class CcDinnerMaterialUsagePlanDo {
	Long ccDinnerMaterialUsagePlanID;
	Long OriginalClientAppID;
	Long CateringServiceProviderID;
	Long CSPUniqueOrgID;
	Integer CustomerEntityTypeID;
	Long CustomerEntityID;
	Long ProjectID;
	Timestamp UsageDatetime;
	Short isInfomationCompleted;
	Short isConfirmedByCSP;
	Timestamp ConfirmedDatetime;
	String OrderItemIDs;
	Short isJobProcessing;
	Timestamp ProcessBeginDatetime;
	String Description;
	Integer CreatorEntityTypeID;
	Long CreatorID;
	Timestamp CreatedDatetime;
	Integer LastModifierEntityTypeID;
	Long LastModifierID;
	Timestamp LastModifiedDatetime;
	Short isDeleted;
	Integer DeletedByEntityTypeID;
	Long DeletedByEntityID;
	Timestamp DeletedDatetime;
	Short isDisabled;
	Integer DisabledByEntityTypeID;
	Long DisabledByEntityID;
	Timestamp DisabledDatetime;
	Short isMock;
	Short isTombStone;
	
	public Long getCcDinnerMaterialUsagePlanID() {
		return ccDinnerMaterialUsagePlanID;
	}
	public void setCcDinnerMaterialUsagePlanID(Long ccDinnerMaterialUsagePlanID) {
		this.ccDinnerMaterialUsagePlanID = ccDinnerMaterialUsagePlanID;
	}
	public Long getOriginalClientAppID() {
		return OriginalClientAppID;
	}
	public void setOriginalClientAppID(Long originalClientAppID) {
		OriginalClientAppID = originalClientAppID;
	}
	public Long getCateringServiceProviderID() {
		return CateringServiceProviderID;
	}
	public void setCateringServiceProviderID(Long cateringServiceProviderID) {
		CateringServiceProviderID = cateringServiceProviderID;
	}
	public Long getCSPUniqueOrgID() {
		return CSPUniqueOrgID;
	}
	public void setCSPUniqueOrgID(Long cSPUniqueOrgID) {
		CSPUniqueOrgID = cSPUniqueOrgID;
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
	public Long getProjectID() {
		return ProjectID;
	}
	public void setProjectID(Long projectID) {
		ProjectID = projectID;
	}
	public Timestamp getUsageDatetime() {
		return UsageDatetime;
	}
	public void setUsageDatetime(Timestamp usageDatetime) {
		UsageDatetime = usageDatetime;
	}
	public Short getIsInfomationCompleted() {
		return isInfomationCompleted;
	}
	public void setIsInfomationCompleted(Short isInfomationCompleted) {
		this.isInfomationCompleted = isInfomationCompleted;
	}
	public Short getIsConfirmedByCSP() {
		return isConfirmedByCSP;
	}
	public void setIsConfirmedByCSP(Short isConfirmedByCSP) {
		this.isConfirmedByCSP = isConfirmedByCSP;
	}
	public Timestamp getConfirmedDatetime() {
		return ConfirmedDatetime;
	}
	public void setConfirmedDatetime(Timestamp confirmedDatetime) {
		ConfirmedDatetime = confirmedDatetime;
	}
	public String getOrderItemIDs() {
		return OrderItemIDs;
	}
	public void setOrderItemIDs(String orderItemIDs) {
		OrderItemIDs = orderItemIDs;
	}
	public Short getIsJobProcessing() {
		return isJobProcessing;
	}
	public void setIsJobProcessing(Short isJobProcessing) {
		this.isJobProcessing = isJobProcessing;
	}
	public Timestamp getProcessBeginDatetime() {
		return ProcessBeginDatetime;
	}
	public void setProcessBeginDatetime(Timestamp processBeginDatetime) {
		ProcessBeginDatetime = processBeginDatetime;
	}
	public String getDescription() {
		return Description;
	}
	public void setDescription(String description) {
		Description = description;
	}
	public Integer getCreatorEntityTypeID() {
		return CreatorEntityTypeID;
	}
	public void setCreatorEntityTypeID(Integer creatorEntityTypeID) {
		CreatorEntityTypeID = creatorEntityTypeID;
	}
	public Long getCreatorID() {
		return CreatorID;
	}
	public void setCreatorID(Long creatorID) {
		CreatorID = creatorID;
	}
	public Timestamp getCreatedDatetime() {
		return CreatedDatetime;
	}
	public void setCreatedDatetime(Timestamp createdDatetime) {
		CreatedDatetime = createdDatetime;
	}
	public Integer getLastModifierEntityTypeID() {
		return LastModifierEntityTypeID;
	}
	public void setLastModifierEntityTypeID(Integer lastModifierEntityTypeID) {
		LastModifierEntityTypeID = lastModifierEntityTypeID;
	}
	public Long getLastModifierID() {
		return LastModifierID;
	}
	public void setLastModifierID(Long lastModifierID) {
		LastModifierID = lastModifierID;
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
	public Short getIsDisabled() {
		return isDisabled;
	}
	public void setIsDisabled(Short isDisabled) {
		this.isDisabled = isDisabled;
	}
	public Integer getDisabledByEntityTypeID() {
		return DisabledByEntityTypeID;
	}
	public void setDisabledByEntityTypeID(Integer disabledByEntityTypeID) {
		DisabledByEntityTypeID = disabledByEntityTypeID;
	}
	public Long getDisabledByEntityID() {
		return DisabledByEntityID;
	}
	public void setDisabledByEntityID(Long disabledByEntityID) {
		DisabledByEntityID = disabledByEntityID;
	}
	public Timestamp getDisabledDatetime() {
		return DisabledDatetime;
	}
	public void setDisabledDatetime(Timestamp disabledDatetime) {
		DisabledDatetime = disabledDatetime;
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
