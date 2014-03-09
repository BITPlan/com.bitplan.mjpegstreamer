package com.bitplan.camgrab.resources;

import java.util.Map;
import java.util.logging.Logger;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import com.bitplan.rest.freemarker.FreeMarkerConfiguration;
import com.sun.jersey.api.core.ResourceContext;

public class TemplateResource {
	protected static Logger LOGGER = Logger.getLogger("com.bitplan.camgrab");

	private String templateName = "undefined.ftl";

	/**
	 * @return the templateName
	 */
	public String getTemplateName() {
		return templateName;
	}

	/**
	 * @param templateName
	 *          the templateName to set
	 */
	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}

	/**
	 * @return the templatePath
	 */
	public String getTemplatePath() {
		return templatePath;
	}

	/**
	 * @param templatePath
	 *          the templatePath to set
	 */
	public void setTemplatePath(String templatePath) {
		this.templatePath = templatePath;
	}

	private String templatePath;
	
	/**
	 * get the given icon
	 * 
	 * @param uri
	 * @param name
	 * @return
	 */
	protected String getIconImage(final UriInfo uri,String title, final String name) {
		String image = "<img src='" + uri.getBaseUri() + "images/" + name
				+ "' alt='" + title + "' title='" +title+"'>";
		return image;
	}

	/**
	 * get the given template response
	 * 
	 * @param uri
	 * @param resourceContext
	 * @param fileinfo
	 * @param rootMap
	 * @param templateName
	 * @return
	 * @throws Exception
	 */
	protected Response templateResponse(@Context UriInfo uri,
			@Context ResourceContext resourceContext, Map<String, Object> rootMap,
			String templateName) throws Exception {
		if (this.getTemplatePath() != null)
			FreeMarkerConfiguration.addTemplatePath(this.getTemplatePath());
		FreeMarkerConfiguration.addTemplateClass(FreeMarkerConfiguration.class,
				"/templates");
		String html = FreeMarkerConfiguration.doProcessTemplate(templateName,
				rootMap);
		return Response.ok(html).build();
	}

}
