package com.tfit.BdBiProcSrvShEduOmc.dto.user;

import java.util.List;

public class AmDataPermL2 {
	String id;
	String label;
	List<AmDataPermL3> children;
	
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
	public List<AmDataPermL3> getChildren() {
		return children;
	}
	public void setChildren(List<AmDataPermL3> children) {
		this.children = children;
	}
}
