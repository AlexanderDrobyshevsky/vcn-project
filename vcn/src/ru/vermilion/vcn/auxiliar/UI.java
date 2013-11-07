package ru.vermilion.vcn.auxiliar;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;

public class UI {
	
	// Should be called after shellis prepared and packed!
	public static void centerShell(Shell parentShell, Shell centerShell) {
	    Rectangle bounds = parentShell.getBounds();
	    Rectangle rect = centerShell.getBounds();
	    
	    int x = bounds.x + (bounds.width - rect.width) / 2;
	    int y = bounds.y + (bounds.height - rect.height) / 2;
	    
	    centerShell.setLocation(x, y);
	}
	
	public static void centerShell(Shell shell) {
	    Monitor primary = shell.getDisplay().getPrimaryMonitor();
	    Rectangle bounds = primary.getBounds();
	    Rectangle rect = shell.getBounds();
	    
	    int x = bounds.x + (bounds.width - rect.width) / 2;
	    int y = bounds.y + (bounds.height - rect.height) / 2;
	    
	    shell.setLocation(x, y);
	}
	
	public static void prepareComposite(Composite composite, int glCells, int horizintalSpacing, int verticalSpacing,
			int marginHeight, int marginWidth) {
		composite.setBackground(composite.getDisplay().getSystemColor(SWT.COLOR_WHITE));

		GridLayout gl = new GridLayout(glCells, false);
		gl.horizontalSpacing = horizintalSpacing;
		gl.verticalSpacing = verticalSpacing;
		gl.marginHeight = marginHeight;
		gl.marginWidth = marginWidth;
		composite.setLayout(gl);
	}
	
	public static Color getGeneralBackgroudColor(Shell shell) {
		return shell.getDisplay().getSystemColor(SWT.COLOR_WHITE);
	}
}
