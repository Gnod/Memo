package com.gnod.memo.handler;

import java.util.EventObject;

import com.gnod.memo.activity.App;
import com.gnod.memo.command.Command;
import com.gnod.memo.command.CommandStack;
import com.gnod.memo.command.StackListener;

public abstract class AbstractHandler{

	private Command mCommand = null;

	private StackListener commandStackListener = new StackListener() {
		@Override
		public void commandStackChanged(EventObject event, Command command) {
			CommandStack stack = (CommandStack)event.getSource();
			handleStackChanged(stack, command);
		}
	};
	
	public void registerStackListener(){
		App.getCommandStack().addListener(commandStackListener);
	}
	
	public void dispose(){
		App.getCommandStack().removeListener(commandStackListener);
	}
	
	public void undo(){
		App.getCommandStack().undo();
	}
	
	public void redo(){
		App.getCommandStack().redo();
	}
	
	public void invoke(){
		executeCurrentCommand();
	}
	
	protected abstract void handleStackChanged(CommandStack stack, Command command);

	protected void executeCurrentCommand(){
		Command curCommand = getCurrentCommand();
		if(curCommand != null && curCommand.canExecute()){
			executeCommand(curCommand);
		}
		setCurrentCommand(null);
	}
	
	protected void executeCommand(Command curCommand) {
		try{
			App.getCommandStack().execute(curCommand);
		}finally{
		}
	}
	
	public Command getCurrentCommand() {
		return mCommand;
	}

	public void setCurrentCommand(Command mCommand) {
		this.mCommand = mCommand;
	}
}
