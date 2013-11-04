package ru.vermilion.vcn.app.capabilities;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import ru.vermilion.vcn.app.VermilionCascadeNotebook;
import ru.vermilion.vcn.app.dialogs.PageSearchDialog;

public class PageSearch implements ICapability {
	
	private VermilionCascadeNotebook vermilionCascadeNotebook;
	
	private int lastSearchingPosition = 0;
	
	private String searchingText = "";
	
	public PageSearch(Menu submenu, VermilionCascadeNotebook vermilionCascadeNotebook) {
		this.vermilionCascadeNotebook = vermilionCascadeNotebook;
		
		addMenuItems(submenu);
	}
	
	private void addMenuItems(Menu submenu) {
		MenuItem pageSearchItem = new MenuItem (submenu, SWT.PUSH);
		pageSearchItem.addListener (SWT.Selection, new Listener () {
			public void handleEvent(Event e) {
				pageSearchAction();
			}
		});
		pageSearchItem.setText ("Page Find.. \tCtrl + F");
		pageSearchItem.setAccelerator(SWT.MOD1 | 'F');
		
		MenuItem pageReSearchItem = new MenuItem (submenu, SWT.PUSH);
		pageReSearchItem.addListener (SWT.Selection, new Listener () {
			public void handleEvent(Event e) {
				pageReSearchAction();
			}
		});
		pageReSearchItem.setText ("Page Find Next\tF3");
		pageReSearchItem.setAccelerator(SWT.F3);
	}
	
	private void pageSearchAction() {
		PageSearchDialog pageSearchDialog = new PageSearchDialog(vermilionCascadeNotebook.getEditor().getShell());
		pageSearchDialog.open();
		PageSearchDialog.DialogResult dr = pageSearchDialog.getResult();
		
		if (dr != null) {
			searchingText = dr.searchText;
			
			String editorText = vermilionCascadeNotebook.getEditor().getText();
			int searchIndex = editorText.indexOf(searchingText);
			lastSearchingPosition = searchIndex;
			
			if (searchIndex >= 0) {
				vermilionCascadeNotebook.getEditor()
				   .setSelection(new Point(searchIndex, searchIndex + searchingText.length()));
			}
		}
	}
	
	private void pageReSearchAction() {
		int searchIndex = -1;
		if (lastSearchingPosition >= 0) {
			String editorText = vermilionCascadeNotebook.getEditor().getText();
			searchIndex = editorText.indexOf(searchingText, lastSearchingPosition + 1);
			lastSearchingPosition = searchIndex;
		}
		
		if (searchIndex >= 0) {
			vermilionCascadeNotebook.getEditor()
			    .setSelection(new Point(searchIndex, searchIndex + searchingText.length()));
		}		
	}
	
}
