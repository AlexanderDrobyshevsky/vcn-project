package ru.vermilion.vcn.app.elements;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;

import ru.vermilion.vcn.app.VermilionCascadeNotebook;
import ru.vermilion.vcn.app.dialogs.AboutDialog;
import ru.vermilion.vcn.app.dialogs.HelpDialog;

public class ApplicationMenu {

	private VermilionCascadeNotebook vermilionCascadeNotebook;
	private Shell shell;
	
	private MenuItem wrapItem;
	
	public ApplicationMenu(VermilionCascadeNotebook vermilionCascadeNotebook) {
		this.vermilionCascadeNotebook = vermilionCascadeNotebook;
		this.shell = vermilionCascadeNotebook.getShell();
	}
	
	public void createMenu() {
		Menu bar = new Menu (shell, SWT.BAR);
		shell.setMenuBar(bar);
		
		MenuItem fileItem = new MenuItem (bar, SWT.CASCADE);
		fileItem.setText ("&File");
		
		Menu submenu = new Menu (shell, SWT.DROP_DOWN);
		fileItem.setMenu(submenu);

		//menuItem = new MenuItem(menu, SWT.SEPARATOR);
		
		MenuItem saveMenuItem = new MenuItem(submenu, SWT.PUSH);
		saveMenuItem.setText("Save \tCtrl + S");
		saveMenuItem.setAccelerator(SWT.MOD1 | 's');

		saveMenuItem.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				vermilionCascadeNotebook.save();
			}
		});
		
//		MenuItem loadMenuItem = new MenuItem (submenu, SWT.PUSH);
//		loadMenuItem.setText ("Load \tCtrl + L");
//		loadMenuItem.setAccelerator (SWT.MOD1 | 'l');
//		
//		loadMenuItem.addListener (SWT.Selection, new Listener () {
//			public void handleEvent (Event e) {
//					loadXml();
//			}
//		});
		
		MenuItem exitMenuItem = new MenuItem (submenu, SWT.PUSH);
		exitMenuItem.setText ("E&xit");
		exitMenuItem.addListener (SWT.Selection, new Listener () {
			public void handleEvent (Event e) {
				vermilionCascadeNotebook.save();
				vermilionCascadeNotebook.unlockProgrammProcess();
				System.exit(0);
			}
		});
		
		
		MenuItem editorItem = new MenuItem (bar, SWT.CASCADE);
		editorItem.setText ("&Editor");
		
		submenu = new Menu (shell, SWT.DROP_DOWN);
		editorItem.setMenu(submenu);
		
		MenuItem item = new MenuItem (submenu, SWT.PUSH);
		item.addListener (SWT.Selection, new Listener () {
			public void handleEvent(Event e) {
				vermilionCascadeNotebook.getEditor().selectAll();
				Point selection = vermilionCascadeNotebook.getEditor().getSelection();
				if (selection.x < selection.y) {
					vermilionCascadeNotebook.getEditor().copy();
				}
			}
		});
		item.setText ("Select &All \tCtrl + A");
		item.setAccelerator (SWT.MOD1 | 'A');
		
		///////////////
		
		wrapItem = new MenuItem(submenu, SWT.CHECK);
		wrapItem.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				System.out.println("wrapItem Selection Event!!!");
				vermilionCascadeNotebook.setWrapEditor(wrapItem.getSelection(), false);
				vermilionCascadeNotebook.setModified();
			}
		});
		wrapItem.setText ("Word Wrap");
		//wrapItem.setAccelerator (SWT.MOD1 | 'W');
		
		MenuItem pageSerchItem = new MenuItem (submenu, SWT.PUSH);
		pageSerchItem.addListener (SWT.Selection, new Listener () {
			public void handleEvent(Event e) {

			}
		});
		pageSerchItem.setText ("Page Search \tCtrl + F");
		pageSerchItem.setAccelerator (SWT.MOD1 | 'F');
		
		///////////
		
		
		MenuItem helpItem = new MenuItem (bar, SWT.CASCADE);
		helpItem.setText ("&Help");
		
		submenu = new Menu (shell, SWT.DROP_DOWN);
		helpItem.setMenu(submenu);
		
		MenuItem aboutItem = new MenuItem (submenu, SWT.PUSH);
		aboutItem.addListener (SWT.Selection, new Listener () {
			public void handleEvent(Event e) {
				AboutDialog aboutDialog = new AboutDialog(vermilionCascadeNotebook.getShell());
				aboutDialog.open();
			}
		});
		aboutItem.setText ("About VCN");
		
		MenuItem helpSubsectionItem = new MenuItem (submenu, SWT.PUSH);
		helpSubsectionItem.addListener (SWT.Selection, new Listener () {
			public void handleEvent(Event e) {
				new HelpDialog(shell).openHelpDialog();
			}
		});
		helpSubsectionItem.setText ("VCN Help \tF1");
		//helpSubsectionItem.setAccelerator (SWT.MOD1 | 'H');
		helpSubsectionItem.setAccelerator (SWT.F1);
	}

	public void setMenuWrapItem(boolean wrap) {
		wrapItem.setSelection(wrap);
	}
	
	
}
