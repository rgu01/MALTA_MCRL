package edu.collaboration.model.queries;

import org.fmaes.j2uppaal.datastructures.base.CompositeUppaalElement;

public class UPPAgentUppaalQuery extends CompositeUppaalElement{

	  public UPPAgentUppaalQuery() {
	    // TODO Auto-generated constructor stub
	    super();
	    this.tagName = "query";
	  }
	  
	 public void setFormula(UPPAgentUppaalFormula formula) {
		    // TODO Auto-generated method stub
		    if (formula == null) {
		      return;
		    }
		    childrenUppaalElements.add(formula);
	  }
	 

	 public void setComment(UPPAgentUppaalComment comment) {
		    // TODO Auto-generated method stub
		    if (comment == null) {
		      return;
		    }
		    childrenUppaalElements.add(comment);
	  }
}
