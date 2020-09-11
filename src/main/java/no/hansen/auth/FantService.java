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
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
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
    public Response addItem(@FormParam("itemTitle") String itemTitle,  @FormParam("itemPrice") BigDecimal itemPrice,
            @FormParam("itemDesc") String itemDesc) {
        Item itemSale = new Item();
        User itemSeller = this.getCurrentUser();
        itemSale.setItemTitle(itemTitle);
        itemSale.setItemPrice(itemPrice);
        itemSale.setItemDesc(itemDesc);
        itemSale.setItemSeller(itemSeller);
        return Response.ok(em.merge(itemSale)).build();
    }
    
    @GET
    @Path("getitems")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Item> getItems() {
        return em.createNativeQuery("SELECT * FROM ITEM", Item.class).getResultList();
    }
    
    /* TODO */
    @GET
    @Path("photo/{name}")
    public Response getPhoto(@PathParam("name") String name, @QueryParam("width") int width) {
        return Response.ok().build();
    }
    
    @DELETE
    @Path("remove")
    @RolesAllowed(value = {Group.USER})
    public Response deleteItem(@QueryParam("itemid") Long itemId) {
        Item itemToDelete = em.find(Item.class, itemId);
         if (itemToDelete != null) {
             User itemDeleter = this.getCurrentUser();
             if(itemToDelete.getItemSeller().equals(itemDeleter)) { //if the item a user wants to delete is found and user has authority, delete item.
                 em.remove(itemToDelete);
                 return Response.ok().build();
             }
             return Response.status(Response.Status.UNAUTHORIZED).build(); //if the item is found but user does not have authority to delete.
         }
        return Response.notModified().build(); //if no item is found do nothing.
    }
    
    
    public Response purchaseItem(@FormParam("itembuyer") User itemBuyer, 
            @FormParam("itemid") Long itemId) {
        Item itemToPurchase = em.find(Item.class, itemId);
        if (itemToPurchase != null) {
            if (itemToPurchase.getItemBuyer() == null) { //finds buyer if item is found
                itemBuyer = this.getCurrentUser();
                itemToPurchase.setItemBuyer(itemBuyer);
                MailService mail = new MailService();
                mail.sendEmail(itemToPurchase.getItemSeller().getEmail(), 
                        "Your item with ID: " + itemToPurchase.getItemId() + " and title: " + itemToPurchase.getItemTitle() + " has been purchased.",
                        "Your item sold for " + itemToPurchase.getItemPrice() + "kr by: " + itemToPurchase.getItemBuyer().getEmail());
                //Buyer has been found and message sent to seller.
                return Response.ok().build();
            } 
            //Buyer of an item is already known...?
        }
        //Item was not found
        return Response.notModified().build();
    }
}
