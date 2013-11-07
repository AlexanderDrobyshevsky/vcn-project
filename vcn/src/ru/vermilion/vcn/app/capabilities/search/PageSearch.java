package ru.vermilion.vcn.app.capabilities.search;

import org.eclipse.swt.graphics.Point;

import ru.vermilion.vcn.app.VermilionCascadeNotebook;
import ru.vermilion.vcn.app.capabilities.ICapability;
import ru.vermilion.vcn.app.dialogs.PageSearchDialog;

public class PageSearch implements ICapability {
	
	private VermilionCascadeNotebook vermilionCascadeNotebook;
	
	private int lastSearchingPosition = 0;
	
	private String searchingText = "Vermilion";
	
	public PageSearch(VermilionCascadeNotebook vermilionCascadeNotebook) {
		this.vermilionCascadeNotebook = vermilionCascadeNotebook;
	}
	
	public void pageSearchAction() {
		PageSearchDialog pageSearchDialog = new PageSearchDialog(vermilionCascadeNotebook.getEditor().getShell());
		pageSearchDialog.open();
		PageSearchDialog.DialogResult dr = pageSearchDialog.getResult();
		
		if (dr != null) {
			searchingText = dr.searchText;
			
			String editorText = vermilionCascadeNotebook.getEditor().getText();
			lastSearchingPosition = vermilionCascadeNotebook.getEditor().getCaretPosition();
			lastSearchingPosition = editorText.indexOf(searchingText, lastSearchingPosition);
			
			if (lastSearchingPosition >= 0) {
				vermilionCascadeNotebook.getEditor()
				   .setSelection(new Point(lastSearchingPosition, lastSearchingPosition + searchingText.length()));
			}
			
			vermilionCascadeNotebook.getEditor().setFocus();
		}
	}
	
	public void pageReSearchAction() {
		vermilionCascadeNotebook.getEditor().setFocus();
		String editorText = vermilionCascadeNotebook.getEditor().getText();
		lastSearchingPosition = vermilionCascadeNotebook.getEditor().getCaretPosition() - 1;
		lastSearchingPosition = editorText.indexOf(searchingText, lastSearchingPosition + 1);
		
		if (lastSearchingPosition >= 0) {
			vermilionCascadeNotebook.getEditor()
			    .setSelection(new Point(lastSearchingPosition, lastSearchingPosition + searchingText.length()));
		}	
	}

    public void setSearchParameters(String searchingText, int lastSearchingPosition) {
    	this.searchingText = searchingText;
    	this.lastSearchingPosition = lastSearchingPosition;
    }

}
