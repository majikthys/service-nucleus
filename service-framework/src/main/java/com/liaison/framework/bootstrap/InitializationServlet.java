package com.liaison.framework.bootstrap;

import java.lang.management.ManagementFactory;

import com.liaison.commons.jpa.DAOInit;
import com.liaison.commons.jpa.DAOInitMBean;
import com.liaison.framework.audit.AuditStatement;
import com.liaison.framework.audit.DefaultAuditStatement;
import com.liaison.framework.audit.pci.PCIV20Requirement;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
import javax.management.StandardMBean;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Initialization Servlet
 * <p/>
 * <P>
 * Bootstrapper
 * <p/>
 * TODO: Probably not the best place for this. Should likely move this (and all
 * servlets) to TODO within the guice framework.
 * 
 * @author Robert.Christian
 * @version 1.0
 */
public class InitializationServlet extends HttpServlet {

	private static final long serialVersionUID = 6667801554309619804L;

	protected static Logger logger = LogManager
			.getLogger(InitializationServlet.class);

	private static DAOInit daoInit;

	public void init(ServletConfig config) throws ServletException {

		 
		DefaultAuditStatement audit = new DefaultAuditStatement(
				PCIV20Requirement.PCI10_2_6, AuditStatement.Status.SUCCEED,
				"Initialization via servlet");

		//Registering DAOInit instance as mbean
		MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
		daoInit = new DAOInit();
		try {
			ObjectName name = new ObjectName("com.liaison:type=DB");
			StandardMBean bean = new StandardMBean(daoInit, DAOInitMBean.class);
			mbs.registerMBean(bean, name);
		} catch (MalformedObjectNameException  
				|  NotCompliantMBeanException | InstanceAlreadyExistsException | MBeanRegistrationException e) {
			throw new RuntimeException("Unable to load datasource initialization mbean at servlet init", e);
		}
		
		logger.info("+++++ INIT DATASOURCE +++++++");
		daoInit.initDataSource();// TODO should this be here?
		logger.info("+++++ INIT CONNECTION +++++++");
		daoInit.initConnection(); // TODO should this be here?

		logger.info("Servlet Init", audit);
	}
}
