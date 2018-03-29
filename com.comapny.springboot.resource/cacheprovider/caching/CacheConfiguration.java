public interface CacheConfiguration {
    
    String getCacheName();
    
    boolean isDynamic();
    
    String getCacheNameGeneratorPolicyString();

    CacheEntryKeyGenerationPolicy getCacheEntryKeyGenerator();

    boolean isBlocking();
}
