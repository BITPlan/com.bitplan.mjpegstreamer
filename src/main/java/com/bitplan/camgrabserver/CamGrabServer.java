package com.bitplan.camgrabserver;

import com.bitplan.resthelper.RestServerImpl;

/**
 * RestFul server for camera grabbing
 * 
 * @author wf
 * 
 */
public class CamGrabServer extends RestServerImpl {

	public CamGrabServer() {
		settings.setHost("0.0.0.0");
		settings.setPort(8092);
		//settings.setSecure(true);
		//settings.setWantClientAuth(true);
		//settings.setNeedClientAuth(true);
		// System.setProperty("javax.net.debug", "all");
		settings
				.setPackages("com.bitplan.camgrab.resources");
		//String[] filters = { "com.bitplan.smartCRM.SecurityProvider" };
		//settings.setContainerRequestFilters(filters);
	}

	/**
	 * start
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		CamGrabServer cs = new CamGrabServer();
		cs.settings.parseArguments(args);
		cs.startWebServer();
	} // main

}
