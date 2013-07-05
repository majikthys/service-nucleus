package com.liaison.hellodao.query;

import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

@NamedQueries({
    @NamedQuery(name="findWorld",
                query="SELECT w, m FROM HelloWorld w JOIN w.helloMoons m WHERE w.siSguid = :siid" )
}) 

public interface HelloQuery 
{
	public static final String FIND_WORLD = "findWorld";
	public static final String FIND_WORLD_SIID = "siid";
}
