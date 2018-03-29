public interface CacheEntryKeyGenerationPolicy {

	public String generateCacheEntryKey(Object... arguments);
}
