package imt.auto.junit;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.Test;
import org.powermock.api.easymock.PowerMock;


/**
 * @author umj5
 *
 */
public class AutoPojoTester {

    private Object classToTest;
    private HashMap<String, Method> allMethodsMap = new HashMap<String, Method>(0);;
    private Method[] allMethodsArray;
    private int idxOfSetterMethod = 0;
    private String assertErrors = "";
    private String invokeErrors = "";
    private String ls = System.getProperty("line.separator");
    private List<String> methodNamesToNotTest = new ArrayList<String>(0);
    
        
    public void setupAutoPojoTester(Object classToTest) {
    	
    	this.classToTest = classToTest;
    	setAllMethodsArray();
        setAllMethodsMap();
    	
    }
                      
    @Test
    public void testAllGetterAndSetterMethods() {
    	    	    	
    	for (Method setterMethod: getListOfSetterMethods(allMethodsArray) ) {
    		setterMethod.setAccessible(true);
    		testSingleGetterAndSetterPair(setterMethod);
    		idxOfSetterMethod++;
    	
    	}
    	
    	if ((assertErrors.equalsIgnoreCase("") == false) || (invokeErrors.equalsIgnoreCase("") == false))  {
    		
    		System.out.print(assertErrors + invokeErrors );
    		fail("Errors Encountered while executing testAllGetterAndSetterMethods() - see console for details");
    		
    		
    	}
    		
    	
    }
    
    @Test
    public void testToString() {
    	
    	Method toStringMethod = (Method) allMethodsMap.get("toString");
    	Object valueFromToString = null;
    	
    	if (toStringMethod == null || isMethodNameInNotToTestList("toString")) {
    		return;
    	}
    	    	
    	try {
    		valueFromToString = toStringMethod.invoke(classToTest);
		}
		catch (Exception e) {
			System.out.print(ls + "The following errors occurred while testing the toString Method:" + ls + e.getMessage());
    		fail("Errors Encountered while executing testToString() - see console for details");
						
		}
		
		assertNotNull(valueFromToString);
    	
    }
    
    @Test
    public void testEquals() {
    	
    	Method equalsMethod = (Method) allMethodsMap.get("equals");
    	Boolean result1 = false;
    	Boolean result2 = true;
    	    	
    	if (equalsMethod == null || isMethodNameInNotToTestList("equals")) {
    		return;
    	}
    	    	
    	try {
    		result1 = (Boolean) equalsMethod.invoke(classToTest, classToTest);
    		Object newVersionOfClassToTest = PowerMock.createMock(classToTest.getClass());
    		result2 = (Boolean) equalsMethod.invoke(classToTest, newVersionOfClassToTest);
    		
		}
		catch (Exception e) {
			System.out.print(ls + "The following errors occurred while testing the equals Method:" + ls + e.getMessage());
    		fail("Errors Encountered while executing testEquals - see console for details");
						
		}
		
		assertTrue(result1);
		assertFalse(result2);
    	
    }
            
    public void addMethodNameToNotTestList(String methodName) {
    	
    	methodNamesToNotTest.add(methodName);
    	
    }
    
    protected boolean isMethodNameInNotToTestList(String methodName) {
    	
    	for (String methodNameToSkip : methodNamesToNotTest ) {
    		
    		if (methodName.equalsIgnoreCase(methodNameToSkip)) {
    			return true;
    		}
    	}
    	
    	return false;
    }
    
    protected boolean shouldGetterAndSetterBeSkipped(Method getterMethod, Method setterMethod) {
    	
    	/*
    	 *  Don't even try and test if the method names for the getters
    	 *  and setters don't match up
    	 *  
    	 */
    	if (getterMethod == null) 		{
			return true;
		}
    	
    	/*
    	 * Don't test the getter/setter pair if either the getter or setter
    	 * method name is in the not to test list
    	 * 
    	 */
    	if ( (isMethodNameInNotToTestList(setterMethod.getName())) ||
    		 (isMethodNameInNotToTestList(getterMethod.getName()))) {
    		return true;

    	}
    	
    	/*
    	 *  Skip the getter and setter pair if the return type is not the same as the
    	 *  parameter type
    	 */
    	if (!(getterMethod.getReturnType().getName().equalsIgnoreCase(setterMethod.getParameterTypes()[0].getName()))) {
    		return true;
    	}
    	
    	return false;
    }
    
