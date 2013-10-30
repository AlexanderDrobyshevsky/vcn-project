package ru.vermilion.vcn.app.capabilities;

import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Text;

public class EditorAutoSelectedTextCopy implements ICapability {
	
	private Text editor;

	public EditorAutoSelectedTextCopy(Text text) {
		this.editor = text;
		
		addTextCopySelectionListener();
	}
	
	private void addTextCopySelectionListener() {
		editor.addMouseListener(new org.eclipse.swt.events.MouseListener() {
			public void mouseDoubleClick(org.eclipse.swt.events.MouseEvent e) {
				copySelectedText();
			}

			public void mouseDown(org.eclipse.swt.events.MouseEvent e) {
				copySelectedText();
			}

			public void mouseUp(org.eclipse.swt.events.MouseEvent e) {
				copySelectedText();
			}
		});
		
		editor.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent e) {
			}

			public void keyReleased(KeyEvent e) {
				copySelectedText();
			}
		});
	}
	
	private void copySelectedText() {
		Point selection = editor.getSelection();
		if (selection.x < selection.y) {
			editor.copy();
		}
	}
}
