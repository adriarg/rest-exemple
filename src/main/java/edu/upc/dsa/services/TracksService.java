package edu.upc.dsa.services;

import edu.upc.dsa.TracksManager;
import edu.upc.dsa.TracksManagerImpl;
import edu.upc.dsa.models.Track;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.log4j.Logger;

import javax.ws.rs.*;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

@Api(value = "/tracks", description = "Endpoint to Track Service")
@Path("/tracks")
public class TracksService {

    final static Logger logger = Logger.getLogger(TracksService.class);

    private TracksManager tm;

    public TracksService() {
        this.tm = TracksManagerImpl.getInstance();
        if (tm.size() == 0) {
            this.tm.addTrack("La Barbacoa", "Georgie Dann");
            this.tm.addTrack("Despacito", "Luis Fonsi");
            this.tm.addTrack("Enter Sandman", "Metallica");
        }
        logger.info("TracksService initialized with default tracks.");
    }

    @GET
    @ApiOperation(value = "get all Track", notes = "Retrieve all tracks")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Successful", response = Track.class, responseContainer = "List"),
    })
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTracks() {
        List<Track> tracks = this.tm.findAll();
        logger.info("Getting all tracks.");
        GenericEntity<List<Track>> entity = new GenericEntity<List<Track>>(tracks) {};
        return Response.status(201).entity(entity).build();
    }

    @GET
    @ApiOperation(value = "get a Track", notes = "Retrieve a track by ID")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Successful", response = Track.class),
            @ApiResponse(code = 404, message = "Track not found")
    })
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTrack(@PathParam("id") String id) {
        logger.info("Getting track with ID: " + id);
        Track t = this.tm.getTrack(id);
        if (t == null) {
            logger.warn("Track with ID: " + id + " not found.");
            return Response.status(404).build();
        }
        return Response.status(201).entity(t).build();
    }

    @DELETE
    @ApiOperation(value = "delete a Track", notes = "Delete a track by ID")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Successful"),
            @ApiResponse(code = 404, message = "Track not found")
    })
    @Path("/{id}")
    public Response deleteTrack(@PathParam("id") String id) {
        logger.info("Deleting track with ID: " + id);
        Track t = this.tm.getTrack(id);
        if (t == null) {
            logger.warn("Track with ID: " + id + " not found.");
            return Response.status(404).build();
        }
        this.tm.deleteTrack(id);
        return Response.status(201).build();
    }

    @PUT
    @ApiOperation(value = "update a Track", notes = "Update a track")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Successful"),
            @ApiResponse(code = 404, message = "Track not found")
    })
    @Path("/")
    public Response updateTrack(Track track) {
        logger.info("Updating track: " + track);
        Track t = this.tm.updateTrack(track);
        if (t == null) {
            logger.warn("Track not found for update.");
            return Response.status(404).build();
        }
        return Response.status(201).build();
    }

    @POST
    @ApiOperation(value = "create a new Track", notes = "Create a new track")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Successful", response = Track.class),
            @ApiResponse(code = 500, message = "Validation Error")
    })
    @Path("/")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response newTrack(Track track) {
        if (track.getSinger() == null || track.getTitle() == null) {
            logger.error("Validation Error: Track has null fields.");
            return Response.status(500).entity(track).build();
        }
        this.tm.addTrack(track);
        logger.info("New track created: " + track);
        return Response.status(201).entity(track).build();
    }
}
