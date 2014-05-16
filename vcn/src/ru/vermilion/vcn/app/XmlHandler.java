package ru.vermilion.vcn.app;

import java.io.File;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.util.Arrays;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TreeItem;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import ru.vermilion.vcn.app.staff.Editor;
import ru.vermilion.vcn.app.staff.VCNTreeItem;
import ru.vermilion.vcn.auxiliar.GeneralUtils;
import ru.vermilion.vcn.auxiliar.UI;
import ru.vermilion.vcn.auxiliar.VCNConstants;

/**
 * Try to get program lock
Got program lock
Editor default font size detected = 9
Tree default font size detected = 9
set modified
Exception in thread "main" java.lang.NullPointerException
        at ru.vermilion.vcn.app.XmlHandler.constructXml(XmlHandler.java:48)
        at ru.vermilion.vcn.app.XmlHandler.saveXml(XmlHandler.java:334)
        at ru.vermilion.vcn.app.XmlHandler.initXML(XmlHandler.java:148)
        at ru.vermilion.vcn.app.VermilionCascadeNotebook.createContent(Vermilion
CascadeNotebook.java:221)
        at ru.vermilion.vcn.app.VermilionCascadeNotebook.init(VermilionCascadeNo
tebook.java:135)
        at ru.vermilion.vcn.app.ApplicationStart.main(ApplicationStart.java:9)

 *
 */
public class XmlHandler {

	private VermilionCascadeNotebook vermilionCascadeNotebook;

	public XmlHandler(VermilionCascadeNotebook vermilionCascadeNotebook) {
		this.vermilionCascadeNotebook = vermilionCascadeNotebook;
	}

	private Document constructXml(Document xml) {
		Element rootElement = xml.createElement(VCNConstants.ROOT);
		rootElement.setAttribute(VCNConstants.XML_FORMAT_VERSION_ATTR_NAME, VCNConstants.XML_FORMAT_VERSION);
		addOption(rootElement, VCNConstants.TREE_FONT_SIZE, VCNTreeItem.getFontSize());
		addOption(rootElement, VCNConstants.EDITOR_FONT_SIZE, Editor.getFontSize());
		addOption(rootElement, VCNConstants.TREE_LINES, VCNConfiguration.isTreeLines);
		Color sc = VCNConfiguration.getGradientSelectionColor();
		// see bug
		if (sc != null) {
			addOption(rootElement, VCNConstants.TREE_SELECTION_COLOR, sc.getRed() + "-" + sc.getGreen() + "-" + sc.getBlue());
		}
		
		xml.appendChild(rootElement);

		TreeItem[] treeItems = vermilionCascadeNotebook.getTree().getItems();
		for (TreeItem treeItem : treeItems) {
			addNode(rootElement, treeItem, xml);
		}

		return xml;
	}
	
	private void addOption(Element rootElement, String attrName, Serializable attrValue) {
		if (attrValue != null) {
			rootElement.setAttribute(attrName, attrValue.toString());
		}
	}

	private void addNode(Element rootElement, TreeItem treeItem, Document xml) {
		Element childElement = xml.createElement(VCNConstants.NODE);
		childElement.setAttribute(VCNConstants.NAME, treeItem.getText());
		childElement.setAttribute(VCNConstants.EXPANDED, treeItem.getExpanded() + "");

		Element textElement = xml.createElement(VCNConstants.TEXT);
		VCNTreeItem item = (VCNTreeItem)treeItem;
		textElement.setAttribute(VCNConstants.CONTENT, (String) ((VCNTreeItem)treeItem).getContent());
		textElement.setAttribute(VCNConstants.WRAP, ((VCNTreeItem)treeItem).isWrap() + "");
		textElement.setAttribute(VCNConstants.IS_BOLD_ITEM_NAME, ((VCNTreeItem)treeItem).isBold() + "");
		
		if (item.isOwnForeground()) {
			Color color = item.getForeground();
			textElement.setAttribute(VCNConstants.ITEM_NAME_COLOR, color.getRed() + "-" + color.getGreen() + "-" + color.getBlue());
		}
		childElement.appendChild(textElement);

		rootElement.appendChild(childElement);

		TreeItem[] tis = treeItem.getItems();
		if (tis == null) {
			return;
		}

		for (TreeItem ti : tis) {
			addNode(childElement, ti, xml);
		}
	}
	
