package com.plusline.medialarm.ui;

import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.plusline.medialarm.R;
import com.plusline.medialarm.management.PassedAlarmManager;
import com.plusline.medialarm.management.TakeLogManager;
import com.plusline.medialarm.type.DrugInfo;

/**
 * 약 복용 여부 질문 대화상자
 */
public class QuestionDialog {

    private View thisDialog;
    private DrugInfo currentDrug;

    //
    //
    //
    public QuestionDialog(View dialog) {
        thisDialog = dialog;
        thisDialog.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        hide();

        setOnClickListeners();
    }


    private void setOnClickListeners() {
        thisDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hide();
            }
        });

        thisDialog.findViewById(R.id.yes_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TakeLogManager.get().insertLog(currentDrug.getName(), true);
                hide();
            }
        });

        thisDialog.findViewById(R.id.no_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TakeLogManager.get().insertLog(currentDrug.getName(), false);
                hide();
            }
        });
    }


    public void show(DrugInfo drugInfo) {
        currentDrug = drugInfo;
        TextView questionText = (TextView) thisDialog.findViewById(R.id.question_text);
        questionText.setText(String.format("'%s' 약을 정말 복용 하셨나요? ", drugInfo.getName()));

        thisDialog.setVisibility(View.VISIBLE);
        PassedAlarmManager.get().pushDrug(null);
    }


    public void hide() {
        thisDialog.setVisibility(View.INVISIBLE);
    }

}
