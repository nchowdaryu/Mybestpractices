import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.TypeUtils;

import com.company.logging.api.message.Message;
import com.company.logging.api.message.MessageUtils;
import com.company.logging.api.performance.Profiler;

/**
 * Spring-AOP based logging aspect for use as a declarative alternative to 
 * programmatic logging, both for diagnostic and performance logging.
 * 
 * This aspect has the lowest precedence because other AOP interceptions 
 * may cause the method not to be invoked.
 * 
 * @author u16t
 */
@Aspect
@Component
class LogAspect implements Ordered {
        
    private static final String ENTRANCE_MESSAGE = "Entering method [%s]";
    
    private static final String ENTRANCE_ARGUMENTS_MESSAGE = " with arguments [%s]";

    private static final String EXIT_MESSAGE = "Exiting method [%s]";
    
    private static final String EXIT_RETURN_MESSAGE = " with return value [%s]";
    
    private static final String EXCEPTION_MESSAGE = "Method [%s] threw exception [%s]. Stacktrace: %3$s";
    
    static final String NULL_STRING = "null";
    
    static final String ANNOTATION_VALUE_ATTRIBUTE_NAME = "value";
    
    static final char ARGUMENT_DELIMITER = ',';
    
    static final String UNKNOWN_ARGUMENT_VALUE = "?";
    
    private static final String ARGUMENT_VALUE_FORMAT = "%s(%s=%s)";
    
    @Autowired
    @Qualifier("AspectOrder.LAST")
    private int order;
    
    @Autowired
    LogWriter logWriter;
    
    public int getOrder() {
        return order;
    }
    
    @Around("@annotation(loggable)")
    public Object interceptForLogging(ProceedingJoinPoint joinPoint, Loggable loggable) throws Throwable {
        
        validateState(joinPoint);
                
        Object returnValue = null;
        
        Profiler profiler = null;
        
        if (isLogExecutionRequestingEntranceMessage(loggable.execution())) {
            logEntranceMessage(joinPoint, loggable);
        }
      
        if(shouldLogPerformanceMessage(loggable)) {
            profiler = createPerformanceProfiler(joinPoint, loggable);
            profiler.start();
        }
 
        try {
            returnValue = joinPoint.proceed();
        }
        catch(Throwable t) {
            if(profiler != null) {
                profiler.stop();
            }           
            if(isLogExecutionRequestingThrowingMessage(loggable.execution())) {            
                logThrowingMessage(joinPoint, loggable, t);
            }
            throw t;
        }
        
        if(profiler != null) {
            profiler.stop();
        } 
        if(isLogExecutionRequestingExitMessage(loggable.execution())) {
            logExitMessage(joinPoint, loggable, returnValue);     
        }
        
        return returnValue;
    }

    boolean shouldLogPerformanceMessage(Loggable loggable) {
        return loggable.enablePerformanceLogging();
    }

    boolean isLogExecutionRequestingEntranceMessage(LogExecution[] logExecution) {        
        return ArrayUtils.contains(logExecution, LogExecution.BEFORE);        
    }
    
    boolean isLogExecutionRequestingExitMessage(LogExecution[] logExecution) {
        return ArrayUtils.contains(logExecution, LogExecution.AFTER_RETURNING);
    }
    
    boolean isLogExecutionRequestingThrowingMessage(LogExecution[] logExecution) {
        return ArrayUtils.contains(logExecution, LogExecution.AFTER_THROWING);
    }

    void validateState(ProceedingJoinPoint joinPoint) {
        Assert.state(joinPoint != null && joinPoint.getTarget() != null, "Invalid LogAspect/AOP configuration -- unable to determine target that is intercepted.");
    }
    
    boolean isVoidReturnType(ProceedingJoinPoint joinPoint) {
        return void.class.equals(((MethodSignature) joinPoint.getSignature()).getReturnType());
    }

    void logExitMessage(ProceedingJoinPoint joinPoint, Loggable loggable, Object returnObject) { 
        
        String exitMessageFormat = EXIT_MESSAGE;
        String returnTypeMethodValue = StringUtils.EMPTY;
               
        if(!isVoidReturnType(joinPoint) && StringUtils.isNotBlank(loggable.returnTypeMethodName())){
            exitMessageFormat = exitMessageFormat.concat(EXIT_RETURN_MESSAGE);
            returnTypeMethodValue = getObjectValue(returnObject, loggable.returnTypeMethodName());
        }
        
        Message exitMessage = MessageUtils.createDiagnosticMessage(exitMessageFormat, joinPoint.getSignature().toLongString(), returnTypeMethodValue);
        logWriter.logMessageBasedOnLogLevel(loggable.normalLevel(), exitMessage, joinPoint.getTarget().getClass(), joinPoint.getSignature(), loggable.normalCode());
    }

