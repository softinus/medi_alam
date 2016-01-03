package com.plusline.medialarm.ui;

import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.plusline.medialarm.R;
import com.plusline.medialarm.management.TakeLogManager;
import com.plusline.medialarm.type.TakeLog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * 보고서 레이아웃
 */
public class ReportLayout {
    private static final String tag = "ReportLayout";

    private View thisLayout;
    private List<Calendar> takeDays = new ArrayList<>();
    private List<TakeLog> takeLogs = new ArrayList<>();

    private ExpandableListView takeLogListView;
    private TakeLogListAdapter takeLogListAdapter;


    //
    //
    //
    public ReportLayout(View layout) {
        thisLayout = layout;
        thisLayout.setVisibility(View.GONE);

        takeLogListView = (ExpandableListView) thisLayout.findViewById(R.id.take_log_list_view);
        takeLogListAdapter = new TakeLogListAdapter();
        takeLogListView.setAdapter(takeLogListAdapter);
    }

    private void initReportData() {
        takeDays.clear();
        takeLogs.clear();

        takeLogs.addAll(TakeLogManager.get().loadAllLog());
        for(TakeLog log : takeLogs) {
            Log.d(tag, "Take Log: " + log);
            Calendar takeDay = createDay(log);
            if(containsDay(takeDay)) {
                Log.d(tag, "Contains date " + Convert.toStr(takeDay));
                continue;
            }
            takeDays.add(takeDay);
        }

        takeLogListAdapter.notifyDataSetChanged();
    }

    private List<TakeLog> getTakeLogsInDay(Calendar takeDay) {
        List<TakeLog> logs = new ArrayList<>();
        for(TakeLog log : takeLogs) {
            if(isSameDay(log.getTakeTime(), takeDay)) {
                logs.add(log);
            }
        }

        return logs;
    }

    private boolean containsDay(Calendar day) {
        for(Calendar takeDay : takeDays) {
            if(isSameDay(day, takeDay)) {
                return true;
            }
        }
        return false;
    }

    private boolean isSameDay(Calendar first, Calendar second) {
        return first.get(Calendar.YEAR) == second.get(Calendar.YEAR) &&
                first.get(Calendar.MONTH) == second.get(Calendar.MONTH) &&
                first.get(Calendar.DAY_OF_MONTH) == second.get(Calendar.DAY_OF_MONTH);
    }

    private Calendar createDay(TakeLog log) {
        Calendar takeTime = log.getTakeTime();
        Calendar day = Calendar.getInstance();
        day.set(
                takeTime.get(Calendar.YEAR), takeTime.get(Calendar.MONTH), takeTime.get(Calendar.DAY_OF_MONTH),
                0, 0, 0);
        return day;
    }


    public void show() {
        initReportData();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                thisLayout.setVisibility(View.VISIBLE);
                Animation showAnim = AnimationUtils.loadAnimation(thisLayout.getContext(), R.anim.push_up_in);
                thisLayout.startAnimation(showAnim);
            }
        }, 300);
    }


    public void hide() {
        Animation hideAnimation = AnimationUtils.loadAnimation(thisLayout.getContext(), R.anim.push_down_out);
        hideAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                thisLayout.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        thisLayout.startAnimation(hideAnimation);
    }

    public boolean isShow() {
        return View.VISIBLE == thisLayout.getVisibility();
    }

    //
    //
    //
    private class TakeLogListAdapter extends BaseExpandableListAdapter {
        @Override
        public int getGroupCount() {
            return takeDays.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            Calendar takeDay = (Calendar) getGroup(groupPosition);
            List<TakeLog> takeLogs = getTakeLogsInDay(takeDay);
            return takeLogs.size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            return takeDays.get(groupPosition);
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            Calendar takeDay = (Calendar) getGroup(groupPosition);
            List<TakeLog> takeLogs = getTakeLogsInDay(takeDay);
            return takeLogs.get(childPosition);
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
                convertView = View.inflate(thisLayout.getContext(), R.layout.item_report_take_day, null);
            }

            Calendar takeDay = (Calendar) getGroup(groupPosition);
            if(null != takeDay) {
                TextView takeDayText = (TextView) convertView.findViewById(R.id.take_day_text);
                takeDayText.setText(Convert.toStr(takeDay, "MMM dd, E", Locale.KOREA) + "요일");
            }
            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            if(null == convertView) {
                convertView = View.inflate(thisLayout.getContext(), R.layout.item_report_take_log, null);
            }

            TakeLog takeLog = (TakeLog) getChild(groupPosition, childPosition);
            if(null != takeLog) {
                TextView takeLogText = (TextView) convertView.findViewById(R.id.take_log_text);
                takeLogText.setText(Html.fromHtml(
                        String.format("%s, %s - <font color='#d13818'>%s</font>",
                        takeLog.getDrugName(),
                        Convert.toStr(takeLog.getTakeTime(), "a h:m"),
                        takeLog.isTaken() ? "투약함" : "빠트림")));
            }

            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return false;
        }
    }
}
