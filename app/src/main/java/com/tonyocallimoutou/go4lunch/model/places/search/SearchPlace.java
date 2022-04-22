package com.tonyocallimoutou.go4lunch.model.places.search;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class SearchPlace {
    @SerializedName("predictions")
    @Expose
    private List<Prediction> predictions = new ArrayList<Prediction>();
    @SerializedName("status")
    @Expose
    private String status;

    /**
     *
     * @return
     * The results
     */
    public List<Prediction> getResults() {
        return predictions;
    }

    /**
     *
     * @param predictions
     * The results
     */
    public void setResults(List<Prediction> predictions) {
        this.predictions = predictions;
    }

    /**
     *
     * @return
     * The status
     */
    public String getStatus() {
        return status;
    }

    /**
     *
     * @param status
     * The status
     */
    public void setStatus(String status) {
        this.status = status;
    }

}
