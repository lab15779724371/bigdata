package com.tfit.BdBiProcSrvShEduOmc.dto.ev;

import java.util.List;

public class OeSubAnsBody {
	String epId;
	List<SubEpCont> subCont;
	
	public String getEpId() {
		return epId;
	}
	public void setEpId(String epId) {
		this.epId = epId;
	}
	public List<SubEpCont> getSubCont() {
		return subCont;
	}
	public void setSubCont(List<SubEpCont> subCont) {
		this.subCont = subCont;
	}
}
