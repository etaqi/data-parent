package com.tdp.data.web.service;

import com.sun.org.apache.bcel.internal.generic.IF_ACMPEQ;
import com.tdp.data.web.pojo.UrlModel;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.cursor.Cursor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.awt.*;
import java.io.BufferedWriter;
import java.io.FileWriter;

/**
 * @author admin
 */
@Service
@Slf4j
public class DbService {

    @Resource private DbMapper dbMapper;

    @Transactional(rollbackFor = Exception.class)
    public boolean writeDomainByParameter(String tag, Integer keywordType, Integer limitSize, String filePath){
        log.info("查询关键词ID为:{}的数据", keywordType);
        long startTime = System.currentTimeMillis();
        Cursor<UrlModel> cursor = dbMapper.selectAllUrlByTag(tag, keywordType, limitSize);
        long searchEndTime = System.currentTimeMillis();
        log.info("查询关键词ID为:{}耗时:{}s", keywordType, (searchEndTime - startTime)/1000);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))){
            for (UrlModel t : cursor) {
                writer.write(t.getUrl_string());
                writer.newLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        log.info("写入完毕:{},列表行数:{},写入耗时:{}s", filePath, cursor.getCurrentIndex(), (System.currentTimeMillis() - searchEndTime)/1000);
        return true;
    }

}
