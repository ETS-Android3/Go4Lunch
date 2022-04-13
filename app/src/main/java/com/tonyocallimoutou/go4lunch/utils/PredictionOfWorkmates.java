package com.tonyocallimoutou.go4lunch.utils;

import com.tonyocallimoutou.go4lunch.model.User;
import com.tonyocallimoutou.go4lunch.model.places.search.Prediction;
import com.tonyocallimoutou.go4lunch.model.places.search.StructuredFormatting;

import java.util.ArrayList;
import java.util.List;

public class PredictionOfWorkmates {

    public static List<Prediction> getWorkmates (String input, List<User> workmates) {

        // List Workmates
        input = input.toUpperCase();

        List<User> workmatesPrediction = new ArrayList<>();


        for (User user : workmates) {
            String name = user.getUsername().toUpperCase();
            if (name.startsWith(input)) {
                workmatesPrediction.add(user);
            }
        }

        // List Prediction

        List<Prediction> predictions = new ArrayList<>();

        for (User user : workmatesPrediction) {
            Prediction prediction = new Prediction();
            StructuredFormatting structuredFormatting = new StructuredFormatting();
            structuredFormatting.setMainText(user.getUsername());

            prediction.setStructuredFormatting(structuredFormatting);
            prediction.setPlaceId(user.getUid());

            predictions.add(prediction);
        }

        return predictions;


    }
}
