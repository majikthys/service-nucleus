package com.liaison.hellodao.dao;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;

import com.liaison.hellodao.query.HelloQuery;


public class HelloDAO
{
	public List<Object []> findHelloWorld( EntityManager em, String strSIID ) 
	{
		List<Object []> list = new ArrayList<Object []>();

		List<?> results = em.createNamedQuery( HelloQuery.FIND_WORLD )
				.setParameter( HelloQuery.FIND_WORLD_SIID, strSIID )
					.getResultList();        	
        	
    	Iterator<?> iter = results.iterator();
		
        while ( iter.hasNext() )
        {
        	Object [] oar = (Object [])iter.next();
        	list.add( oar );
        }
        
        return ( list );
	}
}