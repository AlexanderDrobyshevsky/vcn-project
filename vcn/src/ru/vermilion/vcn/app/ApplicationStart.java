package ru.vermilion.vcn.app;

import org.eclipse.swt.widgets.Shell;

public class ApplicationStart {

	public static void main(String [] args) {
		VermilionCascadeNotebook vermilionCascadeNotebook = new VermilionCascadeNotebook();
		vermilionCascadeNotebook.init();

		Shell vcnShell = vermilionCascadeNotebook.getShell();
		vcnShell.open();
		while (!vcnShell.isDisposed()) {
			if (!vcnShell.getDisplay().readAndDispatch ()) vcnShell.getDisplay().sleep ();
		}
		
		if (!vcnShell.isDisposed()) {
			vcnShell.getDisplay().dispose();
		}
	}
}
