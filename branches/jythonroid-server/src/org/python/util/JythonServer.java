package org.python.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Observable;
import java.util.Observer;
import java.util.Properties;

import org.python.core.CompilerFlags;
import org.python.core.Py;
import org.python.core.PyException;
import org.python.core.PyFile;
import org.python.core.PyModule;
import org.python.core.PyObject;
import org.python.core.PyString;
import org.python.core.PyStringMap;
import org.python.core.PySystemState;
import org.python.core.__builtin__;
import org.python.debug.FixMe;

/**
 * 
 * @author fuzhiqin
 * 
 */
class ObservableWriter extends StringWriter {
	class OBS extends Observable {
		public OBS(Observer obser, String data) {
			addObserver(obser);
			setChanged();
			notifyObservers(data);
			System.out.println("ObservableWriter:" + data);
		}
	}

	Observer obser = null;

	public ObservableWriter(Observer obser) {
		this.obser = obser;
	}

	public void write(int oneByte) {
		super.write(oneByte);
		new OBS(obser, getBuffer().toString());
		emptyBuffer(getBuffer());
	}

	public void write(String s) {
		super.write(s);
		new OBS(obser, getBuffer().toString());
		emptyBuffer(getBuffer());
	}

	public void write(char[] buff) throws IOException {
		super.write(buff);
		new OBS(obser, getBuffer().toString());
		emptyBuffer(getBuffer());
	}

	public void write(char[] buff, int start, int end) {
		super.write(buff, start, end);
		new OBS(obser, getBuffer().toString());
		emptyBuffer(getBuffer());
	}

	private void emptyBuffer(StringBuffer sb) {
		int length = sb.length();
		sb.delete(0, length);
	}

}

/**
 * 
 * @author fuzhiqin
 * 
 */
class ObservableOutputStream extends ByteArrayOutputStream {

}

/**
 * 
 * @author fuzhiqin
 * 
 */
class ShellServer extends Observable implements Runnable, Observer {
	private int port = 6000;

	private OutputStream os = null;

	private InputStream is = null;

	private BufferedReader br = null;

	private PrintWriter pw = null;

	private boolean keeponrun = true;

	JythonServer js = null;

	public ShellServer(JythonServer js) {
		this.js = js;
	}

