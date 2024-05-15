package hu.infokristaly.utils;

import hu.infokristaly.front.annotations.QueryFieldInfo;
import hu.infokristaly.front.annotations.QueryInfo;

public class QueryMirror {

    @QueryInfo(fields={@QueryFieldInfo()})
    public String query;
    
}
