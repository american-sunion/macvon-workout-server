package com.macvon.utils;


import java.util.List;
import java.util.Map;

import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.filter.Filters;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;
import org.springframework.util.Assert;

@SuppressWarnings("rawtypes")
public class XmlUtils {

	protected static XPathExpression getXPathExpression(Map<String, XPathExpression> xPathExpressions, XPathFactory factory, String sExpression)
			throws Exception {
		XPathExpression expression = xPathExpressions.get(sExpression);
		if (null == expression) {
			expression =factory.compile(sExpression);
			xPathExpressions.put(sExpression, expression);
		}
		return expression;
	}

	public static Element findSingleNode(XPathFactory factory, Document document, String nodeName)
			throws Exception {
		List<Element> allNodes = findAllNodes(factory, document, nodeName);
		Element singleNode = null;
		Assert.isTrue(allNodes.size() <= 1, moreThanOneSingleNode(nodeName));
		if (allNodes.size() == 1) {
			singleNode = allNodes.get(0);
		}
		return singleNode;
	}
	public static Element findSingleNode(XPathFactory factory, Element parentElement, String nodeName)
			throws Exception {
		List<Element> allNodes = findAllNodes(factory, parentElement, nodeName);
		Element singleNode = null;
		Assert.isTrue(allNodes.size() <= 1,moreThanOneSingleNode(nodeName));
		if (allNodes.size() == 1) {
			singleNode = allNodes.get(0);
		}
		return singleNode;
	}
	public static List<Element> findAllNodes(XPathFactory factory, Element parentElement, String nodeName)
			throws Exception {
		final String sXPath = "//" + nodeName;
		XPathExpression<Element> expr = factory.compile(sXPath, Filters.element());
		return expr.evaluate(parentElement);
	}
	public static List<Element> findAllNodes(XPathFactory factory, Document document, String nodeName)
			throws Exception {
		final String sXPath = "//" + nodeName;
		XPathExpression<Element> expr = factory.compile(sXPath, Filters.element());
		return expr.evaluate(document);
	}
	public static Element getChild(Element parent, String nodeName) throws Exception {
		Element singleNode = null;
		List<Element> allChildren = getChildren(parent, nodeName);
		Assert.isTrue(allChildren.size() <= 1,moreThanOneSingleNode(nodeName));
		if (allChildren.size() == 1) {
			singleNode = allChildren.get(0);
		}
		return singleNode;
	}

	public static List<Element> getChildren(Element parent, String nodeName)
			throws Exception {
		return parent.getChildren(nodeName);
	}

	public static List<Element> getChildren(Element parent) throws Exception {
		return parent.getChildren();
	}

	public static String getAttributeValue(Element node, String attributeName)
			throws Exception {
		List<Attribute> attrs = node.getAttributes();
		for(Attribute attr: attrs) {
			if(attr.getName().equals(attributeName)) {
				return attr.getValue();
			}
		}
		return null;
	}

	// Required versions
	public static Element findRequiredSingleNode(XPathFactory factory, Document parent, String nodeName)
			throws Exception {
		Element node = findSingleNode(factory, parent, nodeName);
		Assert.notNull(node, missingRequiredNodeMsg(nodeName));
		return node;
	}
	public static Element findRequiredSingleNode(XPathFactory factory, Element parent, String nodeName)
			throws Exception {
		Element node = findSingleNode(factory, parent, nodeName);
		Assert.notNull(node,missingRequiredNodeMsg(nodeName));
		return node;
	}
	public static List<Element> findRequiredAllNodes(XPathFactory factory, Element parent, String nodeName)
			throws Exception {
		List<Element> nodes = findAllNodes(factory, parent, nodeName);
		Assert.notNull(nodes, missingRequiredNodeMsg(nodeName));
		return nodes;
	}

	public static Element getRequiredChild(Element parent, String nodeName)
			throws Exception {
		Element child = getChild(parent, nodeName);
		Assert.notNull(child,missingRequiredChildNode(nodeName));
		return child;
	}

	public static List<Element> getRequiredChildren(Element parent, String nodeName)
			throws Exception {
		List<Element> children = getChildren(parent, nodeName);
		Assert.notNull(children,missingRequiredChildNode(nodeName));
		return children;
	}

	public static List<Element> getRequiredChildren(Element parent) throws Exception {
		List<Element> children = getChildren(parent);
		Assert.notNull(children, missingRequiredChildNode(""));
		return children;
	}

	public static String getRequiredAttributeValue(Element node, String attributeName)
			throws Exception {
		String attributeValue = getAttributeValue(node, attributeName);
		 Assert.notNull(attributeValue,
				missingRequiredAttributeMsg(attributeName));
		return attributeValue;
	}

	// Error messages
	protected static String missingRequiredNodeMsg(String nodeName) {
		return "Missing required node(s) " + nodeName;
	}

	protected static String missingRequiredAttributeMsg(String attribName) {
		return "Missing required attribute " + attribName;
	}

	protected static String missingRequiredChildNode(String nodeName) {
		return "Missing required child node(s) " + nodeName;
	}

	protected static String moreThanOneSingleNode(String nodeName) {
		return "More than one " + nodeName + " element was found";
	}
}
