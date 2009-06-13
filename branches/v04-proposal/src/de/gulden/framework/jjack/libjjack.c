/*
 * Project:  JJack - Java bridge API for the JACK Audio Connection Kit
 *
 * Module:   libjjack.so
 *
 * Native implementation of class de.gulden.framework.jjack.JJackNativeClient.
 *
 * Author:      Jens Gulden
 * Modified by: Peter J. Salomonsen
 * Modified by: Andrew Cooke
 * Modified by: Peter Brinkmann
 *
 * 2006-12-17 (PJS): Changed INF pointer to long (64bit) for amd64 support
 * 2007-04-09 (PJS): No longer object allocation for each process call
 * 2007-07-10 (AC):  - The size of the sample is found using size() rather than being
 *                     hardcoded as 4
 *                   - Some ints are cast to jsize which is long on 64 bit systems (see
 *                     ftp://www6.software.ibm.com/software/developer/jdk/64bitporting/64BitJavaPortingGuide.pdf)
 *                   - The allocation of inf->byteBufferArray[mode] is made outside the loop
 *                     over ports. This avoids a null pointer exception in Java when there are
 *                     zero ports (eg for output on a "consumer only" process).
 * 2009-03-16 (PB):  - Added support for autoconnect by name to other clients
 * 2009-05-11 (PB):  - General refactoring
 * 2009-05-11 (PB):  - Eliminated static field lookup; all values are
 * now passed as function arguments
 * 2009-05-12 (PB):  - Moved native bridge to JJackNativeClient
 * 2009-05-13 (PB):  - Got rid of some static calls
 * 2009-05-13 (PB):  - added shutdown callback
 * 2009-05-19 (PB):  - support for further streamlining of Java callback
 * 
 * Possible compile commands:
 *
 * gcc -fPIC -I/usr/java/java/include -I/usr/java/java/include/linux -I/usr/include/jack -c libjjack.c
 * gcc -shared -fPIC -ljack -o libjjack.so libjjack.o
 *
 * You may need a symlink 'libjack.so' -> 'libjack0.1xx.x' inside /usr/lib.
 *
 * For MacOS X build, see make/build_macosx.xml.
 */

#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <errno.h>
#include <jni.h>
#include <jack.h>
#include "libjjack.h"

/*
 * Maximum number of ports to be allocated. 
 */
#define MAX_PORTS 64

/*
 * Symbols for input, output. 
 */
typedef enum {
    INPUT, 
    OUTPUT, 
    MODES   /* total count */
} MODE;

/*
 * Constant strings. Some of these reflect the names of Java identifiers. 
 */
const char *CLASS_BYTEBUFFER = "java/nio/ByteBuffer";
const char *CLASS_JJACKEXCEPTION = "de/gulden/framework/jjack/JJackException";
const char *METHOD_PROCESS = "processBytes";
const char *METHOD_PROCESS_SIG = "([Ljava/nio/ByteBuffer;[Ljava/nio/ByteBuffer;Z)V";
const char *METHOD_SHUTDOWN = "handleShutdown";
const char *METHOD_SHUTDOWN_SIG = "()V";
const char *MODE_LABEL[MODES] = {"input", "output"};
const unsigned long MODE_JACK[MODES] = { JackPortIsInput, JackPortIsOutput };

/*
 * Global memory to store values needed for performing the Java callback
 * from the JACK thread. A pointer to this structure is passed to the 
 * process()-function as argument 'void *arg'.
*/
typedef struct Inf {
    jobject owner;
    jack_client_t *client;
    int portCount[MODES];
    jack_port_t *port[MODES][MAX_PORTS];
    jack_default_audio_sample_t *sampleBuffers[MODES][MAX_PORTS];
    jobjectArray byteBufferArray[MODES];
    int isDaemon;
} *INF;

JavaVM *cached_jvm;              /* jvm pointer */
jclass cls_ByteBuffer;    /* handle of class java.nio.ByteBuffer */
jmethodID processCallback = NULL;
jmethodID shutdownCallback = NULL;


