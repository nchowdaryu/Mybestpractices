import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.SqlReturnResultSet;

import com.company.fas.literature.webservice.dao.rowmapper.LiteratureDeliveryMethodsRowMapper;
import com.company.fas.literature.webservice.dao.rowmapper.LiteratureDetailsRowMapper;
import com.company.fas.literature.webservice.dao.rowmapper.LiteratureFundsRowMapper;
import com.company.fas.literature.webservice.dao.rowmapper.LiteraturePackageRowMapper;
import com.company.fas.literature.webservice.dao.util.LiteratureDAOsConstants;
import com.company.service.technical.db.RetailAbstractStoredProcedure;
import com.company.service.technical.db.RetailStoredProcedureHeader;
import com.company.spring.storedproc.SqlParameterFactory;
import com.company.spring.storedproc.SqlParameterModes;
import com.company.spring.storedproc.StoredProcHeader;
import com.company.spring.storedproc.annotations.StoredProcedure;

/**
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.stereotype.Component;

/**
 * Indicates that the annotated class is a <i>stored procedure</i>.
 * <p />
 * This annotation also serves as a specialization of {@link Component @Component}, allowing for implementation classes to be autodetected through
 * classpath scanning.
 * <p />
 * For more information about stereotype annotations used in classpath scanning, see:
 * http://static.springframework.org/spring/docs/2.5.x/reference/beans.html#beans-stereotype-annotations
 */
