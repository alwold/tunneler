package com.alwold.tunneler;

/**
 *
 * @author alwold
 */
public class ConfigurationException extends Exception {

	public ConfigurationException(Throwable cause) {
		super(cause);
	}

	public ConfigurationException(String message, Throwable cause) {
		super(message, cause);
	}

	public ConfigurationException(String message) {
		super(message);
	}

	public ConfigurationException() {
	}

}