/* 
 * Main JACK audio process chain callback. From here, we will branch into the
 * Java virtual machine to let Java code perform the processing.
 */
int process(jack_nframes_t nframes, void *arg) {
  INF inf = arg;
  JNIEnv *env;
  int mode, i;
  jboolean reallocated = JNI_FALSE;

  int res = (inf->isDaemon ?
        (*cached_jvm)->AttachCurrentThreadAsDaemon(cached_jvm,
                (void**) &env, NULL) :
        (*cached_jvm)->AttachCurrentThread(cached_jvm, (void**) &env, NULL));
  if (res) {
    fprintf(stderr, "FATAL: cannot attach JACK thread to JVM\n");
    return -1;
  }
  
  for(mode=INPUT; mode<=OUTPUT; mode++) {
    for(i=0; i<inf->portCount[mode]; i++) {
      // Only reallocate if the buffer position changes
      jack_default_audio_sample_t *tempSampleBuffer = 
            (jack_default_audio_sample_t *)
                  jack_port_get_buffer(inf->port[mode][i], nframes);
      if (tempSampleBuffer!=inf->sampleBuffers[mode][i]) {
        reallocated = JNI_TRUE;
        inf->sampleBuffers[mode][i] = tempSampleBuffer;
        jobject byteBuffer = (*env)->NewDirectByteBuffer(env,
                  tempSampleBuffer,
                    (jsize)(nframes*sizeof(jack_default_audio_sample_t)));
        (*env)->SetObjectArrayElement(env, inf->byteBufferArray[mode],
                  (jsize) i, byteBuffer);
      }
    }
  }

  (*env)->CallVoidMethod(env, inf->owner, processCallback,
      inf->byteBufferArray[INPUT], inf->byteBufferArray[OUTPUT], reallocated);
  
  return 0;      
}

/*
 * Shutdown callback to let Java know if the client gets zombified.
 */
void shutdown(void *arg) {
  INF inf = (INF) arg;
  JNIEnv *env;

  if ((*cached_jvm)->AttachCurrentThread(cached_jvm, (void**) &env, NULL)) {
    return;
  }

  (*env)->CallVoidMethod(env, inf->owner, shutdownCallback);

  (*cached_jvm)->DetachCurrentThread(cached_jvm);
}

/*
 * Throw a JJackException in Java with an optional second description text. 
 */
void throwExc2(JNIEnv *env, char *msg, char *msg2) {	
    jclass clsExc = (*env)->FindClass(env, CLASS_JJACKEXCEPTION);	
    char m[255] = "";
    if (msg != NULL) {
        strcat(m, msg);
    }
    if (msg2 != NULL) { /* optional second string */
        strcat(m, " ");
        strcat(m, msg2);
    }
    if (clsExc == 0) {
        fprintf(stderr, "fatal: cannot access class JJackException.\nerror:\n%s\n", m);
    } else {
        (*env)->ThrowNew(env, clsExc, m);
    }
}	

/*
 * Throw a JJackException in Java with a description text. 
 */
void throwExc(JNIEnv *env, char *msg) {	
    throwExc2(env, msg, NULL);
}

/*
 * Allocate string memory. 
 */
const char *allocchars(JNIEnv *env, jstring js) {
    return (*env)->GetStringUTFChars(env, js, 0);
}

/*
 * Deallocate string memory.
 */
void freechars(JNIEnv *env, jstring js, const char *s) {
    (*env)->ReleaseStringUTFChars(env, js, s);
}

/*
 * Get env pointer for current thread.
 */
int getEnv(JNIEnv **envPtr) {
  return (*cached_jvm)->GetEnv(cached_jvm, (void**) envPtr, JNI_VERSION_1_4);
}

/*
 * Init function, implicitly called by JVM.
 */
JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *jvm, void *reserved) {
  JNIEnv *env;

  cached_jvm = jvm;

  if (getEnv(&env)) {
    return JNI_ERR;
  }

  jobject obj = (*env)->FindClass(env, CLASS_BYTEBUFFER);	
  if (obj == NULL) {
    return JNI_ERR;
  }
  cls_ByteBuffer = (*env)->NewWeakGlobalRef(env, obj);	

  return JNI_VERSION_1_4;
}

/*
 * Shutdown function, implicitly called by JVM.
 */
JNIEXPORT void JNICALL JNI_OnUnload(JavaVM *jvm, void *reserved) {
  JNIEnv *env;
  if (getEnv(&env)) {
    return;
  }
  (*env)->DeleteWeakGlobalRef(env, cls_ByteBuffer);
}


/*
 * private native long openClient(String, int, int) throws JJackException
 */
JNIEXPORT jlong JNICALL Java_de_gulden_framework_jjack_JJackNativeClient_openClient(JNIEnv *env, jobject obj, jstring clientName, jint portsIn, jint portsOut, jboolean isDaemon) {
  INF inf = (INF) malloc(sizeof(struct Inf));
  const char *name;
  jack_client_t *client;
  char *portName;
  int mode, i;

  if (inf==NULL) {
      throwExc(env, "can't allocate memory");
      return 0;
  }

  if (processCallback==NULL) { // first call?
    jclass cls = (*env)->GetObjectClass(env, obj);
    jmethodID mid = (*env)->GetMethodID(env, cls,
              METHOD_PROCESS, METHOD_PROCESS_SIG);
    if (mid==NULL) {
      throwExc2(env, "method not found", METHOD_PROCESS);
      return 0;
    }
    processCallback = mid;  // no global ref necessary

    mid = (*env)->GetMethodID(env, cls,
              METHOD_SHUTDOWN, METHOD_SHUTDOWN_SIG);
    if (mid==NULL) {
      throwExc2(env, "method not found", METHOD_SHUTDOWN);
      return 0;
    }
    shutdownCallback = mid;  // no global ref necessary
  }

  inf->owner = (*env)->NewWeakGlobalRef(env, obj);
  inf->isDaemon = (isDaemon==JNI_TRUE);

  name = allocchars(env, clientName);
  fprintf(stderr, "natively opening jack client \"%s\"\n", name);

  client = jack_client_open(name, JackNullOption, NULL);
  freechars(env, clientName, name);
  if (client==0) {
    throwExc(env, "can't open client, jack server not running?");
    return 0;
  }
  inf->client = client;

  jack_set_process_callback(client, process, inf);
  jack_on_shutdown(client, shutdown, inf);

  portName = malloc(100);
  for (mode=INPUT; mode<=OUTPUT; mode++) {
    int portCount = (mode==INPUT) ? portsIn : portsOut;
    if (portCount > MAX_PORTS) {
      portCount = MAX_PORTS;
    }
    inf->portCount[mode] = portCount;

    inf->byteBufferArray[mode] = (*env)->NewGlobalRef(env,
                (*env)->NewObjectArray(env,
                    (jsize)inf->portCount[mode], cls_ByteBuffer, NULL));
    for (i=0; i<inf->portCount[mode]; i++) {
      sprintf(portName, "%s_%i", MODE_LABEL[mode], (i+1));
      inf->port[mode][i] = jack_port_register(client, portName,
                  JACK_DEFAULT_AUDIO_TYPE, MODE_JACK[mode], 0);
      inf->sampleBuffers[mode][i] = NULL;
    }
  }
  free(portName);
  fprintf(stderr, "using %i input ports, %i output ports\n",
                      inf->portCount[INPUT], inf->portCount[OUTPUT]);

  return (jlong) inf;
}

/*
 * private void startClient(String, String) throws JJackException
 *
 * sourceTarget==null: no autoconnect
 * sourceTarget=="":   autoconnect to physical ports
 * sourceTarget==name: autoconnect to ports of named client
 */
