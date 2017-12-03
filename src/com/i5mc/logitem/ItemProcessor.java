package com.i5mc.logitem;

import lombok.RequiredArgsConstructor;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

/**
 * Created by on 12月3日.
 */
@RequiredArgsConstructor
public class ItemProcessor {

    private final Map<Integer, Pair<ItemInfo, Integer>> all;

    public ItemProcessor(List<ItemInfo> input) {
        all = new HashMap<>();
        input.forEach(info -> all.put(info.getId(), new Pair<>(info, 0)));
    }

    public List<ItemInfo> hit(Iterable<ItemStack> input) {
        input.forEach(item -> {
            if (item == null || item.getTypeId() == 0) {
                return;
            }
            Pair<ItemInfo, Integer> p = all.get(item.getTypeId());
            if (p == null) {
                return;
            }
            ItemInfo info = p.getKey();
            if (!(item.getData().getData() - info.getSubId() == 0 || item.getDurability() - info.getSubId() == 0)) {
                return;
            }
            if (!(info.getName() == null) && !contain(item.getItemMeta().getDisplayName(), info.getName())) {
                return;
            }
            p.setValue(item.getAmount() + p.getValue());
        });
        return all.values().stream().filter(val -> !(val.getValue() < val.getKey().getLimit())).map(Pair::getKey).collect(toList());
    }

    boolean contain(String left, String value) {
        return !(left == null) && left.contains(value);
    }

}