	public void run() {

		try {
			ServerSocket ss = new ServerSocket(port);
			Socket s = ss.accept();
			os = s.getOutputStream();
			is = s.getInputStream();
			br = new BufferedReader(new InputStreamReader(is));
			pw = new PrintWriter(new OutputStreamWriter(os));
			StringBuffer buffer=new StringBuffer();
			while (keeponrun) {
				String input = br.readLine();
				if (input != null) {
					System.out.println("input:" + input);
			        PyObject code = null;
			        buffer.append(input);
			        try {
			        	code = Py.compile_command_flags(buffer.toString(), "<input>", "single", null, true);
				        // Case 2
				        if (code == Py.None){
				        	//result="\n";       	
				        	buffer.append("\n");
				        	pw.println(FixMe.ps2);
							pw.flush();
								result = "";
				        } else {
							// Case 3				            
				            js.exec(code);
							pw.println(result +FixMe.ps1);
							pw.flush();
							result = "";
							buffer.setLength(0);
							
				        }

			        
			        } catch (PyException exc) {
			            if (Py.matchException(exc, Py.SyntaxError)) {
			                // Case 1
			            	result=exc.toString();
			            	pw.println(buffer.toString()+"1"+result+"\n>>>");
			            	pw.flush();
			            	result="";
			            	buffer.setLength(0);
			            } else if (Py.matchException(exc, Py.ValueError) ||
			                       Py.matchException(exc, Py.OverflowError)) {
			                // Should not print the stack trace, just the error.
                            result=exc.toString();
			            	pw.println("2"+result+"\n>>>");
			            	pw.flush();
			            	result="";
			            	buffer.setLength(0);
			            } else {
                            result=exc.toString();
			            	pw.println("3"+result+"\n>>>");
			            	pw.flush();
			            	result="";
			            	buffer.setLength(0);
			            }
			        }

					
					
					
					
//					
//					try {
//						js.exec(input);
//						pw.println(result+">>>");
//						pw.flush();
//						result="";
//					} catch (Exception e) {
//						pw.println(e.toString()+">>>");
//						pw.flush();
//					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	String result="";
	public void update(java.util.Observable arg0, Object arg1) {
		String res = (String) arg1;
		result=result+res;
		System.out.println("result:" + result);
//		pw.print(result);
//		pw.flush();
	}

	public void close() {

		try {
			this.keeponrun = false;
			this.pw.close();
			this.br.close();
			this.is.close();
			this.os.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}

/**
 * 
 * @author fuzhiqin
 * 
 */
public class JythonServer {
	public String ps1=">>> ";
	public String ps2="+++ ";
	private ShellServer shellserver = null;

	private PyStringMap dict = null;

	private PyModule module;

	protected PySystemState systemState;

	private PyObject locals;

	protected CompilerFlags cflags = null;

	public JythonServer() {
	}

	private void setShellServer(ShellServer ss) {
		this.shellserver = ss;
	}

	private void initailize() {

		if (systemState == null) {
			systemState = Py.getSystemState();
			PySystemState.initialize();
			if (systemState == null) {
				systemState = new PySystemState();
			}
		}
		if (dict == null) {
			dict = new PyStringMap();
		}
		module = new PyModule("main", dict);
		setState();
		locals = module.__dict__;
		ObservableWriter ow = new ObservableWriter(shellserver);
		setOut(ow);
		setErr(ow);
	}

	/**
	 * Initializes the jython runtime. This should only be called once, and
	 * should be called before any other python objects are created (including a
	 * PythonInterpreter).
	 * 
	 * @param preProperties
	 *            A set of properties. Typically System.getProperties() is used.
	 * @param postProperties
	 *            An other set of properties. Values like python.home,
	 *            python.path and all other values from the registry files can
	 *            be added to this property set. PostProperties will override
	 *            system properties and registry properties.
	 * @param argv
	 *            Command line argument. These values will assigned to sys.argv.
	 */
	public static void initialize(Properties preProperties,
			Properties postProperties, String[] argv) {
		PySystemState.initialize(preProperties, postProperties, argv);
	}

	protected void setState() {
		Py.setSystemState(systemState);
	}

	/**
	 * Set the Python object to use for the standard output stream
	 * 
	 * @param outStream
	 *            Python file-like object to use as output stream
	 */
	public void setOut(PyObject outStream) {
		systemState.stdout = outStream;
	}

	/**
	 * Set a java.io.Writer to use for the standard output stream
	 * 
	 * @param outStream
	 *            Writer to use as output stream
	 */
	public void setOut(java.io.Writer outStream) {
		setOut(new PyFile(outStream));
	}

	/**
	 * Set a java.io.OutputStream to use for the standard output stream
	 * 
	 * @param outStream
	 *            OutputStream to use as output stream
	 */
	public void setOut(java.io.OutputStream outStream) {
		setOut(new PyFile(outStream));
	}

	public void setErr(PyObject outStream) {
		systemState.stderr = outStream;
	}

	public void setErr(java.io.Writer outStream) {
		setErr(new PyFile(outStream));
	}

	public void setErr(java.io.OutputStream outStream) {
		setErr(new PyFile(outStream));
	}

	/**
	 * Evaluate a string as Python source and return the result
	 * 
	 * @param s
	 *            the string to evaluate
	 */
	public PyObject eval(String s) {
		setState();
		return __builtin__.eval(new PyString(s), locals);
	}

	/**
	 * Execute a string of Python source in the local namespace
	 * 
	 * @param s
	 *            the string to execute
	 */
	InteractiveConsole interp=new InteractiveConsole();
	public void exec(String s) {
		setState();
//		Py
//				.exec(Py.compile_flags(s, "<string>", "exec", cflags), locals,
//						locals);
		runsource(s);
		
	}

	/**
	 * Execute a Python code object in the local namespace
	 * 
	 * @param code
	 *            the code object to execute
	 */
	public void exec(PyObject code) {
		setState();
		Py.exec(code, locals, locals);
	}

	/**
	 * Execute a file of Python source in the local namespace
	 * 
	 * @param s
	 *            the name of the file to execute
	 */
	public void execfile(String s) {
		setState();
		__builtin__.execfile_flags(s, locals, locals, cflags);
	}

	public void execfile(java.io.InputStream s) {
		execfile(s, "<iostream>");
	}

	public void execfile(java.io.InputStream s, String name) {
		setState();
		Py.runCode(Py.compile_flags(s, name, "exec", cflags), locals, locals);
	}

	// Getting and setting the locals dictionary
	public PyObject getLocals() {
		return locals;
	}

	public void setLocals(PyObject d) {
		locals = d;
	}

	/**
	 * Set a variable in the local namespace
	 * 
	 * @param name
	 *            the name of the variable
	 * @param value
	 *            the value to set the variable to. Will be automatically
	 *            converted to an appropriate Python object.
	 */
	public void set(String name, Object value) {
		locals.__setitem__(name.intern(), Py.java2py(value));
	}

	/**
	 * Set a variable in the local namespace
	 * 
	 * @param name
	 *            the name of the variable
	 * @param value
	 *            the value to set the variable to
	 */
	public void set(String name, PyObject value) {
		locals.__setitem__(name.intern(), value);
	}

	/**
	 * Get the value of a variable in the local namespace
	 * 
	 * @param name
	 *            the name of the variable
	 */
	public PyObject get(String name) {
		return locals.__finditem__(name.intern());
	}

	/**
	 * Get the value of a variable in the local namespace Value will be returned
	 * as an instance of the given Java class.
	 * <code>interp.get("foo", Object.class)</code> will return the most
	 * appropriate generic Java object.
	 * 
	 * @param name
	 *            the name of the variable
	 * @param javaclass
	 *            the class of object to return
	 */
	public Object get(String name, Class javaclass) {
		return Py.tojava(locals.__finditem__(name.intern()), javaclass);
	}

	public void cleanup() {
		systemState.callExitFunc();
	}
	
	
	
	//interactive 
    public void interact(String banner) {
        if(banner != null) {
            write(banner);
            write("\n");
        }
        // Dummy exec in order to speed up response on first command
        //exec("2");
        // System.err.println("interp2");
        boolean more = false;
        while(true) {
            PyObject prompt = more ? systemState.ps2 : systemState.ps1;
            String line;
            try {
                line = raw_input(prompt);
            } catch(PyException exc) {
                if(!Py.matchException(exc, Py.EOFError))
                    throw exc;
                write("\n");
                break;
            }
            more = push(line);
        }
    }
    
    
    public StringBuffer buffer = new StringBuffer();
    public String filename="<stdin>";
    /**
     * Push a line to the interpreter.
     * 
     * The line should not have a trailing newline; it may have internal
     * newlines. The line is appended to a buffer and the interpreter's
     * runsource() method is called with the concatenated contents of the buffer
     * as source. If this indicates that the command was executed or invalid,
     * the buffer is reset; otherwise, the command is incomplete, and the buffer
     * is left as it was after the line was appended. The return value is 1 if
     * more input is required, 0 if the line was dealt with in some way (this is
     * the same as runsource()).
     */
    public boolean push(String line) {
        if(buffer.length() > 0)
            buffer.append("\n");
        buffer.append(line);
        boolean more = runsource(buffer.toString(), filename);
        if(!more)
            resetbuffer();
        return more;
    }
    public void resetbuffer() {
        buffer.setLength(0);
    }
    public void write(String data) {
        Py.stderr.write(data);
    }
    
    /**
     * Compile and run some source in the interpreter.
     *
     * Arguments are as for compile_command().
     *
     * One several things can happen:
     *
     * 1) The input is incorrect; compile_command() raised an exception
     * (SyntaxError or OverflowError).  A syntax traceback will be printed
     * by calling the showsyntaxerror() method.
     *
     * 2) The input is incomplete, and more input is required;
     * compile_command() returned None.  Nothing happens.
     *
     * 3) The input is complete; compile_command() returned a code object.
     * The code is executed by calling self.runcode() (which also handles
     * run-time exceptions, except for SystemExit).
     *
     * The return value is 1 in case 2, 0 in the other cases (unless an
     * exception is raised).  The return value can be used to decide
     * whether to use sys.ps1 or sys.ps2 to prompt the next line.
     **/
    public boolean runsource(String source) {
        return runsource(source, "<input>", "single");
    }

    public boolean runsource(String source, String filename) {
        return runsource(source, filename, "single");
    }

    public boolean runsource(String source, String filename, String symbol) {
        PyObject code;
        try {
            code = Py.compile_command_flags(source, filename, symbol, cflags, true);
        } catch (PyException exc) {
            if (Py.matchException(exc, Py.SyntaxError)) {
                // Case 1
                showexception(exc);
                return false;
            } else if (Py.matchException(exc, Py.ValueError) ||
                       Py.matchException(exc, Py.OverflowError)) {
                // Should not print the stack trace, just the error.
                showexception(exc);
                return false;
            } else {
                throw exc;
            }
        }
        // Case 2
        if (code == Py.None)
            return true;
        // Case 3
        exec(code);
        return false;
    }
    public void showexception(PyException exc) {
        // Should probably add code to handle skipping top stack frames
        // somehow...
        Py.printException(exc); 
    }
    /**
     * execute a code object.
     *
     * When an exception occurs, self.showtraceback() is called to display
     * a traceback.  All exceptions are caught except SystemExit, which is
     * reraised.
     *
     * A note about KeyboardInterrupt: this exception may occur elsewhere
     * in this code, and may not always be caught.  The caller should be
     * prepared to deal with it.
     **/

    // Make this run in another thread somehow????
    public void runcode(PyObject code) {
        try {
            exec(code);
        } catch (PyException exc) {
            if (Py.matchException(exc, Py.SystemExit)) throw exc;
            showexception(exc);
        }
    }
    
    /**
     * Write a prompt and read a line.
     * 
     * The returned line does not include the trailing newline. When the user
     * enters the EOF key sequence, EOFError is raised.
     * 
     * The base implementation uses the built-in function raw_input(); a
     * subclass may replace this with a different implementation.
     */
    public String raw_input(PyObject prompt) {
        return __builtin__.raw_input(prompt);
    }
    
    
    
    
	/**
	 * that's the main entrance of the program that runs in shell mode you can
	 * run this by such command in Android shell: #dalvikvm -classpath **.apk
	 * org.python.util.JythonServer
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("Starting the service...");
		FixMe.initialize();
		JythonServer js = new JythonServer();
		ShellServer ss = new ShellServer(js);
		js.setShellServer(ss);
		js.initailize();
		new Thread(ss).start();
		System.out.println("Service started...");
	}
}
