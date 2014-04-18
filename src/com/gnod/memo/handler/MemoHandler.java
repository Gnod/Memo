package com.gnod.memo.handler;

import com.gnod.memo.command.Command;
import com.gnod.memo.command.CommandStack;

public class MemoHandler extends AbstractHandler{

	private static MemoHandler handler = null;
	public boolean canUndo = false;
	public boolean canRedo = false;
	
	public static MemoHandler getInstance(){
		if(handler == null)
			handler = new MemoHandler();
		return handler;
	}

	@Override
	public void handleStackChanged(CommandStack stack, Command command) {
		if(stack == null)
			return;
		canUndo = stack.canUndo();
	 	canRedo = stack.canRedo();
	}

	
	
}
