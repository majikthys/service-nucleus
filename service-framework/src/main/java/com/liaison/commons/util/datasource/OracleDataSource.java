/*
 * Copyright Liaison Technologies, Inc. All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Liaison Technologies, Inc. ("Confidential Information").  You shall 
 * not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Liaison Technologies.
 */

package com.liaison.commons.util.datasource;

import static com.liaison.commons.util.settings.PersistenceProperties.DB_PASSWORD;
import static com.liaison.commons.util.settings.PersistenceProperties.DB_URL;
import static com.liaison.commons.util.settings.PersistenceProperties.DB_USER;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import oracle.ucp.UniversalConnectionPoolException;
import oracle.ucp.jdbc.PoolDataSource;
import oracle.ucp.jdbc.PoolDataSourceImpl;


import com.liaison.commons.util.settings.DecryptableConfiguration;
import com.liaison.commons.util.settings.LiaisonConfigurationFactory;

/**
 * Utility methods for interacting with JDBC OracleDataSource
 * @author max
 */
public class OracleDataSource {
    protected static Logger                 logger                    = LogManager.getLogger(OracleDataSource.class);
    private static Boolean                  isInitialized             = false;
    private static final String             JNDI_SUB_CONTEXT_NAME     = "g2:";
    private static final String             ORACLE_DATASOURCE_NAME    = JNDI_SUB_CONTEXT_NAME + "/oracleDS";
    private static final String             ORACLE_CONNECTION_FACTORY = "oracle.jdbc.pool.OracleDataSource";
    private static final String             CONNECTION_POOL_NAME      = "G2 Connection Pool";

    private static DecryptableConfiguration configuration             = LiaisonConfigurationFactory.getConfiguration();

    /**
     * The main initialization entry point.
     * 
     * @throws NamingException
     * @throws SQLException
     * @throws UniversalConnectionPoolException 
     */
    static public void initOracleDataSource() throws NamingException, SQLException, UniversalConnectionPoolException {
        initOracleDataSource(configuration.getDecryptedCharArray(DB_PASSWORD, false));
    }

    /**
     * A test friendly init method
     * 
     * @param password
     * @throws NamingException
     * @throws SQLException
     * @throws UniversalConnectionPoolException 
     */
    static public void initOracleDataSource(char[] password) throws NamingException, SQLException, UniversalConnectionPoolException {
        if ( !isInitialized ) {
            if ( !bindConnectionPool() ) {
                initializeDataSource(password);
            }
        }
    }

    /**
     * Initialize data source and bind to JNDI context.
     * 
     * @param password
     * @throws NamingException
     * @throws SQLException
     * @throws UniversalConnectionPoolException 
     */
    private static void initializeDataSource(char[] password) throws NamingException, SQLException, UniversalConnectionPoolException {
        logger.info("Initializing data source connection pool!");

        Context ctx = new InitialContext();
        ctx.createSubcontext(JNDI_SUB_CONTEXT_NAME);

//        // https://forums.oracle.com/message/4345222#4345222
//        UniversalConnectionPoolManager ucpManager = UniversalConnectionPoolManagerImpl.getUniversalConnectionPoolManager();
//        ucpManager.setJmxEnabled(false);
        
        // Create a PoolDataSource instance explicitly
        // --------------------------------------------
        PoolDataSourceImpl poolDataSource = new PoolDataSourceImpl();
        poolDataSource.setConnectionFactoryClassName(ORACLE_CONNECTION_FACTORY);
        poolDataSource.setUser(configuration.getString(DB_USER));
        poolDataSource.setPassword(String.valueOf(password));
        poolDataSource.setURL(configuration.getString(DB_URL));
        poolDataSource.setConnectionPoolName(CONNECTION_POOL_NAME);
        
//        ucpManager.createConnectionPool(poolDataSource);
//        ucpManager.stopConnectionPool(CONNECTION_POOL_NAME);
//        ucpManager.destroyConnectionPool(CONNECTION_POOL_NAME);
        
        ctx.bind(ORACLE_DATASOURCE_NAME, poolDataSource);

        logger.info("Factory: " + ORACLE_CONNECTION_FACTORY);
        logger.info("User: " + configuration.getString(DB_USER));
        //logger.debug("Password: " + String.valueOf(password));  //TODO this should be removed entirely for production
        logger.info("URL: " + configuration.getString(DB_URL));
        logger.info("Poolname: " + CONNECTION_POOL_NAME);
        logger.info("Dsname: " + ORACLE_DATASOURCE_NAME);

        ctx.close();

        logger.info("Context is bound!");

        isInitialized = true;
    }

    /**
     * Bind a ConnectionPool based on JNDI lookup
     * 
     * @return  - true if able to bind to existing context
     * @throws NamingException
     */
    private static boolean bindConnectionPool() {
        logger.warn("JNDI already bound - verifying now");

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
            logger.info("Conn factory class name: " + pds.getConnectionFactoryClassName());
            logger.info("Connection pool name: " + pds.getConnectionPoolName());
            isInitialized = true;
        } catch (NamingException e) {
            isInitialized = false;
            return isInitialized;
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
        
        return isInitialized;
    }

}
