package com.macvon.query;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.xpath.XPathExpressionException;

import org.jdom2.Element;
import org.jdom2.xpath.XPathFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import com.macvon.utils.GlobalConstants.XML;
import com.macvon.utils.GlobalConstants.XML.NODE;
import com.macvon.utils.XmlUtils;


public enum QueryMapConfigParser {
	INTSANCE;
	public final static Logger LOGGER = LoggerFactory.getLogger(QueryMapConfigParser.class);
	
	public void parseConfigurator(Element configNode)
			throws Exception {
		LOGGER.debug("parseConfigurator start to process Node : {}", configNode);
		XPathFactory xPathFactory = XPathFactory.instance();
		// Validate the document
		validateDocument(xPathFactory, configNode);
		// Create a new RuleConfigurator
		
		// Get the root node
		Element rootNode = XmlUtils.findRequiredSingleNode(xPathFactory, configNode,NODE.ROOT_SQL_MAP_CONFIG);
		if(rootNode!=null) {
			// Init daos
			initDaos(rootNode);	
		} else {
			LOGGER.error("parseConfigurator rootNode not found : {}", rootNode);
		}

	}
	/**
	 * build dao.
	 * @param rootNode
	 * @throws XPathExpressionException
	 */
	private void initDaos(Element rootNode) throws Exception {
		// Get the dao nodes
		List<Element> daoNodes = XmlUtils.getChildren(rootNode,
				XML.NODE.DAO);
		//LOGGER.debug("initDaos(Node rootNode): {}", rootNode);
		for (Element daoNode : daoNodes) {
			DAO dao = createDao(daoNode);
			QueryMapConfig.INSTANCE.getQueryMap().addDao(dao);
		}
	}
	private DAO createDao(Element daoNode) throws Exception {
		final String name = XmlUtils.getRequiredAttributeValue(
				daoNode, XML.ATTRIB.CLASS_NAME);
		//LOGGER.debug("createDao(Node daoNode): {}", name);
		DAO dao = new DAO(name);
		List<Element> queryNodes = XmlUtils.getChildren(daoNode,
				XML.NODE.QUERY);
		for (Element queryNode : queryNodes) {
			final String id = XmlUtils.getRequiredAttributeValue(
					queryNode, XML.ATTRIB.ID);
			String sql = queryNode.getTextTrim();
			Query query = new Query(id, sql);
			final String mappedEntityClassName = XmlUtils.getAttributeValue(
					queryNode, XML.ATTRIB.MAPPED_ENTITY);
			if(mappedEntityClassName!=null) {
				try {
					query.setEntity(Class.forName(mappedEntityClassName));
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
					throw new RuntimeException("mappedEntityClassName-" + mappedEntityClassName + " "+ e.getMessage());
				}
			}
			//LOGGER.debug("createDao Query:{}", query.toString());
			dao.addQuery(query);
		}
		return dao;
	}
	/*
	 * Validates conditions in the config document not checked when parsing.
	 */
	private void validateDocument(XPathFactory factory, Element node) {
		LOGGER.debug("validateDocument->{}", node);
		try {
			Set<String> daos = new HashSet<String>();
			// Check unique names for dao nodes...
			// dao
			List<Element> daoNodes = XmlUtils.findAllNodes(factory, node,
					XML.NODE.DAO);
			LOGGER.debug("validateDocument->findAllNodes: {}", daoNodes.size());
			for (Element daoNode : daoNodes) {
				final String name = XmlUtils.getRequiredAttributeValue(
						daoNode, XML.ATTRIB.CLASS_NAME);

				Assert.isTrue(!daos.contains(name), "Attribute " + XML.ATTRIB.CLASS_NAME + " must be unique among all "
						+ XML.NODE.DAO + " nodes");
				Set<String> querys = new HashSet<String>();
				List<Element> queryNodes = XmlUtils.findAllNodes(factory, daoNode,
						XML.NODE.QUERY);
				for (Element queryNode : queryNodes) {
					final String id = XmlUtils.getRequiredAttributeValue(
							queryNode, XML.ATTRIB.ID);
					Assert.isTrue(!daos.contains(name), "Attribute "
							+ XML.ATTRIB.ID + " must be unique among all "
							+ XML.NODE.QUERY + " nodes");
					querys.add(id);
				}
				daos.add(name);
			}

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}

