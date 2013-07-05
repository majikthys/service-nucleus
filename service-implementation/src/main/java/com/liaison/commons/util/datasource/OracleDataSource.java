package com.liaison.commons.util.datasource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;

import org.apache.log4j.Logger;

import oracle.ucp.jdbc.PoolDataSource;
import oracle.ucp.jdbc.PoolDataSourceImpl;

import com.liaison.commons.util.settings.DecryptableConfiguration;
import com.liaison.commons.util.settings.LiaisonConfigurationFactory;
import static com.liaison.commons.util.settings.PersistenceProperties.*;


/**
 * Utility methods for interacting with JDBC OracleDataSource
 * @author max
 */
public class OracleDataSource {
	protected static Logger logger = Logger.getLogger(OracleDataSource.class);
	//TODO move statics to config
	private static boolean bInit = false;
	private static final String JNDI_SUB_CONTEXT_NAME = "g2:";
	private static final String ORACLE_DATASOURCE_NAME = JNDI_SUB_CONTEXT_NAME
			+ "/oracleDS";
	private static final String ORACLE_CONNECTION_FACTORY = "oracle.jdbc.pool.OracleDataSource";
	private static final String CONNECTION_POOL_NAME = "G2 Connection Pool";
	private static DecryptableConfiguration configuration = LiaisonConfigurationFactory.getConfiguration();

	static public void initOracleDataSource() throws Exception {
		initOracleDataSource(configuration.getDecryptedCharArray(DB_PASSWORD, true));
	}

	static public void initOracleDataSource(char[] password)
			throws Exception {
		if (bInit == true) {
			System.out.println("JNDI already bound - verifying now");
			logger.info("JNDI already bound - verifying now");

			InitialContext ctx;
			PreparedStatement ps = null;
			ResultSet rs = null;
			Connection con = null;

			try {
				ctx = new InitialContext();
				Object o = ctx.lookup(ORACLE_DATASOURCE_NAME);
				PoolDataSource pds = (PoolDataSource) o;

				logger.info("Return context is " + o.getClass().getName());
				logger.info("User: " + pds.getUser());
				logger.info("URL: " + pds.getURL());
				logger.info("Conn factory class name: "
						+ pds.getConnectionFactoryClassName());
				logger.info("Connection pool name: "
						+ pds.getConnectionPoolName());
			} finally {
				if (rs != null) {
					try {
						rs.close();
					} catch (SQLException e) {
					}
				}

				if (ps != null) {
					try {
						ps.close();
					} catch (SQLException e) {
					}
				}

				if (con != null) {
					try {
						con.close();
					} catch (SQLException e) {
					}
				}
			}
		} else {
			logger.info("Initializing data source connection pool!");

			Context ctx = new InitialContext();
			ctx.createSubcontext(JNDI_SUB_CONTEXT_NAME);

			// Create a PoolDataSource instance explicitly
			// --------------------------------------------
			PoolDataSource pds = new PoolDataSourceImpl();

			pds.setConnectionFactoryClassName(ORACLE_CONNECTION_FACTORY);
			pds.setUser(configuration.getString(DB_USER));
			pds.setPassword(String.valueOf(password));
			pds.setURL(configuration.getString(DB_URL));
			pds.setConnectionPoolName(CONNECTION_POOL_NAME);

			ctx.bind(ORACLE_DATASOURCE_NAME, pds);

			logger.info("Factory: " + ORACLE_CONNECTION_FACTORY);
			logger.info("User: " + configuration.getString(DB_USER));			
			logger.debug("Password: " + String.valueOf(password)); //TODO this should be removed entirely for production
			logger.info("URL: " + configuration.getString(DB_URL));
			logger.info("Poolname: " + CONNECTION_POOL_NAME);
			logger.info("Dsname: " + ORACLE_DATASOURCE_NAME);

			ctx.close();

			/*
			 * ctx = new InitialContext(); Object o = ctx.lookup(
			 * ORACLE_DATASOURCE_NAME ); pds = (PoolDataSource)o; Connection con
			 * = pds.getConnection(); con.close();
			 */

			logger.info("Context is bound!");

			bInit = true;
		}
	}
}
