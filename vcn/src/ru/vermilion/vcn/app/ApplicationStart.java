package ru.vermilion.vcn.app;

import javax.swing.JOptionPane;

import org.eclipse.swt.widgets.Shell;

import ru.vermilion.vcn.auxiliar.GeneralUtils;
import ru.vermilion.vcn.auxiliar.UI;

public class ApplicationStart {

	public static void main(String [] args) {
		try {
			VermilionCascadeNotebook vermilionCascadeNotebook = VermilionCascadeNotebook.getInstance();
			vermilionCascadeNotebook.init();
	
			Shell vcnShell = vermilionCascadeNotebook.getMainComposite().getShell();
			vcnShell.open();
			while (!vcnShell.isDisposed()) {
				try {
					if (!vcnShell.getDisplay().readAndDispatch ()) vcnShell.getDisplay().sleep ();
				} catch (Exception ex) {
					if (!vcnShell.getShell().isDisposed()) {
						UI.messageDialog(VermilionCascadeNotebook.getInstance().getShell(), "Program fatal error (" + ex + ")", 
								"Program fatal error: " + ex +"\r\nError stack: \r\n\r\n" + GeneralUtils.getStackTrace(ex));
					} 
					
					ex.printStackTrace();
				}
			}
			
			if (!vcnShell.isDisposed()) {
				vcnShell.getDisplay().dispose();
			}
		} catch (Throwable te) {
			JOptionPane.showMessageDialog(null, te.getMessage() + "\r\n" + 
						"Program terminates", "Program Fault", JOptionPane.ERROR_MESSAGE);
		}
	}
}
