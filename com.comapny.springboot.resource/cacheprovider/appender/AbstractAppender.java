import static com.company.spring.caching.Constants.DEFAULT_METHODARGS_PLACEHOLDER;
import static com.company.spring.caching.Constants.DEFAULT_METHODNAME_PLACEHOLDER;
import static com.company.spring.caching.Constants.DEFAULT_TARGET_PLACEHOLDER;
import static com.company.spring.caching.Constants.FIELD_SEPERATOR;

import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.util.Assert;

import com.vanguard.spring.caching.CacheKeyAppender;

public abstract class AbstractAppender implements CacheKeyAppender {
    
    private AbstractAppender mNextAppender;
    
    private boolean useMethodName = true;

    private boolean useTargetClassName = true;

    private boolean useMethodArguments = true;
    
    public AbstractAppender() {
        super();
    }
    
    public AbstractAppender(AbstractAppender appender) {
        this();
        this.mNextAppender = appender;
    }
    
    public abstract String processCacheKeyData(ProceedingJoinPoint pjp);

    public String generateCacheKeyFromJoinPoint(ProceedingJoinPoint pjp) {
        Assert.notNull(pjp);
        
        StringBuilder sb = new StringBuilder();
        
        recursiveCompositeCall(pjp, sb);
        
        return sb.toString();
    }

    /*
     * Built this extra nesting method to minimize object creation.  Only one StringBuilder is
     * created per call, without this, I would double the number of temporary strings created
     * (processCacheKeyData already creates a temporary String object)
     */
    private void recursiveCompositeCall(ProceedingJoinPoint pjp, StringBuilder sb) {
        sb.append(processCacheKeyData(pjp));
        
        if (mNextAppender != null) {
            sb.append(FIELD_SEPERATOR);
            mNextAppender.recursiveCompositeCall(pjp, sb);
        }
    }
    
    void nextAppender(AbstractAppender next) {
        mNextAppender = next;
    }
    
    /*
     * TODO convert to the Chain of Command pattern
     */
    protected String getCacheKey(String targetName, String methodName, String argsString) {
        StringBuilder sb = new StringBuilder();
        if (shouldUseTargetClassName()) {
            sb.append(targetName);
        } else {
            sb.append(DEFAULT_TARGET_PLACEHOLDER);
        }
        if (shouldUseMethodName()) {
            sb.append(FIELD_SEPERATOR).append(methodName);
        } else {
            sb.append(FIELD_SEPERATOR).append(DEFAULT_METHODNAME_PLACEHOLDER);
        }
        if (shouldUseMethodArguments()) {
            sb.append(FIELD_SEPERATOR).append(argsString);
        } else {
            sb.append(FIELD_SEPERATOR).append(DEFAULT_METHODARGS_PLACEHOLDER);
        }
        return sb.toString();
    }

    public boolean shouldUseTargetClassName() {
        return useTargetClassName;
    }

    public boolean shouldUseMethodArguments() {
        return useMethodArguments;
    }

    public boolean shouldUseMethodName() {
        return useMethodName;
    }
    
}
