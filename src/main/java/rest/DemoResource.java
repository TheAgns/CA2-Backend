package rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dtos.*;
import entities.User;

import java.io.IOException;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import javax.annotation.security.RolesAllowed;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;
import utils.EMF_Creator;
import utils.HttpUtils;
import utils.SetupTestUsers;

/**
 * @author lam@cphbusiness.dk
 */
@Path("info")
public class DemoResource {
    Gson gson = new GsonBuilder().setPrettyPrinting().create();

    
    private static final EntityManagerFactory EMF = EMF_Creator.createEntityManagerFactory();
    @Context
    private UriInfo context;

    @Context
    SecurityContext securityContext;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getInfoForAll() {
        return "{\"msg\":\"Hello anonymous!!\"}";
    }

    //Just to verify if the database is setup
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("all")
    public String allUsers() {

        EntityManager em = EMF.createEntityManager();
        try {
            TypedQuery<User> query = em.createQuery ("select u from User u",entities.User.class);
            List<User> users = query.getResultList();
            return "[" + users.size() + "]";
        } finally {
            em.close();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("user")
    @RolesAllowed("user")
    public String getFromUser() {
        String thisuser = securityContext.getUserPrincipal().getName();
        return "{\"msg\": \"Hello to User: " + thisuser + "\"}";
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("admin")
    @RolesAllowed("admin")
    public String getFromAdmin() {
        String thisuser = securityContext.getUserPrincipal().getName();
        return "{\"msg\": \"Hello to (admin) User: " + thisuser + "\"}";
    }
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("populateUsers")
    public String populate() {
        SetupTestUsers.populateUsers();
       return "You have been populated";
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("fetchSingle")
    public String fetchSingle() throws IOException {
    String response = HttpUtils.fetchData("https://catfact.ninja/fact");
        CatDTO catDTO = gson.fromJson(response,CatDTO.class);
        return gson.toJson(catDTO);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("fetchSeq")
    @RolesAllowed("user")
    public String fetchSequentially() throws IOException {
        LocalTime begin = LocalTime.now();
        String cat = HttpUtils.fetchData("https://catfact.ninja/fact");
        String boredom = HttpUtils.fetchData("https://www.boredapi.com/api/activity");
        String dog = HttpUtils.fetchData("https://dog.ceo/api/breeds/image/random");
        String ip = HttpUtils.fetchData("https://api.ipify.org/?format=json");
        CatDTO catDTO = gson.fromJson(cat,CatDTO.class);
        BoredomDTO boredomDTO = gson.fromJson(boredom,BoredomDTO.class);
        DogDTO dogDTO = gson.fromJson(dog,DogDTO.class);
        IpDTO ipDTO = gson.fromJson(ip,IpDTO.class);

        CombinedDTO combinedDTO = new CombinedDTO(boredomDTO,catDTO,dogDTO,ipDTO);

        LocalTime end = LocalTime.now();
        long result = ChronoUnit.MILLIS.between(begin,end);


        return gson.toJson(combinedDTO) /*+ "sequantially fetch: " +  result*/;    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("fetchParallel")
    @RolesAllowed("admin")
    public String fetchParallel() throws InterruptedException {
        LocalTime begin = LocalTime.now();
        String[] str = {
                "https://catfact.ninja/fact",
                "https://www.boredapi.com/api/activity",
                "https://dog.ceo/api/breeds/image/random",
                "https://api.ipify.org/?format=json"
        };

        CatDTO catDTO = null;
        BoredomDTO boredomDTO = null;
        DogDTO dogDTO = null;
        IpDTO ipDTO = null;
        CombinedDTO combinedDTO = null;

/*
WebApplicationException(String message, int status)
Construct a new instance with a blank message and specified HTTP status code.
*/
 try {


        List<String> JsonResponse = HttpUtils.fetchMany(str);
        catDTO = gson.fromJson(JsonResponse.get(0),CatDTO.class);
        boredomDTO = gson.fromJson(JsonResponse.get(1),BoredomDTO.class);
        dogDTO = gson.fromJson(JsonResponse.get(2),DogDTO.class);
        ipDTO = gson.fromJson(JsonResponse.get(3),IpDTO.class);

        combinedDTO = new CombinedDTO(boredomDTO,catDTO,dogDTO,ipDTO);

 } catch (Exception e){
     throw new WebApplicationException("Api failed", 500);
 }
        LocalTime end = LocalTime.now();
        long result = ChronoUnit.MILLIS.between(begin,end);

        return gson.toJson(combinedDTO);
    }
}