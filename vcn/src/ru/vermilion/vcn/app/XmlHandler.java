package ru.vermilion.vcn.app;

import java.io.File;
import java.io.FileOutputStream;
import java.io.Serializable;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

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
import ru.vermilion.vcn.auxiliar.VCNConstants;

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
		textElement.setAttribute(VCNConstants.CONTENT, (String) ((VCNTreeItem)treeItem).getContent());
		textElement.setAttribute(VCNConstants.WRAP, ((VCNTreeItem)treeItem).isWrap() + "");
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
			int editorFontSize = Integer.valueOf(root.getAttribute(VCNConstants.TREE_FONT_SIZE));
			VCNTreeItem.setFontSize(editorFontSize);
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

				if (nodeName.equals(VCNConstants.TEXT)) {
					String content = childElement.getAttribute(VCNConstants.CONTENT);
					String wrapAttr = childElement.getAttribute(VCNConstants.WRAP);
					
					boolean wrap = new Boolean(wrapAttr);
					if (wrapAttr == null || wrapAttr.trim().isEmpty()) {
						wrap = true;
					}
					
					parentItem.setContent(content);
					parentItem.setWrap(wrap);
				}
			}
		}
	}

	public void saveXml() {
		if (!vermilionCascadeNotebook.getModified()) {
			return;
		}

		File file = new File(VCNConstants.WORK_FILE_PATH);

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;

		try {
			builder = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			// TODO message window
			e.printStackTrace();
		}

		Document xml = builder.newDocument();

		vermilionCascadeNotebook.flushEditor();

		constructXml(xml);

		try {
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
			ex.printStackTrace();
		}

		vermilionCascadeNotebook.setInModified();
	}
}
