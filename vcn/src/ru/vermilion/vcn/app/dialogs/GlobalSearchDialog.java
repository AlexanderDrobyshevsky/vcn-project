package ru.vermilion.vcn.app.dialogs;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
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

		UI.prepareComposite(shell, 1, 5, 5, 0, 0);

		final Composite contentClient = new Composite(shell, SWT.NONE);
		UI.prepareComposite(contentClient, 2, 5, 5, 5, 5);

		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.minimumHeight = 50;
		gd.minimumWidth = 200;
		contentClient.setLayoutData(gd);

		Label messageLabel = new Label(contentClient, SWT.BOLD);
		messageLabel.setBackground(UI.getGeneralBackgroudColor(shell));
		messageLabel.setText("Text to search: ");

		gd = new GridData(SWT.CENTER, SWT.CENTER, false, false);
		messageLabel.setLayoutData(gd);
	
		final Text searchPhrase = new Text(contentClient, SWT.BORDER);
		searchPhrase.setFocus();
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.minimumWidth = 200;
		gd.horizontalAlignment = SWT.CENTER;
		searchPhrase.setLayoutData(gd);	
		
		// Case sensitive check box
		final Button isCaseSens = new Button(contentClient, SWT.CHECK);
		isCaseSens.setText("&Case sensitive");
		isCaseSens.setBackground(UI.getGeneralBackgroudColor(shell));
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.minimumWidth = 150;
		gd.horizontalAlignment = SWT.LEFT;
		gd.horizontalSpan = 2;
		gd.horizontalIndent = 25;
		isCaseSens.setLayoutData(gd);	
		
		// Handle Node Names
		final Button isHandleNodeNames = new Button(contentClient, SWT.CHECK);
		isHandleNodeNames.setText("Handle just &node names");
		isHandleNodeNames.setBackground(UI.getGeneralBackgroudColor(shell));
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.minimumWidth = 150;
		gd.horizontalAlignment = SWT.LEFT;
		gd.horizontalSpan = 2;
		gd.horizontalIndent = 25;
		isHandleNodeNames.setLayoutData(gd);
		
		// Start over
		final Button isStartOver = new Button(contentClient, SWT.CHECK);
		isStartOver.setText("&Start over (otherwise start from current node)");
		isStartOver.setBackground(UI.getGeneralBackgroudColor(shell));
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.minimumWidth = 150;
		gd.horizontalAlignment = SWT.LEFT;
		gd.horizontalSpan = 2;
		gd.horizontalIndent = 25;
		isStartOver.setLayoutData(gd);
		
		
		Composite buttonsClient = new Composite(shell, SWT.NONE);
		UI.prepareComposite(buttonsClient, 2, 10, 10, 5, 5);
		
		gd = new GridData(GridData.HORIZONTAL_ALIGN_END);
		gd.horizontalAlignment = SWT.RIGHT;
		buttonsClient.setLayoutData(gd);
		
		final Button OK = new Button(buttonsClient, SWT.PUSH);
		OK.setText("     FIND     ");
		OK.setBackground(UI.getGeneralBackgroudColor(shell));

		GridData okGridData = new GridData(GridData.HORIZONTAL_ALIGN_END);
		OK.setLayoutData(okGridData);
		
		Button cancel = new Button(buttonsClient, SWT.PUSH);
		cancel.setText("     Cancel     ");
		cancel.setBackground(UI.getGeneralBackgroudColor(shell));

		GridData cancelGridData = new GridData(GridData.HORIZONTAL_ALIGN_END);
		cancel.setLayoutData(cancelGridData);
		
		
		OK.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				result = new DialogResult();
				result.searchText = searchPhrase.getText();
				
				result.isCaseSensitive = isCaseSens.getSelection();
				result.isCheckNodes = isHandleNodeNames.getSelection();
				result.isStartOver = isStartOver.getSelection();
				
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
		
		Composite podol = new Composite(shell, SWT.NONE);
		UI.prepareComposite(podol, 1, 0, 0, 5, 5);
		
		messageLabel = new Label(podol, SWT.BOLD);
		messageLabel.setBackground(UI.getGeneralBackgroudColor(shell));
		messageLabel.setText("Shortcut: you can use F4 to repeat the search");

		gd = new GridData(SWT.LEFT, SWT.CENTER, true, true);
		messageLabel.setLayoutData(gd);

		shell.setDefaultButton(OK);

		shell.pack();
	}
	
	public static class DialogResult {
		public static final Object CANCEL = null;
		
		public String searchText;
		
		public boolean isCaseSensitive;
		public boolean isCheckNodes;
		public boolean isStartOver;
	} 
	
	public DialogResult getResult() {
		return result;
	}
}
