package ru.vermilion.vcn.app;

import java.io.File;
import java.io.FileOutputStream;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Sash;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;

import ru.vermilion.vcn.app.capabilities.EditorAutoSelectedTextCopy;
import ru.vermilion.vcn.app.capabilities.ICapability;
import ru.vermilion.vcn.app.capabilities.ProgramAutoSave;
import ru.vermilion.vcn.app.capabilities.TreeDragAndDrop;
import ru.vermilion.vcn.app.elements.ApplicationMenu;
import ru.vermilion.vcn.app.elements.ApplicationTreePopupMenu;
import ru.vermilion.vcn.app.listeners.EditorVerifyListener;
import ru.vermilion.vcn.app.listeners.TreeSelectionListener;
import ru.vermilion.vcn.app.staff.Editor;
import ru.vermilion.vcn.app.staff.VCNTreeItem;
import ru.vermilion.vcn.auxiliar.MessageOKDialog;
import ru.vermilion.vcn.auxiliar.UI;
import ru.vermilion.vcn.auxiliar.VCNConstants;

/**
 * JIRA:
 * 
 * + 16. Message dialog for new program lock
 * + 17. Logo
 * + 18. Help menu with About Window
 * + 19. Help window with help
 * + 20. Global Refactoring
 * + 21. Nodes moving possibility
 * + 22. Dialog windows all adjustment, center main win, open on last.
 * + 23. Help
 * + 24. Word wrap for text
 * + 25. Cypher sign
 *   25! Increase/Decrease font size
 * + 26. Search in current document/page
 * + 28. Put version of xml to file for new versions
 * * 29! Search through whole editor
 *   30. Comb listeners with exceptions
 *   31. ~Copy qualified name
 * + 32. Feature: Clipboard has copied text-> Select in app text to replace with clipboad - but have selected text (in clipboard) instead of text your really want to put 
 *   33. Live Copy Of Nodes (One live node for many parents)
 *   34. Make auto backups
 *   35. Crypto possibilities with password access;
 *   36. Node sharing possibilities (through server)
 *   37! Document Search Monitor: like "3/16 Found" in the bottom of main window;
 *   38! Add to the help page search possibilities;
 *   39! Add case sensitive option to page search dialog; 
 *   40. Node import/export
 *   41. Page Search: CTRL-F - must activate page search at tree too;  
 *   42. Add to menu local search and global search (menu hot keys is general for program, but should be tested on other OS)
 *   43. Add possibility choose node color!!
 *   44. Add possibility choose node size!! 
 *   45. High Light Current Tree Item
 *   
 * 
 * Bugs:
 * 1. + Press cancel than creating new sub-node - save * appear
 * 2. + Empty node possible
 * 3. Opens and closes of node - are not saved
 * 4. [Fatal Error] data.xml:4:9333: Character reference "&#6" is an invalid XML character (handle correctly such exception!).
 * 
 * @author Alexander Drobyshevsky
 */

/**
 */
public final class VermilionCascadeNotebook {

	private Editor editor = null;
	
	private Tree tree = null;
	
	private Shell shell = null;
	
	public static final String TITLE = "Vermilion Cascade Notebook";
	
	public static final String TITLE_WITH_VERSION = "Vermilion Cascade Notebook, v. 1.1";
	
	private static final String TITLE_MODIFIED = " * " + TITLE_WITH_VERSION;
	
	private boolean isModified = false;
	
	private FileOutputStream programLocker;
	
    private VerifyListener editorVerifyListener = new EditorVerifyListener(this);
    
    private XmlHandler xmlHandler = new XmlHandler(this);
    
    private ApplicationMenu appMenu;
    
	public Sash sash;
	
	
	VermilionCascadeNotebook() {
		
	}
	
	void init() {
		Display display = new Display();
		shell = new Shell (display);
		shell.setText(TITLE_WITH_VERSION);
		
		lockProgrammProcess(shell);
		
		shell.setImage(new Image(display, VermilionCascadeNotebook.class.getResourceAsStream("/images/logo.png")));
		
		createContent();
		
		addCapability(new ProgramAutoSave(this));
		addCapability(new TreeDragAndDrop(this));
		
		shell.pack();
		shell.setSize (700, 500);
		
		UI.centerShell(shell);
	}

	private void lockProgrammProcess(Shell shell) {
		System.out.println("Try to get program lock");

		initDataDir();
		File locker = new File(VCNConstants.LOCK_FILE_PATH);
		
		locker.delete();
		
		boolean isAlreadyLocked = locker.isFile();
		
        if (isAlreadyLocked) {
    		MessageOKDialog md = new MessageOKDialog(shell, 
    				"VCN: Program already running",
    				"Program locked by other instance.\r\n" +
    				"Just one program instance can be executed from same directory.");
    		md.open(); 
    		
        	System.out.println("Program locked by other instance");
        	System.exit(0);
        }
        
        try {
			locker.createNewFile();
			
			programLocker = new FileOutputStream(locker);			
		} catch (Exception ex) {
    		MessageOKDialog md = new MessageOKDialog(shell, 
    				"VCN: Error loading program",
    				"Can't create required files. \r\n" +
    				"Failed to work with the file system.\r\n" +
    				"Can't get program lock.");
    		md.open();
    		
			System.out.println("Can not get program lock");
			ex.printStackTrace();
			System.exit(0);
		}
        
        System.out.println("Got program lock");
	}
	
