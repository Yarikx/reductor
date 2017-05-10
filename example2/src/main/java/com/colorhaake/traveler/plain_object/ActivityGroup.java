package com.colorhaake.traveler.plain_object;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by colorhaake on 2017/3/25.
 */

public class ActivityGroup {
    public String class_name;
    public String type;
    public String id;
    public List<ActivityEvent> items = new ArrayList<>();

    @Override
    public String toString() {
        return "ActivityGroup{" +
                "class_name='" + class_name + '\'' +
                ", type='" + type + '\'' +
                ", id='" + id + '\'' +
                ", items=" + items +
                '}';
    }
}
