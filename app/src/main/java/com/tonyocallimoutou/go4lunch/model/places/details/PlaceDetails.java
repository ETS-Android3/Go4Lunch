package com.tonyocallimoutou.go4lunch.model.places.details;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.tonyocallimoutou.go4lunch.model.places.RestaurantDetails;

import java.util.List;

public class PlaceDetails {
    @SerializedName("html_attributions")
    @Expose
    private List<Object> htmlAttributions = null;
    @SerializedName("result")
    @Expose
    private RestaurantDetails result;
    @SerializedName("status")
    @Expose
    private String status;

    public List<Object> getHtmlAttributions() {
        return htmlAttributions;
    }

    public void setHtmlAttributions(List<Object> htmlAttributions) {
        this.htmlAttributions = htmlAttributions;
    }

    public RestaurantDetails getResult() {
        return result;
    }

    public void setResult(RestaurantDetails result) {
        this.result = result;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
