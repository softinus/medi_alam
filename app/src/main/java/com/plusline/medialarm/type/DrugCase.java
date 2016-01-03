package com.plusline.medialarm.type;

import android.database.Cursor;

import com.plusline.medialarm.ui.Convert;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 *  약통 클래스, 하나의 약통에는 세개의 카트리지가 있다.
 */
public class DrugCase {

    private Calendar todayForCase;
    private Map<Cartridge.Type, Cartridge> cartridges = new HashMap<>();

    //
    //
    //
    private DrugCase() {

    }

    public Calendar getDayInfo() {
        return todayForCase;
    }

    public Cartridge getCartridge(Cartridge.Type type) {
        return cartridges.get(type);
    }

    @Override
    public String toString() {
        return "DrugCase" + Convert.toStr(todayForCase, "[yyyy-MM-dd]");
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof DrugCase) {
            DrugCase other = (DrugCase) o;
            return todayForCase.get(Calendar.DAY_OF_MONTH) == other.todayForCase.get(Calendar.DAY_OF_MONTH);
        }

        return false;
    }

    @Override
    public int hashCode() {
        return todayForCase.get(Calendar.DAY_OF_MONTH);
    }

    public static DrugCase create(Calendar calendar, Cursor cartridgeCursor) {
        DrugCase drugCase = new DrugCase();

        drugCase.todayForCase = calendar;
        while(cartridgeCursor.moveToNext()) {
            Cartridge cartridge = Cartridge.create(cartridgeCursor);
            drugCase.cartridges.put(cartridge.getType(), cartridge);
        }

        return drugCase;
    }


}
