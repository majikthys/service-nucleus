package com.liaison.commons.jpa;

import java.sql.SQLException;
import java.util.Date;

import javax.naming.NamingException;

import oracle.ucp.UniversalConnectionPoolException;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.liaison.commons.util.datasource.OracleDataSource;
import com.liaison.framework.audit.AuditStandardsRequirement;
import com.liaison.framework.audit.AuditStatement;
import com.liaison.framework.audit.DefaultAuditStatement;
import com.liaison.framework.audit.AuditStatement.Status;
import com.liaison.framework.audit.pci.*;
//TODO this is a stupid name
public class DAOInit implements DAOInitMBean {
	protected static Logger logger = LogManager.getLogger(DAOInit.class);
	protected static AuditStatement CC_ACCESS_AUDIT_ATTEMPT =	new DefaultAuditStatement(PCIV20Requirement.PCI10_2_1, Status.ATTEMPT, "Admin decryption of CC data");
	protected static AuditStatement CC_ACCESS_AUDIT_SUCEED =	new DefaultAuditStatement(PCIV20Requirement.PCI10_2_1, Status.SUCCEED, "Admin decryption of CC data");
	protected static AuditStatement CC_ACCESS_AUDIT_FAIL =	new DefaultAuditStatement(PCIV20Requirement.PCI10_2_1, Status.FAILED, "Admin decryption of CC data");

	public Object  someMethod() {
		try {
			logger.info(CC_ACCESS_AUDIT_ATTEMPT);	
			//Do something.
			logger.info(CC_ACCESS_AUDIT_SUCEED);				
		} catch (Exception e) {
			logger.info(CC_ACCESS_AUDIT_FAIL);	
		}
	}
	
	@Override
	public void initConnection()  {
		
		
		logger.info("JMX DAOInit.init() request");
		try {
			DAOUtil.init();
		} catch (Throwable t) {
			System.out.println("---------- DAOUtil.init() Exception ---------");
			System.out.println("---------- DAOUtil.init() Exception ---------");
			System.out.println("---------- DAOUtil.init() Exception ---------");
			System.out.println(ExceptionUtils.getFullStackTrace(t));
			System.out.println("---------- DAOUtil.init() Exception ---------");
			System.out.println("---------- DAOUtil.init() Exception ---------");
			System.out.println("---------- DAOUtil.init() Exception ---------");
			logger.error("JMX attempt to DAOUtil.init() Failed", t);
		}

	}
	
	@Override
	public void initDataSource()  {
		logger.info("JMX initDataSource request");
		try {
			OracleDataSource.initOracleDataSource();
		} catch (NamingException | SQLException | UniversalConnectionPoolException e) {
			System.out.println("/---------- initOracleDataSource Exceptio ---------");
			System.out.println(ExceptionUtils.getFullStackTrace(e));
			System.out.println("---------- initOracleDataSource Exceptio ---------/");

			logger.error("JMX attempt to initOracleDataSource Failed", e); //(need to catch in local log)
			throw new RuntimeException(e); //propogate to JMX 
		} 
		
	}


	public String getHelloMBean() {
		ClassLoader contextClassLoader = null;
		  try
		    {
		    // your codes here
				initDataSource();
				initConnection();
		    // Get the current class-loader. This might be the class-loader from Tomcat
		     contextClassLoader = Thread.currentThread().getContextClassLoader();

		    // Set the class-loader to be the one from the DO and not the one from Tomcat
		    Thread.currentThread().setContextClassLoader(getClass().getClassLoader());

		    // your codes here
		    }
		    catch (Exception e)
		    {
		       // your codes here
		    }
		    finally
		    {
		       // Remember to set back the original class-loader
		       Thread.currentThread().setContextClassLoader(contextClassLoader);
		    }
		
		StringBuilder info = new  StringBuilder();
		ClassLoader threadLoader = Thread.currentThread().getContextClassLoader();
		info.append("\nThread.currentThread().getContextClassLoader(): " + threadLoader);
		if (null != threadLoader) {
			info.append("\nparent: " + threadLoader.getParent());
		
		}
		ClassLoader classCLoader = getClass().getClassLoader();
		info.append("\nCurrent Class loader: " + classCLoader);
		if (null != classCLoader) {
			info.append("\nparent: " + classCLoader.getParent());
		}		
		
//		initDataSource();
//		initConnection();
		return "Hello, the time is: " + (new Date()).toString() + "\n" + info.toString() ; 
	}

}
