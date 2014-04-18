package com.gnod.memo.command;

public abstract class Command {
	
	private String mLabel;

	public Command(){
	}
	
	public Command(String label){
		this.mLabel = label;
	}
	
	public boolean canExecute(){
		return true;
	}
	
	public boolean canUndo(){
		return true;
	}
	
	public void execute(){
	}
	
	public void redo(){
		execute();
	}
	
	public void undo(){
	}
	
	public void dispose(){
	}
	
	public String getLabel() {
		return mLabel;
	}

	public void setLabel(String mLabel) {
		this.mLabel = mLabel;
	}
}
