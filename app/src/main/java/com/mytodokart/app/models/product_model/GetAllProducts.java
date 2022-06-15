package com.mytodokart.app.models.product_model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import com.mytodokart.app.models.filter_model.post_filters.FiltersAttributes;
import com.mytodokart.app.models.filter_model.post_filters.FiltersPrice;


public class GetAllProducts {

    @SerializedName("page_number")
    @Expose
    private int pageNumber;
    @SerializedName("user_zip")
    @Expose
    private String user_zip;
    @SerializedName("language_id")
    @Expose
    private int languageId;
    @SerializedName("customers_id")
    @Expose
    private String customersId;
    @SerializedName("categories_id")
    @Expose
    private String categoriesId;
    @SerializedName("products_id")
    @Expose
    private String productsId;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("filters")
    @Expose
    private List<FiltersAttributes> filters = null;
    @SerializedName("price")
    @Expose
    private FiltersPrice price;
    @SerializedName("currency_code")
    @Expose
    private String currencyCode;

    @SerializedName("vendors_id")
    @Expose
    private String vendor_id;
    @SerializedName("limit")
    @Expose
    private int limit;

    public String getVendor_id() {
        return vendor_id;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public void setVendor_id(String vendor_id) {
        this.vendor_id = vendor_id;
    }
    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public int getLanguageId() {
        return languageId;
    }

    public void setLanguageId(int languageId) {
        this.languageId = languageId;
    }

    public String getCustomersId() {
        return customersId;
    }

    public void setCustomersId(String customersId) {
        this.customersId = customersId;
    }

    public String getUser_zip() {
        return user_zip;
    }

    public void setUser_zip(String user_zip) {
        this.user_zip = user_zip;
    }

    public String getCategoriesId() {
        return categoriesId;
    }

    public void setCategoriesId(String categoriesId) {
        this.categoriesId = categoriesId;
    }

    public String getProductsId() {
        return productsId;
    }

    public void setProductsId(String productsId) {
        this.productsId = productsId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<FiltersAttributes> getFilters() {
        return filters;
    }

    public void setFilters(List<FiltersAttributes> filters) {
        this.filters = filters;
    }

    public FiltersPrice getPrice() {
        return price;
    }

    public void setPrice(FiltersPrice price) {
        this.price = price;
    }

}