package com.bitplan.camgrab.resources;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.io.FileUtils;

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
	
	/**
	 * fast channel copy
	 * http://thomaswabner.wordpress.com/2007/10/09/fast-stream-copy-using-javanio-channels/
	 * @param src
	 * @param dest
	 * @throws IOException
	 */
	public static void fastChannelCopy(final ReadableByteChannel src, final WritableByteChannel dest) throws IOException {
    final ByteBuffer buffer = ByteBuffer.allocateDirect(16 * 1024);
    while (src.read(buffer) != -1) {
      // prepare the buffer to be drained
      buffer.flip();
      // write to the channel, may block
      dest.write(buffer);
      // If partial transfer, shift remainder down
      // If buffer is empty, same as doing clear()
      buffer.compact();
    }
    // EOF will leave buffer in fill state
    buffer.flip();
    // make sure the buffer is fully drained.
    while (buffer.hasRemaining()) {
      dest.write(buffer);
    }
  }
	@GET
	@Path("eos")
	@Produces("image/jpeg")
	public Response eos(@Context UriInfo uri,
	@Context ResourceContext resourceContext) throws Exception {
		// http://stackoverflow.com/questions/14410344/jersey-rest-support-resume-media-streaming
		// http://stackoverflow.com/questions/12012724/jersey-example-of-using-streamingoutput-as-response-entity
		// http://stackoverflow.com/questions/1595945/creating-my-own-mjpeg-stream
		StreamingOutput stream = new StreamingOutput() {
	    @Override
	    public void write(OutputStream os) throws IOException,
	    WebApplicationException {
	    	// URL movieUrl = ClassLoader.getSystemResource("testmovie/movie.mjpg");	    	
	    	// ReadableByteChannel rbc = Channels.newChannel(movieUrl.openStream());
	    	File movieFile=new File("src/test/resources/testmovie/movie.mjpg");
	    	ReadableByteChannel rbc = Channels.newChannel(new FileInputStream(movieFile));
	  	  WritableByteChannel outc = Channels.newChannel(os);
	    	CamGrabResource.fastChannelCopy(rbc, outc);
	    	/*
	      Writer writer = new BufferedWriter(new OutputStreamWriter(os));
	      writer.flush();
	      */
	    }
	  };
	  return Response.ok(stream).build();

	}


}
