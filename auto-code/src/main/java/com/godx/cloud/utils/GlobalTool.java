package com.godx.cloud.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;

/**
 * 全局工具类
 *
 * @author makejava
 * @version 1.0.0
 * @since 2018/08/14 18:11
 */
@SuppressWarnings("unused")
@Slf4j
public class GlobalTool extends NameUtil {
    private static volatile GlobalTool globalTool;

    /**
     * Jackson对象
     */
    private ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 私有构造方法
     */
    private GlobalTool() {
    }

    /**
     * 单例模式
     */
    public static GlobalTool getInstance() {
        if (globalTool == null) {
            synchronized (GlobalTool.class) {
                if (globalTool == null) {
                    globalTool = new GlobalTool();
                }
            }
        }
        return globalTool;
    }

    /**
     * 创建集合
     *
     * @param items 初始元素
     * @return 集合对象
     */
    public Set<?> newHashSet(Object... items) {
        return items == null ? new HashSet<>() : new HashSet<>(Arrays.asList(items));
    }

    /**
     * 创建列表
     *
     * @param items 初始元素
     * @return 列表对象
     */
    public List<?> newArrayList(Object... items) {
        return items == null ? new ArrayList<>() : new ArrayList<>(Arrays.asList(items));
    }

    /**
     * 创建列表
     *
     * @param items 初始元素
     * @return 列表对象
     */
    public String test(Object... items) {
        log.info("Test1");
        return "test";
    }

    /**
     * 无返回执行，用于消除返回值
     *
     * @param obj 接收执行返回值
     */
    public void call(Object... obj) {

    }

    /**
     * 获取某个类的所有字段
     *
     * @param cls 类
     * @return 所有字段
     */
    private List<Field> getAllFieldByClass(Class<?> cls) {
        List<Field> result = new ArrayList<>();
        do {
            result.addAll(Arrays.asList(cls.getDeclaredFields()));
            cls = cls.getSuperclass();
        } while (!cls.equals(Object.class));
        return result;
    }

    private static final long MAX = 100000000000000000L;

    /**
     * 生成长度为18位的序列号，保持代码美观
     *
     * @return 序列化
     */
    public String serial() {
        Random random = new Random();
        StringBuilder builder = new StringBuilder();
        // 正负号生成
        if (random.nextFloat() > 0.5F) {
            builder.append("-");
        }
        // 首位不能为0
        builder.append(random.nextInt(9) + 1);
        // 生成剩余位数
        do {
            builder.append(random.nextInt(10));
        } while (builder.length() < 18);
        // 加上结束符号
        builder.append("L");
        return builder.toString();
    }

}
