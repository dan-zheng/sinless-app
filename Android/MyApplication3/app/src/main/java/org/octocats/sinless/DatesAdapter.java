package org.octocats.sinless;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.octocats.sinless.models.Action;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by nisarg on 21/1/17.
 */

public class DatesAdapter extends BaseAdapter {
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
    public int getCount() {
        return dates.size();
    }

    @Override
    public Object getItem(int i) {
        return dates.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View vi = view;
        if (vi == null)
            vi = inflater.inflate(R.layout.view_row, null);
        TextView headerText = (TextView) vi.findViewById(R.id.header_text);
        headerText.setText(dates.get(i));

        ActionAdapter adapter = new ActionAdapter(context, dataMap.get(dates.get(i)));
        ListView actionsList = (ListView) vi.findViewById(R.id.actions_list);
        actionsList.setAdapter(adapter);

        return vi;
    }
}
