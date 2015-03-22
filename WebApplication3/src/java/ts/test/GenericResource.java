/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ts.test;

import java.util.Arrays;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import ts.games.words.core.Board;
import ts.games.words.core.BoardItems;
import ts.games.words.core.GameController;

/**
 * REST Web Service
 *
 * @author lasha
 */
@Path("generic")
public class GenericResource {

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of GenericResource
     */
    public GenericResource() {
    }

    /**
     * Retrieves representation of an instance of ts.test.GenericResource
     * @return an instance of Board
     */
    @GET
    @Produces("application/json")
    public BoardItems getJson() {
        return null;
    }

    /**
     *
     * @return
     */
    @GET
    @Path("test2")
    @Produces("application/json")
    public Board getJson2() {
        //Board board = new Board(12, Arrays.asList("ლეოპარდი", "იაგუარი", "ვეფხვი", "ანტილოპა", "მგელი", "გიენა", "ლომი", "კურდღელი", "სპილო", "კატა"));
        Board board = GameController.getCurrentInstance().startGame("1", 1, 12, 10);
        return board;
    }
    
    /**
     * PUT method for updating or creating an instance of GenericResource
     * @param content representation for the resource
     * @return an HTTP response with content of the updated or created resource.
     */
    @PUT
    @Consumes("application/xml")
    public void putXml(Board content) {
    }
}
