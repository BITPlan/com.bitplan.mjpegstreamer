package com.bitplan.camgrab.resources;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import com.sun.jersey.api.core.ResourceContext;

@Path("/camgrab") 
public class CamGrabResource extends TemplateResource {
	
	public CamGrabResource() {
		setTemplateName("camgrab.ftl");
	}
	
	@GET
	public Response camgrab(@Context UriInfo uri,
			@Context ResourceContext resourceContext) throws Exception {
		Map<String, Object> rootMap = new HashMap<String, Object>();
		rootMap.put("title","CamGrabServer");
		rootMap.put("startIcon", getIconImage(uri, "start","start"));
		rootMap.put("rotateIcon", getIconImage(uri, "rotate","paper0r"));
		rootMap.put("settingsIcon", getIconImage(uri, "settings","1394392895_settings"));
		rootMap.put("camurl", SettingsResource.url);
		
		/*
		rootMap.put("downloadicon", getIcon(uri, "document_down"));
		rootMap.put("downloadlink", getActionLink(uri, ActionType.download));
		rootMap.put("browseicon", getIcon(uri, "folder_view"));
		rootMap.put("browselink", getActionLink(uri, ActionType.browse));
		rootMap.put("openicon", getIcon(uri, fileinfo.getFile()));
		rootMap.put("openlink", getActionLink(uri, ActionType.open));
		*/
		Response result = this.templateResponse(uri, resourceContext, rootMap, getTemplateName());
		return result;
	}
	
	@GET
	@Path("eos")
	public String eos(@Context UriInfo uri,
	@Context ResourceContext resourceContext) throws Exception {
		return "EOS Streaming needed";
	}


}