    protected void testSingleGetterAndSetterPair(Method setterMethod) {
    	
    	Method associatedGetterMethod = getAssociatedGetterMethod(setterMethod);
    	    	
    	if (shouldGetterAndSetterBeSkipped(associatedGetterMethod, setterMethod)) {
    		return;
    	}
    	
		Object valueUsedInSetter = null;
		Object valueFromGetter = null;
								
		try {
			valueFromGetter = associatedGetterMethod.invoke(classToTest);
			valueUsedInSetter = getObjectValueToUseInSetter(associatedGetterMethod); 
			setterMethod.invoke(classToTest, valueUsedInSetter);
			valueFromGetter = associatedGetterMethod.invoke(classToTest);
		}
		catch (Exception e) {
			
			invokeErrors = invokeErrors + "invoke Error encountered while testing " + associatedGetterMethod.getName() + " and " + setterMethod.getName() + " Methods" +  ls + e.getMessage()  + ls + ls ;
				
		}
		
		verifyValueSetViaAssertions(valueUsedInSetter, valueFromGetter, associatedGetterMethod);
    	
    	
    }
    
    protected Object getObjectValueToUseInSetter(Method getterMethod) throws Exception {
    	    	   	    	
    	Object valueUsedInSetter;
    	
    	if (isReturnTypeAnArray(getterMethod)) {
    		valueUsedInSetter  = Array.newInstance(getterMethod.getReturnType().getComponentType(), 1);
    		return valueUsedInSetter;
       	}
    	
    	if (isReturnTypePrimitive(getterMethod)) {
    		
    		valueUsedInSetter = getterMethod.invoke(classToTest); 
    		valueUsedInSetter = getMeaningFullPrimitiveValue(valueUsedInSetter);
    		return valueUsedInSetter;
    		
    	}
    	
    	if (isReturnTypeEnum(getterMethod)) {
    		
    		Object[] constants = getterMethod.getReturnType().getEnumConstants(); 
    		valueUsedInSetter = constants[0];
    		return valueUsedInSetter;
    	}
    	    	    	    	    	
    	valueUsedInSetter = getterMethod.invoke(classToTest);
    	valueUsedInSetter = PowerMock.createMock(getterMethod.getReturnType());
    	
    	
    	return valueUsedInSetter; 
    	
    	
    }
    
    /**
     * 
     * This method was needed because reflection will cast a primitive to
     * It's related object; for instance boolean to Boolean;
     * 
     * Because of this, our assert must use AssertEquals instead of
     * AssertSame because the reflection invoke for the getter will
     * create a new object which is not the same object reference that
     * was passed into the setter.  As such assertSame will not work
     * for primitives.
     * 
     * Instead, we will create unique values in testing to assure
     * quality.
     * 
     * 
     * 
     * @param valueUsedInSetter
     * @return
     * @throws Exception
     */
    protected Object getMeaningFullPrimitiveValue(Object valueUsedInSetter) throws Exception {
    	
    	Class classUsedInSetter = valueUsedInSetter.getClass();
    	    	    	    	    	
    	if (Number.class.isAssignableFrom(classUsedInSetter)) {
    		valueUsedInSetter = getUniqueNumericValueForSetter(valueUsedInSetter);
    		return valueUsedInSetter;
    	}
    	
    	if (Boolean.class.isInstance(valueUsedInSetter)) {
    		valueUsedInSetter = Boolean.TRUE;
    		return valueUsedInSetter;
       	}
    	
    	if (Character.class.isInstance(valueUsedInSetter)) {
    		valueUsedInSetter = Character.UPPERCASE_LETTER;
    		return valueUsedInSetter;
       	}
    	
    	return valueUsedInSetter;
    	
    	
    	    	
    }
    
    // We will use a unique index of the setter method to insure unique values when we do the
    // assertEquals per getter/setter pair
    protected Object getUniqueNumericValueForSetter(Object valueUsedInSetter) throws Exception {
    	
    	Class classUsedInSetter = valueUsedInSetter.getClass();
    	Class[] paramString = new Class[1];	
    	paramString[0] = String.class;
    	
    	// The max value for a byte is 127
    	if (Byte.class.isInstance(valueUsedInSetter))
    	{
    		if (idxOfSetterMethod > 126) {
    			idxOfSetterMethod = 1;
    		}
    	}
    	
    	String integerString = Integer.toString(idxOfSetterMethod); 
   		Method anyNumberBasedMethod = classUsedInSetter.getDeclaredMethod("valueOf", paramString);
   		return  anyNumberBasedMethod.invoke(valueUsedInSetter.getClass(), integerString);
   		  
    }
    
