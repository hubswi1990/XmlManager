package pl.hubswi90.xmlManager;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XmlManager {

	private JFrame jframeXmlManager;
	private JTable table;
	private TableRowSorter<TableModel> rowSorter;
	File xmlFile;
	DocumentBuilder documentBuilder;
	DocumentBuilderFactory documentbuilderFactory;
	Document document;
	DefaultTableModel tableModel;
	private JButton btnReadXmlFile;
	private JButton btnAddNewLine;
	private JButton btnRemoveRow;
	private JButton btnDTDValidate;
	private JTextField searchField;
	
	@SuppressWarnings("rawtypes")
	private JComboBox searchColumn;
	
	private JLabel jlabelRoot;
	private JLabel jlabelElementLabel;
	private JLabel jlebelElement;
	private JLabel jlebelSearch;
	
	private static final int FRAME_WIDTH = 838;
	private static final int FRAME_HEIGHT = 490;
	private static final String PATH_TO_WORKING_DIRECTORY = "D:\\Workspace\\eclipseJavaSE\\XmlManager";

	File fileChooser(String windowTitle, String fileExtension) {
		JFileChooser fileChooser = new JFileChooser(PATH_TO_WORKING_DIRECTORY);
		fileChooser.setDialogTitle(windowTitle);
		fileChooser.showOpenDialog(jframeXmlManager.getContentPane());

		fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Only " + fileExtension.toUpperCase() + " file", fileExtension));
		fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Only XSD file", "xsd"));

		File file = fileChooser.getSelectedFile();
		return file;
	}
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					XmlManager window = new XmlManager();
					window.jframeXmlManager.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public XmlManager() {
		initialize();
	}

	@SuppressWarnings("rawtypes")
	private void initialize() {
		jframeXmlManager = new JFrame();
		jframeXmlManager.setTitle("XML Manager");
		jframeXmlManager.setBounds(centeringFrameOnWidth(FRAME_WIDTH), centeringFrameOnHeight(FRAME_HEIGHT), 838, 490);
		jframeXmlManager.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 11, 802, 238);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);

		btnReadXmlFile = new JButton("Read XML file");
		btnReadXmlFile.setBounds(10, 329, 165, 23);
		btnReadXmlFile.addActionListener(new ActionListener() {
			@SuppressWarnings("unchecked")
			public void actionPerformed(ActionEvent e) {
				xmlFile = fileChooser("Select XML file", "xml");
				if (xmlFile == null) return;
				
				tableModel = new DefaultTableModel();
				List<String> column = new ArrayList<>();

				documentbuilderFactory = DocumentBuilderFactory.newInstance();
				try {
					documentBuilder = documentbuilderFactory.newDocumentBuilder();
					document = documentBuilder.parse(xmlFile);
					document.getDocumentElement().normalize();
					Element root = document.getDocumentElement();
					NodeList nodeList = root.getChildNodes();
					
					jlabelRoot.setText(root.getNodeName());
					
					boolean elementIsSet = false;
					
					for (int i = 1; i < nodeList.getLength(); i++) {
						if (nodeList.item(i).getNodeType() != Node.ELEMENT_NODE) continue;
						List<String> values = new ArrayList<>();
						
						if (!elementIsSet) {
							elementIsSet = true;
							jlebelElement.setText(nodeList.item(i).getNodeName());
						}

						if (nodeList.item(i).hasAttributes()) {
							NamedNodeMap atrib = nodeList.item(i).getAttributes();
							if (atrib != null) {
								for (int j = 0; j < atrib.getLength(); j++) {
									String columnName = nodeList.item(i).getNodeName() + " : "
											+ atrib.item(j).getNodeName();
									if (!column.contains(columnName)) {
										column.add(columnName);
										tableModel.addColumn(columnName);
									}
									values.add(atrib.item(j).getNodeValue());
								}
							}
						}
						NodeList nodeListInner = nodeList.item(i).getChildNodes();
						for (int j = 0; j < nodeListInner.getLength(); j++) {
							NamedNodeMap atrib = nodeListInner.item(j).getAttributes();
							if (atrib != null) {
								for (int k = 0; k < atrib.getLength(); k++) {
									String columnName = nodeListInner.item(j).getNodeName() + " : "
											+ atrib.item(k).getNodeName();
									if (!column.contains(columnName)) {
										column.add(columnName);
										tableModel.addColumn(columnName);
									}
									values.add(atrib.item(k).getNodeValue());
								}
							}
							String columnName = nodeListInner.item(j).getNodeName();
							if (!nodeListInner.item(j).getTextContent().isEmpty()
									&& nodeListInner.item(j).getNodeType() == Node.ELEMENT_NODE
									&& !column.contains(columnName)) {
								column.add(columnName);
								tableModel.addColumn(columnName);
							}
							if (nodeListInner.item(j).getNodeType() == Node.ELEMENT_NODE
									&& !nodeListInner.item(j).getTextContent().trim().isEmpty()) {
								values.add(nodeListInner.item(j).getTextContent());
							}
						}
						tableModel.addRow(values.toArray());
					}
				} catch (ParserConfigurationException e1) {
					e1.printStackTrace();
				} catch (SAXException e1) {
					e1.printStackTrace();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				
				for(Object col : column) {
					searchColumn.addItem(col);
				}

				table.setModel(tableModel);
				rowSorter = new TableRowSorter<>(table.getModel());
				table.setRowSorter(rowSorter);
			}
		});

		btnAddNewLine = new JButton("Add new line");
		btnAddNewLine.setBounds(10, 363, 165, 23);
		btnAddNewLine.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				tableModel.addRow(new Object[] {});
			}
		});

		btnRemoveRow = new JButton("Delete row");
		btnRemoveRow.setBounds(10, 397, 165, 23);
		btnRemoveRow.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (table.getSelectedRow() >= 0) {
					int dialogResult = JOptionPane.showConfirmDialog(jframeXmlManager.getContentPane(),
							"Are you sure you want to delete a row?", "Warning", 2);
					if (dialogResult == JOptionPane.YES_OPTION) {
						tableModel.removeRow(table.getSelectedRow());
					}
				}
			}
		});
		
		btnDTDValidate = new JButton("Validate XSD file");
		btnDTDValidate.setBounds(464, 363, 150, 23);
		btnDTDValidate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				File fileXSD = fileChooser("Select XSD file", "xsd");
				if (fileXSD == null) return;
				
		        try {
		            SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		            Schema schema = factory.newSchema(fileXSD);
		            Validator validator = schema.newValidator();
		            validator.validate(new StreamSource(xmlFile));
		            JOptionPane.showMessageDialog(jframeXmlManager.getContentPane(), "XML is valid.");
		        } catch (SAXException | IOException ex) {
		        	ex.printStackTrace();
		        	JOptionPane.showMessageDialog(jframeXmlManager.getContentPane(), "XML is not valid.");
		        }
			}
		});
		
		searchField = new JTextField();
		searchField.setBounds(248, 330, 150, 20);
		searchField.getDocument().addDocumentListener(new DocumentListener(){

			@Override
			public void insertUpdate(DocumentEvent e) {
			    String text = searchField.getText();

                if (text.trim().length() == 0) {
                    rowSorter.setRowFilter(null);
                } else {
                    rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + text, searchColumn.getSelectedIndex()));
                }
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
			    String text = searchField.getText();

                if (text.trim().length() == 0) {
                    rowSorter.setRowFilter(null);
                } else {
                    rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + text, searchColumn.getSelectedIndex()));
                }
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
			}
			
		});
		
		searchField.setColumns(10);
		
		searchColumn = new JComboBox();
		searchColumn.setBounds(248, 364, 150, 20);
		
		JButton btnSaveXML = new JButton("Save XML file");
		btnSaveXML.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				File newFile = null;
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setDialogTitle("Select a location to save file");
				fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("XML file", "xml"));
				fileChooser.setSelectedFile(new File(jlabelRoot.getText() + ".xml"));
				
				if (fileChooser.showSaveDialog(jframeXmlManager.getContentPane()) == JFileChooser.APPROVE_OPTION) {
				  newFile = fileChooser.getSelectedFile();
				}
				
				if (newFile == null) {
					JOptionPane.showMessageDialog(jframeXmlManager.getContentPane(), "No file chosen to write!");
					return;
				}
				
				try {
					DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
					DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
					
					// root elements
					Document doc = docBuilder.newDocument();
					Element rootElement = doc.createElement(jlabelRoot.getText());
					doc.appendChild(rootElement);
					
					// Create list of columns
					Map<Integer, String> listColumns = new TreeMap<>();
					for (int i = 0; i < tableModel.getColumnCount(); i++) {
						listColumns.put(i, tableModel.getColumnName(i));
					}
					
					// Search for root child element in column name and get attributes
					Map<Integer, String> rootChildAttrib = new TreeMap<>();
					for (int j = 0; j < listColumns.size(); j++) {
						String col = listColumns.get(j).replace(jlebelElement.getText()+" : ", "");
						if (!listColumns.get(j).equals(col)) {
							rootChildAttrib.put(j, col);
						}
					}
					
					// Create elements from table
					Vector tabElements = tableModel.getDataVector();
					
					for (int i = 0; i < tableModel.getRowCount(); i++) {
						Vector row = (Vector) tabElements.get(i);
						
						// Create child root element
						Element firstEl = doc.createElement(jlebelElement.getText());
						rootElement.appendChild(firstEl);
						// Check for attrib's for child root element
						for (Entry<Integer, String> entry : rootChildAttrib.entrySet()) {
							Attr attr = doc.createAttribute(entry.getValue());
							attr.setValue((String) row.get(entry.getKey()));
							firstEl.setAttributeNode(attr);
						}
						
						List<Integer> excluded = new ArrayList<>();
						for (int j = 0; j < row.size(); j++) {
							if (rootChildAttrib.get(j) != null || excluded.contains(j)) continue;
							
							String elementName = null;
							String elementAttrib = null;
							String textOfElement = null;
							
							// Check for element and attributes
							String[] col = listColumns.get(j).split(" : ");
							if (col.length == 2) {
								elementName = col[0];
								elementAttrib = col[1];
								
								// Search for the same name of element and exclude in next iterate
								for (int k = 0; k < listColumns.size(); k++) {
									if (listColumns.get(k).equals(col[0])) {
										textOfElement = (String) row.get(k);
										excluded.add(k);
										break;
									}
								}
							}
							else {
								elementName = listColumns.get(j);
								textOfElement = (String) row.get(j);
							}
							
							Element element = doc.createElement(elementName);
							if (textOfElement != null) {
								element.appendChild(doc.createTextNode(textOfElement));
							}
							firstEl.appendChild(element);
							
							if (elementAttrib != null) {
								Attr attr = doc.createAttribute(elementAttrib);
								attr.setValue((String) row.get(j));
								element.setAttributeNode(attr);
							}
						}
					}
					
					TransformerFactory transformerFactory = TransformerFactory.newInstance();
					Transformer transformer = transformerFactory.newTransformer();
					transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
				    transformer.setOutputProperty(OutputKeys.METHOD, "xml");
				    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
				    transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
				    transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
					DOMSource source = new DOMSource(doc);
					StreamResult result = new StreamResult(newFile);
					
					transformer.transform(source, result);
					
					xmlFile = newFile;
					JOptionPane.showMessageDialog(jframeXmlManager.getContentPane(), "The file has been successfully saved.");
					
				} catch (Exception e1) {
					JOptionPane.showMessageDialog(jframeXmlManager.getContentPane(), "Something wrong. File is not saved!");
				}
				
			}
		});
		btnSaveXML.setBounds(464, 329, 150, 23);
		
		JLabel lblRootLabel = new JLabel("Root:");
		lblRootLabel.setBounds(10, 270, 54, 14);
		
		jlabelRoot = new JLabel("...");
		lblRootLabel.setLabelFor(jlabelRoot);
		jlabelRoot.setBounds(78, 270, 97, 14);

		table = new JTable();

		table.setModel(new DefaultTableModel(new Object[][] {}, new String[] {}));
		rowSorter = new TableRowSorter<>(table.getModel());
		jframeXmlManager.getContentPane().setLayout(null);

		scrollPane.setViewportView(table);
		jframeXmlManager.getContentPane().add(scrollPane);
		jframeXmlManager.getContentPane().add(btnRemoveRow);
		jframeXmlManager.getContentPane().add(btnReadXmlFile);
		jframeXmlManager.getContentPane().add(btnAddNewLine);
		jframeXmlManager.getContentPane().add(lblRootLabel);
		jframeXmlManager.getContentPane().add(jlabelRoot);
		jframeXmlManager.getContentPane().add(btnSaveXML);
		jframeXmlManager.getContentPane().add(searchColumn);
		jframeXmlManager.getContentPane().add(searchField);
		jframeXmlManager.getContentPane().add(btnDTDValidate);
		
		jlabelElementLabel = new JLabel("Element:");
		jlabelElementLabel.setBounds(10, 295, 54, 14);
		jframeXmlManager.getContentPane().add(jlabelElementLabel);
		
		jlebelElement = new JLabel("...");
		jlabelElementLabel.setLabelFor(jlebelElement);
		jlebelElement.setBounds(78, 295, 97, 14);
		jframeXmlManager.getContentPane().add(jlebelElement);
		
		jlebelSearch = new JLabel("Search:");
		jlebelSearch.setLabelFor(searchField);
		jlebelSearch.setBounds(248, 305, 150, 14);
		jframeXmlManager.getContentPane().add(jlebelSearch);
	}
	
	public static int centeringFrameOnWidth(int frameWidth) {
		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		int width = dimension.width / 2 - frameWidth / 2;
		return width;
	}
	
	public static int centeringFrameOnHeight(int frameHeight) {
		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		int height = dimension.height / 2 - frameHeight / 2;
		return height;
	}
}