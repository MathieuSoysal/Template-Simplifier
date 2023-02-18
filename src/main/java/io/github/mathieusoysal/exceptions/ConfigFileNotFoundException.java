package io.github.mathieusoysal.exceptions;

public class ConfigFileNotFoundException extends Exception {

	private static final long serialVersionUID = 1L;

	public ConfigFileNotFoundException() {
		super("Config file not found. Please create a .template-guider.md file in the root of your repository.");
	}
	
}
