package ru.vermilion.vcn.app;

import java.io.File;
import java.io.FileOutputStream;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
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
 * @author Alexander Drobyshevsky
 */

public final class VermilionCascadeNotebook {

	private Editor editor = null;
	
	private Tree tree = null;
	
	private Composite mainComposite = null;
	
	public static final String TITLE = "Vermilion Cascade Notebook";
	
	public static final String TITLE_WITH_VERSION = "Vermilion Cascade Notebook, v. 1.4-SNAPSHOT";
	
	private static final String TITLE_MODIFIED = " * " + TITLE_WITH_VERSION;
	
	private boolean isModified = false;
	
	private FileOutputStream programLocker;
	
    private VerifyListener editorVerifyListener = new EditorVerifyListener(this);
    
    private XmlHandler xmlHandler = new XmlHandler(this);
    
    private ApplicationMenu appMenu;
    
	public Sash sash;
	
	private Label topLabel;
	
	private Label statusLabel;
	
	private static final VermilionCascadeNotebook vermilionCascadeNotebook = new VermilionCascadeNotebook();
	
		
	private VermilionCascadeNotebook() {
		
	}
	
	public static VermilionCascadeNotebook getInstance() {
		return vermilionCascadeNotebook;
	}
	
	void init() {
		Display display = new Display();
		Shell shell = new Shell (display);
		shell.setText(TITLE_WITH_VERSION);
		
		lockProgrammProcess(shell);
		
		shell.setImage(new Image(display, VermilionCascadeNotebook.class.getResourceAsStream("/images/logo.png")));
		
		GridLayout gl = new GridLayout(1, false);
		gl.horizontalSpacing = 0;
		gl.verticalSpacing = 1;
		gl.marginHeight = 0;
		gl.marginWidth = 1;

		shell.setLayout(gl);
		
		Composite upComposite = new Composite(shell, SWT.BORDER);
		gl = new GridLayout(1, false);
		gl.marginHeight = 0;
		gl.marginWidth = 2;
		upComposite.setLayout(gl);
		upComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		topLabel = new Label(upComposite, SWT.BOLD);
		topLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		topLabel.setText("/");
		
		Composite midComposite = new Composite(shell, SWT.NONE);
		midComposite.setBackground(midComposite.getDisplay().getSystemColor(SWT.COLOR_GREEN));
		midComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Composite botComposite = new Composite(shell, SWT.BORDER);
		gl = new GridLayout(1, false);
		gl.marginHeight = 0;
		gl.marginWidth = 4;
		botComposite.setLayout(gl);
		botComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		statusLabel = new Label(botComposite, SWT.BOLD);
		statusLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		statusLabel.setText("Status Line");
		
		
		this.mainComposite = midComposite;
		
		createContent();
		
		addCapability(new ProgramAutoSave(this));
		addCapability(new TreeDragAndDrop(this));
		
		upComposite.pack();
		botComposite.pack();
		
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

		tree = new Tree(mainComposite, SWT.BORDER);
		tree.setLinesVisible(VCNConfiguration.isTreeLines);
		editor = new Editor(mainComposite, SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
		
		xmlHandler.initXML();
		
		// addSelectionListeners(tree);
		
		ApplicationTreePopupMenu treePopupMenu = new ApplicationTreePopupMenu(this);
		treePopupMenu.addTreePopupMenu();
		
		addEditorListeners(editor);
	    
		createSashFormContent();
		xmlHandler.loadXmlToTree();
		
		addSelectionListeners(tree);
		
		addShellDisposeListener();
	}

	private void addEditorListeners(Editor editor) {
		addCapability(new EditorAutoSelectedTextCopy(editor));
		editor.addVerifyListener(editorVerifyListener);
	}

	private void createSashFormContent() {
		sash = new Sash (mainComposite, SWT.VERTICAL);
		
		final FormLayout formLayout = new FormLayout();
		mainComposite.setLayout(formLayout);
		
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
				Rectangle shellRect = mainComposite.getClientArea ();
				int right = shellRect.width - sashRect.width - limit;
				e.x = Math.max (Math.min (e.x, right), limit);
				if (e.x != sashRect.x)  {
					sashData.left = new FormAttachment (0, e.x);
					mainComposite.layout ();
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
			editor = new Editor(mainComposite, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL | SWT.WRAP);
		} else {
			editor = new Editor(mainComposite, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);
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
		
		
		this.getMainComposite().layout();
		
		if (updateMenu) {
			appMenu.setMenuWrapItem(wrap);
		}
	}
	
	// EDITOR
	public void decreaseEditorFontSize() {
		Editor.setFontSize(Editor.getFontSize() - 1);
		vermilionCascadeNotebook.getEditor().applyFontSize();
		vermilionCascadeNotebook.setStatusLabel("Font size has been decreased to " + Editor.getFontSize());
		setModified();
	}
	
	public void increaseEditorFontSize() {
		Editor.setFontSize(Editor.getFontSize() + 1);
		vermilionCascadeNotebook.getEditor().applyFontSize();
		vermilionCascadeNotebook.setStatusLabel("Font size has been increased to " + Editor.getFontSize());
		setModified();
	}
	
	// TREE
	public void decreaseTreeFontSize() {
		VCNTreeItem.setFontSize(VCNTreeItem.getFontSize() - 1);
		applyTreeFontSize();
		vermilionCascadeNotebook.setStatusLabel("Tree size has been decreased to " + VCNTreeItem.getFontSize());
		setModified();
	}

	public void increaseTreeFontSize() {
		VCNTreeItem.setFontSize(VCNTreeItem.getFontSize() + 1);
		applyTreeFontSize();
		vermilionCascadeNotebook.setStatusLabel("Tree size has been increased to " + VCNTreeItem.getFontSize());
		setModified();
	}
	
	boolean isOwnFont = false;
	public void applyTreeFontSize() {
		FontData[] fontData = tree.getFont().getFontData();
		fontData[0].setHeight(VCNTreeItem.getFontSize());
		if (isOwnFont) tree.getFont().dispose();
		tree.setFont(new Font(tree.getDisplay(), fontData[0]));
		isOwnFont = true;
        if (getEditor().getTreeItem() != null) {
            getEditor().getTreeItem().rebaseTextFont();
        }
		this.getMainComposite().layout();
	}

	public void setWrapEditor(boolean wrap) {
		setWrapEditor(wrap, true);
	}
	
	public void setModified() {
		System.out.println("set modified");
		isModified = true;
		mainComposite.getShell().setText(TITLE_MODIFIED);
	}
	
	// clears modified status
	public void setInModified() {
		isModified = false;
		mainComposite.getShell().setText(TITLE_WITH_VERSION);
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
	
	public Composite getMainComposite() {
		return mainComposite;
	}
	
	public ApplicationMenu getAppMenu() {
		return appMenu;
	}

	public void save() {
    	xmlHandler.saveXml();
    }
	
	public void flushEditor() {
		Editor editor = getEditor();
		
		VCNTreeItem item = editor.getTreeItem();
		if (item != null && !item.isDisposed()) {
			item.setContent(editor.getText());
		}
	}
	
	public void setTopLabel(String text) {
		topLabel.setText(text);
		topLabel.getParent().layout();
	}
	
	public void setStatusLabel(String text) {
		statusLabel.setText(text);
	}

	private void addShellDisposeListener() {
		mainComposite.addDisposeListener(new DisposeListener() {

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
	}
	
	public VerifyListener getEditorVerifyListener() {
		return editorVerifyListener;
	}
	
	public void addCapability(ICapability capability) {
		
	}
	
}
