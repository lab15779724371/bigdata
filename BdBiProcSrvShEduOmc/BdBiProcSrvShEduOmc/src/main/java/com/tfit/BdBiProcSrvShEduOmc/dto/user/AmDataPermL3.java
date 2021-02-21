package com.tfit.BdBiProcSrvShEduOmc.dto.user;

import java.util.List;

import com.tfit.BdBiProcSrvShEduOmc.dto.IdLabel;

public class AmDataPermL3 {
	String id;
	String label;
	List<IdLabel> children;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public List<IdLabel> getChildren() {
		return children;
	}
	public void setChildren(List<IdLabel> children) {
		this.children = children;
	}
}