	public void unlockProgrammProcess() {
		try {
			programLocker.close();
			
			new File(VCNConstants.LOCK_FILE_PATH).delete();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println("Program lock released");
	}
	
	public void initDataDir() {
		File dataDir = new File(VCNConstants.WORK_FOLDER);
		
		boolean dirAndExist = dataDir.isDirectory();
		
		if (!dirAndExist) {
			dataDir.mkdir();
		}
	}
	
	private void createContent() {
		appMenu = new ApplicationMenu(this);
		appMenu.createMenu();

		tree = new Tree(shell, SWT.BORDER);
		tree.setLinesVisible(true);
		//tree.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_DARK_GREEN));
		//tree.setForeground(shell.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		editor = new Editor(shell, SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
		
		xmlHandler.initXML();
		
		addSelectionListeners(tree);
		
		ApplicationTreePopupMenu treePopupMenu = new ApplicationTreePopupMenu(this);
		treePopupMenu.addTreePopupMenu();
		
		addEditorListeners(editor);
	    
		createSashFormContent();
		xmlHandler.loadXmlToTree();
		
		addShellDisposeListener();
	}

	private void addEditorListeners(Editor editor) {
		addCapability(new EditorAutoSelectedTextCopy(editor));
		//addCapability(new PageSearch(editor));
		editor.addVerifyListener(editorVerifyListener);
	}

	private void createSashFormContent() {
		sash = new Sash (shell, SWT.VERTICAL);
		
		final FormLayout formLayout = new FormLayout ();
		shell.setLayout (formLayout);
		
		FormData treeLayoutFormData = new FormData ();
		treeLayoutFormData.left = new FormAttachment (0, 0);
		treeLayoutFormData.right = new FormAttachment (sash, 0);
		treeLayoutFormData.top = new FormAttachment (0, 0);
		treeLayoutFormData.bottom = new FormAttachment (100, 0);
		tree.setLayoutData (treeLayoutFormData);

		final int limit = 50, percent = 30;
		final FormData sashData = new FormData ();
		sashData.left = new FormAttachment (percent, 0);
		sashData.top = new FormAttachment (0, 0);
		sashData.bottom = new FormAttachment (100, 0);
		sash.setLayoutData (sashData);
		sash.addListener (SWT.Selection, new Listener () {
			public void handleEvent (Event e) {
				Rectangle sashRect = sash.getBounds ();
				Rectangle shellRect = shell.getClientArea ();
				int right = shellRect.width - sashRect.width - limit;
				e.x = Math.max (Math.min (e.x, right), limit);
				if (e.x != sashRect.x)  {
					sashData.left = new FormAttachment (0, e.x);
					shell.layout ();
				}
			}
		});
		
		FormData editorLayoutFormData = new FormData();
		editorLayoutFormData.left = new FormAttachment (sash, 0);
		editorLayoutFormData.right = new FormAttachment (100, 0);
		editorLayoutFormData.top = new FormAttachment (0, 0);
		editorLayoutFormData.bottom = new FormAttachment (100, 0);
		editor.setLayoutData(editorLayoutFormData);
	}
	
	public void setWrapEditor(boolean wrap, boolean updateMenu) {
		Editor oldEditor = this.editor;
		
		Editor editor;
		if (wrap) {
			editor = new Editor(shell, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL | SWT.WRAP);
		} else {
			editor = new Editor(shell, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);
		}
		
		VCNTreeItem treeItem = oldEditor.getTreeItem();
		editor.setTreeItem(treeItem);
		editor.setText(oldEditor.getText());
		treeItem.setWrap(wrap);
		
		this.editor.dispose();
		this.setEditor(editor);
		
		addEditorListeners(editor);
		
		FormData editorLayoutFormData = new FormData();
		editorLayoutFormData.left = new FormAttachment (sash, 0);
		editorLayoutFormData.right = new FormAttachment (100, 0);
		editorLayoutFormData.top = new FormAttachment (0, 0);
		editorLayoutFormData.bottom = new FormAttachment (100, 0);
		editor.setLayoutData(editorLayoutFormData);
		
		
		this.getShell().layout();
		
		if (updateMenu) {
			appMenu.setMenuWrapItem(wrap);
		}
	}
	
	public void setWrapEditor(boolean wrap) {
		setWrapEditor(wrap, true);
	}
	
//	public void updateMenu() {
//		appMenu.setMenuWrapItem(editor.getTreeItem().isWrap());
//	}
	
	public void setModified() {
		System.out.println("set modified");
		isModified = true;
		shell.setText(TITLE_MODIFIED);
	}
	
	public void setInModified() {
		isModified = false;
		shell.setText(TITLE_WITH_VERSION);
	}
	
	public boolean getModified() {
		return isModified;
	}
	
	public Tree getTree() {
		return tree;
	}
	
	public Editor getEditor() {
		return editor;
	}
	
	public void setEditor(Editor newEditor) {
		editor = newEditor;
	}
	
	public Shell getShell() {
		return shell;
	}
	
//    public ApplicationMenu getAppMenu() {
//		return appMenu;
//	}

	public void save() {
    	xmlHandler.saveXml();
    }
	
	private void addShellDisposeListener() {
		shell.addDisposeListener(new DisposeListener() {

			@Override
			public void widgetDisposed(DisposeEvent e) {
				System.out.println("dispose!");
				xmlHandler.saveXml();
				
				unlockProgrammProcess();
			}
			
		});
	}
	
	private void addSelectionListeners(Tree tree) {
		tree.addListener(SWT.Selection, new TreeSelectionListener(this));
		//addCapability(new GlobalSearch(tree));
	}
	
	public VerifyListener getEditorVerifyListener() {
		return editorVerifyListener;
	}
	
	public void addCapability(ICapability capability) {
		
	}
	
}
