package net.vurs.service.definition.view.page;

import java.io.File;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import freemarker.cache.FileTemplateLoader;
import freemarker.template.Configuration;

import net.vurs.service.definition.EntityService;
import net.vurs.service.definition.LogicService;
import net.vurs.service.definition.view.ViewManager;
import net.vurs.transaction.Transaction;
import net.vurs.util.ErrorControl;
import net.vurs.util.RelativePath;

public class PageManager extends ViewManager<PageView> {

	private Configuration templateConfig;
	
	public PageManager(EntityService entityService, LogicService logicService) {
		super(entityService, logicService);
		setupTemplates();
	}
	
	private void setupTemplates() {
		try {
			File templateDir = new File(RelativePath.root().getAbsolutePath()
												.concat(File.separator)
												.concat("templates"));
			
			this.templateConfig = new Configuration();
			this.templateConfig.setLocale(java.util.Locale.CANADA);
			this.templateConfig.setNumberFormat("0.##");  
			
			FileTemplateLoader ftl = new FileTemplateLoader(templateDir);
			this.templateConfig.setTemplateLoader(ftl);
		} catch (Exception e) {
			ErrorControl.logException(e);
		}
	}

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response) {
		Transaction transaction = this.entityService.startGlobalTransaction();
		
		PageRequest pageRequest = new PageRequest(this.entityService, this.logicService, this.templateConfig, request, response);
		handle(pageRequest);
		
		transaction.finish();
	}
	
}
