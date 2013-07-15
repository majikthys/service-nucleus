/*
 * Copyright Liaison Technologies, Inc. All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Liaison Technologies, Inc. ("Confidential Information").  You shall 
 * not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Liaison Technologies.
 */

package com.liaison.commons.util;

import java.sql.Connection;
import java.sql.SQLException;

import oracle.sql.BLOB;

public class BLOBUtil
{
    public static BLOB createBLOB( Connection con ) throws SQLException
    {
        BLOB blob = BLOB.createTemporary( con, false, BLOB.DURATION_SESSION );
        return ( blob );
    }

    public static void freeBLOB( BLOB blob ) throws SQLException
    {
        if (blob != null) {
            if (blob.isTemporary()) {
                if (blob.isOpen()) {
                    blob.close();
                }
                blob.freeTemporary();
            }
        }
    }
}
