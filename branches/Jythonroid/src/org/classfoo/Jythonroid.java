package org.classfoo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import org.python.util.PythonInterpreter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.Selection;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.view.View.OnLongClickListener;
import android.widget.EditText;
/**
 * the gui part of jythonroid<br>
 * it will only contains a easy console of python<br>
 * @author fuzhiqin
 * 
 */
public class Jythonroid extends Activity {
//	ShellClient shellclient = null;

	private String ps1 = ">>> ";

	private String ps2 = "+++ ";

	private EditText shell = null;

	// private ShellEditer shell=null;

//	class ShellClient implements Runnable {
//		private String address = "127.0.0.1";
//
//		private int port = 6000;
//
//		private InputStream is = null;
//
//		private OutputStream os = null;
//
//		private boolean keeponrun = true;
//
//		private Jythonroid jr = null;
//
//		private BufferedReader br = null;
//
//		private PrintWriter pw = null;
//
//		private Handler hd = null;
//
//		public ShellClient(Jythonroid jythonroid, Handler hd) {
//			this.jr = jythonroid;
//			this.hd = hd;
//			// wait till connected to the server
//			Socket s = null;
//			do {
//				try {
//					s = new Socket(address, port);
//					this.wait(1000);
//				} catch (UnknownHostException e1) {
//				} catch (IOException e1) {
//				}
//				// FIXME sending some message here
//				// callHandler();
//				catch (InterruptedException e) {
//				}
//			} while (s == null);
//			callHandler("initial", true);
//			try {
//				is = s.getInputStream();
//				os = s.getOutputStream();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			br = new BufferedReader(new InputStreamReader(is));
//			pw = new PrintWriter(new OutputStreamWriter(os));
//			// sendMsg("2");
//		}
//
//		public void run() {
//			// is initialized
//			try {
//				while (keeponrun) {
//					String result = br.readLine();
//					callHandler("result", result);
//				}
//			} catch (IOException e) {
//			}
//		}
//
//		private void callHandler(String key, String str) {
//			Message msg = Message.obtain();
//			Bundle data = new Bundle();
//			data.putString(key, str);
//			msg.setData(data);
//			hd.sendMessage(msg);
//		}
//
//		private void callHandler(String key, Boolean boo) {
//			Message msg = Message.obtain();
//			Bundle data = new Bundle();
//			data.putBoolean(key, boo);
//			// data.put(key, boo);
//			msg.setData(data);
//			hd.sendMessage(msg);
//		}
//
//		public void sendMsg(String msg) {
//			pw.println(msg);
//			pw.flush();
//		}
//	}

	/**
	 * provide an interactive shell in the screen
	 */
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.main);
		shell = (EditText) findViewById(R.id.shell);
		// shell=new ShellEditer(this, null, null);
		//setContentView(shell);
		shell.setEnabled(true);
		initializeShell(shell);
//		Handler hd = new Handler() {
//			public void handleMessage(Message msg) {
//				if (msg.getData().containsKey("initial")) {
//					alert("initialized");
//					shell.setEnabled(true);
//				} else {
//					shell.append("\n" + (String) msg.getData().get("result"));
//				}
//			}
//		};
		// running the backend
//		new Thread(new Runnable() {
//			public void run() {
//				try {
//					Runtime
//							.getRuntime()
//							.exec(
//									"dalvikvm -classpath "
//											+ "/data/app/org.classfoo.apk "
//											+ "org.python.util.JythonServer");
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//			}
//		}, "JythonServer").start();
//		shellclient = new ShellClient(this, hd);
//		new Thread(shellclient).start();
	}

	public void println(String line) {

	}

	/**
	 * initialize the workplace widget
	 * 
	 * @param EditText
	 *            shell initialize this EditText widget, adding some action
	 *            listeners.
	 */
	private int linenumber = 0;

	private int position = 3;

	private String message = "";

	private int deep = 0;

	private void initializeShell(EditText shell) {
		final StringBuffer sb = new StringBuffer();
		// FIXME set the cursor to the end of '>>>'
		// cursorToEnd(shell);
		// Spanned str = Html.fromHtml("<font color='#FF0000'>sdaf</font>");
		// shell.append(str);
		shell.setSelection(3);
		shell.setOnKeyListener(new OnKeyListener() {
			public boolean onKey(View v, int i, KeyEvent k) {
				EditText shell = (EditText) v;
				switch (i) {
				case 66:
//					if (k.getAction() == 1) {
						shell.setSelection(shell.getText().length());
						String[] f = shell.getText().toString().split("\n");
						String msg = f[f.length - 1].substring(3);
						PythonInterpreter pi=new PythonInterpreter();
						StringWriter sw=new StringWriter();
						pi.setOut(sw);
						pi.exec(msg);
						alert(sw.toString());
						
//						shellclient.sendMsg(msg);
//					}
					return true;
				case 19:
					alert("19");
					return true;
				case 20:
					alert("20");
					return true;
				default:
					// if(i>=29&&i<=54){
					// sb.append(String.valueOf(i));
					// }
					return false;
				}
			}

		});
		shell.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				v.computeScroll();
				v.getBaseline();
				EditText shell = (EditText) v;

			}

		});
		shell.setOnFocusChangeListener(new OnFocusChangeListener() {
			public void onFocusChange(View v, boolean hasFocus) {
			}

		});
		shell.setOnLongClickListener(new OnLongClickListener() {

			public boolean onLongClick(View v) {
				return false;
			}

		});
		// shell.setOnCreateContextMenuListener(l).setOnPopulateContextMenuListener();
	}

	public void cursorToEnd(EditText shell) {
		Editable etext = shell.getText();
		int position = etext.length();
		// end of buffer, for instance
		Selection.setSelection(etext, position);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, 0, 2, R.drawable.icon);
		menu.add(0, 1, 0, R.string.sure);
		menu.add(0, 2, 1, R.string.app_name);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (featureId) {
		case 0:
			alert("classfoo");
			return true;
		case 1:
			alert("iloveyou");
			return true;
		case 2:
			alert("comeonbaby");
			return true;
		}
		return false;
	}

	/**
	 * alert a string message on the screen
	 * 
	 * @param msg
	 */
	private void alert(CharSequence msg) {
		new AlertDialog.Builder(this).setIcon(R.drawable.icon).setTitle(msg)
				.setPositiveButton(R.string.sure,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {

							}
						}).setCancelable(true).setMessage("Hey").show();
	}

}
