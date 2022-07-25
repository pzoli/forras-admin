package hu.infokristaly.back.model;

import java.util.TreeMap;

public class MonthMap extends TreeMap<Integer, CatMap> {

    private static final long serialVersionUID = -2922181347872888280L;
    
    public MonthMap() {
        super();
    }

    public CatMap getCatMap(Integer mounthIndex) {
        CatMap result = get(mounthIndex);
        if (result == null) {
            result = new CatMap();
        }
        return result;
    }

}
