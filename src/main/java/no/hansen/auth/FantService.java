/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.hansen.auth;

import java.math.BigDecimal;
import java.util.List;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import lombok.extern.java.Log;
import org.eclipse.microprofile.jwt.JsonWebToken;

/**
 *
 * @author LasseKH
 */
@Path("fantservice")
@Stateless
@Log
public class FantService {
    @PersistenceContext
    EntityManager em;
    @Inject
    AuthenticationService as;
    
    @Inject
    JsonWebToken principal;
    
    @GET
    @Path("currentuser")    
    @RolesAllowed(value = {Group.USER})
    @Produces(MediaType.APPLICATION_JSON)
    private User getCurrentUser() {
        return em.find(User.class, principal.getName());
    }
    
    @POST
    @Path("additem")
    @RolesAllowed(value = {Group.USER})
    @Produces(MediaType.APPLICATION_JSON)
    public Response.ResponseBuilder addItem(@FormParam("itemTitle") String itemTitle,  @FormParam("itemPrice") BigDecimal itemPrice,
            @FormParam("itemDesc") String itemDesc) {
        Item itemSale = new Item();
        User itemSeller = this.getCurrentUser();
        itemSale.setItemTitle(itemTitle);
        itemSale.setItemPrice(itemPrice);
        itemSale.setItemDesc(itemDesc);
        itemSale.setItemSeller(itemSeller);
        return Response.ok(em.merge(itemSale));
    }
    
    @GET
    @Path("getitems")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Item> getItems() {
        return em.createNativeQuery("SELECT * FROM ITEM", Item.class).getResultList();
    }
    
    public Response getPhoto() {
        
    }
    
    @DELETE
    @Path("remove")
    @RolesAllowed(value = {Group.USER})
    public Response delete(@QueryParam("id") Long id) {
        Item itemToDelete = 
        
    }
    
    public Response purchase(@FormParam("itembuyer") User itemBuyer, 
            @FormParam("id") Long id) {
        
    }
}
