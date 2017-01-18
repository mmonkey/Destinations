package com.github.mmonkey.destinations.comparators;

import com.github.mmonkey.destinations.entities.BedEntity;

import java.util.Comparator;

public class BedComparator implements Comparator<BedEntity> {

    @Override
    public int compare(BedEntity b1, BedEntity b2) {
        if (b1.getLastUse().after(b2.getLastUse())) {
            return -1;
        } else if (b1.getLastUse().before(b2.getLastUse())) {
            return 1;
        }
        return 0;
    }

}
