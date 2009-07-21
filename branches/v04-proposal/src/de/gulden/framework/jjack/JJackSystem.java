/*
 * Project: JJack - Java bridge API for the JACK Audio Connection Kit
 * Class:   de.gulden.framework.jjack.JJackSystem
 * Version: 0.3
 *
 * Date:    2007-12-30
 *
 * Licensed under the GNU Lesser General Public License (LGPL).
 * This comes with NO WARRANTY. See file LICENSE for details.
 *
 * Author:  Jens Gulden
 * Modified by: Peter Brinkmann 05/2009
 */

package de.gulden.framework.jjack;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;

/**
 * JJack system class.
 *  
 * @author  Jens Gulden
 * @version  0.4
 */
public class JJackSystem implements JJackConstants {

	// ------------------------------------------------------------------------
	// --- final static fields                                              ---
	// ------------------------------------------------------------------------

	/**
	 * JJack version number.
	 */
	public static final String VERSION = "0.4";

	/**
	 * Flag for debug mode.
	 */
	public static final boolean DEBUG = false;

	/**
	 * Name of the system property specifying the name of the native JACK client to register.
	 */
	private static final String PROPERTY_CLIENT_NAME = "jjack.client.name";

	/**
	 * Default name of the native JACK client to register.
	 */
	private static final String DEFAULT_CLIENT_NAME = "JJack";

	/**
	 * Name of the system property specifying whether JJack should give more verbose output.
	 */
	private static final String PROPERTY_VERBOSE = "jjack.verbose";

	/**
	 * Default verbose mode.
	 */
	private static final String DEFAULT_VERBOSE = DEBUG ? "true" : "false";

	/**
	 * Name of the system property specifying how many ports to allocate.
	 */
	private static final String PROPERTY_PORTS = "jjack.ports";

	/**
	 * Additional suffix of the system property specifying how many input ports to allocate.
	 */
	private static final String SUFFIX_INPUT = ".in";

	/**
	 * Suffix of the system property specifying how many output ports to allocate.
	 */
	private static final String SUFFIX_OUTPUT = ".out";

	/**
	 * Default ports count.
	 */
	private static final String DEFAULT_PORTS = "2";

	/**
	 * Suffix of the system property specifying whether ports should be auto-connected to physical JACK ports.
	 */
	private static final String SUFFIX_AUTOCONNECT = ".autoconnect";

	/**
	 * Default auto-connect mode.
	 */
	private static final String DEFAULT_AUTOCONNECT = "false";

	private static final String SUFFIX_TARGET = ".target";
	private static final String DEFAULT_TARGET = null;

	// ------------------------------------------------------------------------
	// --- static fields                                                    ---
	// ------------------------------------------------------------------------

	private static String clientName;

	private static int portsInput;

	private static int portsOutput;

	private static boolean portsInputAutoconnect;

	private static boolean portsOutputAutoconnect;

	private static String portsInputTarget;

	private static String portsOutputTarget;

	private static boolean verbose = false;

	private static int sampleRate = 0, bufferSize = 0;

	private static JJackAudioProcessor client = null;

	private static JJackNativeClient nativeClient = null;


	// ------------------------------------------------------------------------
	// --- static initializer                                               ---
	// ------------------------------------------------------------------------

