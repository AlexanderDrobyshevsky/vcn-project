package ru.vermilion.vcn.app.dialogs;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;

import ru.vermilion.vcn.app.VermilionCascadeNotebook;
import ru.vermilion.vcn.auxiliar.MessageOKDialog;

public class HelpDialog extends MessageOKDialog {
	
	private static final String HELP = 
			"This program has been intended in order to arrange multiple \r\n" +
			"notes in hierarchical structure. It helps you to keep a lot of \r\n" +
			"notes in structured, easy accessible, convenient way. At one place. \r\n\r\n" +
			
			"Next features are available: \r\n" +
			"- Autosave every 10 seconds \r\n" +
			"  (You have not to press CTRL+S every time you have changes) \r\n" +
			"- Auto copy selected text into clipboard just after selection \r\n" +
			"- You can easily move any node to any other node by mouse \r\n" +
			"  dragging (drag and drop)\r\n" +
			"- Use CTRL+H to search through a whole tree \r\n" + 
			"- Use CTRL+F to search on current page \r\n\r\n" +
	        "You can create and evolve your own, convenient for you, hierarchical \r\n" +
	        "structure by popup menu on the left side bar. It is accessible by \r\n" +
	        "clicking right mouse button on an item. You are available to create new nodes, \r\n" +
	        "new subnodes, delete nodes and rename ones. \r\n" +
	        "One more new option is possibility of choosing color and style of node\r\n" +
	        "depending on your own logic" +
	        
	        "\r\n\r\n" +

            "Further interesting possibilities will be presented in new \r\n" +
            "program versions. \r\n" +
            "Keep in touch.\r\n" +
            "Have a good day.\r\n";
	
	public HelpDialog(Shell parent) {
		super(parent, SWT.NONE);
		
		title = VermilionCascadeNotebook.TITLE + " Program Help";
		
		message = HELP;
	}
	
	public void openHelpDialog() {
		
		super.open();
		
	}

}
