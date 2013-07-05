package com.liaison;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.liaison.commons.jpa.DAOUtil;
import com.liaison.commons.util.datasource.OracleDataSource;
import com.liaison.hellodao.Main;

public class HelloServlet extends HttpServlet
{
	private static final long serialVersionUID = 1L;
	private static boolean bInit = false;

	public void doGet( HttpServletRequest request,
					   HttpServletResponse response ) throws ServletException,
					   										 IOException
   {
		if ( bInit == false )
		{
			try
			{
				OracleDataSource.initOracleDataSource(); // TODO This needs to be moved to JMX
		        DAOUtil.init();	// TODO This does the work of loading all JAP entity files.  We should change to allow the qeury string to be passed.
				bInit = true;
			}
			catch ( Throwable t )
			{
				t.printStackTrace();
			}
		}
		
		PrintWriter out = response.getWriter();
		
		Main m = new Main();
		try
		{
			out.println( m.doit( "Hello Cruel Cruel World" ) );
		}
		catch( Throwable t )
		{
			t.printStackTrace();
		}
   }
	
}