package ru.vermilion.vcn.app.listeners;

import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import ru.vermilion.vcn.app.VermilionCascadeNotebook;
import ru.vermilion.vcn.app.staff.Editor;
import ru.vermilion.vcn.app.staff.VCNTreeItem;

public class TreeSelectionListener implements Listener {

	private VermilionCascadeNotebook vermilionCascadeNotebook;
	
	public TreeSelectionListener(VermilionCascadeNotebook vermilionCascadeNotebook) {
		this.vermilionCascadeNotebook = vermilionCascadeNotebook;
	}
	
	// Self-made Events are possible. Just event.item may be set! 
	public void handleEvent(Event event) {
		assert event != null && event.item != null;
		System.out.println("triggered TreeSelectionListener.handleEvent. Event = " + event.item);
		
		Editor editor = vermilionCascadeNotebook.getEditor();
		
		VCNTreeItem item = editor.getTreeItem();
		System.out.println("editor item = " + item);
		if (!item.isDisposed()) {
			System.out.println("For item = '" + item + "' set content = '" + editor.getText() + "'");
			item.setContent(editor.getText());
		}
		
		String itemText = ((VCNTreeItem)event.item).getContent();
		
		editor.removeVerifyListener(vermilionCascadeNotebook.getEditorVerifyListener());
		editor.setText(itemText);
		editor.addVerifyListener(vermilionCascadeNotebook.getEditorVerifyListener());
		editor.setTreeItem((VCNTreeItem)event.item);
		
		vermilionCascadeNotebook.setWrapEditor(((VCNTreeItem)event.item).isWrap());

		vermilionCascadeNotebook.setTopLabel(((VCNTreeItem)event.item).getPath());
	}
}
