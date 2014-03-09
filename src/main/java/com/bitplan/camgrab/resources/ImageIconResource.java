package com.bitplan.camgrab.resources;
/**
 * Copyright (C) 2013 BITPlan GmbH
 *
 * Pater-Delp-Str. 1
 * D-47877 Willich-Schiefbahn
 *
 * http://www.bitplan.com
 * 
 */
import java.io.InputStream;
import java.net.URISyntaxException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

@Path("images")
/**
 * Icon Access
 * @author wf
 */
public class ImageIconResource {

	@GET
  @Path("{ext}")
  @Produces("image/png")
  public InputStream getImageRepresentation(@PathParam("ext") String ext) throws Exception {
		InputStream icon=getIcon(ext);
		return icon;
	}
	
	/**
	 * get the Icon for the given extension
	 * @param name
	 * @return
	 * @throws URISyntaxException
	 */
	public static InputStream getIcon(String name) throws URISyntaxException{
		InputStream iconImage	= ImageIconResource.class.getResourceAsStream("/images/"+name+".png");
		if (iconImage==null)
			iconImage=ImageIconResource.class.getResourceAsStream("/images/1394398258_stock_unknown.png");

		return iconImage;
	}
}
