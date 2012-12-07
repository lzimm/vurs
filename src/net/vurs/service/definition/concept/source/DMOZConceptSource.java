package net.vurs.service.definition.concept.source;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.vurs.conn.sql.SQLConnection;
import net.vurs.entity.definition.concept.Concept;
import net.vurs.entity.definition.concept.ConceptLink;
import net.vurs.service.definition.entity.manager.concept.ConceptLinkManager;
import net.vurs.service.definition.entity.manager.concept.ConceptManager;
import net.vurs.util.ErrorControl;
import net.vurs.util.EscapeUtil;

public class DMOZConceptSource {
	public static String DMOZ_STRUCTURE_DUMP = "/usr/local/dmoz/structure.rdf.u8";
	public static String DMOZ_CONTENT_DUMP = "/usr/local/dmoz/content.rdf.u8";
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	private ConceptManager conceptManager = null;
	private ConceptLinkManager conceptLinkManager = null;
	
	public DMOZConceptSource(ConceptManager conceptManager, ConceptLinkManager conceptLinkManager) {
		this.conceptManager = conceptManager;
		this.conceptLinkManager = conceptLinkManager;
	}
	
	public void init() {
		SQLConnection conn = this.conceptManager.acquireConnection();
		
		List<Map<String, Object>> res = conn.query(String.format("SELECT COUNT(*) AS `count` FROM `%s` WHERE `%s` = '%s';", 
									this.conceptManager.getTable(),
									Concept.source.getName(),
									Concept.Source.DMOZ.getIndex()));
		
		if (res.get(0).get("count").equals(new Long(0))) {
			TreeMap<String, StructureParserState> orderedStructureParse = this.buildTree(conn);
			this.buildLinks(conn, orderedStructureParse);
		}
			
		conn.release();
	}
	
	private static class StructureParserState {
		public static enum Expecting {
			NONE,
			TITLE,
			DESCRIPTION
		};
		
		public String id = "";
		public String parent = "1";
		public String title = "";
		public String path = "";
		public String description = "";
		public Expecting expecting = Expecting.NONE;
		
		@Override
		public String toString() {
			return this.path;
		}
	}
	
	@SuppressWarnings("unchecked")
	private TreeMap<String, StructureParserState> buildTree(SQLConnection conn) {
		this.logger.info("Building concept tree from DMOZ dump");
		
		TreeMap<String, StructureParserState> orderedStructureParse = new TreeMap<String, StructureParserState>();
		
		try {
			InputStream in = new FileInputStream(DMOZ_STRUCTURE_DUMP);
			XMLInputFactory inputFactory = XMLInputFactory.newInstance();
			XMLEventReader eventReader = inputFactory.createXMLEventReader(in);
			
			StructureParserState state = null;
			
			while (eventReader.hasNext()) {
				XMLEvent event = eventReader.nextEvent();
				
				if (event.isStartElement()) {
					StartElement startElement = event.asStartElement();

					if (startElement.getName().getLocalPart().equals("Topic")) {
						state = new StructureParserState();
						Iterator<Attribute> attributes = startElement.getAttributes();
						while (attributes.hasNext()) {
							Attribute attribute = attributes.next();
							if (attribute.getName().getLocalPart().equals("id")) {
								state.path = attribute.getValue();
							}
						}
					}
					
					if (state != null && startElement.getName().getLocalPart().equals("Title")) {
						state.expecting = StructureParserState.Expecting.TITLE;
					}
					
					if (state != null && startElement.getName().getLocalPart().equals("Description")) {
						state.expecting = StructureParserState.Expecting.DESCRIPTION;
					}
				}
				
				if (state != null && event.isCharacters()) {
					Characters characters = event.asCharacters();
					if (!state.expecting.equals(StructureParserState.Expecting.NONE)) {
						switch (state.expecting) {
							case TITLE:
								state.title = characters.getData();
							break;
							
							case DESCRIPTION:
								state.description = characters.getData();
							break;
						}
						
						state.expecting = StructureParserState.Expecting.NONE;
					}
				}
				
				if (state != null && event.isEndElement()) {
					EndElement endElement = event.asEndElement();
					
					if (endElement.getName().getLocalPart().equals("Topic")) {
						orderedStructureParse.put(state.path, state);
						
						if (orderedStructureParse.size() % 100000 == 0) {
							this.logger.info(String.format("- parsed %s entries", orderedStructureParse.size()));
						}
						
						state = null;
					}
				}
			}
			
			String structureInsertStart = String.format("REPLACE INTO `%s` (`%s`, `%s`, `%s`, `%s`, `%s`, `%s`, `%s`) VALUES ",
					this.conceptManager.getTable(),
					Concept.id.getName(),
					Concept.path.getName(),
					Concept.name.getName(),
					Concept.parent.getName(),
					Concept.description.getName(),
					Concept.source.getName(),
					Concept.state.getName());
			
			StringBuilder structureBuffer = new StringBuilder(structureInsertStart);
			
			Integer structureID = 0;
			for (Entry<String, StructureParserState> e: orderedStructureParse.entrySet()) {
				StructureParserState s = e.getValue();
				s.id = (structureID++).toString();
				
				String[] pathParts = s.path.split("/");
				if (pathParts.length > 1) {
					String[] parentPath = Arrays.copyOf(pathParts, pathParts.length - 1);
					StringBuilder parentBuffer = new StringBuilder();
					Iterator<String> parentIter = Arrays.asList(parentPath).iterator();
					while (parentIter.hasNext()) {
						String part = parentIter.next();
						parentBuffer.append(part);
						if (parentIter.hasNext()) {
							parentBuffer.append('/');
						}
					}
					
					StructureParserState p = orderedStructureParse.get(parentBuffer.toString());
					
					if (p == null) {
						this.logger.error(String.format("Structure with path %s has no parent", s.path));
					} else if (p.id.length() == 0) {
						this.logger.error(String.format("Structure with path %s has parent %s with no id", s.path, p.path));
					} else {
						s.parent = p.id;
					}
				}
				
				if (structureID % 1000 != 1) {
					structureBuffer.append(", ");
				}
				
				structureBuffer.append(String.format("('%s', '%s', '%s', '%s', '%s', '%s', '%s')",
						s.id,
						EscapeUtil.escape(s.path, EscapeUtil.BACKSLASH, EscapeUtil.MYSQL_CHARS),
						EscapeUtil.escape(s.title, EscapeUtil.BACKSLASH, EscapeUtil.MYSQL_CHARS),
						s.parent,
						EscapeUtil.escape(s.description, EscapeUtil.BACKSLASH, EscapeUtil.MYSQL_CHARS),
						Concept.Source.DMOZ.getIndex(),
						Concept.State.PREPARING.getIndex()));
				
				if (structureID % 1000 == 0) {
					conn.update(structureBuffer.toString());
					structureBuffer = new StringBuilder(structureInsertStart);
					
					if (structureID % 100000 == 0) {
						this.logger.info(String.format(" - inserted %s entries", structureID));
					}
				}
			}
		} catch (Exception e) {
			ErrorControl.logException(e);
		}
		
		return orderedStructureParse;
	}
	
