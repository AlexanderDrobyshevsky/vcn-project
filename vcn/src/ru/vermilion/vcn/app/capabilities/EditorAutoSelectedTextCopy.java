package ru.vermilion.vcn.app.capabilities;

import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Text;

import ru.vermilion.vcn.app.VermilionCascadeNotebook;

public class EditorAutoSelectedTextCopy implements ICapability {
	
	private Text editor;
	
	private Clipboard clipboard;
	
	private String lastClipboardText;
	
	private String currentClipboardText;
	
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
					
					System.out.println("time delta = " + (System.currentTimeMillis() - lastClipboardTime));
					Point selection = editor.getSelection();
					String currentClipboard = (String)clipboard.getContents(TextTransfer.getInstance());
					if (System.currentTimeMillis() - lastClipboardTime < 4000 
							&& lastClipboardText != null && selection.x < selection.y
							// if not equals => external modification happened; => don't modify clipboard;
							&& currentClipboardText != null && currentClipboardText.equals(currentClipboard)) {
						System.out.println("clipboard modification from '" + (String)clipboard.getContents(TextTransfer.getInstance()) + "' -> '" + lastClipboardText + "'");
					    TextTransfer textTransfer = TextTransfer.getInstance();
					    clipboard.setContents(new Object[] { lastClipboardText }, new Transfer[] { textTransfer });
					} else {
						System.out.println("No clipboard modification");
					}
				}

				// KeyEvent{Editor {} time=42263837 data=null character=''=0x3 keyCode=0x63 keyLocation=0x0 stateMask=0x40000 doit=true}
				if (e.character == 0x3 && e.keyCode == 0x63 && e.stateMask == 0x40000) {
					// Ctrl+C pressed;
					
					lastClipboardTime = 0;
					
					System.out.println("Explicit Ctrl + C");
				}
				
				// KeyEvent{Editor {} time=42289141 data=null character=''=0x18 keyCode=0x78 keyLocation=0x0 stateMask=0x40000 doit=true}
				if (e.character == 0x18 && e.keyCode == 0x78 && e.stateMask == 0x40000) {
					// Ctrl+X pressed;
					
					lastClipboardTime = 0;
					
					System.out.println("Explicit Ctrl + X");
				}	
				
				// TODO move it to global key listener (todo global key listener)
				// Event e = KeyEvent{Editor {} time=172910384 data=null character='+'=0x2b keyCode=0x100002b keyLocation=0x2 stateMask=0x40000 doit=true}
				if (e.character == 0x2b && e.keyCode == 0x100002b && e.stateMask == 0x40000) {
					VermilionCascadeNotebook.getInstance().increaseEditorFontSize();
					
					System.out.println("Explicit Ctrl [+]");
				}
				
				//Event e = KeyEvent{Editor {} time=172933519 data=null character='-'=0x2d keyCode=0x100002d keyLocation=0x2 stateMask=0x40000 doit=true}
				if (e.character == 0x2d && e.keyCode == 0x100002d && e.stateMask == 0x40000) {
					VermilionCascadeNotebook.getInstance().decreaseEditorFontSize();
					
					System.out.println("Explicit Ctrl [-]");
				}
				
				

				// debug: 
				//System.out.println("Event e = " + e);
				
				// TODO Ctrl + Delete functionality
//				// KeyEvent{Editor {} time=43841569 data=null character=''=0x7f keyCode=0x7f keyLocation=0x0 stateMask=0x20000 doit=true}
//				if (e.character == 0x18 && e.keyCode == 0x78 && e.stateMask == 0x40000) {
//					// Ctrl+Delete pressed;
//					
//					lastClipboardTime = 0;
//					
//					KeyEvent keyEvent = new KeyEvent(new Event());
//					keyEvent.character = 0x18;
//					keyEvent.keyCode= 0x78;
//					keyEvent.stateMask = 0x40000;
//					
//					editor.notifyListeners(SWT.KeyDown, keyEvent);
//					
//					System.out.println("Explicit Ctrl + Delete Pressed");
//				}
			
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

	        //System.out.println("get clipboard before copy: '" + lastClipboardText + "'");
	        //System.out.println("save last clipboard time");
			
			editor.copy();
			
			currentClipboardText =  (String)clipboard.getContents(TextTransfer.getInstance());
			
			//System.out.println("get clipboard after copy: '" + currentClipboardText + "'");
		}
	}
}
