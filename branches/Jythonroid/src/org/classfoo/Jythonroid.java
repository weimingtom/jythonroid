package org.classfoo;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import org.python.util.ClassFoo;
import org.python.util.PythonInterpreter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
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
 * 
 * @author fuzhiqin
 * 
 */
public class Jythonroid extends Activity {
	// ShellClient shellclient = null;
	private PythonInterpreter pi = null;
	private String ps1 = ">>> ";

	private String ps2 = "+++ ";

	private EditText shell = null;

	/**
	 * provide an interactive shell in the screen
	 */
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.main);
		shell = (EditText) findViewById(R.id.shell);
		// shell=new ShellEditer(this, null, null);
		// setContentView(shell);
		shell.setEnabled(true);
		initializeShell(shell);
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
					alert(new ClassFoo().getStr());
					
					// if (k.getAction() == 1) {
					// initialize the PythonInterpreter
//					new Thread(){
//						public void run(){
//							PythonInterpreter pi=new PythonInterpreter();	
//							alert(pi.toString());
//						}
//					}.start();

//					try {
//						alert("a");
//						DexFile f=new DexFile(new File("/data/app/org.classfoo.apk"));
//						alert("b");
//						Class<?> c = f.loadClass("org/python/util/PythonInterpreter", System.class.getClassLoader());
//						alert("c");
//						PythonInterpreter pi = (PythonInterpreter) c.newInstance();
//						alert("d'");
//
//					} catch (IllegalAccessException e) {
//						alert(e.toString());
//					} catch (InstantiationException e) {
//						alert(e.toString());
//					}catch (IOException e) {
//						alert(e.toString());
//					}
					shell.setSelection(shell.getText().length());
					String[] f = shell.getText().toString().split("\n");
					String msg = f[f.length - 1].substring(3);
					// pi.exec(msg);

					alert("执行完毕");
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

	class foo extends URLClassLoader {
		public foo(String path) throws MalformedURLException {
			super(new URL[] { new URL(path) });
		}

		@Override
		protected Class<?> findClass(String className)
				throws ClassNotFoundException {
			alert("查找类");
			return super.findClass(className);
		}
	}
}
