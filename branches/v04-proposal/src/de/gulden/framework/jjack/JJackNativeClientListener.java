package de.gulden.framework.jjack;

public interface JJackNativeClientListener {
	/**
	 * Deal with a zombified jack client.
	 * @param e
	 */
	void handleShutdown(JJackNativeClientEvent e);
}
