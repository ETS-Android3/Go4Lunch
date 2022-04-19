package com.tonyocallimoutou.go4lunch.ui.setting;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.preference.PreferenceManager;

import com.tonyocallimoutou.go4lunch.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SettingTimePickerFragment extends DialogFragment {

    @BindView(R.id.setting_time_picker)
    TimePicker timePicker;

    private int hour;
    private int minutes;
    private int defaultHour;
    private int defaultMinutes;
    private int hourAlreadyDefine;
    private int minutesAlreadyDefine;

    public static String NO_RESTAURANT = "NO_RESTAURANT";
    public static String RESTAURANT = "RESTAURANT";
    private String type;

    private String title = "";
    private String message = "";

    TimeDialogListener listener;


    public interface TimeDialogListener {
        void onFinishDialog(String type, int hour, int minutes);
    }

    public SettingTimePickerFragment(TimeDialogListener listener, Context context, String s) {
        this.listener = listener;
        if (s.equals(NO_RESTAURANT)) {
            title = context.getString(R.string.setting_title_time_picker_no_restaurant);
            message = context.getString(R.string.setting_message_time_picker_no_restaurant);
            defaultHour = 11;
            defaultMinutes =0;
            hourAlreadyDefine = PreferenceManager.getDefaultSharedPreferences(context).getInt(context.getString(R.string.shared_preference_hour_no_restaurant),11);
            minutesAlreadyDefine = PreferenceManager.getDefaultSharedPreferences(context).getInt(context.getString(R.string.shared_preference_minutes_no_restaurant),0);
        }
        else if (s.equals(RESTAURANT)) {
            title = context.getString(R.string.setting_title_time_picker_restaurant);
            message = context.getString(R.string.setting_message_time_picker_restaurant);
            defaultHour = 12;
            defaultMinutes = 0;
            hourAlreadyDefine = PreferenceManager.getDefaultSharedPreferences(context).getInt(context.getString(R.string.shared_preference_hour_restaurant),12);
            minutesAlreadyDefine = PreferenceManager.getDefaultSharedPreferences(context).getInt(context.getString(R.string.shared_preference_minutes_restaurant),0);
        }
        else {
            Log.w("TAG", "SettingTimePickerFragment: " );
        }
        type = s;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_setting_dialog_time,null);

        ButterKnife.bind(this,view);

        timePicker.setIs24HourView(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            timePicker.setHour(hourAlreadyDefine);
            timePicker.setMinute(minutesAlreadyDefine);
        }

        return new AlertDialog.Builder(getActivity())
                .setView(view)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(getString(R.string.setting_save_time_picker), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            hour = timePicker.getHour();
                            minutes = timePicker.getMinute();
                        }
                        else {
                            hour = timePicker.getCurrentHour();
                            minutes = timePicker.getCurrentMinute();
                        }

                        listener.onFinishDialog(type, hour,minutes);
                    }
                })
                .setNegativeButton(getString(R.string.setting_default_time_picker), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        hour = defaultHour;
                        minutes = defaultMinutes;

                        listener.onFinishDialog(type,hour,minutes);
                    }
                })
                .create();
    }
}
