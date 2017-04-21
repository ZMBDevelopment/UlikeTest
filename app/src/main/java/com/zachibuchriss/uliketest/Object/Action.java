package com.zachibuchriss.uliketest.Object;

/**
 * Created by ZAHI on 20/04/2017.
 */

public class Action {

    //Attributes

    private String type;
    private boolean enabled;
    private int priority;
    private int[] valid_days;
    private int cool_down;

    //Constructor

    public Action(String type, boolean enabled, int priority, int[] valid_days, int cool_down){
        this.type = type;
        this.enabled = enabled;
        this.priority = priority;
        this.valid_days = valid_days;
        this.cool_down = cool_down;
    }


    //Empty Constructor

    public Action(){
    }

    public int getCool_down() {
        return cool_down;
    }

    public String getType() {
        return type;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public int getPriority() {
        return priority;
    }

    public int[] getValid_days() {
        return valid_days;
    }

    public int getDay(int i){
        return valid_days[i];
    }

}
