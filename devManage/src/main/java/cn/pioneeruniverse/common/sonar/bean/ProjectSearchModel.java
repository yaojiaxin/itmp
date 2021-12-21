package cn.pioneeruniverse.common.sonar.bean;

import java.util.List;



public class ProjectSearchModel {

	 private Paging paging;
	    private List<Component> components;

	    public Paging getPaging() {
	        return paging;
	    }

	    public List<Component> getComponents() {
	        return components;
	    }
}