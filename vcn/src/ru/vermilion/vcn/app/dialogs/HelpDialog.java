package ru.vermilion.vcn.app.dialogs;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;

import ru.vermilion.vcn.app.VermilionCascadeNotebook;
import ru.vermilion.vcn.auxiliar.MessageOKDialog;

public class HelpDialog extends MessageOKDialog {
	
	private static final String HELP = 
			"This program intended in order to arrange multiple \r\n" +
			"notes in hierarchical structure. It helps you to keep a lot of \r\n" +
			"notes in structured, easy accessible convenient way. In one place. \r\n\r\n" +
			
			"Available following features: \r\n" +
			"- Autosave every 10 seconds \r\n" +
			"  (You should not press CTRL+S every time you have changes) \r\n" +
			"- Auto copy selected text to clipboard just after selection \r\n" +
			"- You can move any nodes to any other nodes by mouse \r\n" +
			"  gragging (drag and drop)\r\n\r\n" +
	
	        "You can create and evolve your own, convenient for you hierarchical \r\n" +
	        "structure by popup menu on the left side bar, accessible by \r\n" +
	        "clicking right mouse button. You available to create new nodes, \r\n" +
	        "create new subnodes, delete nodes and rename nodes.\r\n\r\n" +

	         "Farther interesting possibilities will be presented in new \r\n" +
	         "program versions. \r\n" +
	         "Keep in touch.\r\n" +
	         "Have a good one.\r\n";
	
	public HelpDialog(Shell parent) {
		super(parent, SWT.NONE);
		
		title = VermilionCascadeNotebook.TITLE + " Program Help";
		
		message = HELP;
	}
	
	public void openHelpDialog() {
		
		super.open();
		
	}

}
