package ru.vermilion.vcn.app.capabilities.search;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.TreeItem;

import ru.vermilion.vcn.app.VermilionCascadeNotebook;
import ru.vermilion.vcn.app.capabilities.ICapability;
import ru.vermilion.vcn.app.dialogs.GlobalSearchDialog;
import ru.vermilion.vcn.app.staff.VCNTreeItem;

public class GlobalSearch implements ICapability {

	private VermilionCascadeNotebook vermilionCascadeNotebook;
	
	private String lastSearchText = "Vermilion";
	
	private VCNTreeItem lastFoundItem;
	
	private VCNTreeItem currentItem;
	
	boolean isCaseSensitive = false;
	
	boolean isCheckNodes = false;
	
	public GlobalSearch(VermilionCascadeNotebook vermilionCascadeNotebook) {
		this.vermilionCascadeNotebook = vermilionCascadeNotebook;
	}
	
	public boolean globalSearchAction() {
		GlobalSearchDialog documentSearchDialog = new GlobalSearchDialog(vermilionCascadeNotebook.getTree().getShell());
		documentSearchDialog.open();
		GlobalSearchDialog.DialogResult dr = documentSearchDialog.getResult();

		if (dr != GlobalSearchDialog.DialogResult.CANCEL) {
			System.out.println("# global search");
			lastFoundItem = null;
			lastSearchText = dr.searchText;
			isCaseSensitive = dr.isCaseSensitive;
			isCheckNodes = dr.isCheckNodes;
			
			TreeItem[] selection = vermilionCascadeNotebook.getTree().getSelection();
			if (!dr.isStartOver && selection.length == 1) {
				currentItem = (VCNTreeItem)selection[0];
			} else {
				currentItem = (VCNTreeItem)vermilionCascadeNotebook.getTree().getItem(0);
			}
			
			return searchAction();
		}
		
		return false;
	}
	
	// is found something
	public boolean globalReSearchAction() {
		TreeItem[] selection = vermilionCascadeNotebook.getTree().getSelection();
		if (selection.length == 1 && currentItem == selection[0]) {
			lastFoundItem = currentItem;
			
			return searchAction();
		} 
		
		if (selection.length == 1 && currentItem != selection[0]) {
			lastFoundItem = null;
			currentItem = (VCNTreeItem)selection[0];
			System.out.println("#debug currentItem = " + currentItem.getPath());
			
			return searchAction();
		}
		
		return false;
	}

	// is to be rechecked the page
	private boolean searchAction() {
		
		// impossible (otherwise -> logic error)
		assert(lastSearchText != null && !lastSearchText.isEmpty());
		
		if (lastSearchText == null || lastSearchText.isEmpty()) {
			return false;
		}
		
		TreeItem foundItem = search();
		
		GlobalSearch.this.lastFoundItem = ((VCNTreeItem)foundItem);
		currentItem = ((VCNTreeItem)foundItem);
		
		if (foundItem != null) {
			vermilionCascadeNotebook.getTree().setSelection(foundItem);
			Event treeSelectionEvent = new Event();
			treeSelectionEvent.item = foundItem;
			vermilionCascadeNotebook.getTree().notifyListeners(SWT.Selection, treeSelectionEvent);
		} else {
			return false;
		}
		
		// because if we search by node names it is useless for page search.
		return !isCheckNodes;
	}
	
	
	private List<TreeItem> foundTreeItems;
	private Integer assumeCurrentIndex;
	private Integer assumeLastFoundItemIndex;

