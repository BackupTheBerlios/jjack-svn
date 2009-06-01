package de.gulden.framework.jjack;

import java.util.EventObject;

public class JJackNativeClientEvent extends EventObject {

	private static final long serialVersionUID = 1L;
	
	public JJackNativeClientEvent(JJackNativeClient source) {
		super(source);
	}
}