@Target( { ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface StoredProcedure {

    /**
     * The value may indicate a suggestion for a logical component name, to be turned into a Spring bean in case of an autodetected component.
     * 
     * @return the suggested component name, if any
     */
    String value() default "";
}
**/
@StoredProcedure
public class RetreiveAllLiteratureStoredProcedure extends RetailAbstractStoredProcedure{	
	
	private static final String STATUS_CODE = "STATUS_CODE";
	private static final String APPLID = "APPLID";
	public static final String LIT_FUNDS_ROW_MAPPER = "LiteratureFundsRowMapper";
	public static final String LIT_DETAILS_ROW_MAPPER = "LiteratureDetailsRowMapper";	
	public static final String LIT_DELIVARY_METHODS_ROW_MAPPER = "LiteratureDeliveryMethodsRowMapper";
	public static final String LIT_PACKAGE_ROW_MAPPER = "LiteraturePackageRowMapper";
	
	@Autowired
	RetailStoredProcedureHeader aRetailStoredProcedureHeader;
	
	@Override
	public String getName() {		
		return LiteratureDAOsConstants.RETREIVE_ALL_LIT_SP_NAME;
	}	
	
	@Override
    protected StoredProcHeader getHeader() {
        return aRetailStoredProcedureHeader;
    }	
	
	@Override
	protected List<SqlParameter> declareParameters() {
		List<SqlParameter> params = new ArrayList<>();		
		params.add(SqlParameterFactory.createString(STATUS_CODE, SqlParameterModes.INOUT));				
		
		return params;
	}

	public Map<String, Object> buildParameters() {
		Map<String, Object> params = new HashMap<>();
		params.put(APPLID, LiteratureDAOsConstants.LIT_APP_ID);
		params.put(STATUS_CODE, LiteratureDAOsConstants.RETREIVE_ALL_LIT_STATUS_CD);
		
		return params;
	}

	@SuppressWarnings("rawtypes")
	@Override
	protected List<SqlReturnResultSet> declareResultSets() {		
		List<SqlReturnResultSet> result = new ArrayList<>();
		result.add(new SqlReturnResultSet(LIT_DETAILS_ROW_MAPPER, new LiteratureDetailsRowMapper()));
		result.add(new SqlReturnResultSet("RS2-Not Using", (RowMapper) null));
		result.add(new SqlReturnResultSet(LIT_FUNDS_ROW_MAPPER, new LiteratureFundsRowMapper()));
		result.add(new SqlReturnResultSet("RS4-Not Using", (RowMapper) null));
		result.add(new SqlReturnResultSet(LIT_DELIVARY_METHODS_ROW_MAPPER, new LiteratureDeliveryMethodsRowMapper()));
		result.add(new SqlReturnResultSet("RS6-Not Using", (RowMapper) null));
		result.add(new SqlReturnResultSet("RS7-Not Using", (RowMapper) null));
		result.add(new SqlReturnResultSet("RS8-Not Using", (RowMapper) null));
		result.add(new SqlReturnResultSet("RS9-Not Using", (RowMapper) null));
		result.add(new SqlReturnResultSet(LIT_PACKAGE_ROW_MAPPER, new LiteraturePackageRowMapper()));			
		
		return result;
	}
	

}

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.jdbc.core.SqlParameter;

import com.company.spring.storedproc.CommonStoredProcHeaderParameters;
import com.company.spring.storedproc.core.AbstractStoredProcHeader;

public class RetailStoredProcedureHeader extends AbstractStoredProcHeader {

	private static final String WEBTOP = "WEBTOP";

	private String packageName;

	@Override
	public List<SqlParameter> getHeaderTypes() {
		return CommonStoredProcHeaderParameters.getSqlParameters();
	}

	@Override
	public Map<String, Object> getHeaderValues() {
		Map<String, Object> parmValues = new HashMap<String, Object>();
		parmValues.put(CommonStoredProcHeaderParameters.PACKAGE, packageName);
		parmValues.put(CommonStoredProcHeaderParameters.APPNM, WEBTOP);
		parmValues.put(CommonStoredProcHeaderParameters.USERID, WEBTOP);
		parmValues.put(CommonStoredProcHeaderParameters.APPLID, WEBTOP);
		parmValues.put(CommonStoredProcHeaderParameters.TS, StringUtils.EMPTY);
		parmValues.put(CommonStoredProcHeaderParameters.ESEVERE,
				StringUtils.EMPTY);
		parmValues.put(CommonStoredProcHeaderParameters.ENUM, 0);
		parmValues.put(CommonStoredProcHeaderParameters.EGROUP, 0);
		parmValues.put(CommonStoredProcHeaderParameters.ESQLCD, 0);
		parmValues
				.put(CommonStoredProcHeaderParameters.EPGM, StringUtils.EMPTY);
		parmValues.put(CommonStoredProcHeaderParameters.ETEXT,
				StringUtils.EMPTY);
		parmValues.put(CommonStoredProcHeaderParameters.EDBAVAIL,
				StringUtils.EMPTY);
		return parmValues;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

}

import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.SqlParameter;

/**
 * Common stored procedure header parameter values. This should be used by {@link StoredProcHeader} subclasses that define these parameters. For
 * example, when implementing {@link StoredProcHeader#getHeaderValues()}, prefer:
 * <p />
 * {@code Map<String, Object> parmValues = new HashMap<String, Object>(); } <br />
 * {@code (add in injected package and other values) } <br />
 * {@code parmValues.put(CommonStoredProcHeaderParameters.ETEXT, StoredProcConstants.SPACE_STRING); }
 * <p />
 * instead of
 * <p />
 * {@code Map<String, Object> parmValues = new HashMap<String, Object>(); } <br />
 * {@code (add in injected package and other values) <br /> } {@code parmValues.put("ETEXT", " "); }
 * <p />
 * <p />
 * Using this constants class reduces the number of unnecessary object instances and also reduces the chance for typos.
 * <p />
 * This class is also extensible for teams that wish to add parameters. Simply create a class that extends CommonStoredProcHeaderParameters and add
 * your own parameters, then the subclass can add to the list provided by CommonStoredProcHeaderParameters.getSqlParameters()
 */
public class CommonStoredProcHeaderParameters {

    /**
     * Must not be private since teams are allowed to subclass, e.g. for ImtUpdateStoredProcHeaderParameters. This enables flexibility to change one
     * class and have it update all of the parameters in application code. However, there should not be any reason to directly instantiate this class
     * within application code.
     */
    protected CommonStoredProcHeaderParameters() {

    }

    // Parameter name constants

    public static final String PACKAGE = "PACKAGE";

    public static final String APPNM = "APPNM";

    public static final String USERID = "USERID";

    public static final String APPLID = "APPLID";

    public static final String TS = "TS";

    public static final String ESEVERE = "ESEVERE";

    public static final String ENUM = "ENUM";

    public static final String EGROUP = "EGROUP";

    public static final String ESQLCD = "ESQLCD";

    public static final String EPGM = "EPGM";

    public static final String ETEXT = "ETEXT";

    public static final String EDBAVAIL = "EDBAVAIL";

    // SqlParameter constants

    public static final SqlParameter PACKAGE_PARAMETER = SqlParameterFactory.createString(PACKAGE, SqlParameterModes.IN);

    public static final SqlParameter APPNM_PARAMETER = SqlParameterFactory.createString(APPNM, SqlParameterModes.IN);

    public static final SqlParameter USERID_PARAMETER = SqlParameterFactory.createString(USERID, SqlParameterModes.IN);

    public static final SqlParameter APPLID_PARAMETER = SqlParameterFactory.createString(APPLID, SqlParameterModes.IN);

    public static final SqlParameter TS_PARAMETER = SqlParameterFactory.createString(TS, SqlParameterModes.INOUT);

    public static final SqlParameter ESEVERE_PARAMETER = SqlParameterFactory.createString(ESEVERE, SqlParameterModes.INOUT);

    public static final SqlParameter ENUM_PARAMETER = SqlParameterFactory.createInteger(ENUM, SqlParameterModes.INOUT);

    public static final SqlParameter EGROUP_PARAMETER = SqlParameterFactory.createInteger(EGROUP, SqlParameterModes.INOUT);

    public static final SqlParameter ESQLCD_PARAMETER = SqlParameterFactory.createInteger(ESQLCD, SqlParameterModes.INOUT);

    public static final SqlParameter EPGM_PARAMETER = SqlParameterFactory.createString(EPGM, SqlParameterModes.INOUT);

    public static final SqlParameter ETEXT_PARAMETER = SqlParameterFactory.createString(ETEXT, SqlParameterModes.INOUT);

    public static final SqlParameter EDBAVAIL_PARAMETER = SqlParameterFactory.createString(EDBAVAIL, SqlParameterModes.INOUT);

    public static List<SqlParameter> getSqlParameters() {
        List<SqlParameter> commonHeaderParameters = new ArrayList<SqlParameter>();
        commonHeaderParameters.add(PACKAGE_PARAMETER);
        commonHeaderParameters.add(APPNM_PARAMETER);
        commonHeaderParameters.add(USERID_PARAMETER);
        commonHeaderParameters.add(APPLID_PARAMETER);
        commonHeaderParameters.add(TS_PARAMETER);
        commonHeaderParameters.add(ESEVERE_PARAMETER);
        commonHeaderParameters.add(ENUM_PARAMETER);
        commonHeaderParameters.add(EGROUP_PARAMETER);
        commonHeaderParameters.add(ESQLCD_PARAMETER);
        commonHeaderParameters.add(EPGM_PARAMETER);
        commonHeaderParameters.add(ETEXT_PARAMETER);
        commonHeaderParameters.add(EDBAVAIL_PARAMETER);
        return commonHeaderParameters;
    }
}

import com.company.spring.storedproc.StoredProcHeader;

/**
 * Base class for stored procedure headers, providing a schema field with a setter for dependency injection. Application teams should typically extend
 * this class instead of directly implementing StoredProcHeader.
 * 
 * @see StoredProcHeader
 */
public abstract class AbstractStoredProcHeader implements StoredProcHeader {

    private String schemaName;

    public void setSchemaName(String schema) {
        this.schemaName = schema;
    }

    public String getSchemaName() {
        return this.schemaName;
    }
}

import javax.inject.Named;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;

import com.company.spring.storedproc.core.AbstractStoredProcedure;

public abstract class RetailAbstractStoredProcedure extends
		AbstractStoredProcedure {

	@Autowired
	@Named("enterprise-database-DataSource") 
	private DataSource dataSource;

	@Override
	protected DataSource getDataSource() {
		return dataSource;
	}

	@Override
	public boolean isTrimmingResults() {
		return true;
	}
}

import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.SqlParameter;

/**
 * Provides common headers and schema configuration for stored procedures
 */
public interface StoredProcHeader {

    /**
     * Region specific schema name - based on configuration
     * 
     * @return the schema to use
     */
    String getSchemaName();

    /**
     * Header parameter types returned as SqlParameters
     * 
     * @return List<SqlParameter> containing the header types
     */
    List<SqlParameter> getHeaderTypes();

    /**
     * Values for defined header types
     * 
     * @return Map containing the header values where the keys are header parameter names
     */
    Map<String, Object> getHeaderValues();
}

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MarkerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetSupportingSqlParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.jdbc.support.MetaDataAccessException;

import com.company.logging.api.logger.ReportLogger;
import com.company.logging.api.performance.Profiler;
import com.company.spring.storedproc.StoredProcHeader;
import com.company.spring.storedproc.StoredProcInitializationException;
import com.company.spring.storedproc.StoredProcPerformanceMonitor;

/**
 * An AbstractStoredProcedure is a multi-threaded, reusable base object representing a call to a stored procedure. It is designed for extension with
 * subclasses providing procedure name and call arguments.
 * 
 * <p />
 * The following collaborating objects are needed for processing:
 * <ol>
 * <li>Configured javax.sql.DataSource</li>
 * <li>Stored procedure name</li>
 * <li><b>Optional</b> An implementation of {@link StoredProcHeader}</li>
 * <li><b>Optional</b> {@link StoredProcPerformanceMonitor}, for overriding default values used when logging SP execution time</li>
 * <li><b>Optional</b> subclass of {@link StoredProcErrorTranslator}. If not provided, the default behavior of {@link StoredProcErrorTranslator} will
 * be used.</li>
 * </ol>
 * 
 * <p />
 * This class also provides the option to use metadata processing to simplify the code needed to access basic stored procedures/functions. By default
 * this functionality is disabled, but can be enabled by overriding the {@link #isUsingMetaData() isUsingMetaData(boolean)} method. The metadata
 * processing is based on the DatabaseMetaData provided by the JDBC driver. Since we rely on the JDBC driver, this "auto-detection" can only be used
 * for databases that are known to provide accurate metadata. These currently include DB2, Oracle, Derby, MySQL, and Microsoft SQL Server. For any
 * other databases you are required to declare all parameters explicitly. You can of course declare all parameters explicitly even if the database
 * provides the necessary metadata. In that case your declared parameters will take precedence. You can also turn off any metadata processing if you
 * want to use parameter names that do not match what is declared during the stored procedure compilation.
 * <p />
 * <b>Note:</b> the Stored Procedure Abstraction module is currently intended for use against DB2 and Oracle stored procedures only. For all other 
 * database interactions, we recommend that you use Spring's SimpleJdbcTemplate directly. Contact NGSA for more information if you have any questions.
 * 
 * @see java.sql.DatabaseMetaData
 * @see StoredProcErrorTranslator
 * @see StoredProcHeader
 * @see org.springframework.jdbc.core.simple.SimpleJdbcCall
 */
public abstract class AbstractStoredProcedure {

    protected static final String STALE_CONNECTION_EXCEPTION = "StaleConnectionException";

    protected static final String DISCONNECT_EXCEPTION = "DisconnectException";    
    
    private static final List<SqlParameter> EMPTY_PROCEDURE_PARAMETERS = Collections.emptyList();

    private static final List<? extends ResultSetSupportingSqlParameter> EMPTY_RESULTSETS = Collections.emptyList();

    private static final Map<String, Integer> EMPTY_RETRY_VALUES = Collections.emptyMap();
    
    private static final Map<String, Object> EMPTY_PARAMETER_MAP = Collections.emptyMap();

    private static final String LOG_CODE_WARN_NO_PROFILER_SYSTEM_VALUE = "2032040";
    
    private static final String LOG_CODE_WARN_RETRY_INVOKED = "2032042";
    
    private static final String LOG_CODE_WARN_EXHAUSTED_RETRY_ATTEMPTS = "2032044";
    
    private static final int LOG_CODE_REPORT_RECOVERED_FROM_RETRYABLE_FAILURE = 2032045;

	private static final String LOG_CODE_ALERT_FAILED_CONNECTION_WHILE_GETTING_PROFILER = "2032046";
	    
    private String spAttemptedTask;

    private StoredProcErrorTranslator spErrorTranslator;

    private SimpleJdbcCall spExecutor;
    
    private final String className = this.getClass().getName();
    
	Logger logger = LoggerFactory.getLogger(className);
    
    private boolean useMetaData = false;

    private String perfSystem = StringUtils.EMPTY;

    private List<SqlParameter> orderedParameters;

    /**
     * Called after all of the properties were set on the bean to perform additional validate/configuration of the component
     */
    @PostConstruct
    protected final void init() {
    	logger.trace("Starting initialization logic.");

        StoredProcHeader header = getHeader();
                      
        if(isTrimmingResults()){
        	logger.trace("Will be trimming result data.");
        }
        
        setExecutor(new SimpleJdbcCall(new VanguardJdbcTemplate(getDataSource(), isTrimmingResults())).withProcedureName(getName()));
        
        if (header != null && StringUtils.isNotBlank(header.getSchemaName())) {
            spAttemptedTask = String.format("%1$s.%2$s (%3$s)", header.getSchemaName(), getName(), ClassUtils.getShortClassName(this.getClass()));
            getExecutor().withSchemaName(header.getSchemaName());
        } else {
            spAttemptedTask = String.format("%1$s (%2$s)", getName(), ClassUtils.getShortClassName(this.getClass()));
        }
        
        getExecutor().setAccessCallParameterMetaData(isUsingMetaData());
        getExecutor().getJdbcTemplate().setResultsMapCaseInsensitive(true);

        setupErrorTranslator();
        setupParameters();

        onInit();

        logger.trace("Successfully completed all initialization logic.");
    }

    /**
     * Setup the error translator to be used, creating a default error translator if not provided with one
     */
    void setupErrorTranslator() {
        StoredProcErrorTranslator customTranslator = getErrorTranslator();
        if (customTranslator == null) {
            this.spErrorTranslator = new StoredProcErrorTranslator(getExecutor().getJdbcTemplate().getDataSource());
        } else {
        	logger.trace("Using custom implementation of error translator: {}", customTranslator.getClass().getSimpleName());
            this.spErrorTranslator = customTranslator;
        }
        getJdbcTemplate().setExceptionTranslator(this.spErrorTranslator);
    }

    /**
     * Use a method to get the perfSystem variable
     * in order to wrap setup of variable if it has
     * not been set already.
     */
	String getPerfSystem() throws RuntimeException {
		if (perfSystem == null) {
			setupPerformanceProfiler();
		}
		return perfSystem;
	}

	/**
	 * Setup logging of stored procedure execution time. Typically the system
	 * value will be a DB2 database name, extracted from a type 4 driver URL.
	 */
	void setupPerformanceProfiler() throws RuntimeException {
		synchronized (this) {
			if (perfSystem != null) {
				return;
			}
			
			StoredProcPerformanceMonitor spPerformanceMonitor = getPerformanceMonitor();

			if (spPerformanceMonitor != null && StringUtils.isNotEmpty(spPerformanceMonitor.getSystem())) {
				perfSystem = spPerformanceMonitor.getSystem();
				return;
			}

			try {
				String dbURL = (String) JdbcUtils.extractDatabaseMetaData(getDataSource(), "getURL");
				if (org.springframework.util.StringUtils.startsWithIgnoreCase(dbURL, "jdbc:db2:")) {
					perfSystem = StringUtils.substringAfterLast(dbURL, "/");
				} else if (org.springframework.util.StringUtils.startsWithIgnoreCase(dbURL, "jdbc:oracle:")) {
					perfSystem = StringUtils.substringAfterLast(dbURL, ":");
				} else {
					perfSystem = dbURL;
				}
				
				logger.trace("Successfully extracted this profiler system value from the DataSource: {}", perfSystem);
			} catch (MetaDataAccessException e) {
				Throwable cause = e.getCause();

				if (cause instanceof CannotGetJdbcConnectionException) {
					logger.error(MarkerFactory.getMarker(LOG_CODE_ALERT_FAILED_CONNECTION_WHILE_GETTING_PROFILER), 
							"Failure to connect to the database and extract the URL from its metadata - either an error in the URL, "
							+ "credentials (including access/permissions), or the database is down.", e);
					throw (CannotGetJdbcConnectionException) cause;
				}
				
				logger.warn(MarkerFactory.getMarker(LOG_CODE_WARN_NO_PROFILER_SYSTEM_VALUE), 
						"Could not extract database URL from DataSource to generate profiler system value, non-fatal so proceeding.", e);
				perfSystem = StringUtils.EMPTY;
			}
		}

	}


    /**
     * Is metadata being used to determine parameters and SQL types? Override and set to true if the stored procedure should use database metadata 
     * (check with your tech leads/architects first, since this causes an additional round trip to the database).
     * 
     * @return boolean indicating if metadata is in use, the default value is false
     */
    public boolean isUsingMetaData() {
        return this.useMetaData;
    }

    /**
     * Is trimming of the following values returned by SP execution needed? <br />
     * (1) Output (in/out and out mode) parameter values that are Strings, and <br />
     * (2) ResultSet values that are Strings
     * <p />
     * Typically this should be needed only within Retail, and should only be used when needed to avoid unnecessary overhead/processing. Trimming is
     * done using StringUtils.trimToEmpty(String):
     * <p />
     * <b>Note:</b> this also will convert any null BigDecimal values (ResultSet or output parameter) to BigDecimal.ZERO
     * 
     * @return boolean indicating if trimming is done, the default value is false
     */
    public boolean isTrimmingResults() {
        return false;
    }

    /**
     * AbstractStoredProcedure subclasses may override this method to provide custom parameter values for logging stored procedure execution time to
     * the TE VgRealtimePerfMonitor (RTPM)
     * 
     * @return StoredProcPerformanceMonitor subclass with values for the RTPM call
     */
    public StoredProcPerformanceMonitor getPerformanceMonitor() {
        return null;
    }

    /**
     * Executes the stored procedure with the provided procedure values, logging SP execution time using the provided correlation id value which ties
     * multiple IO performance events together.
     * <p />
     * Callers should provide a non-empty correlation id value (which must not contain SSN or other sensitive data) for logging SP execution time. Use
     * this when the logged on user is a Vanguard associate accessing data for a client, or when there is no logged on user. If null or an empty
     * correlation id value is provided, the default behavior (execute(Map<String,Object>)) will occur.
     * 
     * @param procedureParameters
     *            the values for procedure parameters
     * @param perfCorrelationId
     *            the correlation id to use when logging stored procedure execution time; it must not contain SSN or other sensitive data
     * @return Map with any output parameters and objects representing ResultSets
     * @throws RuntimeException
     *             if an error occurs while executing the procedure
     */
    public Map<String, Object> execute(Map<String, Object> procedureParameters, String perfCorrelationId) throws DataAccessException {

        // Create defensive copy because application could send immutable Map
        Map<String, Object> inputParameters = new HashMap<String, Object>();

        // The header parameter values must be added before the procedure parameter values, since
        // the procedure parameters may override header parameters for runtime values (e.g. userId)
        
        if (getHeader() != null) {        
            inputParameters.putAll(getHeader().getHeaderValues());
        }
        inputParameters.putAll(procedureParameters);
        
        // Add default IBM stale connection exceptions thrown from WebSphere-based DataSources (DB2, Oracle UCP, etc.) 
        // With the EntirePool purge policy setting, all connections will be flushed so only one retry is necessary to open a new connection
        // Use LinkedHashMap to retain order, in case the provided Map is also a LinkedHashMap
        Map<String, Integer> retryValues = new LinkedHashMap<String, Integer>();
        retryValues.putAll(declareRetryValues());
        if(!retryValues.containsKey(STALE_CONNECTION_EXCEPTION)) {
            retryValues.put(STALE_CONNECTION_EXCEPTION, 1);
        }
        if(!retryValues.containsKey(DISCONNECT_EXCEPTION)) {
            retryValues.put(DISCONNECT_EXCEPTION, 1);
        }            
        
        String trimmedCorrelationId = StringUtils.trimToEmpty(perfCorrelationId);
        Profiler profiler = Profiler.createProfile(this.getClass(), getName(), getPerfSystem(), trimmedCorrelationId);
        Map<String, Object> results;

        logger.trace(this.spErrorTranslator.buildParameterNameValueMessage(spAttemptedTask, inputParameters, orderedParameters, "Input parameters for"));
        logger.trace(this.spErrorTranslator.buildSessionCaptureToolMessage(spAttemptedTask, inputParameters, orderedParameters, "Session Capture Debug Tool Format : Input parameters for"));
        logger.trace("Starting {} execution(s) using profiler correlation id: {}", getName(), trimmedCorrelationId);
        
        // Ensure profiler stop is called whether or not the SP execution throws an exception
        profiler.start();
        try {
            results = this.spExecutor.execute(inputParameters);            
        } catch (Exception ex) {
            profiler.stop();            
            Exception lastException = ex;
            int numberOfRetries = 0;

            for(Map.Entry<String, Integer> retryValueEntry : retryValues.entrySet()) {
                if(ex.toString().contains(retryValueEntry.getKey())) {
                    numberOfRetries = retryValueEntry.getValue();
                    logger.warn(MarkerFactory.getMarker(LOG_CODE_WARN_RETRY_INVOKED), 
                    		"{} execution failed and contains retryable value [{}], will retry execution up to [{}] times. Root cause: [{}.{}] Stacktrace: ",
                    		getName(), retryValueEntry.getKey(), numberOfRetries, 
                    		ClassUtils.getPackageName(ExceptionUtils.getRootCause(ex), ClassUtils.getPackageName(ex, StringUtils.EMPTY)),
                    		ExceptionUtils.getRootCauseMessage(ex), ex);
                    break;
                }
            }
            
            for(int i = 1; i <= numberOfRetries; i++) {
                Profiler retryProfiler = Profiler.createProfile(this.getClass(), getName(), getPerfSystem(), trimmedCorrelationId);
                retryProfiler.start();
                try {
                    results = this.spExecutor.execute(inputParameters);
                    retryProfiler.stop();
                    
                    ReportLogger.report(this.getClass(), LOG_CODE_REPORT_RECOVERED_FROM_RETRYABLE_FAILURE, "%s successfully recovered upon retry execution [%s] (1-based scale).", getName(), i);
                    return results;
                }
                catch(Exception ex2) {
                    retryProfiler.stop();
                    lastException = ex2;
                }                
            }
            
            logger.warn(MarkerFactory.getMarker(LOG_CODE_WARN_EXHAUSTED_RETRY_ATTEMPTS), "{} retry executions exhausted without success, will not retry again and will now throw the translated exception after [{}] retries. Root cause: [{}.{}] Stacktrace: ",
            		getName(), numberOfRetries, ClassUtils.getPackageName(ExceptionUtils.getRootCause(lastException), ClassUtils.getPackageName(lastException, StringUtils.EMPTY)), 
            		ExceptionUtils.getRootCauseMessage(lastException), lastException);
                        
            RuntimeException runtimeException = spErrorTranslator.translateFailedSpExecution(spAttemptedTask, inputParameters, orderedParameters, lastException);            

            logger.trace("Finished {} execution(s) using profiler correlation id: {}", getName(), trimmedCorrelationId);
            logger.trace("Translated exception: ", runtimeException);
            throw runtimeException;
        }       
        profiler.stop();

        logger.trace("Finished {} execution(s) using profiler correlation id: {}", getName(), trimmedCorrelationId);

        inputParameters.putAll(results);
        inspectResultForErrors(inputParameters, orderedParameters);
        return results;
    }

    /**
     * Executes the stored procedure with the provided procedure values, using the default Authenticated User id (POID/Cinergy id) retrieved from
     * ThreadStorage as the correlation id value for logging SP execution time. This default is ideal when a client is logged on since it helps narrow
     * down any potential performance problems down to that specific client.
     * 
     * @param procedureParameters
     *            the values for procedure parameters
     * @return Map with any output parameters and objects representing ResultSets
     * @throws RuntimeException
     *             if an error occurs while executing the procedure
     */
    public Map<String, Object> execute(Map<String, Object> procedureParameters) throws RuntimeException { // parasoft-suppress CODSTA.BP.NTX "2010.06.09-22:36:00 Must support RuntimeException per TT1407"
        return execute(procedureParameters, StringUtils.EMPTY);
    }

    /**
     * Executes the stored procedure with no procedure values, logging SP execution time using the provided correlation id value which ties multiple
     * IO performance events together.
     * <p />
     * Callers should provide a non-empty correlation id value (which must not contain SSN or other sensitive data) for logging SP execution time. Use
     * this when the logged on user is a Vanguard associate accessing data for a client, or when there is no logged on user. If null or an empty
     * correlation id value is provided, the default behavior (same as execute()) will occur.
     * 
     * @param perfCorrelationId
     *            the correlation id to use when logging stored procedure execution time; it must not contain SSN or other sensitive data
     * @return Map with any output parameters and objects representing ResultSets
     * @throws RuntimeException
     *             if an error occurs while executing the procedure
     */
    public Map<String, Object> execute(String perfCorrelationId) throws RuntimeException { // parasoft-suppress CODSTA.BP.NTX "2010.06.09-22:36:00 Must support RuntimeException per TT1407"
        return execute(EMPTY_PARAMETER_MAP, perfCorrelationId);
    }

    /**
     * Executes the stored procedure with no procedure values, using the default Authenticated User id (POID/Cinergy id) retrieved from ThreadStorage
     * as the correlation id value for logging SP execution time. This default is ideal when a client is logged on since it helps narrow down any
     * potential performance problems down to that specific client.
     * 
     * @return Map with any output parameters and objects representing ResultSets
     * @throws RuntimeException
     *             if an error occurs while executing the procedure
     */
    public Map<String, Object> execute() throws RuntimeException { // parasoft-suppress CODSTA.BP.NTX "2010.06.09-22:36:00 Must support RuntimeException per TT1407"
        return execute(EMPTY_PARAMETER_MAP, StringUtils.EMPTY);
    }

    /**
     * Return declared procedure name, which will also be used during performance logging to TE's VgRealtimePerfMonitor (RTPM) through the
     * StoredProcPerformanceAspect
     * 
     * @return String the stored procedure name
     */
    public abstract String getName();

    /**
     * Sets the SimpleJdbcCall (JdbcTemplate) used to execute the procedure
     * 
     * @param call
     *            a SimpleJdbcCall
     */
    protected final void setExecutor(SimpleJdbcCall call) {
        this.spExecutor = call;
    }

    /**
     * Gets the SimpleJdbcCall (JdbcTemplate) used to execute the procedure
     * 
     * @return SimpleJdbcCall currently in use
     */
    protected final SimpleJdbcCall getExecutor() {
        return this.spExecutor;
    }

    /**
     * Gets the underlying JdbcTemplate from the SimpleJdbcCall
     * 
     * @return JdbcTemplate currently in use
     */
    protected final JdbcTemplate getJdbcTemplate() {
        return this.spExecutor.getJdbcTemplate();
    }

    /**
     * Prepares SP for usage by populating procedure parameters
     */
    protected final void setupParameters() {

        List<SqlParameter> params = new ArrayList<SqlParameter>();

        if (!isUsingMetaData()) {
            if (getHeader() != null) {
                params.addAll(getHeader().getHeaderTypes());
            }           
            params.addAll(declareParameters());
        } else {
        	logger.trace("Using database metdata to determine parameters.");
        }

        orderedParameters = new ArrayList<SqlParameter>();
        orderedParameters.addAll(params);

        params.addAll(declareResultSets());
        this.spExecutor.declareParameters(params.toArray(new SqlParameter[params.size()]));
    }

    /**
     * Inspects the stored procedure output parameters to determine if an error occurred
     * 
     * @param result
     *            the map of in/out parameter values returned by executing the stored procedure
     * @param orderedParameters
     *            an ordered list of all SqlParameters used in this stored procedure
     * @throws RuntimeException
     *             if the procedure returns with an error
     */
    protected final void inspectResultForErrors(Map<String, Object> result, List<SqlParameter> orderedParameters) throws RuntimeException { // parasoft-suppress CODSTA.BP.NTX "2010.05.19-19:08:00 Must support RuntimeException per TT1407"
    	logger.trace("Inspecting stored procedure execution result for errors.");

        if (this.spErrorTranslator.isError(result)) {
            RuntimeException exception = this.spErrorTranslator.translate(spAttemptedTask, this.spExecutor.getCallString(), result, orderedParameters);
            if (exception != null) {
            	logger.trace("Translated exception: ", exception);
                throw exception;
            }
        }
        
        logger.trace(this.spErrorTranslator.buildParameterNameValueMessage(spAttemptedTask, result, orderedParameters, "Output parameters for"));
        logger.trace(this.spErrorTranslator.buildSessionCaptureToolMessage(spAttemptedTask, result, orderedParameters, "Session Capture Debug Tool Format : Output parameters for"));
        logger.trace("No error was found/no exception was thrown.");        
    }

    /**
     * Subclasses may override this method to provide a custom error translator. Otherwise the default StoredProcErrorTranslator behavior will be
     * used.
     * 
     * @return the error translator used for this stored procedure
     */
    protected StoredProcErrorTranslator getErrorTranslator() {
        return this.spErrorTranslator;
    }

    /**
     * Optional call back for subclasses to add initialization processing.  For example, this can be used 
     * to register a NativeJdbcExtractor.
     * 
     * @throws StoredProcInitializationException
     */
    protected void onInit() throws StoredProcInitializationException {

    }

    /**
     * Returns declared procedure parameters (not including header parameters defined through {@link #getHeader()}). Override if the stored procedure
     * contains any non-header parameters.
     * 
     * @return List<SqlParameter> containing procedure parameters
     */
    protected List<SqlParameter> declareParameters() {
        return EMPTY_PROCEDURE_PARAMETERS;
    }

    /**
     * Returns declared ResultSets, including any RowMappers/mapper objects used for building them. Override if the stored procedure returns any
     * ResultSets.
     * 
     * @return List<? extends ResultSetSupportingSqlParameter> containing ResultSet declarations
     */
    protected List<? extends ResultSetSupportingSqlParameter> declareResultSets() {
        return EMPTY_RESULTSETS;
    }
    
    /**
     * Returns a Map containing String (retry values) and Integer (number of retries) entries. The Strings represent values that, if found within an exception 
     * thrown during SP execution as defined by exception.toString().contains(retry value), would cause a retry of the SP execution. These String values typically can be 
     * Exception names, but in certain cases, Exception names may not be unique enough and instead you may need to provide a String containing an error code and/or state
     * (e.g. "SQL state [null]; error code [17410]"). The Integers represent the number of retry attempts that should occur if the corresponding String is found within an 
     * exception thrown during SP execution.  For example, the stale connection exception names for an application using WebSphere's connection pool ("StaleConnectionException", 
     * "DisconnectException") would need to be retried one time (if the entire pool is flushed on a stale connection) - see below for additional info on this case.
     * <p />
     * If there is a specific order that should be used, return a LinkedHashMap (which supports priority based on insertion order).  Please note that the number of retries do not stack -- 
     * the first match found within the exception.toString() value will determine the number of retry attempts, and the Map is not used during retry attempts (i.e. no retries of retries). 
     * Any successful SP execution will immediately kick out of the retry attempt loop and return to the caller.
     * <p />
     * The default behavior will be to add the stale connection exception example above ({"StaleConnectionException", 1}, {"DisconnectException", 1}) if the two exception 
     * names are not already populated in the returned map. Override if the stored procedure should use additional or different retry values.
     * 
     * @return Map<String, Integer> containing retry values
     */
    protected Map<String, Integer> declareRetryValues() {
        return EMPTY_RETRY_VALUES;
    }

    /**
     * Return the stored procedure header used for procedure invocations
     * 
     * @return StoredProcHeader containing header parameter types and/or values
     */
    protected StoredProcHeader getHeader() {
        return null;
    }

    /**
     * Return the DataSource used by the JdbcTemplate. This DataSource should be provided to custom StoredProcErrorTranslator subclasses.
     * 
     * @return DataSource used to execute this procedure
     */
    protected abstract DataSource getDataSource();
}

import org.springframework.dao.DataAccessException;

/**
 * Application teams can throw this within an overriden {@link com.vanguard.spring.storedproc.core.AbstractStoredProcedure#onInit()} method
 */
public class StoredProcInitializationException extends DataAccessException {

    /**
     * Default serializable id.
     */
    private static final long serialVersionUID = 1L;

    public StoredProcInitializationException(String anMsg) {
        super(anMsg);
    }

    public StoredProcInitializationException(String anMsg, Throwable anCause) {
        super(anMsg, anCause);
    }
}
