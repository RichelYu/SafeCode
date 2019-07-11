package com.ybr.exception;

public class MsgException extends Exception{
	public MsgException(){
		super();
	}
	public MsgException(String errorMsg){
		super(errorMsg);
	}
}
