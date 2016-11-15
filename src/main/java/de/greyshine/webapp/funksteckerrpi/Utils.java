package de.greyshine.webapp.funksteckerrpi;

import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public abstract class Utils {
	
	private static final Charset CHARSET_UTF8 = Charset.forName( "UTF-8" );
	
	public static final File BASEDIR = toCanonicalFile(new File("."));
	public static final int EOF_STREAM = -1;
	public static final InputStream INPUTSTREAM_EOF = new InputStream() {
		
		private volatile long count=0;
		
		@Override
		public int read() throws IOException {
			count++;
			return EOF_STREAM;
		}

		@Override
		public int available() throws IOException {
			return 0;
		}
		
		public String toString() {
			return getClass().getName() +" ["+ count +"]";
		};
	};
	
	public static final ThreadLocal<Object> TL_SYNC_WAIT = new ThreadLocal<Object>() {
		@Override
		protected Object initialValue() {
			return new Object();
		}
	};
	
	public static String trimToNull(String inValue) {
		inValue = inValue == null ? null : inValue.trim();
		return inValue == null || inValue.isEmpty() ? null : inValue;
	}

	public static String trimToEmpty(String inValue) {
		inValue = inValue == null ? null : inValue.trim();
		return inValue == null || inValue.isEmpty() ? "" : inValue;
	}
	
	public static class Kvp<T,S> {
		
		public T key;
		public S value;
		public Kvp() {}
		public Kvp(T key, S value) {
			this.key = key;
			this.value = value;
		}
		
		
		
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((key == null) ? 0 : key.hashCode());
			result = prime * result + ((value == null) ? 0 : value.hashCode());
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Kvp other = (Kvp) obj;
			if (key == null) {
				if (other.key != null)
					return false;
			} else if (!key.equals(other.key))
				return false;
			if (value == null) {
				if (other.value != null)
					return false;
			} else if (!value.equals(other.value))
				return false;
			return true;
		}
		
		@Override
		public String toString() {
			return "Kvp [key=" + key + ", value=" + value + "]";
		}
	}

	public static File toCanonicalFile(File file) {

		try {

			return file == null ? null : file.getCanonicalFile();

		} catch (Exception e) {
			return file.getAbsoluteFile();
		}
	}
	
	public static RuntimeException toRuntimeException(Exception e) {
		
		if ( e == null ) {
			return new RuntimeException();
		} else if ( e instanceof RuntimeException ) {
			return (RuntimeException) e;
		}
		
		return new RuntimeException( e );
	}

	public static void flush(OutputStream os) {
		
		try {
			os.flush();
		} catch (Exception e) {
		}
		
	}
	
	public static <T> T defaultIfNull(T t, T inDefault) {
		return t != null ? t : inDefault;
	}
	
	public static String defaultIfBlank(String inValue, String inDefault) {

		return isNotBlank(inValue) ? inValue : inDefault;
	}

	public static boolean isNotBlank(String inValue) {

		return !isBlank(inValue);
	}

	public static boolean isBlank(String inValue) {

		if (inValue == null) {
			return true;
		}

		for (char c : inValue.toCharArray()) {
			if (!Character.isWhitespace(c)) {
				return false;
			}
		}

		return true;
	}
	
	public static String consoleToString(String... inCmds) throws IOException {

		final StringBuilder sb = new StringBuilder();

		console(null, new OutputStream() {

			@Override
			public void write(int b) throws IOException {
				sb.append((char) b);
			}
		}, null, null, inCmds);

		return sb.toString();
	}

	public static int console(InputStream stdin, OutputStream stdout, OutputStream stderr, File inDir, String... inCmd) throws IOException {

		if (stdin != null) {
			throw new UnsupportedOperationException("reading from stdin not yet supported");
		}

		inDir = defaultIfNull(inDir, BASEDIR);

		stdout = defaultIfNull(stdout, System.out);
		stderr = defaultIfNull(stderr, System.err);

		final ProcessBuilder pb = new ProcessBuilder(inCmd);
		pb.directory(inDir);

		Process p = pb.start();
		
		class StreamRedirector extends Thread {

			boolean isProcessDone = false;
			boolean isThreadRunning = false;

			final InputStream is;
			final OutputStream os;

			public StreamRedirector(InputStream is, OutputStream os) {

				this.is = is;
				this.os = os;
			}

			public StreamRedirector startThread() {

				start();

				while (!isThreadRunning) {

					synchronized (this) {

						try {
							this.wait(1000);
						} catch (InterruptedException e) {
							//
						}
					}
				}

				return this;
			}

			@Override
			public void run() {

				isThreadRunning = true;

				synchronized (this) {

					this.notifyAll();
				}

				// System.out.println("started: " + Thread.currentThread());

				if (os == null) {
					return;
				}

				waitForProcessData();

				// actual reading of input
				try {

					while (is.available() > 0) {

						final int b = is.read();

						os.write(b);

						if (b == '\n') {

							flush(os);
						}
					}

				} catch (IOException e) {

				} finally {

					flush(os);
				}

				// System.out.println("ended: " + Thread.currentThread());
			}

			private void waitForProcessData() {

				// wait until process is done or something is available
				try {
					while (!isProcessDone && is.available() <= 0) {
						synchronized (this) {
							try {
								this.wait(100);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					}
					// System.out.println( "WAIT for processdata finished
					// [processExited="+ isProcessDone +", data.available="+
					// is.available() +"]" );
				} catch (IOException e) {
					e.printStackTrace();
				}

			}

			public void informProcessDone() {
				this.isProcessDone = true;
				synchronized (this) {

					this.notifyAll();
				}
			}
		}

		final StreamRedirector srStdout = new StreamRedirector(p.getInputStream(), stdout).startThread();
		final StreamRedirector srStderr = new StreamRedirector(p.getErrorStream(), stderr).startThread();
		int exitStatus = -1;

		try {

			exitStatus = p.waitFor();
			// System.out.println("exit=" + exitStatus);

			srStdout.informProcessDone();
			srStderr.informProcessDone();

			join(srStdout, srStderr);

			return exitStatus;

		} catch (InterruptedException e) {

			throw new IOException(e);

		} finally {

			join(srStdout, srStderr);
		}
	}
	
	public static void join(Thread... ts) {

		if (ts == null) {
			return;
		}

		for (Thread thread : ts) {

			try {
				thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}

	}

	public static InputStream getResource(String inPath) {
		
		final InputStream is = inPath == null ? null :Thread.currentThread().getContextClassLoader().getResourceAsStream(inPath); 
		
		return is == null ? INPUTSTREAM_EOF : is;
	}
	
	public static boolean copySafe(InputStream inInputStream, OutputStream inOutputStream, boolean inCloseIs,
			boolean inCloseOs) {
		
		try {
			
			copy(inInputStream, inOutputStream, inCloseIs, inCloseOs);
			
		} catch (Exception e) {
			return false;
		}
		
		return true;
	}
	
	public static long copy(InputStream inInputStream, OutputStream inOutputStream, boolean inCloseIs,
			boolean inCloseOs) throws IOException {

		final byte[] buffer = new byte[2000];

		long count = 0;
		int n = 0;
		
		try {

			while (EOF_STREAM != (n = inInputStream.read(buffer))) {
				inOutputStream.write(buffer, 0, n);
				count += n;
			}

			inOutputStream.flush();
			return count;

		} catch (IOException e) {

			throw e;
			
		} finally {

			if (inCloseIs) {
				close(inInputStream);
			}
			if (inCloseOs) {
				close(inOutputStream);
			}
		}
	}
	
	public static void close(Closeable... inCloseables) {

		if (inCloseables != null) {

			for (Closeable closeable : inCloseables) {

				try {

					closeable.close();

				} catch (Exception e) {
					// swallow
				}
			}
		}
	}

	public static JsonElement readJson(String inJsonText) {
		return Utils.isBlank( inJsonText ) ? JsonNull.INSTANCE : readJson( new ByteArrayInputStream( inJsonText.getBytes( CHARSET_UTF8 ) ) );
	}
	
	public static JsonElement readJson(InputStream inIs) {

		return new JsonParser().parse(new InputStreamReader(inIs, CHARSET_UTF8));
	}
	
	public static JsonObject readJsonObject(File inJsonFile) throws IOException {
		
		return readJson( inJsonFile ).getAsJsonObject();
	}
	
	public static JsonElement readJson(File aFile) throws IOException {

		InputStream theIs = null;

		try {

			theIs = (aFile == null || !aFile.isFile() ? null : new FileInputStream(aFile));
			return readJson(theIs);

		} finally {

			close( theIs );
		}
	}

	public static int min(int inMin, int inValue) {
		return inValue < inMin ? inMin : inValue;
	}

	public static void waitMillis(final int timeToWait) {
		
		long cont = System.currentTimeMillis()+timeToWait;
		long ttw = timeToWait;
			
		final Object syncObject = new Object();
		
		while( ttw > 0 ) {

			try {
				
				synchronized ( syncObject ) {
					syncObject.wait( ttw );	
				}
				
			} catch (InterruptedException e) {
				System.err.println(e);
			}
			
			ttw = cont - System.currentTimeMillis();
		}
	}
	
	public static String formatDate(String inFormat, Date inDate) {
		return new SimpleDateFormat(inFormat).format(inDate);
	}
	
	public static String formatDate(String inFormat) {
		return formatDate( inFormat, new Date() );
	}
	
}
