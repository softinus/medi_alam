package com.plusline.medialarm.type;

import com.plusline.medialarm.R;

/**
 *
 */
public class Drug {


    private Drug() {

    }

    static {
        drugColorIcons = new int[] {
                R.drawable.ic_50,
                R.drawable.ic_51,
                R.drawable.ic_52,
                R.drawable.ic_53,
                R.drawable.ic_54,
                R.drawable.ic_55,
        };
    }


    private static int[] drugColorIcons;

    public static int getDrugIcon(int index) {
        return drugColorIcons[index];
    }

}
