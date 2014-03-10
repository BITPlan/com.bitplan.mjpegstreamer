package com.bitplan.camgrab.resources;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import javax.ws.rs.core.UriInfo;

import com.sun.jersey.api.core.ResourceContext;

/**
 * EOS Camera access
 * @author wf
 *
 */
@Path("/camgrab/eos")
public class EOSResource extends TemplateResource {

	/**
	 * create a new EOS Resource
	 */
	public EOSResource() {
		super();
		setTemplateName("eos.ftl");
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
	@Path("preview")
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
	    	fastChannelCopy(rbc, outc);
	    	/*
	      Writer writer = new BufferedWriter(new OutputStreamWriter(os));
	      writer.flush();
	      */
	    }
	  };
	  return Response.ok(stream).build();

	}

	
}
