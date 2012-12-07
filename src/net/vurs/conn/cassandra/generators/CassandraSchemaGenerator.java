package net.vurs.conn.cassandra.generators;

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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import net.vurs.conn.cassandra.CassandraConnection;
import net.vurs.conn.cassandra.streamer.CassandraComponentStreamer;
import net.vurs.conn.cassandra.typesafety.CassandraBackedDefinition;

import net.vurs.service.definition.EntityService;
import net.vurs.util.ClassWalker;
import net.vurs.util.ClassWalkerFilter;
import net.vurs.util.ErrorControl;
import net.vurs.util.Reflection;

public class CassandraSchemaGenerator {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	private EntityService entityService = null;
	private Collection<Class<? extends CassandraBackedDefinition>> definitions = null;
	private Map<Class<?>, Class<? extends CassandraComponentStreamer<?>>> streamers = null;
	private Map<Class<? extends CassandraBackedDefinition>, CassandraSchemaColumnGenerator<? extends CassandraBackedDefinition>> columnGenerators = null;
	
	public CassandraSchemaGenerator(
			EntityService entityService,
			Collection<Class<? extends CassandraBackedDefinition>> definitions,
			Map<Class<?>, Class<? extends CassandraComponentStreamer<?>>> streamers) {
		this.entityService = entityService;
		this.definitions = definitions;
		this.streamers = streamers;
		
		setupGenerators();
	}

	@SuppressWarnings("unchecked")
	private void setupGenerators() {
		this.columnGenerators = new HashMap<Class<? extends CassandraBackedDefinition>, CassandraSchemaColumnGenerator<? extends CassandraBackedDefinition>>();

		Iterator<Class<?>> itr = new ClassWalker(ClassWalkerFilter.extending(CassandraSchemaColumnGenerator.class));
		
		while (itr.hasNext()) {
			Class<? extends CassandraSchemaColumnGenerator<?>> cls = (Class<? extends CassandraSchemaColumnGenerator<?>>) itr.next();
		    Class<? extends CassandraBackedDefinition> paramType = (Class<? extends CassandraBackedDefinition>) Reflection.getParamType(cls, 0);
		    
		    try {
		    	CassandraSchemaColumnGenerator<?> instance = cls.newInstance();
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
		Document xmldoc = impl.createDocument(null, "Keyspace", null);
		Element root = xmldoc.getDocumentElement();
		root.setAttribute("Name", CassandraConnection.KEYSPACE);
		
		for (Class<? extends CassandraBackedDefinition> def: this.definitions) {
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
			
			this.columnGenerators.get(definitionType.getRawType()).generate(definitionType, column);
			
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
