package org.octocats.sinless;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.octocats.sinless.models.Action;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by nisarg on 21/1/17.
 */

public class ActionAdapter extends BaseAdapter{

    Context context;
    ArrayList<Action> actions;
    private static LayoutInflater inflater = null;

    public ActionAdapter(Context context, ArrayList<Action> data) {
        // TODO Auto-generated constructor stub
        this.context = context;
        this.actions = data;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return actions.size();
    }

    @Override
    public Object getItem(int i) {
        return actions.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View vi = view;
        if (vi == null)
            vi = inflater.inflate(R.layout.view_content, null);
        Action action = actions.get(i);
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
        else {
            amountDed.setText("+$"+action.getAmountDeducted()*-1);
            amountDed.setTextColor(vi.getResources().getColor(R.color.green));
        }
        Log.e("ActionAdapter", "amt "+action.getAmountDeducted());
        return vi;
    }
}
