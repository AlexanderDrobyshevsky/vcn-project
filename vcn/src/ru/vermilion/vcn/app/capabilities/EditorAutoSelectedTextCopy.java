package ru.vermilion.vcn.app.capabilities;

import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Text;

public class EditorAutoSelectedTextCopy implements ICapability {
	
	private Text editor;
	
	private Clipboard clipboard;
	
	private String lastClipboardText;
	
	private long lastClipboardTime;

	public EditorAutoSelectedTextCopy(Text text) {
		this.editor = text;
		clipboard = new Clipboard(editor.getDisplay());
		
		addTextCopySelectionListener();
	}
	
	private void addTextCopySelectionListener() {
		editor.addMouseListener(new org.eclipse.swt.events.MouseListener() {
			public void mouseDoubleClick(org.eclipse.swt.events.MouseEvent e) {
				System.out.println("#: mouseDoubleClick");
				copySelectedText();
			}

			public void mouseDown(org.eclipse.swt.events.MouseEvent e) {
				System.out.println("#: mouseDown");
				copySelectedText();
			}

			public void mouseUp(org.eclipse.swt.events.MouseEvent e) {
				System.out.println("#: mouseUp");
				copySelectedText();
			}
		});
		
		editor.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent e) {
				// keyPressedKeyEvent{Editor {} time=26882185 data=null 
				//character=''=0x16 keyCode=0x76 keyLocation=0x0 stateMask=0x40000 doit=true}
				if (e.character == 0x16 && e.keyCode == 0x76 && e.stateMask == 0x40000) {
					// Ctrl+V pressed;
					
					System.out.println("Ctrl + V pressed ");
					
					System.out.println("time = " + (System.currentTimeMillis() - lastClipboardTime));
					if (System.currentTimeMillis() - lastClipboardTime < 4000 && lastClipboardText != null) {
						System.out.println("#### : " + lastClipboardText);
					    TextTransfer textTransfer = TextTransfer.getInstance();
					    clipboard.setContents(new Object[] { lastClipboardText }, new Transfer[] { textTransfer });
					}
						
				}
			}

			public void keyReleased(KeyEvent e) {
				System.out.println("#: keyReleased");
				copySelectedText();
			}
		});
	}
	

	
	private void copySelectedText() {
		Point selection = editor.getSelection();
		System.out.println("copy-" + (System.currentTimeMillis() - lastClipboardTime));
		if (selection.x < selection.y && System.currentTimeMillis() - lastClipboardTime > 400) {
	        lastClipboardText = (String)clipboard.getContents(TextTransfer.getInstance());
	        lastClipboardTime = System.currentTimeMillis();

	        System.out.println("prev clipboard: " + lastClipboardText);
			
			editor.copy();
		}
	}
}
