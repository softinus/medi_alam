package com.plusline.medialarm.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.plusline.medialarm.R;
import com.plusline.medialarm.management.DrugCaseManager;
import com.plusline.medialarm.type.Cartridge;
import com.plusline.medialarm.type.DrugCase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DrugCaseFragment extends Fragment {
    private static final String tag = "DrugCaseFragment";

    private final int currentDay;
    private View thisView;
    private DrugCase currentDrugCase;

    private TextView morningText;
    private TextView lunchText;
    private TextView eveningText;

    private DrugCartridge morningCartridge;
    private DrugCartridge lunchCartridge;
    private DrugCartridge eveningCartridge;


    //
    //  constructor
    //
    public DrugCaseFragment() {
        currentDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        Log.e(tag, "Wrong call constructor");
    }

    public DrugCaseFragment(int day) {
        this.currentDay = day;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        thisView = inflater.inflate(R.layout.fragment_drug_case, container, false);

        currentDrugCase = DrugCaseManager.get().getCase(currentDay);

        morningText = (TextView) thisView.findViewById(R.id.morning_text);
        lunchText = (TextView) thisView.findViewById(R.id.lunch_text);
        eveningText = (TextView) thisView.findViewById(R.id.evening_text);

        List<View> morningDrugImages = new ArrayList<>();
        morningDrugImages.add(thisView.findViewById(R.id.opened_morning_cartridge));
        morningDrugImages.add(thisView.findViewById(R.id.opened_morning_cartridge_1));
        morningDrugImages.add(thisView.findViewById(R.id.opened_morning_cartridge_2));
        morningDrugImages.add(thisView.findViewById(R.id.opened_morning_cartridge_3));
        morningDrugImages.add(thisView.findViewById(R.id.opened_morning_cartridge_4));

        morningCartridge = new DrugCartridge(morningDrugImages);
        morningCartridge.init(currentDrugCase.getCartridge(Cartridge.Type.Morning));

        List<View> lunchDrugImages = new ArrayList<>();
        lunchDrugImages.add(thisView.findViewById(R.id.opened_lunch_cartridge));
        lunchDrugImages.add(thisView.findViewById(R.id.opened_lunch_cartridge_1));
        lunchDrugImages.add(thisView.findViewById(R.id.opened_lunch_cartridge_2));
        lunchDrugImages.add(thisView.findViewById(R.id.opened_lunch_cartridge_3));
        lunchDrugImages.add(thisView.findViewById(R.id.opened_lunch_cartridge_4));

        lunchCartridge = new DrugCartridge(lunchDrugImages);
        lunchCartridge.init(currentDrugCase.getCartridge(Cartridge.Type.Lunch));

        List<View> eveningDrugImages = new ArrayList<>();
        eveningDrugImages.add(thisView.findViewById(R.id.opened_evening_cartridge));
        eveningDrugImages.add(thisView.findViewById(R.id.opened_evening_cartridge_1));
        eveningDrugImages.add(thisView.findViewById(R.id.opened_evening_cartridge_2));
        eveningDrugImages.add(thisView.findViewById(R.id.opened_evening_cartridge_3));
        eveningDrugImages.add(thisView.findViewById(R.id.opened_evening_cartridge_4));

        eveningCartridge = new DrugCartridge(eveningDrugImages);
        eveningCartridge.init(currentDrugCase.getCartridge(Cartridge.Type.Evening));

        setOpenButtonClickListeners();
        return thisView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    //
    //  약통 오픈과 관련된 인터렉션을 구현한다.
    private void setOpenButtonClickListeners() {
        thisView.findViewById(R.id.morning_touch_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(morningCartridge.opened()) {
                    closeAllCartridge();
                } else {
                    openMorningCartridge();
                }
            }
        });

        thisView.findViewById(R.id.lunch_touch_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(lunchCartridge.opened()) {
                    closeAllCartridge();
                } else {
                    openLunchCartridge();
                }
            }
        });

        thisView.findViewById(R.id.evening_touch_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (eveningCartridge.opened()) {
                    closeAllCartridge();
                } else {
                    openEveningCartridge();
                }
            }
        });
    }

    private void openMorningCartridge() {
        morningCartridge.open();
        lunchCartridge.close();
        eveningCartridge.close();

        morningText.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_morning_on, 0, 0);
        lunchText.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_noon, 0, 0);
        eveningText.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.ic_night);
    }

    private void openLunchCartridge() {
        morningCartridge.close();
        lunchCartridge.open();
        eveningCartridge.close();

        morningText.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_morning, 0, 0);
        lunchText.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_noon_on, 0, 0);
        eveningText.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.ic_night);
    }

    private void openEveningCartridge() {
        morningCartridge.close();
        lunchCartridge.close();
        eveningCartridge.open();

        morningText.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_morning, 0, 0);
        lunchText.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_noon, 0, 0);
        eveningText.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.ic_night_on);
    }

    private void closeAllCartridge() {
        morningCartridge.close();
        lunchCartridge.close();
        eveningCartridge.close();

        morningText.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_morning, 0, 0);
        lunchText.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_noon, 0, 0);
        eveningText.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.ic_night);
    }
}
