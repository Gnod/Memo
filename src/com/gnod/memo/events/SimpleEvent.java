package com.gnod.memo.events;

public class SimpleEvent implements Event{

	private String type = null;
	private Object source = null;
	
	public SimpleEvent(String type){
		this.type = type;
	}
	
	@Override
	public String getType() {
		return type;
	}

	@Override
	public Object getSource() {
		return source;
	}

	@Override
	public void setSource(Object o) {
		this.source = o;
	}
	
}
