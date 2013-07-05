package com.liaison.hellodao;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.liaison.commons.jpa.DAOUtil;
import com.liaison.hellodao.model.HelloWorld;


public class Main
{
    public static void main( String args [] )
    {
    	try
    	{
        	long iStart = System.currentTimeMillis();

        	Main m = new Main();
        	System.out.println( m.doit( args[ 0 ] ) );
        	
        	long iEnd = System.currentTimeMillis();
    		long iTime = (iEnd - iStart) / 1000;
    		System.out.println( iTime );
    	}
    	catch ( Throwable t )
    	{
    		t.printStackTrace();
    	}
    }
    
    public String doit( String strName ) throws Exception
    {
    	SimpleDateFormat sdf = new SimpleDateFormat( "yyyy.MM.dd HH:mm:ss" );
    	String strId = sdf.format( new Date() );
    	
    	HelloWorld helloWorld = new HelloWorld();

    	helloWorld.setName( strName );
        helloWorld.setSiSguid( strId );
        
        DAOUtil.persist( helloWorld );
    	
    	return ( "****" + helloWorld.getPrimaryGUID() + "****" );
    }
}