    protected boolean isReturnTypePrimitive(Method getterMethod) {
    	
    	return getterMethod.getReturnType().isPrimitive();
    	
    }
    
    protected boolean isReturnTypeAnArray(Method getterMethod) {
    	
    	return getterMethod.getReturnType().isArray();
    	
    }
    
    protected boolean isReturnTypeEnum(Method getterMethod) {
    	
    	return getterMethod.getReturnType().isEnum();
    	
    }
          
    /**
     * 
     * This method was needed because java primitives are cast to their complex types
     * when calling invoke via reflection;
     * 
     * ie: int to Integer
     * 
     * Although the value will be the same; they will end up being represented by
     * different objects and the assertSame will fail.
     * 
     *  To address this; we will verify the value is the same via two levels of
     *  asserts: assertSame and assertEquals
     * 
     */
    protected void verifyValueSetViaAssertions(Object valueBeforeSetter, Object valueAfterSetter, Method getterMethod) {
    	
    	String additionalInfoForAssertSameError = "";
    	
    	try {
    	
    		if (isReturnTypePrimitive(getterMethod)) {
    			assertEquals(valueBeforeSetter, valueAfterSetter);
    			return;
    		}
    	
    		additionalInfoForAssertSameError =  ls + "The assert for the above failed because the getters and setters reference different variables";
    		assertSame(valueBeforeSetter, valueAfterSetter);
    	}
    	catch(AssertionError e) {
    		
    		assertErrors = assertErrors + "Assert Error encountered while testing " + getterMethod.getName() + " and set" + getterMethod.getName().substring(3) + " Methods" +  ls + e.getMessage() + additionalInfoForAssertSameError + ls + ls ;
    		
    	}
  		
    	
    }
    
    protected void setAllMethodsMap() {
    	
    	allMethodsMap = new HashMap<String, Method>(allMethodsArray.length);
    	for (Method method : allMethodsArray ) {
    		method.setAccessible(true);
    		allMethodsMap.put(method.getName(), method);
    		
    	}
    	
    }
    
    protected boolean isSetterMethodOkayToTest(Method setterMethod) {
    	
    	// Only allow simple setters with one parameter for auto testing
    	if (setterMethod.getParameterTypes().length > 1) {
    		return false;
    	}
    	
    	return true;
    }
    
    /**
     * 
     * Values an array with a Method object that corresponds to every method
     * declared in the class we are testing.
     * 
     */
    protected void setAllMethodsArray() {
    	
    	allMethodsArray = classToTest.getClass().getDeclaredMethods();
    }
    
    protected List<Method> getListOfSetterMethods(Method[] methods) {
    	    	    	
    	int listOfSettersLength = methods == null ? 0 : methods.length;   
    	
    	List<Method> listOfSetters = new ArrayList<Method>(listOfSettersLength);
    	
    	for (int idx = 0; idx < listOfSettersLength; idx++) {
    		
    		if (methods[idx].getName().startsWith("set")) {
    			if (isSetterMethodOkayToTest(methods[idx])) {
    				listOfSetters.add(methods[idx]);
    			}
    			
    		}
    		
    	}
    	
    	return listOfSetters;
    	
    }
    
    /**
     * Get the GetterMethod Object that goes with the SetterMethod Object.  The
     * assumption is that the method name is exactly the same minus the first
     * three characters: set/get or is in the case of a boolean
     * 
     * @param setterMethod
     * @return
     */
    protected Method getAssociatedGetterMethod(Method setterMethod) {
    	
    	Method getterMethod = null;
    	String methodName = "get" + setterMethod.getName().substring(3); 
    	
    	getterMethod = (Method) allMethodsMap.get(methodName);
    	
    	// Could be a boolean; so give it one more shot
    	if (getterMethod == null) {
    		methodName = "is" + setterMethod.getName().substring(3);
    		getterMethod = (Method) allMethodsMap.get(methodName);
    	}
    	   	
    	return getterMethod;
    	
    }
   
    
}
