package com.example.pencon;

import java.io.Serializable;

public class StringMessage implements Serializable {
private 	int    prefix=-1;
private	    String messageString="";
private     String ownerString="匿名";
 public StringMessage(int prefix, String messageString) {
	this.prefix=prefix;
	this.messageString=messageString;
}
 public StringMessage(int prefix, String messageString,String owner) {
	this.prefix=prefix;
	this.messageString=messageString;
	this.ownerString=owner;
}
public int getPrefix(){
	return this.prefix;	
}
public String getmessageString(){
	return this.messageString;	
}
public String getMessageOwnerString(){
	return this.ownerString;
}
}

