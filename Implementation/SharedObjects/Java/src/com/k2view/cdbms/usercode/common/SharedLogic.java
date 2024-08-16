/////////////////////////////////////////////////////////////////////////
// Project Shared Functions
/////////////////////////////////////////////////////////////////////////

package com.k2view.cdbms.usercode.common;

import java.util.*;
import java.sql.*;
import java.math.*;
import java.io.*;

import com.k2view.cdbms.shared.*;
import com.k2view.cdbms.shared.user.UserCode;
import com.k2view.cdbms.sync.*;
import com.k2view.cdbms.lut.*;
import com.k2view.cdbms.shared.logging.LogEntry.*;
import com.k2view.cdbms.func.oracle.OracleToDate;
import com.k2view.cdbms.func.oracle.OracleRownum;
import com.k2view.cdbms.shared.utils.UserCodeDescribe.*;
import com.k2view.fabric.events.*;
import com.k2view.fabric.fabricdb.datachange.TableDataChange;

import static com.k2view.cdbms.shared.user.ProductFunctions.*;
import static com.k2view.cdbms.shared.user.UserCode.*;
import static com.k2view.cdbms.shared.utils.UserCodeDescribe.FunctionType.*;
import static com.k2view.cdbms.usercode.common.SharedGlobals.*;

@SuppressWarnings({"unused", "DefaultAnnotationParam"})
public class SharedLogic {

    @desc("Get Resource File of LU")
	@out(name = "result", type = Object.class, desc = "")
	public static Object loadFromLUResource(String path) throws Exception {
		return loadResource(path);
	}

    private static final String LOG_SYNC_FIRST_OR_FORCED = "syncFirstOrForced:{}[{}]:{}:{}: {}";

	@desc("This function indicates if current table must be sync'd on first sync, or when explicitly forced via session global.")
    @type(DecisionFunction)
    @out(name = "decision", type = Boolean.class, desc = "")
    public static Boolean syncFirstOrForced() throws Exception {
        final String lut = UserCode.getLuType().luName;
        final String iid = UserCode.getInstanceID();
        final String tableName = UserCode.getTableName();
        final String syncMode = UserCode.getSyncMode();
        if (SyncMode.OFF.toString().equals(syncMode)) {
            log.info(LOG_SYNC_FIRST_OR_FORCED, lut, iid, tableName, syncMode, "returned FALSE due to sync OFF");
            return Boolean.FALSE;
        }
        if (SyncMode.FORCE.toString().equals(syncMode)) {
            log.info(LOG_SYNC_FIRST_OR_FORCED, lut, iid, tableName, syncMode, "returned TRUE due to sync FORCE");
            return Boolean.TRUE;
        }
        if (UserCode.isFirstSync()) {
            log.info(LOG_SYNC_FIRST_OR_FORCED, lut, iid, tableName, syncMode, "returned TRUE due to first sync");
            return Boolean.TRUE;
        }
        if (UserCode.isStructureChanged()) {
            log.info(LOG_SYNC_FIRST_OR_FORCED, lut, iid, tableName, syncMode, "returned TRUE due to structure change");
            return Boolean.TRUE;
        }
        Object sessionGlobal = fabric().fetch("set ?", String.format("%s__%s", lut, tableName)).firstValue();
        if (sessionGlobal == null) {
            log.info(LOG_SYNC_FIRST_OR_FORCED, lut, iid, tableName, syncMode, "returned FALSE due to no session global");
            return Boolean.FALSE;
        }
        log.info(LOG_SYNC_FIRST_OR_FORCED, lut, iid, tableName, syncMode, "returned TRUE due to session global");
        return Boolean.TRUE;
    }

}
