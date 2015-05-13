package com.itachi1706.hypixelstatistics.util.Objects;

import java.util.Comparator;

/**
 * Created by Kenneth on 13/5/2015
 * for HypixelStatistics in package com.itachi1706.hypixelstatistics.util.Objects
 */
public class ExceptionComparator implements Comparator<ExceptionObject> {

    @Override
    public int compare(ExceptionObject lhs, ExceptionObject rhs) {
        if (lhs.getCount() > rhs.getCount())
            return 1;
        if (lhs.getCount() == rhs.getCount())
            return 0;
        return -1;
    }
}
