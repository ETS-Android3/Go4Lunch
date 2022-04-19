package com.tonyocallimoutou.go4lunch.ui.autocomplete;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tonyocallimoutou.go4lunch.R;
import com.tonyocallimoutou.go4lunch.model.places.search.Prediction;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AutocompleteRecyclerViewAdapter  extends RecyclerView.Adapter<AutocompleteRecyclerViewAdapter.ViewHolder>{

    private final List<Prediction> mPredictions;
    private final Context mContext;
    private final PredictionItemClickListener mPredictionItemClickListener;

    public AutocompleteRecyclerViewAdapter(Context context, List<Prediction> predictions, PredictionItemClickListener predictionItemClickListener) {
        this.mContext = context;
        this.mPredictions = predictions;
        this.mPredictionItemClickListener = predictionItemClickListener;
    }

    @NonNull
    @Override
    public AutocompleteRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_autocomplete_item, null);
        return new AutocompleteRecyclerViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AutocompleteRecyclerViewAdapter.ViewHolder holder, int position) {
        if (getItemCount() == 0) {
            holder.predictionText.setText(mContext.getString(R.string.no_prediction));
        }
        else {

            Prediction prediction = mPredictions.get(position);

            holder.predictionText.setText(prediction.getDescription());
        }
    }

    @Override
    public int getItemCount() {
        return mPredictions.size();
    }


    public interface PredictionItemClickListener{
        void onPredictionItemClick(Prediction prediction);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.prediction_text)
        TextView predictionText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    mPredictionItemClickListener.onPredictionItemClick(mPredictions.get(position));
                }
            });
        }
    }
}