	private TreeItem search() {
		
		foundTreeItems = new ArrayList<TreeItem>();
		assumeCurrentIndex= null;
		assumeLastFoundItemIndex = null;
		
		if (currentItem != null)
		System.out.println("# text.currentItem = '" + currentItem.getContent() + "'");
		
		assert (lastFoundItem != null || currentItem != null);
		
		// lastFoundItem = 0 => currentItem != 0;
		// lastFoundItem != 0 => ignore currentItem;
		
		vermilionCascadeNotebook.flushEditor();
		TreeItem[] treeItems = vermilionCascadeNotebook.getTree().getItems();
		for (TreeItem treeItem : treeItems) {
			realSearch(treeItem);
		}	
		
		 int total = foundTreeItems.size();
		 
		 System.out.println("# assumeLastFoundItemIndex = " + assumeLastFoundItemIndex + ";" +
		 		" idx = " + foundTreeItems.indexOf(lastFoundItem) + "; lastFoundItem = " + lastFoundItem );
		 
		 assert lastFoundItem == null || (assumeLastFoundItemIndex == null && foundTreeItems.indexOf(lastFoundItem) != -1 ||
				 assumeLastFoundItemIndex != null && foundTreeItems.indexOf(lastFoundItem) == -1);
		 
		// case 0;
		if (total == 0) {
			vermilionCascadeNotebook.setStatusLabel("Global Search: Not found occurrence of '" + lastSearchText + "'");
			
			return null;
		}
		
		System.out.println("# lastFoundItem = " + lastFoundItem + "; currentItem = " + currentItem);
		
		if (lastFoundItem != null) {
			if (assumeLastFoundItemIndex == null) {
				int lastFoundItemIndex = foundTreeItems.indexOf(lastFoundItem);

				if (lastFoundItemIndex + 1 >= total) {
					vermilionCascadeNotebook.setStatusLabel("Global Search: Not found occurrence of '" + lastSearchText + "' after current node, but ["
							+ total + "] node(s) found in all document");
					
					return null;
				} else {
					Report report = new Report(lastFoundItemIndex + 2, total);
					report.sendReport(lastSearchText);
					
					return foundTreeItems.get(lastFoundItemIndex + 1);
				}
				
			} else {
				if (assumeLastFoundItemIndex + 1 == total) {
					if (foundTreeItems.get(assumeLastFoundItemIndex) == lastFoundItem) {
						vermilionCascadeNotebook.setStatusLabel("Global Search: Not found occurrence of '" + lastSearchText + "' after current node, but ["
								+ total + "] node(s) found in all document");
						
						return null;
					} else {
						Report report = new Report(assumeLastFoundItemIndex + 1, total);
						report.sendReport(lastSearchText);
						
						return foundTreeItems.get(assumeLastFoundItemIndex);
					}
				} 
				
				if (assumeLastFoundItemIndex + 1 >= total) {
					vermilionCascadeNotebook.setStatusLabel("Global Search: Not found occurrence of '" + lastSearchText + "' in/after current node, but ["
							+ total + "] node(s) found in all document");
					
					return null;
				} else {
					Report report = new Report(assumeLastFoundItemIndex + 1, total);
					report.sendReport(lastSearchText);
					
					return foundTreeItems.get(assumeLastFoundItemIndex);
				}
			}
		}
		
		assert (currentItem != null);
		 System.out.println("# assumeCurrentIndex = " + assumeCurrentIndex + ";" +
			 		" idx = " + foundTreeItems.indexOf(currentItem) + "; currentItem = " + currentItem );
		 
		assert (assumeCurrentIndex == null && foundTreeItems.indexOf(currentItem) != -1 ||
				 assumeCurrentIndex != null && foundTreeItems.indexOf(currentItem) == -1);
		
		if (assumeCurrentIndex == null) {
			int currentIndex = foundTreeItems.indexOf(currentItem);
			
			Report report = new Report(currentIndex + 1, total);
			report.sendReport(lastSearchText);
			
			return currentItem;
		} else {
			if (assumeCurrentIndex + 1 == total) {
				if (foundTreeItems.get(assumeCurrentIndex) == currentItem) {
					vermilionCascadeNotebook.setStatusLabel("Global Search: Not found occurrence of '" + lastSearchText + "' after current node, but ["
							+ total + "] node(s) found in all document");
					
					return null;
				} else {
					Report report = new Report(assumeCurrentIndex + 1, total);
					report.sendReport(lastSearchText);
					
					return foundTreeItems.get(assumeCurrentIndex);
				}
			}
			
			if (assumeCurrentIndex + 1 >= total) {
				vermilionCascadeNotebook.setStatusLabel("Global Search: Not found occurrence of '" + lastSearchText + "' in/after current node, but ["
						+ total + "] node(s) found in all document");
				
				return null;
			} else {
				Report report = new Report(assumeCurrentIndex + 1, total);
				report.sendReport(lastSearchText);
				
				return foundTreeItems.get(assumeCurrentIndex);
			}
		}
	}
	
	class Report {
		int position;
		int total;
		
		public Report(int position, int total) {
			this.position = position;
			this.total = total;
		}

		@Override
		public String toString() {
			return "[" + position + "/" + total + "]";
		}
		
		public void sendReport(String searchText) {
			vermilionCascadeNotebook.setStatusLabel("Global Search: Found " + toString() + " occurrence "
				 + "of '" + searchText + "'");
		}
	}
	
	private void realSearch(TreeItem investigatingItem) {
		String itemContent = (String) ((VCNTreeItem)investigatingItem).getContent();
		String itemName = investigatingItem.getText();
		
	    String checkingString;
	    if (isCheckNodes) {
	    	checkingString = itemName; 
	    } else {
	    	checkingString = itemContent;
	    }
		
		if (isCaseSensitive && checkingString.indexOf(lastSearchText) >= 0 ||
				!isCaseSensitive && checkingString.toLowerCase().indexOf(lastSearchText.toLowerCase()) >= 0) {
			
			foundTreeItems.add(investigatingItem);
		}
		
		if (lastFoundItem == investigatingItem) {
			if (!foundTreeItems.contains(lastFoundItem)) {
		    	assumeLastFoundItemIndex = foundTreeItems.size();
		    }
		}
		
		if (currentItem == investigatingItem) {
			if (!foundTreeItems.contains(currentItem)) {
		    	assumeCurrentIndex = foundTreeItems.size();
		    }
		}
		
		TreeItem[] childrenItems = investigatingItem.getItems();
		for (TreeItem treeItem : childrenItems) {
			realSearch(treeItem);
		}
	}
	


	public String getLastSearchText() {
		return lastSearchText;
	}

	public boolean isCaseSensitive() {
		return isCaseSensitive;
	}
	
	

}
