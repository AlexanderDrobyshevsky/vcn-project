package ru.vermilion.vcn.auxiliar;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class StringRequestOKCancelDialog {

	private String defaultText;
	private String title;
	private String inviteText;
	private Shell shell;
	
	private static Color backgroundColor;
	
	public StringRequestOKCancelDialog(Shell shell, String defaultText, String title, String inviteText) {
		this.defaultText = defaultText;
		this.title = title;
		this.inviteText = inviteText;
		this.shell = shell;
		
		backgroundColor = shell.getDisplay().getSystemColor(SWT.COLOR_WHITE);
	}
	
	public String open() {
		Shell dialog = createContent();
		
		UI.centerShell(shell, dialog);
		
		dialog.open();

		while (!dialog.isDisposed()) {
			if (!dialog.getDisplay().readAndDispatch())
				dialog.getDisplay().sleep();
		}

		if (!dialog.isDisposed()) {
			dialog.close();
		}

		System.out.println("#User typed: " + s[0]);

		return s[0];
	}
	
	private String s[] = new String[1];
	
	private Shell createContent() {
		final Shell dialog = new Shell(shell, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		dialog.setText(title);
		dialog.setBackground(backgroundColor);
		
		FormLayout formLayout = new FormLayout();
		formLayout.marginWidth = 10;
		formLayout.marginHeight = 10;
		formLayout.spacing = 10;
		dialog.setLayout(formLayout);

		Label label = new Label(dialog, SWT.NONE);
		label.setText(inviteText);
		label.setBackground(backgroundColor);
		FormData data = new FormData();
		label.setLayoutData(data);

		Button cancel = new Button(dialog, SWT.PUSH);
		cancel.setText("Cancel");
		cancel.setBackground(backgroundColor);
		data = new FormData();
		data.width = 60;
		data.right = new FormAttachment(100, 0);
		data.bottom = new FormAttachment(100, 0);
		cancel.setLayoutData(data);
		cancel.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				System.out.println("User cancelled dialog");
				dialog.close();
			}
		});

		final Text text = new Text(dialog, SWT.BORDER);
		data = new FormData();
		data.width = 200;
		data.left = new FormAttachment(label, 0, SWT.DEFAULT);
		data.right = new FormAttachment(100, 0);
		data.top = new FormAttachment(label, 0, SWT.CENTER);
		data.bottom = new FormAttachment(cancel, 0, SWT.DEFAULT);
		text.setLayoutData(data);
		text.setText(defaultText);

		final Button ok = new Button(dialog, SWT.PUSH);
		ok.setText("OK");
		ok.setBackground(backgroundColor);
		
		data = new FormData();
		data.width = 60;
		data.right = new FormAttachment(cancel, 0, SWT.DEFAULT);
		data.bottom = new FormAttachment(100, 0);
		ok.setLayoutData(data);
		
		ok.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				System.out.println("User typed: " + text.getText());
				s[0] = text.getText();

				dialog.close();
			}
		});
		
		ok.setEnabled(!text.getText().trim().isEmpty());
		text.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				ok.setEnabled(!text.getText().trim().isEmpty());
			}
		});

		dialog.setDefaultButton(ok);

		dialog.pack();
		text.setFocus();
		
		return dialog;
	}
	
}
