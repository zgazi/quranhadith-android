package com.greenledge.common;

import android.support.v4.util.LruCache;
import android.util.Log;

import org.apache.commons.io.FileUtils;


import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Comparator;

public class CacheHelper {
    private static final String TAG = "CacheHelper";

    private static final int SIZE_MEMCACHE = 4 * 1024 * 1024;
    private static final int SIZE_FILECACHE = 2 * 1024 * 1024;

    private File cacheDir;
    private LruCache<String, byte[]> memoryCache = new LruCache<String, byte[]>(SIZE_MEMCACHE) {
        @Override
        protected int sizeOf(String key, byte[] value) {
            return value.length;
        }
    };

    CacheHelper(File cacheDir) {
        this.cacheDir = cacheDir;
        //cleanDir();
    }

    /**
     * Download url using all available caching methods
     */
    public byte[] loadCached(String url) throws IOException {
        return loadCached(url, true);
    }

    /**
     * Download url, use cached version if possible, and save to cache
     * @param allowFs true to enable file system cache; false to use memory cache only
     */
    public byte[] loadCached(String url, boolean allowFs) throws IOException {
        String key = md5(url);

        byte[] data = memoryCache.get(key);
        if (data != null) {
            return data;
        }

        File entry = null;
        if (allowFs) {
            entry = new File(cacheDir, key);
            if (entry.exists()) {
                try {
                    data = FileUtils.readFileToByteArray(entry);
                    memoryCache.put(key, data);

                    return data;
                } catch (IOException e) {
                        Log.e(TAG, "Failed to read cache file", e);
                }
            }
        }

        data = IOHelper.toByteArray(new URL(url));

        Log.i(TAG, "Not found in cache: " + url + "; loaded from net.");

        memoryCache.put(key, data);

        if (allowFs) {
            try {
                FileUtils.writeByteArrayToFile(entry, data);
            } catch (IOException e) {
                Log.e(TAG, "Failed to write cache file", e);
            }
        }
        return data;
    }

    public static String md5(String s) {
        MessageDigest m = null;
        try {
            m = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            //e.printStackTrace();
        }

        m.update(s.getBytes(),0,s.length());
        String hash = new BigInteger(1, m.digest()).toString(16);
        return hash;

    }

    /**
     * Delete old files to bring directory size down to max. SIZE_FILECACHE
     */
    public void cleanDir() {
        File[] files = cacheDir.listFiles();
        //newest first
        Arrays.sort(files, new Comparator<File>() {
            public int compare(File f1, File f2) {
                return Long.valueOf(f2.lastModified()).compareTo(f1.lastModified());
            }
        });

        long currentSize = 0;
        for (File file : files) {
            currentSize += file.length();
            if (currentSize > SIZE_FILECACHE) {
                file.delete();
            }
        }
    }
}
