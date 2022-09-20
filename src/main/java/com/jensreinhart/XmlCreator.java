package com.jensreinhart;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.InputStream;

public class XmlCreator {

    private static final String TEMPLATE_FILE = "/template.xml";
    private static final String FILE_NAME = "LaxwareImport";
    private static final String XML_SUFFIX = ".xml";
    private static final String GERMAN_INVOICE_PARTY_NAME = "Diverse";
    private static final String FRANCE_INVOICE_PARTY_NAME = "Diverse - Ausland";
    private static final String GERMAN_TAX_AREA = "EU";
    private static final String FRANCE_TAX_AREA = "non_EU";

    // All node names
    private static final String XML_ORDER_DATE = "ORDER_DATE";
    private static final String XML_INVOICE_PARTY_NAME = "NAME2";
    private static final String XML_REMARK = "REMARK";
    private static final String XML_ORDER_ITEM_LIST = "ORDER_ITEM_LIST";
    private static final String XML_ORDER_ITEM = "ORDER_ITEM";
    private static final String XML_LINE_ITEM_ID = "LINE_ITEM_ID";
    private static final String XML_ARTICLE_ID = "ARTICLE_ID";
    private static final String XML_SUPPLIER_AID = "SUPPLIER_AID";
    private static final String XML_DESCRIPTION_SHORT = "DESCRIPTION_SHORT";
    private static final String XML_DESCRIPTION_LONG = "DESCRIPTION_LONG";
    private static final String XML_QUANTITY = "QUANTITY";
    private static final String XML_ORDER_UNIT = "ORDER_UNIT";
    private static final String XML_ARTICLE_PRICE = "ARTICLE_PRICE";
    private static final String XML_PRICE_AMOUNT = "PRICE_AMOUNT";
    private static final String XML_PRICE_LINE_AMOUNT = "PRICE_LINE_AMOUNT";
    private static final String XML_TAX = "TAX";
    private static final String XML_TOTAL_ITEM_NUM = "TOTAL_ITEM_NUM";
    private static final String XML_TOTAL_AMOUNT = "TOTAL_AMOUNT";


    /**
     * Save to xml. Example can be found here:
     * https://examples.javacodegeeks.com/core-java/xml/parsers/documentbuilderfactory/modify-xml-file-in-java-using-dom-parser-example/
     * @param order
     * @param nameSuffix
     * @throws Exception
     */
    public static void saveOrderToXml(Order order, String nameSuffix) throws Exception {

        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        InputStream in = XmlCreator.class.getResourceAsStream(TEMPLATE_FILE);
        Document document = documentBuilder.parse(in);

        // Set the date
        // The following works because only one tag with this name exists.
        Node orderDate = document.getElementsByTagName(XML_ORDER_DATE).item(0);
        orderDate.setTextContent(order.getDate());

        // Set the invoice party name depending on the country
        Node invoicePartyName = document.getElementsByTagName(XML_INVOICE_PARTY_NAME).item(1);
        Node taxArea = document.getElementsByTagName(XML_REMARK).item(2);
        if (order.getIsGerman())
        {
            invoicePartyName.setTextContent(GERMAN_INVOICE_PARTY_NAME);
            taxArea.setTextContent(GERMAN_TAX_AREA);
        }
        else
        {
            invoicePartyName.setTextContent(FRANCE_INVOICE_PARTY_NAME);
            taxArea.setTextContent(FRANCE_TAX_AREA);
        }

        // Between the child notes are text nodes!
        // 1, 3, 5 are the indices of the nodes!
        Node orderItemList = document.getElementsByTagName(XML_ORDER_ITEM_LIST).item(0);

        // Add all order items
        int lineItemID = 0;
        double totalAmount = 0;
        for (OrderItem orderItem: order.getOrderItemList()) {
            lineItemID++;
            orderItemList.appendChild(createOrderItem(document, lineItemID, orderItem, order.getIsGerman()));
            totalAmount += (orderItem.getUnitPrice() * orderItem.getQuantity());
        }

        // Set the total number of element items (not the sum of quantity!) and total price
        document.getElementsByTagName(XML_TOTAL_ITEM_NUM).item(0).setTextContent(String.valueOf(lineItemID));
        document.getElementsByTagName(XML_TOTAL_AMOUNT).item(0).setTextContent(String.valueOf(totalAmount));

        // pretty print:
        trimWhitespace(document);

        // write the content into a new xml file
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        DOMSource source = new DOMSource(document);
        StreamResult result = new StreamResult(new File(FILE_NAME + nameSuffix + XML_SUFFIX));
        transformer.transform(source, result);
    }

    private static Node createOrderItem(Document document, int lineItemID, OrderItem orderItem, boolean isGerman) {
        Element orderItemElement = document.createElement(XML_ORDER_ITEM);

        orderItemElement.appendChild(createNode(document, XML_LINE_ITEM_ID, Integer.toString(lineItemID)));

        Element articleIdElement = document.createElement(XML_ARTICLE_ID);
        articleIdElement.appendChild(createNode(document, XML_SUPPLIER_AID, String.valueOf(orderItem.getArticleId())));
        articleIdElement.appendChild(createNode(document, XML_DESCRIPTION_SHORT, orderItem.getDescription()));
        articleIdElement.appendChild(createNode(document, XML_DESCRIPTION_LONG, orderItem.getDescription()));
        orderItemElement.appendChild(articleIdElement);

        orderItemElement.appendChild(createNode(document, XML_QUANTITY, String.valueOf(orderItem.getQuantity())));

        orderItemElement.appendChild(createNode(document, XML_ORDER_UNIT, "0"));

        Element articlePriceElement = document.createElement(XML_ARTICLE_PRICE);
        articlePriceElement.setAttribute("type", "gros_list");
        articlePriceElement.appendChild(createNode(document, XML_PRICE_AMOUNT, String.valueOf(orderItem.getUnitPrice())));
        articlePriceElement.appendChild(createNode(document, XML_PRICE_LINE_AMOUNT, ""));
        if (isGerman)
        {
            articlePriceElement.appendChild(createNode(document, XML_TAX, String.valueOf(orderItem.getTax())));
        }
        else
        {
            // If the Order is not German, set the tax to zero
            articlePriceElement.appendChild(createNode(document, XML_TAX, String.valueOf(0)));
        }
        orderItemElement.appendChild(articlePriceElement);

        return orderItemElement;
    }

    private static Node createNode(Document document, String nodeName, String nodeContent){
        Element node = document.createElement(nodeName);
        node.appendChild(document.createTextNode(nodeContent));
        return node;
    }

    private static void trimWhitespace(Node node)
    {
        NodeList children = node.getChildNodes();
        for(int i = 0; i < children.getLength(); ++i) {
            Node child = children.item(i);
            if(child.getNodeType() == Node.TEXT_NODE) {
                child.setTextContent(child.getTextContent().trim());
            }
            trimWhitespace(child);
        }
    }


}
