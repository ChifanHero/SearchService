package com.sohungry.search.workflow;

import java.util.LinkedList;

public abstract class WorkFlow {
	
	LinkedList<WorkStep> steps = new LinkedList<WorkStep>();
	
	public  LinkedList<WorkStep> getWorkSteps() {
		return steps;
	}
	
	public abstract void defineWorkFlow();

}
