package net.vurs.conn.sql.generators;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.vurs.conn.sql.streamer.SQLComponentStreamer;
import net.vurs.conn.sql.typesafety.SQLBackedDefinition;
import net.vurs.service.definition.EntityService;
import net.vurs.util.ClassWalker;
import net.vurs.util.ClassWalkerFilter;
import net.vurs.util.ErrorControl;
import net.vurs.util.Reflection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SQLSchemaGenerator {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	private EntityService entityService = null;
	private Collection<Class<? extends SQLBackedDefinition>> definitions = null;
	private Map<Class<?>, Class<? extends SQLComponentStreamer<?>>> streamers = null;
	private Map<Class<? extends SQLBackedDefinition>, SQLSchemaTableGenerator<? extends SQLBackedDefinition>> tableGenerators = null;
	
	public SQLSchemaGenerator(
			EntityService entityService,
			Collection<Class<? extends SQLBackedDefinition>> definitions,
			Map<Class<?>, Class<? extends SQLComponentStreamer<?>>> streamers) {
		this.entityService = entityService;
		this.definitions = definitions;
		this.streamers = streamers;
		
		setupGenerators();
	}

	@SuppressWarnings("unchecked")
	private void setupGenerators() {
		this.tableGenerators = new HashMap<Class<? extends SQLBackedDefinition>, SQLSchemaTableGenerator<? extends SQLBackedDefinition>>();

		Iterator<Class<?>> itr = new ClassWalker(ClassWalkerFilter.extending(SQLSchemaTableGenerator.class));
		
		while (itr.hasNext()) {
			Class<? extends SQLSchemaTableGenerator<?>> cls = (Class<? extends SQLSchemaTableGenerator<?>>) itr.next();
		    Class<? extends SQLBackedDefinition> paramType = (Class<? extends SQLBackedDefinition>) Reflection.getParamType(cls, 0);
		    
		    try {
		    	SQLSchemaTableGenerator<?> instance = cls.newInstance();
		    	instance.setup(this.entityService, this.streamers);
		    	
			    this.logger.info(String.format("Found table generator: %s", cls.getName()));
			    this.tableGenerators.put(paramType, instance);
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

	@SuppressWarnings("unchecked")
	private void generate(PrintStream out) throws Exception {
		for (Class<? extends SQLBackedDefinition> def: this.definitions) {
			Class<? extends SQLBackedDefinition> definitionType = (Class<? extends SQLBackedDefinition>) def.getSuperclass();
			
			String name = def.getSimpleName();
			this.logger.info(String.format("Generating table definition for: %s as: %s", name, definitionType));
			this.tableGenerators.get(definitionType).generate(def, out);
		}
	}
	
}
