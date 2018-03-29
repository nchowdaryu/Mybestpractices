import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.CharUtils;
import org.apache.commons.lang.StringUtils;

/**
 * Enables dynamic property lookup of property values loaded through Vanguard Spring Core, and falls back to system properties.  Similar API to TE global settings (ApplicationProperty).  ConcurrentHashMap does not permit 
 * storing null values but this is not a concern for our implementation (empty String default).
 * 
 * @author u16t
 */
public final class PropertyLocator {

    private static final PropertyLocator INSTANCE = new PropertyLocator();

    // Use the default capacity and load factor, expect concurrent updates to be approximately at most 2 threads (i.e. always total refresh, perhaps from JMX)
    // Updates/refresh capability will be added upon need by project teams

    private final ConcurrentHashMap<String, String> propertyMap = new ConcurrentHashMap<String, String>(16, 0.75f, 2);
    
    private static final Pattern placeHolderPattern = Pattern.compile("\\Q$\\E.*\\{.*\\}.*");
    

    /**
     * Retrieve a property value loaded through Vanguard Spring Core, falling back to System properties (in that case, moduleId is ignored).
     * 
     * @param moduleId The module id this property is registered with 
     * @param propertyName The property name to look up
     * @return String The corresponding property value
     * @throws PropertyNotFoundException If the property cannot be found
     */
    public static String getStringProperty(String moduleId, String propertyName) throws PropertyNotFoundException {
        
    	String propertyValue = resolvePropertyName(moduleId, propertyName);    	
        Matcher matcher = placeHolderPattern.matcher(propertyValue);
        
        if (matcher.find()){
        	propertyValue = resolveNestedPlaceholders(propertyValue);
        } 
        
       	return propertyValue;
    }
    
    private static String resolvePropertyName(String moduleId, String propertyName) throws PropertyNotFoundException {
        
        String propertyKey = createPropertyKey(moduleId, propertyName);
        if (getInstance().getPropertyMap().containsKey(propertyKey)) {
            return getInstance().getPropertyMap().get(propertyKey);
        }

        String systemPropertyValue = System.getProperty(propertyName);
        if (systemPropertyValue != null) {
            return systemPropertyValue;
        }

        throw new PropertyNotFoundException(moduleId, propertyName);
    }
    
    private static String resolveNestedPlaceholders(String placeholder){

    	String tempPlaceholder = buildResolvedPlaceholder(placeholder);
        Matcher matcher = placeHolderPattern.matcher(tempPlaceholder);
    	
    	if (matcher.find()){
    		tempPlaceholder = resolveNestedPlaceholders(tempPlaceholder);
    		return tempPlaceholder;
    	}else{
    		return tempPlaceholder;
    	}    	
    }
    
    private static String buildResolvedPlaceholder(String placeholder){
    	String propName = getLastPropNameFromPlaceholder(placeholder);
    	String propModuleId = getLastModuleIdFromPlaceholder(placeholder);
    	String resolvedPropComponent = resolvePropertyName(propModuleId, propName);
    	return replacePlaceholderWithNewValue(placeholder, resolvedPropComponent);
    }
    
    //This method finds the property name from the last and most deeply nested placeholder.
    //It does not matter if it is a system property or one that has been stored in the property hash map.
    private static String getLastPropNameFromPlaceholder(String placeholder){    	
    	return placeholder.substring((placeholder.lastIndexOf("{")+1), placeholder.indexOf("}", placeholder.lastIndexOf("{")));
    }
    
    //This method finds the module id from the last and most deeply nested placeholder.
    //As a result, users can create nested property placeholder definitions for which each level of nesting
    //can refer to any module id.
    private static String getLastModuleIdFromPlaceholder(String placeholder){    	
    	return placeholder.substring((placeholder.lastIndexOf("$")+1), (placeholder.indexOf("{", placeholder.lastIndexOf("$"))-1));
    }
    
   private static String replacePlaceholderWithNewValue(String placeholder, String resolvedPropComponent){
    	
    	String firstStr = placeholder.substring(0, placeholder.lastIndexOf("$"));
    	String lastStr = "";
    	
    	//The following if statement protects against index out of bounds errors on the substring.
    	//If true, there are hard coded characters after the placeholder that must be returned.
    	if (placeholder.lastIndexOf("}") != placeholder.length())
    		lastStr = placeholder.substring((placeholder.indexOf("}", placeholder.lastIndexOf("{"))+1), placeholder.length());
    	
     	return firstStr + resolvedPropComponent + lastStr;
    }


    /**
     * Retrieve a property value loaded through Vanguard Spring Core, falling back to System properties (in that case, moduleId is ignored). 
     * Returns an Integer object.
     * 
     * @param moduleId The module id this property is registered with 
     * @param propertyName The property name to look up
     * @return Integer The corresponding property value as an Integer
     * @throws PropertyNotFoundException If the property cannot be found
     * @throws NumberFormatException If the property value cannot be converted to Integer
     */
    public static Integer getIntegerProperty(String moduleId, String propertyName) throws PropertyNotFoundException, NumberFormatException {
        return Integer.valueOf(getStringProperty(moduleId, propertyName));
    }

