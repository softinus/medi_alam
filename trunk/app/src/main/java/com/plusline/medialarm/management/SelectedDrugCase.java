package com.plusline.medialarm.management;

import android.util.Log;

import com.plusline.medialarm.type.Alarm;
import com.plusline.medialarm.type.Cartridge;
import com.plusline.medialarm.type.DrugCase;
import com.plusline.medialarm.type.DrugInfo;

import java.util.HashSet;
import java.util.Set;

/**
 * 앱에서 선택하는 약통을 관리한다. 약통의 선택은 메인 화면에서 화살표를 좌/우로 움직여서 약통 선택
 */
public class SelectedDrugCase {
    private static final String tag = "SelectedDrugCase";

    public interface OnChangeSelectedCaseListener {
        void onChanged();
    }

    private boolean isModify = false;
    private DrugCase selectedCase = null;
    private Cartridge selectedCartridge = null;
    private DrugInfo currentDrugInfo = null;
    private Set<OnChangeSelectedCaseListener> changedCaseListeners = new HashSet<>();

    //
    //  constructor
    //
    private SelectedDrugCase() {

    }

    public boolean isModify() {
        return isModify;
    }

    public void setIsModify(boolean isModify) {
        this.isModify = isModify;
    }


    // 요일별 약통 선택(메인화면에서 선택)
    public void select(int dayOfWeek) {
        selectedCase = DrugCaseManager.get().getCase(dayOfWeek);
        Log.d(tag, "Select the drug case: " + selectedCase);

        for(OnChangeSelectedCaseListener listener : changedCaseListeners) {
            listener.onChanged();
        }
    }

    public DrugCase getSelectedCase() {
        return selectedCase;
    }


    // 선택된 약통의 카트리지 선택
    public void select(Cartridge.Type type) {
        selectedCartridge = selectedCase.getCartridge(type);
        Log.d(tag, "Select the cartridge: " + selectedCartridge);
    }

    public Cartridge getSelectedCartridge() {
        return selectedCartridge;
    }


    // 약물 생성 / 조회
    public void setCurrentDrug(DrugInfo drugInfo) {
        currentDrugInfo = drugInfo;
    }

    // 임시저장 약물 삭제
    public void removeCurrentDrug() {
        if(null == currentDrugInfo) {
            return;
        }

        int alarmNo = currentDrugInfo.getAlarmNo();
        if(-1 == alarmNo) {
            return;
        }

        AlarmManager.get().removeAlarm(alarmNo);
        currentDrugInfo = null;
    }

    public DrugInfo getCurrentDrugInfo() {
        return currentDrugInfo;
    }


    public void addOnChangeSelectedCaseListener(OnChangeSelectedCaseListener listener) {
        changedCaseListeners.add(listener);
    }

    public void removeOnChangeSelectedCaseListener(OnChangeSelectedCaseListener listener) {
        changedCaseListeners.remove(listener);
    }

    public Alarm getCurrentAlarm() {
        return AlarmManager.get().getAlarm(currentDrugInfo.getAlarmNo());
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    // static methods

    private static SelectedDrugCase instance = new SelectedDrugCase();
    public static SelectedDrugCase get() {
        return instance;
    }
}
