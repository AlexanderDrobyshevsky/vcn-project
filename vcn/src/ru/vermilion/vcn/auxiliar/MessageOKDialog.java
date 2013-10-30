package ru.vermilion.vcn.auxiliar;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class MessageOKDialog extends Dialog {
	private Object result;
	private Shell shell;
	protected String message;
	protected String title;
	
	public MessageOKDialog(Shell parent, int style) {
		super(parent, style);
	}

	public MessageOKDialog(Shell parent, String title, String message) {
		this(parent, SWT.NONE);
		
		this.message = message;
		this.title = title;
	}

	public Object open() {
		createContents();
		
		UI.centerShell(getParent(), shell);
		
		shell.open();
		shell.layout();
		Display display = getParent().getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		
		return result;
	}

	protected void createContents() {
		shell = new Shell(getParent(), SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		shell.setText(title);
		Color backgroundColor = shell.getDisplay().getSystemColor(SWT.COLOR_WHITE);
		shell.setBackground(backgroundColor);

		GridLayout gl = new GridLayout(1, false);
		gl.horizontalSpacing = 20;
		gl.verticalSpacing = 10;
		gl.marginHeight = 10;
		gl.marginWidth = 10;
		shell.setLayout(gl);

		final Composite textClient = new Composite(shell, SWT.NONE);

		gl = new GridLayout(1, false);
		gl.horizontalSpacing = 0;
		gl.verticalSpacing = 0;
		gl.marginHeight = 0;
		gl.marginWidth = 0;
		textClient.setLayout(gl);
		
		textClient.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.minimumHeight = 50;
		gd.minimumWidth = 200;
		textClient.setLayoutData(gd);
		
		Label messageLabel = new Label(textClient, SWT.BOLD);
		messageLabel.setBackground(backgroundColor);
		messageLabel.setText(message);
		
		gd = new GridData (SWT.CENTER, SWT.CENTER, true, true);
		messageLabel.setLayoutData(gd);

		Button OK = new Button(shell, SWT.PUSH);
		OK.setText("     OK     ");
		OK.setBackground(backgroundColor);
		
		GridData okGridData = new GridData(GridData.HORIZONTAL_ALIGN_CENTER);
		OK.setLayoutData(okGridData);
		OK.setFocus();
		OK.addSelectionListener (new SelectionAdapter () {
			public void widgetSelected (SelectionEvent e) {
				shell.close();
			}
		});

		shell.setDefaultButton(OK);
		
		shell.pack();
	}
}