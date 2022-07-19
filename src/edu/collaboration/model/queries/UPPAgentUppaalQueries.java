package edu.collaboration.model.queries;

import org.fmaes.j2uppaal.datastructures.base.CompositeUppaalElement;

public class UPPAgentUppaalQueries extends CompositeUppaalElement{
	public UPPAgentUppaalQueries() {
	    // TODO Auto-generated constructor stub
	    super();
	    this.tagName = "queries";
	  }
	
	public void addQuery(UPPAgentUppaalQuery query) {
	    // TODO Auto-generated method stub
	    if (query == null) {
	      return;
	    }
	    childrenUppaalElements.add(query);
  }
}
