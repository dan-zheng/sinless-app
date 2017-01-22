package org.octocats.sinless;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import org.octocats.sinless.models.Action;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

/**
 * Created by nisarg on 21/1/17.
 */

public class DatesAdapter extends BaseExpandableListAdapter {
    Context context;
    ArrayList<String> dates;
    HashMap<String, ArrayList<Action>> dataMap;
    private static LayoutInflater inflater = null;

    public DatesAdapter(Context context, HashMap<String, ArrayList<Action>> dataMap, ArrayList<String> dates) {
        // TODO Auto-generated constructor stub
        this.context = context;
        this.dates = dates;
        this.dataMap = dataMap;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int getGroupCount() {
        return dataMap.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return dataMap.get(dates.get(i)).size();
    }

    @Override
    public Object getGroup(int i) {
        return dataMap.get(dates.get(i));
    }

    @Override
    public Object getChild(int i, int i1) {
        return dataMap.get(dates.get(i)).get(i1);
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
        View vi = view;
        if (vi == null)
            vi = inflater.inflate(R.layout.view_header, null);
        TextView headerText = (TextView) vi.findViewById(R.id.header_text);
        headerText.setText(dates.get(i));

        return vi;
    }

    @Override
    public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
        /*View vi = view;
        if (vi == null)
            vi = inflater.inflate(R.layout.view_list, null);
        ActionAdapter adapter = new ActionAdapter(context, dataMap.get(dates.get(i)));
        ListView actionsList = (ListView) vi.findViewById(R.id.actions_list);
        actionsList.setAdapter(adapter);
        Log.e("DatesAdapter", "i "+ i +" i1 "+ i1);
        return vi;*/
        View vi = view;
        if (vi == null)
            vi = inflater.inflate(R.layout.view_content, null);
        Action action = dataMap.get(dates.get(i)).get(i1);
        TextView time = (TextView) vi.findViewById(R.id.time);

        SimpleDateFormat formatter = new SimpleDateFormat("hh:mm aaa");
        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(action.getTime());
        String timeStr = formatter.format(calendar.getTime());
        time.setText("• "+timeStr);

        TextView actionType = (TextView) vi.findViewById(R.id.txtActionType);
        actionType.setText(action.getActionType());

        TextView amountDed = (TextView) vi.findViewById(R.id.txtAmount);
        if(action.getAmountDeducted() > 0)
            amountDed.setText("-$"+action.getAmountDeducted());
        else if(action.getAmountDeducted() < 0) {
            amountDed.setText("+$"+action.getAmountDeducted()*-1);
            amountDed.setTextColor(vi.getResources().getColor(R.color.green));
        } else{
            amountDed.setText("$"+action.getAmountDeducted());
            amountDed.setTextColor(vi.getResources().getColor(R.color.white));
        }
        Log.e("ActionAdapter", "amt "+action.getAmountDeducted());
        return vi;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return false;
    }
}