	private static class ContentParserState {
		public String id = "";
		public String path = "";
		public String link = "";

		@Override
		public String toString() {
			return this.path + ": " + this.link;
		}
	}
	
	@SuppressWarnings("unchecked")
	private void buildLinks(SQLConnection conn, TreeMap<String, StructureParserState> orderedStructureParse) {
		this.logger.info("Building concept links from DMOZ dump");
		
		try {
			InputStream in = new FileInputStream(DMOZ_CONTENT_DUMP);
			XMLInputFactory inputFactory = XMLInputFactory.newInstance();
			XMLEventReader eventReader = inputFactory.createXMLEventReader(in);
			
			ContentParserState state = null;
			
			String contentInsertStart = String.format("REPLACE INTO `%s` (`%s`, `%s`, `%s`, `%s`) VALUES ",
					this.conceptLinkManager.getTable(),
					ConceptLink.id.getName(),
					ConceptLink.concept.getName(),
					ConceptLink.link.getName(),
					ConceptLink.state.getName());
			
			StringBuilder contentBuffer = new StringBuilder(contentInsertStart);
			
			Integer linkID = 0;
			
			while (eventReader.hasNext()) {
				XMLEvent event = eventReader.nextEvent();
				
				if (event.isStartElement()) {
					StartElement startElement = event.asStartElement();

					if (startElement.getName().getLocalPart().equals("Topic")) {
						state = new ContentParserState();
						Iterator<Attribute> attributes = startElement.getAttributes();
						while (attributes.hasNext()) {
							Attribute attribute = attributes.next();
							if (attribute.getName().getLocalPart().equals("id")) {
								state.path = attribute.getValue();
							}
						}
					}
					
					if (state != null && startElement.getName().getLocalPart().equals("link")) {
						Iterator<Attribute> attributes = startElement.getAttributes();
						while (attributes.hasNext()) {
							Attribute attribute = attributes.next();
							if (attribute.getName().getLocalPart().equals("resource")) {
								state.link = attribute.getValue();
							}
						}
					}
				}

				if (state != null && event.isEndElement()) {
					EndElement endElement = event.asEndElement();
					
					if (endElement.getName().getLocalPart().equals("Topic")) {
						StructureParserState s = orderedStructureParse.get(state.path);
						
						if (s == null) {
							this.logger.error(String.format("Link with path %s has no parent", state.path));
						}
						
						state.id = (linkID++).toString();
						
						if (linkID % 1000 != 1) {
							contentBuffer.append(", ");
						}
						
						contentBuffer.append(String.format("('%s', '%s', '%s', '%s')",
								state.id,
								s.id,
								EscapeUtil.escape(state.link, EscapeUtil.BACKSLASH, EscapeUtil.MYSQL_CHARS),
								ConceptLink.State.WAITING.getIndex()));
						
						if (linkID % 1000 == 0) {
							conn.update(contentBuffer.toString());
							contentBuffer = new StringBuilder(contentInsertStart);
						}
						
						if (linkID % 100000 == 0) {
							this.logger.info(String.format("- inserted %s entries", linkID));
						}
						
						state = null;
					}
				}
			}
		} catch (Exception e) {
			ErrorControl.logException(e);
		}
	}
	
}