JNIEXPORT void JNICALL Java_de_gulden_framework_jjack_JJackNativeClient_startClient(JNIEnv *env, jobject obj, jlong infPtr, jstring sourceTarget, jstring sinkTarget) {
  INF inf = (INF) infPtr;
  const char **ports;
  int mode, i;

  if (jack_activate(inf->client)) {
    throwExc(env, "cannot activate client");
    return;
  }

  /* autoconnect ports if requested (can't do this before client is activated)*/
  for (mode=INPUT; mode<=OUTPUT; mode++) {
    jstring target = (mode==INPUT) ? sourceTarget : sinkTarget;
    if (target!=NULL) {
      char *targetPort = allocchars(env, target);
      int portFlags = (*targetPort) ? 0 : JackPortIsPhysical;
      fprintf(stderr, "autoconnecting %s ports\n", MODE_LABEL[mode]);
      ports = jack_get_ports(inf->client, targetPort, NULL,
                    portFlags|MODE_JACK[ 1 - mode ]);
      freechars(env, target, targetPort);
      if (ports == NULL) {
          /* (1-mode as we connect outputs to inputs and inputs to outputs) */
        throwExc2(env, "Cannot find ports to autoconnect to", MODE_LABEL[mode]);
        return;
      }

      for (i=0; i<inf->portCount[mode]; i++) {
        fprintf(stderr, "%s %i\n", MODE_LABEL[mode], (i+1));
        /* will fail if more ports are requested than available */
        if (mode==INPUT) {
          if (jack_connect(inf->client, ports[i],
              jack_port_name(inf->port[mode][i]))) {
            throwExc(env, "cannot autoconnect input port");
            return;
          }
        } else { // mode == OUTPUT
          if (jack_connect(inf->client,
                    jack_port_name(inf->port[mode][i]), ports[i])) {
            throwExc(env, "cannot autoconnect output port");
            return;
          }
        }
      }
      free (ports);
    }
  }
}

/*
 * private void closeClient(long)
 */
JNIEXPORT void JNICALL Java_de_gulden_framework_jjack_JJackNativeClient_closeClient(JNIEnv *env, jobject obj, jlong infPtr) {
  INF inf = (INF) infPtr;
  if (inf!=0 && inf->client!=NULL) {
    jack_client_close(inf->client);
    (*env)->DeleteGlobalRef(env, inf->byteBufferArray[INPUT]);
    (*env)->DeleteGlobalRef(env, inf->byteBufferArray[OUTPUT]);
    (*env)->DeleteWeakGlobalRef(env, inf->owner);
  }
  free(inf);
}

/*
 * private static native int getMaxPorts()
 */
JNIEXPORT jint JNICALL Java_de_gulden_framework_jjack_JJackNativeClient_getMaxPorts(JNIEnv *env, jclass cls) {
  return (jint) MAX_PORTS;
}

/*
 * public static native int getSampleRate() throw JJackException
 */
JNIEXPORT jint JNICALL Java_de_gulden_framework_jjack_JJackNativeClient_getSampleRate(JNIEnv *env, jclass cls) {
  jack_client_t *client = jack_client_open("JJack auxiliary client",
            JackNoStartServer, NULL);
  if (client!=NULL) {
    jint sampleRate = jack_get_sample_rate(client);
    jack_client_close(client);
    return sampleRate;
  } else {
    throwExc(env, "unable to open client; jack not running?");
    return 0;
  }
}

/*
 * public static native int getBufferSize() throw JJackException
 */
JNIEXPORT jint JNICALL Java_de_gulden_framework_jjack_JJackNativeClient_getBufferSize(JNIEnv *env, jclass cls) {
  jack_client_t *client = jack_client_open("JJack auxiliary client",
            JackNoStartServer, NULL);
  if (client!=NULL) {
    jint bufSize = jack_get_buffer_size(client);
    jack_client_close(client);
    return bufSize;
  } else {
    throwExc(env, "unable to open client; jack not running?");
    return 0;
  }
}