	static {
		// set parameters from system properties
		clientName = System.getProperty(PROPERTY_CLIENT_NAME, DEFAULT_CLIENT_NAME);
		String s = System.getProperty(PROPERTY_VERBOSE, DEFAULT_VERBOSE);
		verbose = Boolean.valueOf(s).booleanValue();
		s = System.getProperty(PROPERTY_PORTS+SUFFIX_INPUT, System.getProperty(PROPERTY_PORTS, DEFAULT_PORTS));
		portsInput = Integer.valueOf(s).intValue();
		s = System.getProperty(PROPERTY_PORTS+SUFFIX_OUTPUT, System.getProperty(PROPERTY_PORTS, DEFAULT_PORTS));
		portsOutput = Integer.valueOf(s).intValue();
		s = System.getProperty(PROPERTY_PORTS+SUFFIX_INPUT+SUFFIX_AUTOCONNECT, System.getProperty(PROPERTY_PORTS+SUFFIX_AUTOCONNECT, DEFAULT_AUTOCONNECT));
		portsInputAutoconnect = Boolean.valueOf(s).booleanValue();
		s = System.getProperty(PROPERTY_PORTS+SUFFIX_OUTPUT+SUFFIX_AUTOCONNECT, System.getProperty(PROPERTY_PORTS+SUFFIX_AUTOCONNECT, DEFAULT_AUTOCONNECT));
		portsOutputAutoconnect = Boolean.valueOf(s).booleanValue();
		portsInputTarget = System.getProperty(PROPERTY_PORTS+SUFFIX_INPUT+SUFFIX_TARGET, DEFAULT_TARGET);
		portsOutputTarget = System.getProperty(PROPERTY_PORTS+SUFFIX_OUTPUT+SUFFIX_TARGET, DEFAULT_TARGET);

		try {
			sampleRate = JJackNativeClient.getSampleRate();
			bufferSize = JJackNativeClient.getBufferSize();

			nativeClient = new JJackNativeClient(clientName, portsInput, portsOutput, new JJackAudioProcessor() {
				public void process(JJackAudioEvent e) {
					JJackAudioProcessor cl = client; // copy to avoid potential synchronization issues
					if (cl!=null) {
						JJackSystem.process(cl, e);
					}
				}
			}, false);
			nativeClient.start(portsInputAutoconnect ? "" : portsInputTarget, portsOutputAutoconnect ? "" : portsOutputTarget);
		} catch(JJackException e) {
			if (nativeClient!=null) {
				nativeClient.close();
				nativeClient = null;
			}
			e.printStackTrace();
		}
	}

	// ------------------------------------------------------------------------
	// --- constructor                                                      ---
	// ------------------------------------------------------------------------

	/**
	 * Private constructor for getting an instance of Runnable.
	 */
	private JJackSystem() {
		//nop
	}


	// ------------------------------------------------------------------------
	// --- static methods                                                   ---
	// ------------------------------------------------------------------------

	/**
	 * Returns the sample rate used by the JACK daemon.
	 *  
	 * @return  the sample rate
	 */
	public static int getSampleRate() {
		return sampleRate;
	}

	/**
	 * Returns the buffer size used by the JACK daemon.
	 *  
	 * @return  the sample rate
	 */
	public static int getBufferSize() {
		return bufferSize;
	}

	/**
	 * Sets the audio processor which is responsible for signal processing.
	 * The <code>process()</code>-method of this audio processor will then regularly be called.
	 */
	public static void setProcessor(JJackAudioProcessor cl) {
		client = cl;
	}

	/**
	 * Returns the audio processor which is responsible for signal processing.
	 *  
	 * @return  the audio processor (may be null)
	 */
	public static JJackAudioProcessor getProcessor() {
		return client;
	}

	/**
	 * Returns the name of the native JACK client that has been registered by the JJack system.
	 *  
	 * @return  the client name
	 */
	public static String getJackClientName() {
		return clientName;
	}

	/**
	 * Returns the number of ports available either for input or output mode.
	 *  
	 * @param inout either constant <code>INPUT</code> or <code>OUTPUT</code>
	 * @return  the number of ports
	 */
	public static int countPorts(int inout) {
		switch (inout) {
		case INPUT:
			return portsInput;
		case OUTPUT:
			return portsOutput;
		default:
			return 0;
		}
	}

	/**
	 * Calculates number of samples used for a single channel
	 * in the given amount of milliseconds.
	 * The current system sample rate is being taken into account.
	 *  
	 * @return  number of sample values
	 */
	public static int calculateSampleCount(int milliseconds) {
		int sr = getSampleRate();
		int count = (sr * milliseconds) / 1000;
		return count;
	}

	/**
	 * Returns whether the JJack sytem runs in verbose mode.
	 *  
	 * @return  true if verbose mode
	 */
	public static boolean verbose() {
		return verbose;
	}

	public static boolean isInitialized() {
		return nativeClient!=null;
	}

	public static void shutdown() throws JJackException {
		// nop
	}

