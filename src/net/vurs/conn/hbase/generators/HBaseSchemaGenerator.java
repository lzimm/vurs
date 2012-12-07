package net.vurs.conn.hbase.generators;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import net.vurs.conn.cassandra.typesafety.CassandraBackedDefinition;
import net.vurs.conn.hbase.streamer.HBaseComponentStreamer;
import net.vurs.conn.hbase.typesafety.HBaseBackedDefinition;
import net.vurs.service.definition.EntityService;
import net.vurs.util.ClassWalker;
import net.vurs.util.ClassWalkerFilter;
import net.vurs.util.ErrorControl;
import net.vurs.util.Reflection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class HBaseSchemaGenerator {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	private EntityService entityService = null;
	private Collection<Class<? extends HBaseBackedDefinition>> definitions = null;
	private Map<Class<?>, Class<? extends HBaseComponentStreamer<?>>> streamers = null;
	private Map<Class<? extends HBaseBackedDefinition>, HBaseSchemaColumnGenerator<? extends HBaseBackedDefinition>> columnGenerators = null;
	
	public HBaseSchemaGenerator(
			EntityService entityService,
			Collection<Class<? extends HBaseBackedDefinition>> definitions,
			Map<Class<?>, Class<? extends HBaseComponentStreamer<?>>> streamers) {
		this.entityService = entityService;
		this.definitions = definitions;
		this.streamers = streamers;
		
		setupGenerators();
	}
	
	@SuppressWarnings("unchecked")
	private void setupGenerators() {
		this.columnGenerators = new HashMap<Class<? extends HBaseBackedDefinition>, HBaseSchemaColumnGenerator<? extends HBaseBackedDefinition>>();

		Iterator<Class<?>> itr = new ClassWalker(ClassWalkerFilter.extending(HBaseSchemaColumnGenerator.class));
		
		while (itr.hasNext()) {
			Class<? extends HBaseSchemaColumnGenerator<?>> cls = (Class<? extends HBaseSchemaColumnGenerator<?>>) itr.next();
		    Class<? extends HBaseBackedDefinition> paramType = (Class<? extends HBaseBackedDefinition>) Reflection.getParamType(cls, 0);
		    
		    try {
		    	HBaseSchemaColumnGenerator<?> instance = cls.newInstance();
		    	instance.setup(this.entityService, this.streamers);
		    	
			    this.logger.info(String.format("Found column generator: %s", cls.getName()));
			    this.columnGenerators.put(paramType, instance);
		    } catch (Exception e) {
		    	ErrorControl.logException(e);
		    }
		}
	}
	
	public void generate(File schemaFile) {
		try {
			schemaFile.delete();
			schemaFile.createNewFile();
			PrintStream out = new PrintStream(new FileOutputStream(schemaFile));
			
			generate(out);
			
			out.close();
		} catch (Exception e) {
			ErrorControl.logException(e);
		}
	}

	private void generate(PrintStream out) throws Exception {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		DOMImplementation impl = builder.getDOMImplementation();
		Document xmldoc = impl.createDocument(null, "ColumnFamilies", null);
		Element root = xmldoc.getDocumentElement();
		
		for (Class<? extends HBaseBackedDefinition> def: this.definitions) {
			ParameterizedType definitionType = null;
			for (Type iface: def.getGenericInterfaces()) {
				if (CassandraBackedDefinition.class.isAssignableFrom(def)) {
					definitionType = (ParameterizedType) iface;
				}
			}
			
			String name = def.getSimpleName();
			this.logger.info(String.format("Generating column definition for: %s as: %s", name, definitionType.getRawType()));
			
			Element column = xmldoc.createElement("ColumnFamily");
			column.setAttribute("Name", name);
			
			this.columnGenerators.get(definitionType.getRawType()).generate(def, definitionType, xmldoc, column);
			
			root.appendChild(column);
		}
		
		DOMSource domSource = new DOMSource(xmldoc);
		StreamResult streamResult = new StreamResult(out);
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer serializer = tf.newTransformer();
		serializer.setOutputProperty(OutputKeys.INDENT, "yes");
		serializer.transform(domSource, streamResult); 
	}

}
