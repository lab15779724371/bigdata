package com.tfit.BdBiProcSrvShEduOmc.util;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Node{
	//节点id
    String id;
    //节点标签
    String label;
    //此节点下的子节点children，可以无限制 地向下扩展子节点
    List<Node> children;
    
    //link构造方法
    public Node(String id, String label){
    	children = new ArrayList<>();
        this.id = id;
        this.label = label;
    }
    
    //为树添加节点
    public void add(Node node){
        this.children.add(node);
    }
    
    //以下为所有属性的set、get方法
    public List<Node> getList() {
        return children;
    }

    public void setList(List<Node> children) {
        this.children = children;
    }

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
}