	private ErrorHandler errorHandler = new ErrorHandler() {

		@Override
		public void warning(SAXParseException exception) throws SAXException {
			System.out.println("# warning " + exception.getMessage());
		}
		
		@Override
		public void error(SAXParseException exception) throws SAXException {
			System.out.println("# error " + exception.getMessage());
		}
		
		@Override
		public void fatalError(SAXParseException exception) throws SAXException {
			System.out.println("# fatalError " + exception.getMessage());
		}
	};

	private Document getDocument() {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(false);
		DocumentBuilder builder;
		Document doc = null;

		try {
			factory.setFeature("http://apache.org/xml/features/continue-after-fatal-error", true);
			builder = factory.newDocumentBuilder();
			builder.setErrorHandler(errorHandler);

			doc = builder.parse(new File(VCNConstants.WORK_FILE_PATH));
			doc.setXmlStandalone(false);
		} catch (SAXParseException ex) {
			ex.printStackTrace();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return doc;
	}
	
	public void initXML() {
		vermilionCascadeNotebook.initDataDir();

		File dataFile = new File(VCNConstants.WORK_FILE_PATH);

		boolean isExistDataFile = dataFile.isFile();

		if (!isExistDataFile) {
			GeneralUtils.clearTree(vermilionCascadeNotebook.getTree());

			TreeItem iItem = new VCNTreeItem(vermilionCascadeNotebook.getTree(), 0);
			iItem.setText(VCNConstants.ROOT);
			vermilionCascadeNotebook.setModified();
			saveXml();
		}
	}
	
	private void handleDocumentOptions(Element root) {
		try {
			int editorFontSize = Integer.valueOf(root.getAttribute(VCNConstants.EDITOR_FONT_SIZE));
			Editor.setFontSize(editorFontSize);
		} catch (Exception ex) {
            ex.printStackTrace();			
		}
		
		try {
			int treeFontSize = Integer.valueOf(root.getAttribute(VCNConstants.TREE_FONT_SIZE));
			VCNTreeItem.setFontSize(treeFontSize);
			VermilionCascadeNotebook.getInstance().applyTreeFontSize();
		} catch (Exception ex) {
            ex.printStackTrace();			
		}		
		
		try {
			boolean isTreeLines = Boolean.valueOf(root.getAttribute(VCNConstants.TREE_LINES));
            VCNConfiguration.isTreeLines = isTreeLines;
            System.out.println("-- config VCNConfiguration.isTreeLines = " + VCNConfiguration.isTreeLines);
            VermilionCascadeNotebook.getInstance().getTree().setLinesVisible(VCNConfiguration.isTreeLines);
            VermilionCascadeNotebook.getInstance().getAppMenu().setTreeLineItemSelection(VCNConfiguration.isTreeLines);
		} catch (Exception ex) {
            ex.printStackTrace();			
		}
		
		String selectionColorStr = root.getAttribute(VCNConstants.TREE_SELECTION_COLOR);
		Color color = getColor(selectionColorStr, VermilionCascadeNotebook.getInstance().getMainComposite().getDisplay());
		if (color != null) {
			VCNConfiguration.setGradientSelectionColor(color);
		}
	}

	public void loadXmlToTree() {
		Document xml = getDocument();

		System.out.println("Begin load xml = " + xml);
		GeneralUtils.clearTree(vermilionCascadeNotebook.getTree());

		if (xml != null) {
			Element root = xml.getDocumentElement();
			handleDocumentOptions(root);

			NodeList children = root.getChildNodes();

			for (int i = 0; i < children.getLength(); i++) {
				Node child = children.item(i);

				if (child instanceof Element) {
					Element childElement = (Element) child;
					String nodeName = childElement.getNodeName();

					if (nodeName.equals(VCNConstants.NODE)) {
						// recursion
						String nodeNameAttr = childElement.getAttribute(VCNConstants.NAME);
						boolean isExpanded = new Boolean(childElement.getAttribute(VCNConstants.EXPANDED));

						VCNTreeItem item = new VCNTreeItem(vermilionCascadeNotebook.getTree(), 0);
						item.setText(nodeNameAttr);

						fillNode(childElement, item);

						item.setExpanded(isExpanded);
					}
				}
			}
		} else {
			// TODO Show Error Message;
			System.out.println("No xml data");
			System.exit(1);
		}

		vermilionCascadeNotebook.getTree().select(vermilionCascadeNotebook.getTree().getItem(0));
		vermilionCascadeNotebook.getEditor()
			.setText((String) ((VCNTreeItem)vermilionCascadeNotebook.getTree().getItem(0)).getContent());
		vermilionCascadeNotebook.getEditor()
			.setTreeItem((VCNTreeItem)vermilionCascadeNotebook.getTree().getItem(0));
		
		vermilionCascadeNotebook.setWrapEditor(vermilionCascadeNotebook.getEditor().getTreeItem().isWrap());

		vermilionCascadeNotebook.setInModified();
		
		vermilionCascadeNotebook.setTopLabel(vermilionCascadeNotebook.getEditor().getTreeItem().getPath());

		System.out.println("Load finished");
	}

	private void fillNode(Element parentElement, VCNTreeItem parentItem) {
		NodeList children = parentElement.getChildNodes();

		for (int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);

			if (child instanceof Element) {
				Element childElement = (Element) child;
				String nodeName = childElement.getNodeName();

				if (nodeName.equals(VCNConstants.NODE)) {
					// recursion
					String nodeNameAttr = childElement.getAttribute(VCNConstants.NAME);
					boolean isExpanded = new Boolean(
							childElement.getAttribute(VCNConstants.EXPANDED));

					VCNTreeItem item = new VCNTreeItem(parentItem, 0);
					item.setText(nodeNameAttr);

					fillNode(childElement, item);

					item.setExpanded(isExpanded);
				}

				// no attr => empty string (not null)
				if (nodeName.equals(VCNConstants.TEXT)) {
					String content = childElement.getAttribute(VCNConstants.CONTENT);
					String wrapAttr = childElement.getAttribute(VCNConstants.WRAP);
					String isBoldStr = childElement.getAttribute(VCNConstants.IS_BOLD_ITEM_NAME);
					String nodeColorStr = childElement.getAttribute(VCNConstants.ITEM_NAME_COLOR);
					System.out.println("isBoldStr = '" + isBoldStr + "' => " + getBoolean(isBoldStr));
					
					boolean wrap = new Boolean(wrapAttr);
					if (wrapAttr == null || wrapAttr.trim().isEmpty()) {
						wrap = true;
					}
					
					parentItem.setContent(content);
					parentItem.setWrap(wrap);
					
					if (getBoolean(isBoldStr) != null) {
						parentItem.setBold(getBoolean(isBoldStr));
					}
					
					Color color = getColor(nodeColorStr, parentItem.getDisplay());
					if (color != null) {
						parentItem.setForeground(color);
					}
				}
			}
		}
	}
	
