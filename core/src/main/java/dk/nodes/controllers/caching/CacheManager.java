package dk.nodes.controllers.caching;

import android.content.Context;

import java.io.Serializable;
import java.util.HashMap;

import dk.nodes.filehandler.NFileHandler;
import dk.nodes.utils.NLog;

public class CacheManager<T> {

	private static HashMap<String, CacheState> mCacheStates = new HashMap<String, CacheState>();
	
	private Context mContext;
	private String mKey;
	private long mCacheTime;
	
	public CacheManager( Context context, String key, long cacheTime ) {
		mContext = context;
		mCacheTime = cacheTime;
		mKey = key;
		loadState();
	}
	
	public void saveData( T t ) {
		if( mContext == null ) {
			NLog.e( "CacheManager", "saveData: Context was null" );
			return;
		}
		
		NFileHandler fileHandler = new NFileHandler( mContext, mKey );
		
		fileHandler.saveData( t );
		
		CacheState cacheState = mCacheStates.get( mKey );
		
		if( cacheState == null ) {
			cacheState = new CacheState();
		}
		
		cacheState.time = System.currentTimeMillis();
		mCacheStates.put( mKey, cacheState );
		
		saveState();
	}
	
	public void loadData( CacheCallback<T> callback ) {
		if( mContext == null ) {
			NLog.e( "CacheManager", "loadData: Context was null" );
			return;
		}
		
		NFileHandler fileHandler = new NFileHandler( mContext, mKey );
		T t = null;
		
		// We should refactor filehandler so it handles generics instead
		try {
			t = (T) fileHandler.loadData();
		} catch( Exception e ) {
			NLog.e( "CacheManager", "Failed casting to generic type: " + t.getClass().toString() );
			callback.onNotFound();
		}
		
		CacheState cacheState = mCacheStates.get( mKey );
		
		if( t == null ) {
			NLog.i( "CacheManager", "Cache is not found for key: " + mKey );
			callback.onNotFound();
		} else if( cacheState != null && cacheState.isExpired( mCacheTime ) ) {
			NLog.i( "CacheManager", "Cache is expired for key: " + mKey );
			callback.onExpired( t );
		} else {
			NLog.i( "CacheManager", "Cache is valid for key: " + mKey );
			callback.onValid( t );
		}
	}
	
	private void loadState() {
		if( mCacheStates == null ) {
			NFileHandler stateFileHandler = new NFileHandler( mContext, "cacheStates" );
			mCacheStates = (HashMap<String, CacheState>) stateFileHandler.loadData();
		}
	}
	
	private void saveState() {
		if( mCacheStates == null ) {
			NFileHandler stateFileHandler = new NFileHandler( mContext, "cacheStates" );
			stateFileHandler.saveDataAsync( mCacheStates );
		}
	}
	
	public static class CacheState implements Serializable {
		private static final long	serialVersionUID	= 616031910667702611L;
		public long time;
		public boolean isExpired( long age ) {
			return ( time + age ) > System.currentTimeMillis();
		}
	}
	
	public interface CacheCallback<T> {
		public void onValid(T t);
		public void onExpired(T t);
		public void onNotFound();
	}
}
