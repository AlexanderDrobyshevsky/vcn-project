package ru.vermilion.vcn.auxiliar;

import org.eclipse.swt.graphics.Rectangle;
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
}