	private Boolean getBoolean(String value) {
		if (value == null || value.trim().isEmpty()) {
			return null;
		}
		
		return new Boolean(value);
	}
	
	private Color getColor(String value, Display display) {
		if (value == null || value.trim().isEmpty()) {
			return null;
		}
		
		String rgb[] = value.split("-");
		if (rgb.length != 3) {
			return null;
		}
		
		return new Color(display, new RGB(Integer.valueOf(rgb[0]), Integer.valueOf(rgb[1]), Integer.valueOf(rgb[2])));
	}

	// Can be invoked to create initial data.xml
	public void saveXml() {
		if (!vermilionCascadeNotebook.getModified()) {
			return;
		}

		File file = new File(VCNConstants.WORK_FILE_PATH);

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;

		try {
			builder = factory.newDocumentBuilder();

			Document xml = builder.newDocument();

			vermilionCascadeNotebook.flushEditor();

			constructXml(xml);
			
			System.out.println("Write data.xml..");
			Transformer t = TransformerFactory.newInstance().newTransformer();

			// t.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, "");
			// t.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, "");

			t.setOutputProperty(OutputKeys.INDENT, "yes");
			t.setOutputProperty(OutputKeys.METHOD, "xml");

			t.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

			FileOutputStream fos = new FileOutputStream(file);
			StreamResult sr = new StreamResult(fos);
			t.transform(new DOMSource(xml), sr);

			fos.flush();
			fos.close();

			System.out.println("Write data.xml.");
		} catch (Exception ex) {
			VermilionCascadeNotebook.getInstance().setInModified();
			UI.messageDialog(VermilionCascadeNotebook.getInstance().getShell(), "Error writing user data '" + ex + 
					"'\r\nRecent modifictions were not saved\r\n", 
					"Writing user data process has caused an error: " + ex +"\r\nError stack: \r\n\r\n" + GeneralUtils.getStackTrace(ex));
			
			ex.printStackTrace();
		}

		VermilionCascadeNotebook.getInstance().setInModified();
	}
}
