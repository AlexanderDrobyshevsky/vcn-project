package ru.vermilion.vcn.app.dialogs;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
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
import org.eclipse.swt.widgets.Text;

import ru.vermilion.vcn.auxiliar.UI;

public class GlobalSearchDialog extends Dialog {
	private DialogResult result;
	private Shell shell;

	public GlobalSearchDialog(Shell parent, int style) {
		super(parent, style);
	}

	public GlobalSearchDialog(Shell parent) {
		this(parent, SWT.NONE);
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
		shell.setText("Global Search");
		Color backgroundColor = shell.getDisplay().getSystemColor(SWT.COLOR_WHITE);
		shell.setBackground(backgroundColor);

		GridLayout gl = new GridLayout(1, false);
		gl.verticalSpacing = 5;
		gl.marginHeight = 0;
		gl.marginWidth = 0;
		shell.setLayout(gl);

		final Composite contentClient = new Composite(shell, SWT.NONE);

		gl = new GridLayout(2, false);
		gl.horizontalSpacing = 5;
		gl.verticalSpacing = 0;
		gl.marginHeight = 5;
		gl.marginWidth = 5;
		contentClient.setLayout(gl);

		contentClient.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.minimumHeight = 50;
		gd.minimumWidth = 200;
		contentClient.setLayoutData(gd);

		Label messageLabel = new Label(contentClient, SWT.BOLD);
		messageLabel.setBackground(backgroundColor);
		messageLabel.setText("Text to search: ");

		gd = new GridData(SWT.CENTER, SWT.CENTER, true, true);
		messageLabel.setLayoutData(gd);
		
		
		final Text searchPhrase = new Text(contentClient, SWT.BORDER);
		searchPhrase.setFocus();
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.minimumWidth = 200;
		gd.horizontalAlignment = SWT.CENTER;
		searchPhrase.setLayoutData(gd);	
		
		Composite buttonsClient = new Composite(shell, SWT.NONE);
		buttonsClient.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_WHITE));

		gl = new GridLayout(2, false);
		gl.horizontalSpacing = 10;
		gl.verticalSpacing = 10;
		gl.marginHeight = 5;
		gl.marginWidth = 5;
		buttonsClient.setLayout(gl);
		
		gd = new GridData(GridData.HORIZONTAL_ALIGN_END);
		gd.horizontalAlignment = SWT.RIGHT;
		buttonsClient.setLayoutData(gd);
		
		final Button OK = new Button(buttonsClient, SWT.PUSH);
		OK.setText("     OK     ");
		OK.setBackground(backgroundColor);

		GridData okGridData = new GridData(GridData.HORIZONTAL_ALIGN_END);
		OK.setLayoutData(okGridData);
		
		Button cancel = new Button(buttonsClient, SWT.PUSH);
		cancel.setText("     Cancel     ");
		cancel.setBackground(backgroundColor);

		GridData cancelGridData = new GridData(GridData.HORIZONTAL_ALIGN_END);
		cancel.setLayoutData(cancelGridData);
		
		
		OK.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				result = new DialogResult();
				result.searchText = searchPhrase.getText();
				
				shell.close();
			}
		});
		
		OK.setEnabled(!searchPhrase.getText().trim().isEmpty());
		searchPhrase.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				OK.setEnabled(!searchPhrase.getText().trim().isEmpty());
			}
		});
		
		cancel.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				shell.close();
			}
		});

		shell.setDefaultButton(OK);

		shell.pack();
	}
	
	public static class DialogResult {
		public String searchText;
	} 
	
	public DialogResult getResult() {
		return result;
	}
}
