package net.andreaskluth;

import java.io.IOException;
import java.io.OutputStream;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @RestController
    @RequestMapping("/")
    public static class MvcController {

        @RequestMapping("/")
        public ResponseEntity<String> mixed() {
            return ResponseEntity.ok().body("OK+");
        }

    }

    @Path("/endless")
    public static class EndlessJsonResource {

        private ObjectMapper objectMapper;

        @Autowired
        public EndlessJsonResource(ObjectMapper mapper) {
            this.objectMapper = mapper;
        }

        @GET
        public Response endless() {
            StreamingOutput stream = new StreamingOutput() {
                @Override
                public void write(OutputStream os)
                        throws IOException, WebApplicationException {
                    JsonGenerator jg = objectMapper.getFactory().createGenerator(os,
                            JsonEncoding.UTF8);
                    jg.writeStartObject();
                    jg.writeFieldName("endless");
                    jg.writeStartArray();

                    for (int i = 0; i < 10000; i++) {
                        jg.writeString(Integer.toString(i));
                    }

                    jg.writeEndArray();
                    jg.writeEndObject();
                    jg.flush();
                    jg.close();
                }
            };
            return Response.ok().entity(stream).type(MediaType.APPLICATION_JSON).build();
        }
    }

    @Component
    @ApplicationPath("/api")
    public static class JerseyConfig extends ResourceConfig {

        public JerseyConfig() {
            
            register(EndlessJsonResource.class);
        }

    }

}
