package ru.vermilion.vcn.app.capabilities;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.TreeItem;

import ru.vermilion.vcn.app.VermilionCascadeNotebook;
import ru.vermilion.vcn.app.dialogs.GlobalSearchDialog;
import ru.vermilion.vcn.app.staff.VCNTreeItem;

public class GlobalSearch implements ICapability {

	private VermilionCascadeNotebook vermilionCascadeNotebook;
	
	private String lastSearchText;
	
	private TreeItem lastFoundItem;
	
	// TODO Не выделяет при поиске по нодам
	// TODO Названия в меню карявые
	// TODO При переходе по F4,  F3 некорректно потом работает: держит старую позицию, надо с нуля!
	// TODO Поудалять старые комментарии
	// TODO Внести в журнал пункты что еще не сделано по глобальному поиску: разбить по Issue.
	public GlobalSearch(Menu submenu, VermilionCascadeNotebook vermilionCascadeNotebook) {
		this.vermilionCascadeNotebook = vermilionCascadeNotebook;
		
		//addGlobalSearchKeyListener();
		addMenuItems(submenu);
	}
	
	private void addMenuItems(Menu submenu) {
		MenuItem pageSearchItem = new MenuItem (submenu, SWT.PUSH);
		pageSearchItem.addListener (SWT.Selection, new Listener () {
			public void handleEvent(Event e) {
				globalSearchAction();
			}
		});
		pageSearchItem.setText ("Global Find.. \tCtrl + H");
		pageSearchItem.setAccelerator(SWT.MOD1 | 'H');
		
		MenuItem pageReSearchItem = new MenuItem (submenu, SWT.PUSH);
		pageReSearchItem.addListener (SWT.Selection, new Listener () {
			public void handleEvent(Event e) {
				globalReSearchAction();
			}
		});
		pageReSearchItem.setText ("Global Find Next\tF4");
		pageReSearchItem.setAccelerator(SWT.F4);
	}
	
	private void globalSearchAction() {
		//System.out.println("#: Event" + e);
		// Ctrl+J pressed -- TODO -> Ctrl-H
		//if (e.character == 0xa && e.keyCode == 0x6a && e.stateMask == 0x40000) {
			GlobalSearchDialog documentSearchDialog = 
					new GlobalSearchDialog(vermilionCascadeNotebook.getTree().getShell());
			documentSearchDialog.open();
			GlobalSearchDialog.DialogResult dr = documentSearchDialog.getResult();
			
			if (dr != GlobalSearchDialog.DialogResult.CANCEL) {
				searchAction(null, dr.searchText);
			}
		//}
		
//		// F4 - Pressed
//		if (e.character == 0x00 && e.keyCode == 0x100000d && e.stateMask == 0x0 && lastSearchText != null) {
//			System.out.println("F4 - At Global Search");
//
//			searchAction(lastFoundItem, lastSearchText);
//		}
	}
	
	private void globalReSearchAction() {
		searchAction(lastFoundItem, lastSearchText);
	}

//	private void addGlobalSearchKeyListener() {
//		tree.addKeyListener(new KeyListener() {
//			public void keyPressed(KeyEvent e) {
//				
//				System.out.println("#: Event" + e);
//				// Ctrl+J pressed -- TODO -> Ctrl-H
//				if (e.character == 0xa && e.keyCode == 0x6a && e.stateMask == 0x40000) {
//					GlobalSearchDialog documentSearchDialog = new GlobalSearchDialog(tree.getShell());
//					documentSearchDialog.open();
//					GlobalSearchDialog.DialogResult dr = documentSearchDialog.getResult();
//					
//					searchAction(null, dr.searchText);
//				}
//				
//				// F4 - Pressed
//				if (e.character == 0x00 && e.keyCode == 0x100000d && e.stateMask == 0x0 && lastSearchText != null) {
//					System.out.println("F4 - At Global Search");
//
//					searchAction(lastFoundItem, lastSearchText);
//				}
//			}
//			
//
//
//			public void keyReleased(KeyEvent e) { }
//		});
//
//	}

	private void searchAction(TreeItem lastFoundItem, String searchText) {
		if (searchText == null || searchText.isEmpty()) {
			return;
		}
		
		TreeItem foundItem = search(lastFoundItem, searchText);
		
		if (foundItem != null) {
			vermilionCascadeNotebook.getTree().setSelection(foundItem);
			Event treeSelectionEvent = new Event();
			treeSelectionEvent.item = foundItem;
			vermilionCascadeNotebook.getTree().notifyListeners(SWT.Selection, treeSelectionEvent);
		}
		
		GlobalSearch.this.lastFoundItem = foundItem;
		lastSearchText = searchText;
	}
	
	private boolean lastItemAlreadyFound;
	private TreeItem search(TreeItem lastFoundItem, String searchString) {
		lastItemAlreadyFound = lastFoundItem == null;
		
		TreeItem[] treeItems = vermilionCascadeNotebook.getTree().getItems();
		for (TreeItem treeItem : treeItems) {
			TreeItem itemFound = realSearch(treeItem, searchString, lastFoundItem);
			
			if (itemFound != null) {
				return itemFound;
			}
		}	
		
		return null;
	}
	
	private TreeItem realSearch(TreeItem investigatingItem, String searchString, TreeItem lastFoundItem) {
		if (lastItemAlreadyFound) {
			String itemContent = (String) ((VCNTreeItem)investigatingItem).getContent();
			
			if (itemContent.indexOf(searchString) >= 0) {
				return investigatingItem;
			}
			
			TreeItem[] childrenItems = investigatingItem.getItems();
			for (TreeItem treeItem : childrenItems) {
				TreeItem itemFound = realSearch(treeItem, searchString, null);
				
				if (itemFound != null) {
					return itemFound;
				}
			}
		} else {
			if (lastFoundItem == investigatingItem) {
				lastItemAlreadyFound = true;
				
				TreeItem[] childrenItems = investigatingItem.getItems();
				for (TreeItem treeItem : childrenItems) {
					TreeItem itemFound = realSearch(treeItem, searchString, null);
					
					if (itemFound != null) {
						return itemFound;
					}					
				}
			} else {
				TreeItem[] childrenItems = investigatingItem.getItems();
				for (TreeItem treeItem : childrenItems) {
					TreeItem itemFound = realSearch(treeItem, searchString, lastFoundItem);
					
					if (itemFound != null) {
						return itemFound;
					}
				}				
			}
			
		}
		
	    return null;
	}
	

}
