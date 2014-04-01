package ru.vermilion.vcn.app.listeners;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TreeItem;

import ru.vermilion.vcn.app.VermilionCascadeNotebook;
import ru.vermilion.vcn.app.staff.Editor;
import ru.vermilion.vcn.app.staff.VCNTreeItem;

public class TreeSelectionListener implements Listener {

	private VermilionCascadeNotebook vermilionCascadeNotebook;
	
	public TreeSelectionListener(VermilionCascadeNotebook vermilionCascadeNotebook) {
		this.vermilionCascadeNotebook = vermilionCascadeNotebook;
		
		selectItem(vermilionCascadeNotebook.getEditor().getTreeItem());
	}
	
	// Self-made Events are possible. Just event.item may be set! 
	// event.item - new item (just selected); item - unselecting item;
	public void handleEvent(Event event) {
		assert event != null && event.item != null;
		System.out.println("triggered TreeSelectionListener.handleEvent.Event = " + event.item);
		
		Editor editor = vermilionCascadeNotebook.getEditor();
		
		VCNTreeItem item = editor.getTreeItem();
		System.out.println("editor item = " + item);
		unselectItem(item);
		if (!item.isDisposed()) {
			item.setContent(editor.getText());
		}
		
		String itemText = ((VCNTreeItem)event.item).getContent();
		
		editor.removeVerifyListener(vermilionCascadeNotebook.getEditorVerifyListener());
		editor.setText(itemText);
		editor.addVerifyListener(vermilionCascadeNotebook.getEditorVerifyListener());
		editor.setTreeItem((VCNTreeItem)event.item);
		
		vermilionCascadeNotebook.setWrapEditor(((VCNTreeItem)event.item).isWrap());

		vermilionCascadeNotebook.setTopLabel(((VCNTreeItem)event.item).getPath());
		
		selectItem((TreeItem)event.item);
	}
	
	private void unselectItem(TreeItem item) {
		System.out.println("Unselect item = " + item.getText());
		FontData[] fontData = item.getFont().getFontData();
		fontData[0].setStyle(SWT.NORMAL);
		item.setFont(new Font(item.getDisplay(), fontData[0]));		
	}
	
	private void selectItem(TreeItem item) {
		System.out.println("Select item = " + item.getText());
		FontData[] fontData = item.getFont().getFontData();
		fontData[0].setStyle(SWT.BOLD);
		item.setFont(new Font(item.getDisplay(), fontData[0]));
	}	
	
}
