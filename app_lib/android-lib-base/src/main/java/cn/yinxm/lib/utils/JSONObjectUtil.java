package cn.yinxm.lib.utils;

import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

import cn.yinxm.lib.utils.log.LogUtil;

/**
 * json工具
 * <p>
 *
 * @author yinxuming
 * @date 2019/3/29
 */
public class JSONObjectUtil {
    /**
     * @param sourceJsonStr 新的json
     * @param targetJsonStr 待合并成的json
     * @return
     */
    public static String mergeJson(String sourceJsonStr, String targetJsonStr) {
        String rtn = null;
        JSONObject sourceObject = null;
        JSONObject targetObject = null;
        if (!TextUtils.isEmpty(sourceJsonStr)) {
            try {
                sourceObject = new JSONObject(sourceJsonStr);
            } catch (JSONException e) {
                LogUtil.e(e);
            }
        } else {
            return targetJsonStr;
        }

        if (!TextUtils.isEmpty(targetJsonStr)) {
            try {
                targetObject = new JSONObject(targetJsonStr);
            } catch (JSONException e) {
                LogUtil.e(e);
            }
        } else {
            return sourceJsonStr;
        }

        if (targetObject != null && sourceObject != null) {
            try {
                targetObject = deepMerge(sourceObject, targetObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if (targetObject != null) {
            rtn = targetObject.toString();
        }

        LogUtil.d("oldJsonStr=" + targetJsonStr);
        LogUtil.d("newJsonStr=" + sourceJsonStr);
        LogUtil.d("mergeJson=" + rtn);
        return rtn;
    }

    public static JSONObject deepMerge(JSONObject source, JSONObject target) throws JSONException {
        Iterator<String> iterator = source.keys();

        while (iterator.hasNext()) {
            String key = iterator.next();
            Object value = source.opt(key);
            if (value == null) {
                continue;
            }
            if (!target.has(key)) {
                // new value for "key":
                target.put(key, value);
            } else {
                // existing value for "key" - recursively deep merge:
                if (value instanceof JSONObject) {
                    JSONObject valueJson = (JSONObject) value;
                    deepMerge(valueJson, target.getJSONObject(key));
                } else {
                    target.put(key, value);
                }
            }
        }
        return target;
    }

    public static void main(String[] args) {
        String str = mergeJson("{\"name\":\"BeJson\",\"url\":\"http://www.bejson.com\"" + ",\"page\":88,\"address\":{\"street\":\"科技园路.\",\"city\":\"江苏苏州\",\"country\":\"中国\"}}", "{\"name\":\"yinxm\",\"sex\":\"man\",\"isNonProfit\":true,\"links\":[{\"name\":\"Google\"," + "\"url\":\"http://www.google.com\"},{\"name\":\"Baidu\",\"url\":\"http://www.baidu.com\"}," + "{\"name\":\"SoSo\",\"url\":\"http://www.SoSo.com\"}]}");
    }
}
