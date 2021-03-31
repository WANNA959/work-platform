package com.godx.cloud.util;

import java.io.File;

public class FileUtil {

    public static void deleteFileDirectory(File file) {
        if (file.isDirectory()) {
            String[] children = file.list();//获取文件夹下所有子文件夹
            //递归删除目录中的子目录下
            for (int i = 0; i < children.length; i++) {
                deleteFileDirectory(new File(file, children[i]));
            }
        }
        // 目录空了，进行删除
        file.delete();
    }
}
