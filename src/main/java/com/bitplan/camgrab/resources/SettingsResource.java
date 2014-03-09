package com.bitplan.camgrab.resources;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import com.sun.jersey.api.core.ResourceContext;

@Path("/settings") 
public class SettingsResource extends TemplateResource {
	public static String deviceName="GalaxyNote";
	public static String url="camgrab/eos";
	
	/**
	 * the SettingsSource
	 */
	public SettingsResource() {
		super();
		setTemplateName("settings.ftl");
	}

	@GET
	public Response settingsMenu(@Context UriInfo uri,
			@Context ResourceContext resourceContext) throws Exception {
		Map<String, Object> rootMap = new HashMap<String, Object>();
		rootMap.put("title","CamGrabServer Settings");
		// 	https://www.iconfinder.com/search/?q=iconset:developerkit
		rootMap.put("homeIcon", getIconImage(uri, "home","1394400199_Home"));		
		// https://www.iconfinder.com/search/?q=iconset:musthave
		rootMap.put("checkIcon", getIconImage(uri, "","Check64"));		
		rootMap.put("galaxyNoteIcon", getIconImage(uri,"Samsung Galaxy Note", "galaxynote"));
		rootMap.put("eosIcon", getIconImage(uri, "Canon EOS 1000D","eos1000d"));
		rootMap.put("dscIcon", getIconImage(uri, "D-Link DCS 930L", "DCS-930L_front"));
		rootMap.put("deviceName",deviceName);
		Response result = this.templateResponse(uri, resourceContext, rootMap, getTemplateName());
		return result;
	}
	
	@GET
	@Path("device/{devicename}")
	public Response settings(@Context UriInfo uri,
			@Context ResourceContext resourceContext, @PathParam("devicename")  String pDevicename,@QueryParam("url")String camurl) throws Exception {
		deviceName=pDevicename;
		url=camurl;
		return settingsMenu(uri,resourceContext);
	}

}
