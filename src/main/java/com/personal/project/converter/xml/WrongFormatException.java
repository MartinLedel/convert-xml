package com.personal.project.converter.xml;

public class WrongFormatException extends Exception { 
	private static final long serialVersionUID = 1L;

	public WrongFormatException(String err) {
        super(err);
    }
}