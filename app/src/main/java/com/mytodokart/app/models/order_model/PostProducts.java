package com.mytodokart.app.models.order_model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;


public class PostProducts {

    @SerializedName("products_id")
    @Expose
    private int productsId;
    @SerializedName("products_name")
    @Expose
    private String productsName;
    @SerializedName("model")
    @Expose
    private String model;
    @SerializedName("price")
    @Expose
    private String price;
    @SerializedName("final_price")
    @Expose
    private String finalPrice;
    @SerializedName("weight")
    @Expose
    private String weight;
    @SerializedName("on_sale")
    @Expose
    private Boolean onSale;
    @SerializedName("unit")
    @Expose
    private String unit;
    @SerializedName("image")
    @Expose
    private String image;
    @SerializedName("manufacture")
    @Expose
    private String manufacture;
    @SerializedName("categories_id")
    @Expose
    private String categoriesId;
    @SerializedName("categories_name")
    @Expose
    private String categoriesName;
    @SerializedName("subtotal")
    @Expose
    private String subtotal;
    @SerializedName("total")
    @Expose
    private String total;
    @SerializedName("customers_basket_quantity")
    @Expose
    private int customersBasketQuantity;
    @SerializedName("attributes")
    @Expose
    private List<PostProductsAttributes> attributes = new ArrayList<>();
    @SerializedName("products_subscription")
    @Expose
    private String products_subscription;
    @SerializedName("subs_freq")
    @Expose
    private String subs_freq;
    @SerializedName("del_slot")
    @Expose
    private String del_slot ;
    @SerializedName("del_date")
    @Expose
    private String del_date  ;


    public String getsubs_freq() {
        return subs_freq;
    }

    public void setsubs_freq(String subs_freq) {
        this.subs_freq = subs_freq;
    }

    public String getSlot() {
        return del_slot ;
    }

    public void setSlot(String del_slot) {
        this.del_slot  = del_slot ;
    }

    public String getDel_date() {
        return del_date ;
    }

    public void setDel_date(String del_date) {
        this.del_date  = del_date ;
    }
    /**
     *
     * @return
     * The productsId
     */

    public String getproducts_subscription() {
        return products_subscription;
    }

    public void setproducts_subscription(String products_subscription) {
        this.products_subscription = products_subscription;
    }

    public int getProductsId() {
        return productsId;
    }

    /**
     *
     * @param productsId
     * The products_id
     */
    public void setProductsId(int productsId) {
        this.productsId = productsId;
    }

    /**
     *
     * @return
     * The productsName
     */
    public String getProductsName() {
        return productsName;
    }

    /**
     *
     * @param productsName
     * The products_name
     */
    public void setProductsName(String productsName) {
        this.productsName = productsName;
    }

    /**
     *
     * @return
     * The model
     */
    public String getModel() {
        return model;
    }

    /**
     *
     * @param model
     * The model
     */
    public void setModel(String model) {
        this.model = model;
    }

    /**
     *
     * @return
     * The price
     */
    public String getPrice() {
        return price;
    }

    /**
     *
     * @param price
     * The price
     */
    public void setPrice(String price) {
        this.price = price;
    }

    /**
     *
     * @return
     * The finalPrice
     */
    public String getFinalPrice() {
        return finalPrice;
    }

    /**
     *
     * @param finalPrice
     * The final_price
     */
    public void setFinalPrice(String finalPrice) {
        this.finalPrice = finalPrice;
    }

    /**
     *
     * @return
     * The image
     */
    public String getImage() {
        return image;
    }

    /**
     *
     * @param image
     * The image
     */
    public void setImage(String image) {
        this.image = image;
    }

    /**
     *
     * @return
     * The customersBasketQuantity
     */
    public int getCustomersBasketQuantity() {
        return customersBasketQuantity;
    }

    /**
     *
     * @param customersBasketQuantity
     * The customers_basket_quantity
     */
    public void setCustomersBasketQuantity(int customersBasketQuantity) {
        this.customersBasketQuantity = customersBasketQuantity;
    }

    /**
     *
     * @return
     * The attributes
     */
    public List<PostProductsAttributes> getAttributes() {
        return attributes;
    }

    /**
     *
     * @param attributes
     * The attributes
     */
    public void setAttributes(List<PostProductsAttributes> attributes) {
        this.attributes = attributes;
    }


    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public Boolean getOnSale() {
        return onSale;
    }

    public void setOnSale(Boolean onSale) {
        this.onSale = onSale;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getManufacture() {
        return manufacture;
    }

    public void setManufacture(String manufacture) {
        this.manufacture = manufacture;
    }

    public String getCategoriesId() {
        return categoriesId;
    }

    public void setCategoriesId(String categoriesId) {
        this.categoriesId = categoriesId;
    }
    
    public String getCategoriesName() {
        return categoriesName;
    }
    
    public void setCategoriesName(String categoriesName) {
        this.categoriesName = categoriesName;
    }
    
    public String getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(String subtotal) {
        this.subtotal = subtotal;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }
}
