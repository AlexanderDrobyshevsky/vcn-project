package ru.vermilion.vcn.app.capabilities;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import ru.vermilion.vcn.app.VermilionCascadeNotebook;
import ru.vermilion.vcn.app.capabilities.search.GlobalSearch;
import ru.vermilion.vcn.app.capabilities.search.PageSearch;

public class ApplicationSearch implements ICapability {
	
	private GlobalSearch globalSearch;
	
	private PageSearch pageSearch;

	
	public ApplicationSearch(Menu submenu, VermilionCascadeNotebook vermilionCascadeNotebook) {
		globalSearch = new GlobalSearch(vermilionCascadeNotebook);
		pageSearch = new PageSearch(vermilionCascadeNotebook);
		
		addPageSearchMenuItems(submenu);
		addGlobalSearchMenuItems(submenu);
	}

	
	private void addPageSearchMenuItems(Menu submenu) {
		MenuItem pageSearchItem = new MenuItem (submenu, SWT.PUSH);
		pageSearchItem.addListener(SWT.Selection, new Listener () {
			public void handleEvent(Event e) {
				pageSearch.pageSearchAction();
			}
		});
		pageSearchItem.setText ("Page Find.. \tCtrl + F");
		pageSearchItem.setAccelerator(SWT.MOD1 | 'F');
		
		MenuItem pageReSearchItem = new MenuItem (submenu, SWT.PUSH);
		pageReSearchItem.addListener(SWT.Selection, new Listener () {
			public void handleEvent(Event e) {
				pageSearch.pageReSearchAction();
			}
		});
		pageReSearchItem.setText ("Page Find Next\tF3");
		pageReSearchItem.setAccelerator(SWT.F3);
	}
	
	private void addGlobalSearchMenuItems(Menu submenu) {
		MenuItem pageSearchItem = new MenuItem (submenu, SWT.PUSH);
		pageSearchItem.addListener (SWT.Selection, new Listener () {
			public void handleEvent(Event e) {
				if (globalSearch.globalSearchAction()) {
					pageSearch.setSearchParameters(globalSearch.getLastSearchText(), -1, globalSearch.isCaseSensitive());
					pageSearch.pageReSearchAction();
				}
			}
		});
		pageSearchItem.setText ("Global Find.. \tCtrl + H");
		pageSearchItem.setAccelerator(SWT.MOD1 | 'H');
		
		MenuItem pageReSearchItem = new MenuItem (submenu, SWT.PUSH);
		pageReSearchItem.addListener (SWT.Selection, new Listener () {
			public void handleEvent(Event e) {
				if (globalSearch.globalReSearchAction()) {
					pageSearch.setSearchParameters(globalSearch.getLastSearchText(), -1, globalSearch.isCaseSensitive());
					pageSearch.pageReSearchAction();
				}
			}
		});
		pageReSearchItem.setText ("Global Find Next\tF4");
		pageReSearchItem.setAccelerator(SWT.F4);
	}
	
}
