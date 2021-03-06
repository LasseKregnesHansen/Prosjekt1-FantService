package no.hansen.auth;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import javax.json.bind.annotation.JsonbTransient;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import static no.hansen.auth.Item.FIND_ALL_ITEMS;
import static no.hansen.auth.Item.FIND_ITEM_BY_IDS;

/**
 *
 * @author LasseKH
 */
@AllArgsConstructor
@Data @Table(name = "AITEM")
@Entity
@NoArgsConstructor
@NamedQuery(name = FIND_ITEM_BY_IDS, query = "select i from Item i order by i.itemId")
@NamedQuery(name = FIND_ALL_ITEMS, query = "select i from Item i")
public class Item implements Serializable {
    public static final String FIND_ITEM_BY_IDS = "Item.findItemByIds";
    public static final String FIND_ALL_ITEMS = "Item.findAllItems";
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long itemId;
    
    @NotEmpty
    private String itemTitle;
    
    @NotNull
    private BigDecimal itemPrice;
    
    private String itemDesc;
    
    @ManyToOne
    private User itemBuyer;
    
    @ManyToOne
    @JoinColumn(nullable = false)
    private User itemSeller;
    
    /*@OneToMany
    private List<ItemImages> itemImage;*/
    
    public Long getItemId(){
        return itemId;
    }
    
    public String getItemTitle() {
        return itemTitle;
    }
    
    public BigDecimal getItemPrice() {
        return itemPrice;
    }
    
    public String getItemDesc() {
        return itemDesc;
    }
    
    public User getItemBuyer() {
        return itemBuyer;
    }
    
    public User getItemSeller() {
        return itemSeller;
    }
}
