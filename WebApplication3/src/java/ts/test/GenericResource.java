/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ts.test;

import java.util.Arrays;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import ts.games.words.core.Board;
import ts.games.words.core.BoardItems;
import ts.games.words.core.GameController;
import ts.games.words.core.Player;
import ts.games.words.core.PlayerController;

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
     *
     * @return an instance of Board
     */
    @GET
    @Produces("application/json")
    public BoardItems getJson() {
        return null;
    }

    /**
     * Starts new game
     *
     * @return game data
     * @throws java.lang.Exception
     */
    @GET
    @Path("test2")
    @Produces("application/json")
    public Board getJson2() throws Exception {
        Board board = new Board(12, Arrays.asList("ლეოპარდი", "იაგუარი", "ვეფხვი", "ანტილოპა", "მგელი", "გიენა", "ლომი", "კურდღელი", "სპილო", "კატა"), 1);
        //Board board = GameController.getCurrentInstance().startGame("1", 1, 12, 10);
        return board;
    }

    @POST
    @Path("start/{userId}/{categoryId}")
    @Produces("application/json")
    public Board createGame(@PathParam("userId") String userId, @PathParam("categoryId") int categoryId) throws Exception {
        Board board = GameController.getCurrentInstance().startGame(userId, categoryId, 12, 10);
        return board;
    }

    @POST
    @Path("end/{gameId}")
    @Produces("application/json")
    public void endGame(@PathParam("gameId") int gameId) throws Exception {
        GameController.getCurrentInstance().endGame(gameId);
    }

    @GET
    @Path("besttime/{userId}")
    @Produces("application/json")
    public String getPlayerBestTime(@PathParam("userId") String userId) throws Exception {
        return String.valueOf(PlayerController.getCurrentInstance().getPlayerBestTime(userId));
    }

    @PUT
    @Path("store")
    @Consumes("application/x-www-form-urlencoded")
    @Produces("application/json")
    public void storePlayerInfo(
            @FormParam("playerId") String playerId,
            @FormParam("firstName") String firstName,
            @FormParam("lastName") String lastName,
            @FormParam("name") String name,
            @FormParam("gender") String gender,
            @FormParam("email") String email,
            @FormParam("verified") String verified) throws Exception {
        Player player = new Player(playerId, firstName, lastName, name, gender, email, verified);
        PlayerController.getCurrentInstance().storePlayerData(player);
    }

}
