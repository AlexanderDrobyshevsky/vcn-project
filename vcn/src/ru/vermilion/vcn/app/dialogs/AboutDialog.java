package ru.vermilion.vcn.app.dialogs;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import ru.vermilion.vcn.app.VermilionCascadeNotebook;
import ru.vermilion.vcn.auxiliar.UI;

public class AboutDialog extends Dialog {
	private Object result;
	private Shell shell;
	private Image logoImage; 
	
	private static final String ABOUT_TITLE = "About";

	public AboutDialog(Shell parent, int style) {
		super(parent, style);
	}

	public AboutDialog(Shell parent) {
		this(parent, SWT.NONE);
	}

	public Object open() {
		createContents();
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
		shell.setText(ABOUT_TITLE);
		Color backgroundColor = shell.getDisplay().getSystemColor(SWT.COLOR_WHITE);
		shell.setBackground(backgroundColor);
		shell.addDisposeListener(new DisposeListener(){
			@Override
			public void widgetDisposed(DisposeEvent e) {
				if (logoImage != null && !logoImage.isDisposed()) {
					logoImage.dispose();
				}
			}
		});

		GridLayout gl = new GridLayout(2, false);
		gl.horizontalSpacing = 20;
		gl.verticalSpacing = 10;
		gl.marginHeight = 10;
		gl.marginWidth = 10;
		shell.setLayout(gl);

		Canvas logoClient = new Canvas(shell, SWT.NONE);
		logoClient.setBackground(backgroundColor);
		GridData logoClientGridData = new GridData(GridData.GRAB_VERTICAL);

		logoClient.setLayoutData(logoClientGridData);

		logoImage = new Image(shell.getDisplay(), VermilionCascadeNotebook.class.getResourceAsStream("/images/logo.png"));

		final Composite textClient = new Composite(shell, SWT.NONE);
		textClient.setBackground(backgroundColor);
		GridData gd = new GridData(GridData.BEGINNING);
		textClient.setLayoutData(gd);

		gl = new GridLayout(1, false);
		gl.horizontalSpacing = 0;
		gl.verticalSpacing = 0;
		gl.marginHeight = 0;
		gl.marginWidth = 0;
		textClient.setLayout(gl);

		styledText(backgroundColor, textClient, VermilionCascadeNotebook.TITLE_WITH_VERSION);
		styledText(backgroundColor, textClient, "Copyright ©2013 by Alexander Drobyshevsky");
		String email = "drobyshevsky@gmail.com";
		StyledText mailText = styledText(backgroundColor, textClient, "E-mail: " + email);
		styledText(backgroundColor, textClient, "Program is free of any restrictions");
		
		StyleRange styleRange = new StyleRange();
		styleRange.start = 8;
		styleRange.length = email.length();
		styleRange.fontStyle = SWT.BOLD;
		styleRange.foreground = shell.getDisplay().getSystemColor(SWT.COLOR_BLUE);
		mailText.setStyleRange(styleRange);
		
		logoClient.addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent e) {
				int height = textClient.computeSize(SWT.DEFAULT, SWT.DEFAULT).y;
				System.out.println("h:" + height);
				e.gc.drawImage(logoImage, 0, 0, 332, 248, 0, 0, height, height);
			}
		});
		
		int height = textClient.computeSize(SWT.DEFAULT, SWT.DEFAULT).y;
		logoClientGridData.widthHint = height;
		logoClientGridData.heightHint = height;

		Button OK = new Button(shell, SWT.PUSH);
		OK.setText("     OK     ");
		OK.setBackground(backgroundColor);
		
		GridData okGridData = new GridData(GridData.HORIZONTAL_ALIGN_CENTER);
		okGridData.horizontalSpan = 2;
		OK.setLayoutData(okGridData);
		OK.setFocus();
		OK.addSelectionListener (new SelectionAdapter () {
			public void widgetSelected (SelectionEvent e) {
				shell.close();
			}
		});

		shell.setDefaultButton(OK);
		
		shell.pack();
		
		UI.centerShell(getParent(), shell);
	}

	private StyledText styledText(Color backgroundColor, final Composite textClient, String text) {
		StyledText lineText = new StyledText(textClient, SWT.READ_ONLY);
		lineText.setText(text);
		lineText.setBackground(backgroundColor);
		
		StyleRange styleRange = new StyleRange();
		styleRange.start = 0;
		styleRange.length = lineText.getText().length();
		styleRange.fontStyle = SWT.BOLD;
		styleRange.foreground = shell.getDisplay().getSystemColor(SWT.COLOR_BLACK);
		lineText.setStyleRange(styleRange);
		
		return lineText;
	}
}