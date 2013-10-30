package ru.vermilion.vcn.app;

import java.io.File;
import java.io.FileOutputStream;

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

import ru.vermilion.vcn.app.staff.VCNTreeItem;
import ru.vermilion.vcn.auxiliar.GeneralUtils;
import ru.vermilion.vcn.auxiliar.VCNConstants;

public class XmlHandler {

	private VermilionCascadeNotebook vermilionCascadeEditor;

	public XmlHandler(VermilionCascadeNotebook vermilionCascadeEditor) {
		this.vermilionCascadeEditor = vermilionCascadeEditor;
	}

	private Document constructXml(Document xml) {
		Element rootElement = xml.createElement(VCNConstants.ROOT);
		rootElement.setAttribute(VCNConstants.XML_FORMAT_VERSION_ATTR_NAME, VCNConstants.XML_FORMAT_VERSION);
		xml.appendChild(rootElement);

		TreeItem[] treeItems = vermilionCascadeEditor.getTree().getItems();
		for (TreeItem treeItem : treeItems) {
			addNode(rootElement, treeItem, xml);
		}

		return xml;
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

	private Document getDocument() {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(false);
		DocumentBuilder builder;
		Document doc = null;

		try {
			builder = factory.newDocumentBuilder();

			doc = builder.parse(new File(VCNConstants.WORK_FILE_PATH));
			doc.setXmlStandalone(false);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return doc;
	}

	public void initXML() {
		vermilionCascadeEditor.initDataDir();

		File dataFile = new File(VCNConstants.WORK_FILE_PATH);

		boolean isExistDataFile = dataFile.isFile();

		if (!isExistDataFile) {
			GeneralUtils.clearTree(vermilionCascadeEditor.getTree());

			TreeItem iItem = new VCNTreeItem(vermilionCascadeEditor.getTree(), 0);
			iItem.setText(VCNConstants.ROOT);
			vermilionCascadeEditor.setModified();
			saveXml();
		}
	}

	public void loadXmlToTree() {
		Document xml = getDocument();

		System.out.println("Begin load xml");
		GeneralUtils.clearTree(vermilionCascadeEditor.getTree());

		if (xml != null) {
			Element root = xml.getDocumentElement();

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

						VCNTreeItem item = new VCNTreeItem(vermilionCascadeEditor.getTree(), 0);
						item.setText(nodeNameAttr);

						fillNode(childElement, item);

						item.setExpanded(isExpanded);
					}
				}
			}
		}

		vermilionCascadeEditor.getTree().select(vermilionCascadeEditor.getTree().getItem(0));
		vermilionCascadeEditor.getEditor()
			.setText((String) ((VCNTreeItem)vermilionCascadeEditor.getTree().getItem(0)).getContent());
		vermilionCascadeEditor.getEditor()
			.setTreeItem((VCNTreeItem)vermilionCascadeEditor.getTree().getItem(0));
		
		vermilionCascadeEditor.setWrapEditor(vermilionCascadeEditor.getEditor().getTreeItem().isWrap());

		vermilionCascadeEditor.setInModified();

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
		if (!vermilionCascadeEditor.getModified()) {
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

		VCNTreeItem item = vermilionCascadeEditor.getEditor().getTreeItem();
		if (item != null && !item.isDisposed()) {
			item.setContent(vermilionCascadeEditor.getEditor().getText());
		}

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

		vermilionCascadeEditor.setInModified();
	}
}
