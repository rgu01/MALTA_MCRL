package edu.collaboration.model.queries;

import org.fmaes.j2uppaal.datastructures.base.SimpleUppaalElement;

public class UPPAgentUppaalFormula extends SimpleUppaalElement  {

	public UPPAgentUppaalFormula() {
		super();
	    this.tagName = "formula";
	}
	
	public UPPAgentUppaalFormula(String value) {
		super();
	    this.tagName = "formula";
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
