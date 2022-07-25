package hu.infokristaly.back.model;

import java.util.TreeMap;

public class YearMap extends TreeMap<Integer, MonthMap> {

    private static final long serialVersionUID = -3586057583993063088L;

    public YearMap() {
        super();
    }

    public MonthMap getMonthMap(Integer yearIndex) {
        MonthMap result = get(yearIndex);
        if (result == null) {
            result = new MonthMap();
        }
        return result;
    }
}
