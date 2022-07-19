package edu.collaboration.model.queries;

import org.fmaes.j2uppaal.datastructures.base.SimpleUppaalElement;

public class UPPAgentUppaalComment extends SimpleUppaalElement  {

	public UPPAgentUppaalComment() {
		super();
	    this.tagName = "comment";
	}
	
	public UPPAgentUppaalComment(String value) {
		super();
	    this.tagName = "comment";
	    this.value = value;
	}
	

	public void setValue(String value) {
		// TODO Auto-generated method stub

	    if (value != null) {
	      this.value = value;
	    }
	}

	public String getValue() {
		// TODO Auto-generated method stub
	    return this.value;
	}

	
}
