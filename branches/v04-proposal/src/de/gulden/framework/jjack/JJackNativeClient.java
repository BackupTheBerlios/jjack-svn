/*
 *
 * Licensed under the GNU Lesser General Public License (LGPL).
 * This comes with NO WARRANTY. See file LICENSE for details.
 *
 * Author:  Peter Brinkmann, using much code by Jens Gulden
 */

package de.gulden.framework.jjack;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.HashSet;
import java.util.Set;


/**
 * Bare bones wrapper for the basic functionality of jack clients
 */
public class JJackNativeClient {

	private static boolean initialized = false;

	private long infPointer = 0;
	private JJackAudioProcessor processor;
	private Set<JJackNativeClientListener> listeners = new HashSet<JJackNativeClientListener>();
	private FloatBuffer[] outAsFloat;
	private FloatBuffer[] inAsFloat;
	private JJackAudioEvent event;
	

	/**
	 * Constructor; opens native client
	 * 
	 * @param name: name of native client
	 * @param portsIn: number of input ports
	 * @param portsOut: number of output ports
	 * @param proc: processor for jack callback
	 * @throws JJackException
	 */
	public JJackNativeClient(String name, int portsIn, int portsOut, JJackAudioProcessor proc) throws JJackException {
		this(name, portsIn, portsOut, proc, true);
	}
	
	/**
	 * @param name
	 * @param portsIn
	 * @param portsOut
	 * @param proc
	 * @param isDaemon: flag indicating whether to attach JACK thread as daemon
	 * @throws JJackException
	 */
	public JJackNativeClient(String name, int portsIn, int portsOut, JJackAudioProcessor proc, boolean isDaemon) throws JJackException {
		if (!isInitialized()) throw new JJackException("jjack not initialized");
		if (name==null || name.equals("")) throw new IllegalArgumentException("name cannot be null or empty");
		if (portsIn<0 || portsIn>getMaxPorts()) throw new IllegalArgumentException("input ports out of range");
		if (portsOut<0 || portsOut>getMaxPorts()) throw new IllegalArgumentException("output ports out of range");
		if (proc==null) throw new IllegalArgumentException("processor cannot be null");

		infPointer = openClient(name, portsIn, portsOut, isDaemon);
		processor = proc;
		inAsFloat = new FloatBuffer[portsIn];
		outAsFloat = new FloatBuffer[portsOut];
		event = new JJackAudioEvent(0, this, inAsFloat, outAsFloat);
	}

	/**
	 * activates the client and connects it to other clients if requested
	 * 
	 * @param sourceTarget: indicates client to connect input ports to; null means no connection, "" means connection to physical ports, otherwise connect to client given by name
	 * @param sinkTarget: indicates client to connect output ports to; same semantics as sourceTarget
	 * @throws JJackException
	 */
	public void start(String sourceTarget, String sinkTarget) throws JJackException {
		if (infPointer!=0) {
			startClient(infPointer, sourceTarget, sinkTarget);
		} else {
			throw new JJackException("native client invalid");
		}
	}

	/**
	 * closes and deallocates the native client
	 */
	public void close() {
		if (infPointer!=0) {
			closeClient(infPointer);
			infPointer = 0;
		}
	}

	/**
	 * @return the current jack sample rate
	 * @throws JJackException if jack is unavailable
	 */
	public static native int getSampleRate() throws JJackException;

	/**
	 * @return the current jack buffer size
	 * @throws JJackException if jack is unavailable
	 */
	public static native int getBufferSize() throws JJackException;

	/**
	 * @return true if and only if the native library was loaded successfully
	 */
	public static boolean isInitialized() {
		return initialized;
	}

	/**
	 * @return the largest admissible number of input/output ports per client
	 */
	public static native int getMaxPorts();

	protected void finalize() throws Throwable {
		try {
			close();
		} finally {
			super.finalize();
		}
	}

	/**
	 * Add a listener to the list of objects to be notified in the event of zombification.
	 * @param listener
	 */
	public synchronized void addListener(JJackNativeClientListener listener) {
		listeners.add(listener);
	}

	/**
	 * Remove a listener.
	 * @param listener
	 */
	public synchronized void removeListener(JJackNativeClientListener listener) {
		listeners.remove(listener);
	}

	// only private stuff after this point

	private native long openClient(String clientName, int portsIn, int portsOut, boolean isDaemon) throws JJackException;
	private native void startClient(long infPtr, String sourceTarget, String sinkTarget) throws JJackException;
	private native void closeClient(long infPtr);

	/**
	 * JACK processing callback; called directly from native code, and only from there.
	 *
	 * @param in: the direct memory access input buffer
	 * @param out: the direct memory access output buffer
	 */
	@SuppressWarnings("unused")
	private void processBytes(ByteBuffer[] in, ByteBuffer[] out, boolean realloc) {
		if (realloc) {
			for (int i=0; i<out.length; i++) {
				outAsFloat[i] = out[i].order(ByteOrder.LITTLE_ENDIAN).asFloatBuffer();
			}
			for (int i=0; i<in.length; i++) {
				inAsFloat[i] = in[i].order(ByteOrder.LITTLE_ENDIAN).asFloatBuffer();
			}
		} else {
			for(int i=0; i<out.length; i++) {
				outAsFloat[i].rewind();
			}
			for(int i=0; i<in.length; i++) {
				inAsFloat[i].rewind();
			}
		}
		event.setTimestamp(System.currentTimeMillis());
		processor.process(event);
	}

	/**
	 * This method is called directly from native code in the event of zombification.
	 */
	@SuppressWarnings("unused")
	private synchronized void handleShutdown() {
		System.err.println("native jack client "+this+" has been zombified!");
		JJackNativeClientEvent e = new JJackNativeClientEvent(this);
		for(JJackNativeClientListener listener: listeners) {
			listener.handleShutdown(e);
		}
	}

	static {
		initialized = true;
		String libJJackFileName=null;
		try
		{
			System.loadLibrary("jjack");
			System.err.println("native jjack library loaded using system library path");
		} catch(Throwable e) {
			try {
				File file = new File("lib/"+System.getProperty("os.arch")+"/"+System.getProperty("os.name")+"/libjjack.so");
				libJJackFileName = file.getAbsolutePath();
				System.load(libJJackFileName);
				System.err.println("loaded jjack native library "+ libJJackFileName );
			} catch (Throwable e2) {
				initialized = false;
				System.err.println("Could not load jjack native library");
				System.err.println("Tried system library path and " + libJJackFileName);
				e.printStackTrace();
				e2.printStackTrace();
			}
		}
	}
}
