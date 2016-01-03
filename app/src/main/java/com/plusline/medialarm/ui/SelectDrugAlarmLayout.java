package com.plusline.medialarm.ui;

import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.plusline.medialarm.R;
import com.plusline.medialarm.management.AlarmManager;
import com.plusline.medialarm.management.DrugCaseManager;
import com.plusline.medialarm.management.SelectedDrugCase;
import com.plusline.medialarm.type.Alarm;
import com.plusline.medialarm.type.Cartridge;
import com.plusline.medialarm.type.Drug;
import com.plusline.medialarm.type.DrugCase;
import com.plusline.medialarm.type.DrugInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Select alarm in today for modifying
 */
public class SelectDrugAlarmLayout {

    private View thisView;
    private int selectedDay;
    private DrugCase currentDrugCase;
    private List<Cartridge.Type> types = new ArrayList<>();

    private ExpandableListView drugAlarmListView;
    private DrugAlarmListAdapter drugAlarmListAdapter;


    //
    //  constructor
    //
    public SelectDrugAlarmLayout(View layout, int day) {
        thisView = layout;
        selectedDay = day;
        currentDrugCase = DrugCaseManager.get().getCase(day);

        types.add(Cartridge.Type.Morning);
        types.add(Cartridge.Type.Lunch);
        types.add(Cartridge.Type.Evening);

        drugAlarmListView = (ExpandableListView) thisView.findViewById(R.id.drug_alarm_list_view);

//        drugAlarmListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
//            @Override
//            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
//                return true;
//            }
//        });

        drugAlarmListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {



                return true;
            }
        });
    }

    public void show() {
        drugAlarmListAdapter = new DrugAlarmListAdapter();
        drugAlarmListView.setAdapter(drugAlarmListAdapter);
        int groupCount = drugAlarmListAdapter.getGroupCount();
        for(int i=0; i<groupCount; ++i) {
            drugAlarmListView.expandGroup(i);
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                thisView.setVisibility(View.VISIBLE);
                Animation showAnim = AnimationUtils.loadAnimation(thisView.getContext(), R.anim.push_up_in);
                thisView.startAnimation(showAnim);
            }
        }, 300);
    }


    public void hide() {
        Animation hideAnimation = AnimationUtils.loadAnimation(thisView.getContext(), R.anim.push_down_out);
        hideAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                thisView.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        thisView.startAnimation(hideAnimation);
    }


    public boolean isShow() {
        return View.VISIBLE == thisView.getVisibility();
    }

    //
    //
    //
    private class DrugAlarmListAdapter extends BaseExpandableListAdapter {
        @Override
        public int getGroupCount() {
            return 3;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            Cartridge cartridge = currentDrugCase.getCartridge(types.get(groupPosition));
            if(null == cartridge) {
                return 0;
            }

            return cartridge.getDrugs().size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            return types.get(groupPosition);
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            Cartridge cartridge = currentDrugCase.getCartridge(types.get(groupPosition));
            if(null == cartridge) {
                return null;
            }

            return cartridge.getDrugs().get(childPosition);
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            if(null == convertView) {
                convertView = View.inflate(thisView.getContext(), R.layout.item_alarm_type, null);
            }

            Cartridge.Type type = (Cartridge.Type) getGroup(groupPosition);
            if(type != null) {
                TextView typeText = (TextView) convertView.findViewById(R.id.type_text);
                typeText.setText(Cartridge.Type.toString(type));
            }
            return convertView;
        }

        @Override
        public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            if(null == convertView) {
                convertView = View.inflate(thisView.getContext(), R.layout.item_alarm_drug, null);
            }

            DrugInfo drugInfo = (DrugInfo) getChild(groupPosition, childPosition);
            if(null != drugInfo) {
                Alarm alarm = AlarmManager.get().getAlarm(drugInfo.getAlarmNo());

                ImageView drugIcon = (ImageView) convertView.findViewById(R.id.drug_icon_image);
                drugIcon.setImageResource(Drug.getDrugIcon(drugInfo.getColorIndex()));

                TextView drugInfoText = (TextView) convertView.findViewById(R.id.drug_info_text);
                drugInfoText.setText(String.format(
                        "%s, %s",
                        drugInfo.getName(),
                        Convert.toStr(alarm.getNextChimeTime(), "a h:m")));

            }

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    modifyDrugAlarm(groupPosition, childPosition);
                }
            });
            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return false;
        }


        private void modifyDrugAlarm(int groupPosition, int childPosition) {
            Cartridge.Type selectedType = (Cartridge.Type) drugAlarmListAdapter.getGroup(groupPosition);
            DrugInfo selectedDrug = (DrugInfo) drugAlarmListAdapter.getChild(groupPosition, childPosition);

            SelectedDrugCase.get().select(selectedDay);
            SelectedDrugCase.get().setIsModify(true);
            SelectedDrugCase.get().select(selectedType);
            SelectedDrugCase.get().setCurrentDrug(selectedDrug);

            Intent addDrug = new Intent(thisView.getContext(), AddDrugActivity.class);
            thisView.getContext().startActivity(addDrug);
            hide();
        }
    }
}
