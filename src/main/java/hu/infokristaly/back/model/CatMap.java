package hu.infokristaly.back.model;

import java.util.TreeMap;

public class CatMap extends TreeMap<Integer, Integer> {

    private static final long serialVersionUID = -3882513828347158398L;

    public CatMap() {
        super();
    }

    private Integer clientCount;

    public void setClientCount(int size) {
        clientCount = size;
    }

    public Integer getClientCount() {
        return clientCount;
    }

    public void incCatCounter(Integer index) {
        if (containsKey(index)) {
            Integer count = get(index);
            if (count == null) {
                count = 1;
            } else {
                count++;
            }
            put(index, count);
        } else {
            put(index, 1);
        }
    }
    
    public Integer getCatCount(Integer index) {
        return get(index);
    }

}
