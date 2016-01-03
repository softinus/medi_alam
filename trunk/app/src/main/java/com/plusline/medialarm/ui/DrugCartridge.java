package com.plusline.medialarm.ui;

import android.view.View;
import android.widget.ImageView;

import com.plusline.medialarm.R;
import com.plusline.medialarm.type.Cartridge;
import com.plusline.medialarm.type.Drug;
import com.plusline.medialarm.type.DrugInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * 약통의 카트리지 UI 클래스
 */
public class DrugCartridge {
    private static final String tag = "DrugCartridge";

    private Cartridge cartridge;
    private List<View> drugImageViews = new ArrayList<>();

    //
    //
    //
    public DrugCartridge(List<View> drugImages) {
        drugImageViews.addAll(drugImages);
        close();
    }


    public void open() {
        List<DrugInfo> drugs = cartridge.getDrugs();

        int position = (4 < drugs.size()) ? 4 : drugs.size();
        for(int i=0; i<drugImageViews.size(); ++i) {
            drugImageViews.get(i).setVisibility((i == position) ? View.VISIBLE : View.INVISIBLE);
//            drugImageViews.get(i).setVisibility(View.INVISIBLE);
        }
    }

    public void close() {
        for(View v : drugImageViews) {
            v.setVisibility(View.INVISIBLE);
        }
    }

    public boolean opened() {
        for(View v : drugImageViews) {
            if(View.VISIBLE == v.getVisibility()) {
                return true;
            }
        }
        return false;
    }


    public void init(Cartridge cartridge) {
        this.cartridge = cartridge;
    }
}
