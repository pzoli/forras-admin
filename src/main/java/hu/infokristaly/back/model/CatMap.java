package hu.infokristaly.back.model;

import java.util.TreeMap;

public class CatMap extends TreeMap<Long, Long> {

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

    public void incCatCounter(Long index) {
        if (containsKey(index)) {
            Long count = get(index);
            if (count == null) {
                count = 1L;
            } else {
                count++;
            }
            put(index, count);
        } else {
            put(index, 1L);
        }
    }
    
    public Long getCatCount(Long index) {
        return get(index);
    }

}
