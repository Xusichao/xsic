package com.xsic.xsic.eventcenter;

import java.util.ArrayList;
import java.util.List;

public class EventCenter {
    public static List<EventListener> mEventList = new ArrayList<>();

    public synchronized static void addEventListener(EventListener eventListener){
        if (eventListener!=null && !mEventList.contains(eventListener)){
            mEventList.add(eventListener);
        }
    }

    public synchronized static void sendEvent(int type, Object extra){
        for (EventListener eventListener : mEventList){
            eventListener.onEvent(type,extra);
        }
    }

    public synchronized static void removeEventListener(EventListener eventListener){
        if (eventListener!=null){
            mEventList.remove(eventListener);
        }
    }
}
