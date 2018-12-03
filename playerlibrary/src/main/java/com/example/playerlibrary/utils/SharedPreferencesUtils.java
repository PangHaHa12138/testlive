package com.example.playerlibrary.utils;



import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

public class SharedPreferencesUtils {

    public static final String FILE_NAME = "shared_data";

    public SharedPreferencesUtils() {
    }

    public static void setParam(Context context, String key, Object object) {
        SharedPreferences sp = context.getSharedPreferences("shared_data", 0);
        Editor editor = sp.edit();
        if (object instanceof String) {
            editor.putString(key, (String)object);
        } else if (object instanceof Integer) {
            editor.putInt(key, (Integer)object);
        } else if (object instanceof Boolean) {
            editor.putBoolean(key, (Boolean)object);
        } else if (object instanceof Float) {
            editor.putFloat(key, (Float)object);
        } else if (object instanceof Long) {
            editor.putLong(key, (Long)object);
        } else {
            editor.putString(key, object.toString());
        }

        SharedPreferencesUtils.SharedPreferencesCompat.apply(editor);
    }

    public static Object getParam(Context context, String key, Object defaultObject) {
        try {
            SharedPreferences sp = context.getSharedPreferences("shared_data", 0);
            if (defaultObject instanceof String) {
                return sp.getString(key, (String)defaultObject);
            } else if (defaultObject instanceof Integer) {
                return sp.getInt(key, (Integer)defaultObject);
            } else if (defaultObject instanceof Boolean) {
                return sp.getBoolean(key, (Boolean)defaultObject);
            } else if (defaultObject instanceof Float) {
                return sp.getFloat(key, (Float)defaultObject);
            } else {
                return defaultObject instanceof Long ? sp.getLong(key, (Long)defaultObject) : null;
            }
        } catch (NullPointerException var4) {
            return null;
        }
    }

    public static void remove(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences("shared_data", 0);
        Editor editor = sp.edit();
        editor.remove(key);
        SharedPreferencesUtils.SharedPreferencesCompat.apply(editor);
    }

    public static void clear(Context context) {
        SharedPreferences sp = context.getSharedPreferences("shared_data", 0);
        Editor editor = sp.edit();
        editor.clear();
        SharedPreferencesUtils.SharedPreferencesCompat.apply(editor);
    }

    public static boolean contains(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences("shared_data", 0);
        return sp.contains(key);
    }

    public static Map<String, ?> getAll(Context context) {
        SharedPreferences sp = context.getSharedPreferences("shared_data", 0);
        return sp.getAll();
    }

    private static class SharedPreferencesCompat {
        private static final Method sApplyMethod = findApplyMethod();

        private SharedPreferencesCompat() {
        }

        private static Method findApplyMethod() {
            try {
                Class cls = Editor.class;
                return cls.getMethod("apply");
            } catch (NoSuchMethodException var1) {
                var1.printStackTrace();
                return null;
            }
        }

        public static void apply(Editor editor) {
            try {
                if (sApplyMethod != null) {
                    sApplyMethod.invoke(editor);
                    return;
                }
            } catch (IllegalArgumentException var2) {
                var2.printStackTrace();
            } catch (IllegalAccessException var3) {
                var3.printStackTrace();
            } catch (InvocationTargetException var4) {
                var4.printStackTrace();
            }

            editor.commit();
        }
    }
}
