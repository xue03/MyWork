//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package cn.com.csii.mobile.zxing.util;

import android.content.Context;
import java.lang.reflect.Field;

public class RUtil {
    private static RUtil rUtil;

    public RUtil() {
    }

    public static RUtil getInstance() {
        if(rUtil == null) {
            rUtil = new RUtil();
            return rUtil;
        } else {
            return rUtil;
        }
    }

    public int getLayoutId(Context context, String xmlName) {
        return this.getResourceId(context, "layout", xmlName);
    }

    public int getDrawableId(Context context, String xmlName) {
        return this.getResourceId(context, "drawable", xmlName);
    }

    public int getId(Context context, String xmlName) {
        return this.getResourceId(context, "id", xmlName);
    }

    public int getRawId(Context context, String xmlName) {
        return this.getResourceId(context, "raw", xmlName);
    }

    public int getResourceId(Context context, String xmlFile, String xmlName) {
        Class localClass = null;
        Field localField = null;

        try {
            localClass = Class.forName(context.getPackageName() + ".R$" + xmlFile);
            localField = localClass.getField(xmlName);
            return Integer.parseInt(localField.get(localField.getName()).toString());
        } catch (ClassNotFoundException var7) {
            return 0;
        } catch (SecurityException var8) {
            return 0;
        } catch (NoSuchFieldException var9) {
            return 0;
        } catch (NumberFormatException var10) {
            return 0;
        } catch (IllegalArgumentException var11) {
            return 0;
        } catch (IllegalAccessException var12) {
            return 0;
        }
    }
}