	/**
	 * Process an audio event by a given audio processor.
	 *  
	 * @param p the audio processor that is to process the event
	 * @param e the audio event
	 */
	public static void process(JJackAudioProcessor p, JJackAudioEvent e) {
		boolean monitorable = (p instanceof JJackAudioProcessorMonitorable);
		if (monitorable) {
			// inform listeners that processing starts. p might be among them.
			for (Iterator it = ((JJackAudioProcessorMonitorable)p).getAudioProcessListeners().iterator(); it.hasNext(); ) {
				JJackAudioProcessListener l = (JJackAudioProcessListener)it.next();
				l.beforeProcess(e);
			}
		}

		p.process(e);

		if (monitorable) {
			// inform listeners that processing has ended. p might be among them.
			for (Iterator it = ((JJackAudioProcessorMonitorable)p).getAudioProcessListeners().iterator(); it.hasNext(); ) {
				JJackAudioProcessListener l = (JJackAudioProcessListener)it.next();
				l.afterProcess(e);
			}
		}
	}

	/**
	 * Logs a message from the specified source object, if verbose mode is enabled.
	 *  
	 * @param src the source object, usually the originator of the log message
	 * @param msg the message text
	 */
	public static void log(Object src, String msg) {
		if (verbose()) {
			String s;
			if (src instanceof JJackMonitor) {
				s = ((JJackMonitor)src).getName()+" ["+src.getClass().getName()+"]";
			} else {
				s = src.getClass().getName();
			}
			System.out.println(s+": "+msg);
		}
	}

	/**
	 * Returns a short information text about the JJack system and its current status.
	 *  
	 * @return  information text
	 */
	public static String getInfo() {
		return
		"JJack version "+VERSION+"\n" + 		"\n" + 		"Client name: "+getJackClientName()+"\n" + 		"Sample rate: "+getSampleRate()+"\n" +
		"# Input ports: "+countPorts(INPUT)+"\n" +
		"# Output ports: "+countPorts(OUTPUT)+"\n" +
		"";
	}

	/**
	 * Output usage description, then exit with error code.
	 */
	private static void usage() {
		System.out.println("usage: java ... (-D" + PROPERTY_CLIENT_NAME + "=Name) JJack <wrapped-class-with-main-method> <arg0 of wrapped-class> <arg1 of wrapped-class> ...");
		System.exit(1);
	}

	/**
	 * Wrapper main()-method, invokes the main()-method of the class specified as arg[0].
	 * By this time, the Jack connection has already been established by the static initializer.
	 *  
	 * @param args command line parameters
	 * @throws Throwable if an unhandled exception occurs inside the wrapped application,
	 * or a JJackException if initializing the Jack client was not successful.
	 */
	public static void main(String[] args) throws Throwable {
		if (!isInitialized()) {
			System.err.println("Error initializing JJack client");
			throw new JJackException("Error initializing JJack client");
		}
		if (args.length==0) {
			usage();
		}
		String cn = args[0];
		if (cn.equalsIgnoreCase("-help") || cn.equalsIgnoreCase("--help") || cn.equals("/?") || cn.equals("-?")) {
			usage();
		}
		Class cl = null;
		try {
			cl = Class.forName(cn);
		} catch (ClassNotFoundException cnfe) {
			System.out.println("Error loading class '"+cn+"': "+cnfe.getMessage());
			usage();
		}
		Method method = null;
		try {
			method = cl.getMethod("main", new Class[] {String[].class});
		} catch (Exception e) {
			System.out.println("Error: '"+cn+"' has no main()-method: "+e.getMessage());
			System.exit(1);
		}
		String[] wrappedArgs = new String[args.length-1];
		for (int i=0; i < wrappedArgs.length; i++) {
			wrappedArgs[i] = args[i+1];
		}
		try {
			method.invoke(null, new Object[] { wrappedArgs });
		} catch (InvocationTargetException ite) {
			throw ite.getTargetException();
		} catch (Exception ex) {
			System.out.println("Error running main() method of '"+cn+"': "+ex.getMessage());
			System.exit(1);
		}
	}
} // end JJackSystem
