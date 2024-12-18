package com.tdp.data.web.service;


import com.tdp.data.web.pojo.UrlDomainModel;
import com.tdp.data.web.pojo.UrlModel;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.cursor.Cursor;
import org.apache.ibatis.mapping.ResultSetType;

import java.util.List;

/**
 * 数据库访问类
 * @author admin
 */
@Mapper
public interface DbMapper {

    /**
     * 查询tag值的总量
     * @param tag
     * @return
     */
    @Select("select count(*) from url_domain where tag = #{tag}")
    int selectCountByTag(@Param("tag") String tag);

    @Select("select count(*) from url_domain where keyword_type =#{keywordType}")
    int selectCountByKeywordType(@Param("keywordType") Integer keywordType);

    /**
     * 根据tag值查询网站归属的类型
     * @param tag   网站标识
     * @param keywordTypes  网站类型，多个以 ',' 隔开
     * @param limitNum  限制的个数
     * @return
     */
    @Select("<script>" +
            "SELECT url_string,keyword_type FROM url_domain" +
            "<where>" +
            " <if test=\"tag != null and tag != ''\"> tag = #{tag} </if>" +
            " <if test=\"keywordTypes != null and keywordTypes != ''\"> and keyword_type in (${keywordTypes}) </if>" +
            "</where> LIMIT #{limitNum} " +
            "</script>")
    Cursor<UrlDomainModel> selectAllByTag(@Param("tag") String tag, @Param("keywordTypes")String keywordTypes, @Param("limitNum") int limitNum);

    /**
     * 根据tag值和网站类型查询网站归属的类型
     * @param tag
     * @return
     */
    @Select("<script>" +
            " SELECT url_string FROM url_domain" +
            " <where>" +
            " <if test=\"tag != null and tag != ''\"> tag = #{tag}</if>" +
            " <if test=\"keywordType != null\">  AND keyword_type =#{keywordType} </if>" +
            " </where>" +
            " <if test=\"limitNum != null\"> LIMIT #{limitNum} </if>" +
            "</script>")
    @Options(resultSetType = ResultSetType.FORWARD_ONLY, fetchSize = 100000)
    Cursor<UrlModel> selectAllUrlByTag(@Param("tag") String tag, @Param("keywordType")Integer keywordType, @Param("limitNum") Integer limitNum);

    /**
     * 插入关键词
     * @param content 内容
     * @param type 类型
     * @return 受影响的行数
     */
    @Insert("insert into keywords(keyword_string, keyword_type) values (#{content},#{type})")
    int insertKeyWords(@Param("content")String content, @Param("type")Integer type);
}
