package ru.vermilion.vcn.app.capabilities.search;

import org.eclipse.swt.graphics.Point;

import ru.vermilion.vcn.app.VermilionCascadeNotebook;
import ru.vermilion.vcn.app.capabilities.ICapability;
import ru.vermilion.vcn.app.dialogs.PageSearchDialog;

public class PageSearch implements ICapability {
	
	private VermilionCascadeNotebook vermilionCascadeNotebook;
	
	private int lastSearchingPosition = 0;
	
	private String searchingText = "Vermilion";
	
	private boolean isCaseSensitive = false;
	
	public PageSearch(VermilionCascadeNotebook vermilionCascadeNotebook) {
		this.vermilionCascadeNotebook = vermilionCascadeNotebook;
	}
	
	public void pageSearchAction() {
		PageSearchDialog pageSearchDialog = new PageSearchDialog(vermilionCascadeNotebook.getEditor().getShell());
		pageSearchDialog.open();
		PageSearchDialog.DialogResult dr = pageSearchDialog.getResult();
		
		if (dr != null) {
			searchingText = dr.searchText;
			isCaseSensitive = dr.isCaseSensitive;
			
			String editorText = vermilionCascadeNotebook.getEditor().getText();
			if (!dr.isStartOver) {
			   lastSearchingPosition = vermilionCascadeNotebook.getEditor().getCaretPosition();
			} else {
			   lastSearchingPosition = 0;
			}
			
			if (isCaseSensitive) {
			   lastSearchingPosition = editorText.indexOf(searchingText, lastSearchingPosition);
		    } else {
		       lastSearchingPosition = editorText.toLowerCase().indexOf(searchingText.toLowerCase(), lastSearchingPosition);
		    }
			
			statusLineStuff(editorText);
			
			vermilionCascadeNotebook.getEditor().setFocus();			
		}
	}

	private OccurrenceReport getTotalOccurencesOnPage(String editorText, 
			boolean isCaseSensitive, String searchingText, int currentSearchingPosition) {
		int lastSearchingPosition = -1;
		int cnt = 0;
		OccurrenceReport report = new OccurrenceReport();
		
		if (!isCaseSensitive) {
			editorText = editorText.toLowerCase();
			searchingText = searchingText.toLowerCase();
		}
		
		while ((lastSearchingPosition = editorText.indexOf(searchingText, 
				lastSearchingPosition + searchingText.length())) != -1 && cnt < 9999) {
			cnt++;
			
			if (lastSearchingPosition == currentSearchingPosition) {
				report.current = cnt;
			}
		}
		
		report.total = cnt;

		return report;
	}

	public void pageReSearchAction() {
		vermilionCascadeNotebook.getEditor().setFocus();
		String editorText = vermilionCascadeNotebook.getEditor().getText();
		lastSearchingPosition = vermilionCascadeNotebook.getEditor().getCaretPosition() - 1;
		
		if (isCaseSensitive) {
			lastSearchingPosition = editorText.indexOf(searchingText, lastSearchingPosition + 1);
		} else {
			lastSearchingPosition = editorText.toLowerCase().indexOf(searchingText.toLowerCase(), lastSearchingPosition + 1);
		}	
		
		statusLineStuff(editorText);
	}
	
	private void statusLineStuff(String editorText) {
		if (lastSearchingPosition >= 0) {
			vermilionCascadeNotebook.getEditor()
			   .setSelection(new Point(lastSearchingPosition, lastSearchingPosition + searchingText.length()));
			
			OccurrenceReport report = getTotalOccurencesOnPage(editorText, isCaseSensitive, searchingText, lastSearchingPosition);
			
			vermilionCascadeNotebook.setStatusLabel("Found " + report + " occurrence " + "of '" + searchingText + "'");
		} else {
			OccurrenceReport report = getTotalOccurencesOnPage(editorText, isCaseSensitive, searchingText, lastSearchingPosition);
			
			if (report.total == 0) {
			    vermilionCascadeNotebook.setStatusLabel("Not found occurrence of '" + searchingText + "'");
			} else {
				vermilionCascadeNotebook.setStatusLabel("Not found occurrence of '" + searchingText + "' after cursor, but ["
						+ report.total + "] occurrence(s) found on the page");	
			}
		}
	}
	
	
	private class OccurrenceReport {
		int current = -1;
		int total;

		public String toString() {
			return "[" + current + "/" + total + "]";
		}
	}
	

    public void setSearchParameters(String searchingText, int lastSearchingPosition, boolean isCaseSensitive) {
    	this.searchingText = searchingText;
    	this.lastSearchingPosition = lastSearchingPosition;
    	this.isCaseSensitive = isCaseSensitive;
    }

}