    /**
     * Retrieve a property value loaded through Vanguard Spring Core, falling back to System properties (in that case, moduleId is ignored). 
     * Returns a Long object.
     * 
     * @param moduleId The module id this property is registered with 
     * @param propertyName The property name to look up
     * @return Long The corresponding property value as a Long
     * @throws PropertyNotFoundException If the property cannot be found
     * @throws NumberFormatException If the property value cannot be converted to Long
     */
    public static Long getLongProperty(String moduleId, String propertyName) throws PropertyNotFoundException, NumberFormatException {
        return Long.valueOf(getStringProperty(moduleId, propertyName));
    }

    /**
     * Retrieve a property value loaded through Vanguard Spring Core, falling back to System properties (in that case, moduleId is ignored). 
     * Returns an Short object.
     * 
     * @param moduleId The module id this property is registered with 
     * @param propertyName The property name to look up
     * @return Short The corresponding property value as a Short
     * @throws PropertyNotFoundException If the property cannot be found
     * @throws NumberFormatException If the property value cannot be converted to Short
     */
    public static Short getShortProperty(String moduleId, String propertyName) throws PropertyNotFoundException, NumberFormatException {
        return Short.valueOf(getStringProperty(moduleId, propertyName));
    }

    /**
     * Retrieve a property value loaded through Vanguard Spring Core, falling back to System properties (in that case, moduleId is ignored). 
     * Returns a Byte object.
     * 
     * @param moduleId The module id this property is registered with 
     * @param propertyName The property name to look up
     * @return Byte The corresponding property value as a Byte
     * @throws PropertyNotFoundException If the property cannot be found
     * @throws NumberFormatException If the property value cannot be converted to Byte
     */
    public static Byte getByteProperty(String moduleId, String propertyName) throws PropertyNotFoundException, NumberFormatException {
        return Byte.valueOf(getStringProperty(moduleId, propertyName));
    }

    /**
     * Retrieve a property value loaded through Vanguard Spring Core, falling back to System properties (in that case, moduleId is ignored). 
     * Returns a Double object.
     * 
     * @param moduleId The module id this property is registered with 
     * @param propertyName The property name to look up
     * @return Double The corresponding property value as a Double
     * @throws PropertyNotFoundException If the property cannot be found
     * @throws NumberFormatException If the property value cannot be converted to Double
     */
    public static Double getDoubleProperty(String moduleId, String propertyName) throws PropertyNotFoundException, NumberFormatException {
        return Double.valueOf(getStringProperty(moduleId, propertyName));
    }

    /**
     * Retrieve a property value loaded through Vanguard Spring Core, falling back to System properties (in that case, moduleId is ignored). 
     * Returns a Float object.
     * 
     * @param moduleId The module id this property is registered with 
     * @param propertyName The property name to look up
     * @return Float The corresponding property value as a Float
     * @throws PropertyNotFoundException If the property cannot be found
     * @throws NumberFormatException If the property value cannot be converted to Float
     */
    public static Float getFloatProperty(String moduleId, String propertyName) throws PropertyNotFoundException, NumberFormatException {
        return Float.valueOf(getStringProperty(moduleId, propertyName));
    }

    /**
     * Retrieve a property value loaded through Vanguard Spring Core, falling back to System properties (in that case, moduleId is ignored). 
     * Returns a Boolean object.
     * 
     * @param moduleId The module id this property is registered with 
     * @param propertyName The property name to look up
     * @return Boolean The corresponding property value as a Boolean
     * @throws PropertyNotFoundException If the property cannot be found
     */
    public static Boolean getBooleanProperty(String moduleId, String propertyName) throws PropertyNotFoundException {
        return Boolean.valueOf(getStringProperty(moduleId, propertyName));
    }

    /**
     * Retrieve a property value loaded through Vanguard Spring Core, falling back to System properties (in that case, moduleId is ignored). 
     * Returns a Character object.
     * 
     * @param moduleId The module id this property is registered with 
     * @param propertyName The property name to look up
     * @return Character The corresponding property value as a Character, as defined by CharUtils.toChar(String)
     * @throws PropertyNotFoundException If the property cannot be found
     */
    public static Character getCharacterProperty(String moduleId, String propertyName) throws PropertyNotFoundException {
        return CharUtils.toChar(getStringProperty(moduleId, propertyName), ' ');
    }

    protected static void setProperty(String moduleId, String propertyName, String propertyValue) {
        getInstance().getPropertyMap().put(createPropertyKey(moduleId, propertyName), propertyValue);
    }

    static PropertyLocator getInstance() {
        return INSTANCE;
    }

    static String createPropertyKey(String... values) {
        return StringUtils.join(values);
    }

    private PropertyLocator() {

    }

    private ConcurrentHashMap<String, String> getPropertyMap() {
        return propertyMap;
    }
}

import org.apache.commons.lang.StringUtils;

/**
 * Exception thrown when a property cannot be found
 * 
 * @author u16t
 */
public class PropertyNotFoundException extends RuntimeException {

    private static final long serialVersionUID = -435845063953996862L;

    private static final String ERROR_NO_PROPERTY_FOUND = "No property found for module [%1$s] and property name [%2$s]. ";

    private String moduleId = StringUtils.EMPTY;

    private String propertyName = StringUtils.EMPTY;

    public PropertyNotFoundException(String moduleId, String propertyName) {
        this.moduleId = moduleId;
        this.propertyName = propertyName;
    }

    @Override
    public String toString() {
        return String.format(ERROR_NO_PROPERTY_FOUND, moduleId, propertyName);
    }
}
