package com.tdp.data.web.utils;

import com.tdp.data.web.pojo.UrlModel;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.cursor.Cursor;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author mark
 * @date 2020/12/23 15:55
 */
@Slf4j
public class FileWriterUtils {


    /**
     * 将文件写入file文件
     * @param urlModelList 域名对象列表
     * @param  filePath    写入的文件路径
     * @return 写入是否成功
     */
    public static boolean writeFile(List<UrlModel> urlModelList, String filePath){
        if (urlModelList.size() == 0){
            return true;
        }
        log.info("开始写入:{},文件行数:{}", filePath, urlModelList.size());

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))){
                for (UrlModel urlModel: urlModelList){
                    writer.write(urlModel.getUrl_string());
                    writer.newLine();
                }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        log.info("写入完毕:{},列表行数:{}", filePath, urlModelList.size());
        return true;
    }

    /**
     *
     * @param urlModelList
     * @param filePath
     * @return
     */
    public static boolean writeFile(Cursor<UrlModel> urlModelList, String filePath){
        log.info("开始写入:{},文件行数:{}", filePath, urlModelList.getCurrentIndex());
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))){
            for (UrlModel t : urlModelList) {
                writer.write(t.getUrl_string());
                writer.newLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        log.info("写入完毕:{},列表行数:{}", filePath, urlModelList.getCurrentIndex());
        return true;
    }
}
