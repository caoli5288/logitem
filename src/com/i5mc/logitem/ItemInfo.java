package com.i5mc.logitem;

import lombok.Data;

import java.util.Map;

import static org.bukkit.util.NumberConversions.toInt;

/**
 * Created by on 12月3日.
 */
@Data
public class ItemInfo {

    private int id;
    private int subId;
    private String name;
    private int limit;
    private String command;

    public ItemInfo(Map<?, ?> input) {
        id = toInt(input.get("id"));
        limit = toInt(input.get("limit"));
        if (id < 1 || limit < 1) throw new IllegalArgumentException("" + input);

        if (input.containsKey("sub_id")) {
            subId = toInt(input.get("sub_id"));
        }
        if (input.containsKey("name") && !(input.get("name") == null)) {
            name = String.valueOf(input.get("name"));
        }
        if (input.containsKey("command") && !(input.get("command") == null)) {
            command = String.valueOf(input.get("command"));
        }
    }
}