    void logEntranceMessage(ProceedingJoinPoint joinPoint, Loggable loggable) {
        
        String argumentValues = constructArgumentsString(joinPoint);
        String entranceMessageFormat = ENTRANCE_MESSAGE;
        
        if(StringUtils.isNotBlank(argumentValues)) {
            entranceMessageFormat = entranceMessageFormat.concat(ENTRANCE_ARGUMENTS_MESSAGE);
        }
        
        Message entranceMessage = MessageUtils.createDiagnosticMessage(entranceMessageFormat, joinPoint.getSignature().toLongString(), argumentValues);
        logWriter.logMessageBasedOnLogLevel(loggable.normalLevel(), entranceMessage, joinPoint.getTarget().getClass(), joinPoint.getSignature(), loggable.normalCode());
    }

    void logThrowingMessage(ProceedingJoinPoint joinPoint, Loggable loggable, Throwable t) {
        Message message = MessageUtils.createDiagnosticMessage(EXCEPTION_MESSAGE, joinPoint.getSignature().toLongString(), getObjectValue(t, loggable.exceptionLogMessageMethodName()), ExceptionUtils.getStackTrace(t));       
        logWriter.logMessageBasedOnLogLevel(loggable.exceptionLevel(), message, joinPoint.getTarget().getClass(), joinPoint.getSignature(), loggable.exceptionCode());
    }
    
    String constructArgumentsString(ProceedingJoinPoint joinPoint) {
        
        Object[] methodArguments = joinPoint.getArgs();
        
        if(ArrayUtils.isEmpty(methodArguments)) {
            return StringUtils.EMPTY;
        }
        
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method joinPointMethod = AopUtils.getMostSpecificMethod(methodSignature.getMethod(), joinPoint.getTarget().getClass());
        
        if(joinPointMethod == null) {
            return getNonExistentMethodValue(joinPoint.getTarget().getClass(), methodSignature.getMethod().getName());
        }
        
        return getLoggingAnnotatedArgumentValues(methodArguments, joinPointMethod.getParameterAnnotations());
    }
    
    String getLoggingAnnotatedArgumentValues(Object[] methodArguments, Annotation[][] parameterAnnotations) {

        StringBuilder sb = new StringBuilder(StringUtils.EMPTY);

        for(int i = 0; i < parameterAnnotations.length ; i++) {
            Annotation[] annotations = parameterAnnotations[i];
            for(Annotation annotation : annotations) {
                if(TypeUtils.isAssignable(IncludeForLogging.class, annotation.getClass())) {                                        
                    sb.append(getArgumentValue(methodArguments[i], (IncludeForLogging) annotation) + ARGUMENT_DELIMITER);
                }
            }
        }
    
        String values = sb.toString();       
        return StringUtils.isBlank(values) ? values : values.substring(0, values.length()-1);
    }

    String getArgumentValue(Object methodArgument, IncludeForLogging loggingAnnotation) {
        
        Method annotationValueMethod = ReflectionUtils.findMethod(loggingAnnotation.getClass(), ANNOTATION_VALUE_ATTRIBUTE_NAME);
        String argumentValueMethodName = (String) ReflectionUtils.invokeMethod(annotationValueMethod, loggingAnnotation);
        
        return getObjectValue(methodArgument, argumentValueMethodName);
    }
    
    String getObjectValue(Object object, String valueMethodName) {
              
        String shortClassName = ClassUtils.getShortClassName(object, NULL_STRING);
        
        if(StringUtils.isBlank(valueMethodName)) {
        	logWriter.getLogger(LogAspect.class).warn("Method name to call is blank, will not call method on {} for logging", shortClassName);
            return UNKNOWN_ARGUMENT_VALUE;
        }
        
        if(object == null) {
            return NULL_STRING;
        }
                     
        Method method = ReflectionUtils.findMethod(object.getClass(), valueMethodName);
        String objectValue = method == null ? getNonExistentMethodValue(object.getClass(), valueMethodName) : ObjectUtils.toString(ReflectionUtils.invokeMethod(method, object), NULL_STRING);
        return String.format(ARGUMENT_VALUE_FORMAT, shortClassName, valueMethodName, objectValue);
    }    

    String getNonExistentMethodValue(Class<?> targetObjectClass, String methodName) {
    	logWriter.getLogger(targetObjectClass).warn("Could not find target method {} on class {}, so not including method argument values in log messages" , methodName, targetObjectClass);
        return UNKNOWN_ARGUMENT_VALUE;
    }
    
    Profiler createPerformanceProfiler(ProceedingJoinPoint joinPoint, Loggable loggable) {
        String operation = loggable.performanceOperation();
        if(StringUtils.isBlank(operation)) {
            MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
            Method joinPointMethod = AopUtils.getMostSpecificMethod(methodSignature.getMethod(), joinPoint.getTarget().getClass());
            operation = joinPointMethod == null ? StringUtils.EMPTY : StringUtils.defaultString(joinPointMethod.getName());
        }
        return Profiler.createProfile(joinPoint.getTarget().getClass(), operation, loggable.performanceSystem(), loggable.performanceCorrelationId());
    }
}
