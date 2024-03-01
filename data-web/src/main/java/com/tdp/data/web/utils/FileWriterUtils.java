package com.tdp.data.web.utils;

import com.tdp.data.web.pojo.UrlModel;

import java.io.File;
import java.io.FileWriter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author mark
 * @date 2020/12/23 15:55
 */
public class FileWriterUtils {


    /**
     * 将文件写入file文件
     * @param urlModelList 域名对象列表
     * @param  filePath    写入的文件路径
     * @return 写入是否成功
     */
    public static boolean writeFile(List<UrlModel> urlModelList, String filePath){

        try {
            FileWriter fileWriter = new FileWriter(new File(filePath));
            String content = urlModelList.stream().map(UrlModel::getUrl_string).collect(Collectors.joining("\n"));
            fileWriter.write(content);
            fileWriter.flush();
            fileWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
