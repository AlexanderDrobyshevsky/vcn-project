package ru.vermilion.vcn.app.listeners;

import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import ru.vermilion.vcn.app.VermilionCascadeNotebook;
import ru.vermilion.vcn.app.staff.Editor;
import ru.vermilion.vcn.app.staff.VCNTreeItem;

public class TreeSelectionListener implements Listener {

	private VermilionCascadeNotebook vermilionCascadeEditor;
	
	public TreeSelectionListener(VermilionCascadeNotebook vermilionCascadeEditor) {
		this.vermilionCascadeEditor = vermilionCascadeEditor;
	}
	
	public void handleEvent (Event event) {
		assert event.item != null;
		
		Editor editor = vermilionCascadeEditor.getEditor();
		
		VCNTreeItem item = editor.getTreeItem();
		if (!item.isDisposed()) {
			item.setContent(editor.getText());
		}
		
		String itemText = ((VCNTreeItem)event.item).getContent();
		if (itemText == null) {
			itemText = "";
		}
		
		editor.removeVerifyListener(vermilionCascadeEditor.getEditorVerifyListener());
		editor.setText(itemText);
		editor.addVerifyListener(vermilionCascadeEditor.getEditorVerifyListener());
		editor.setTreeItem((VCNTreeItem)event.item);
		
		vermilionCascadeEditor.setWrapEditor(((VCNTreeItem)event.item).isWrap());
	}
}
