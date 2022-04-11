package com.tonyocallimoutou.go4lunch.model.places;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class OpeningHours {

    @SerializedName("open_now")
    @Expose
    private Boolean openNow;
    @SerializedName("periods")
    @Expose
    private List<Period> periods = null;
    @SerializedName("weekday_text")
    @Expose
    private List<String> weekdayText = new ArrayList<String>();

    /**
     *
     * @return
     * The openNow
     */
    public Boolean getOpenNow() {
        return openNow;
    }

    /**
     *
     * @param periods
     * The open_now
     */
    public void setPeriods(List<Period> periods) {
        this.periods = periods;
    }

    /**
     *
     * @return
     * The periods
     */
    public List<Period> getPeriods() {
        return periods;
    }

    /**
     *
     * @param openNow
     * The open_now
     */
    public void setOpenNow(Boolean openNow) {
        this.openNow = openNow;
    }

    /**
     *
     * @return
     * The weekdayText
     */
    public List<String> getWeekdayText() {
        return weekdayText;
    }

    /**
     *
     * @param weekdayText
     * The weekday_text
     */
    public void setWeekdayText(List<String> weekdayText) {
        this.weekdayText = weekdayText;
    }

    // Constructor for Test

    public OpeningHours() {
    }

    public OpeningHours(Boolean openNow, List<Period> periods) {
        this.openNow = openNow;
        this.periods = periods;
    }
}
