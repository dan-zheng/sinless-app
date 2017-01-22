package org.octocats.sinless.models;

/**
 * Created by nisarg on 21/1/17.
 */

public class Action {

    String id;
    long time;
    String actionType;
    int amountDeducted;

    public Action(String id, long time, String actionType, int amountDeducted) {
        this.id = id;
        this.time = time;
        this.actionType = actionType;
        this.amountDeducted = amountDeducted;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public int getAmountDeducted() {
        return amountDeducted;
    }

    public void setAmountDeducted(int amountDeducted) {
        this.amountDeducted = amountDeducted;
    }
}

