package ru.vermilion.vcn.app.capabilities;

import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Text;

import ru.vermilion.vcn.app.dialogs.PageSearchDialog;

public class PageSearch implements ICapability {
	
	private Text editor;
	
	private int lastSearchingPosition = 0;
	
	private String searchingText = "";
	
	public PageSearch(Text text) {
		this.editor = text;
		
		addDocumetFindKeyListener();
	}
	
	private void addDocumetFindKeyListener() {
		editor.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent e) {
				// Ctrl+F pressed
				if (e.character == 0x6 && e.keyCode == 0x66 && e.stateMask == 0x40000) {
					PageSearchDialog documentSearchDialog = new PageSearchDialog(editor.getShell());
					documentSearchDialog.open();
					PageSearchDialog.DialogResult dr = documentSearchDialog.getResult();
					
					if (dr != null) {
						searchingText = dr.searchText;
						
						String editorText = editor.getText();
						int searchIndex = editorText.indexOf(searchingText);
						lastSearchingPosition = searchIndex;
						
						if (searchIndex > 0) {
							editor.setSelection(new Point(searchIndex, searchIndex + searchingText.length()));
						}
					}
					
				}
				
				// F3 - Pressed
				if (e.character == 0x00 && e.keyCode == 0x100000c && e.stateMask == 0x0) {
					int searchIndex = -1;
					if (lastSearchingPosition > 0) {
						String editorText = editor.getText();
						searchIndex = editorText.indexOf(searchingText, lastSearchingPosition + 1);
						lastSearchingPosition = searchIndex;
					}
					
					if (searchIndex > 0) {
						editor.setSelection(new Point(searchIndex, searchIndex + searchingText.length()));
					}
				}
			}

			public void keyReleased(KeyEvent e) { }
		});
		
	}

}
