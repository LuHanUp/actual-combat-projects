/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 * <p>
 * https://www.renren.io
 * <p>
 * 版权所有，侵权必究！
 */

package top.luhancc.common.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

/**
 * 返回数据
 *
 * @author Mark sunlightcs@gmail.com
 */
public class R extends HashMap<String, Object> {
    private static final long serialVersionUID = 1L;

    public R() {
        put("code", HttpStatus.SC_OK);
        put("msg", "success");
        put("data", null);
    }

    public static R error() {
        return error(HttpStatus.SC_INTERNAL_SERVER_ERROR, "未知异常，请联系管理员");
    }

    public static R error(String msg) {
        return error(HttpStatus.SC_INTERNAL_SERVER_ERROR, msg);
    }

    public static R error(int code, String msg) {
        R r = new R();
        r.put("code", code);
        r.put("msg", msg);
        r.put("data", null);
        return r;
    }

    public static R ok(Object data) {
        return ok().put("data", data);
    }

    public static R ok(Map<String, Object> map) {
        R r = new R();
        r.putAll(map);
        return r;
    }

    public static R ok() {
        return new R();
    }

    @Override
    public R put(String key, Object value) {
        super.put(key, value);
        return this;
    }

    public boolean isSuccess() {
        return get("code").equals(HttpStatus.SC_OK);
    }

    public <T> T get(Class<T> clazz) {
        return get("data", clazz);
    }

    public <T> T get(String key, Class<T> clazz) {
        Object data = get(key);
        return JSONObject.parseObject(JSON.toJSONString(data), clazz);
    }

    public String getMsg() {
        return get("msg").toString();
    }
}
