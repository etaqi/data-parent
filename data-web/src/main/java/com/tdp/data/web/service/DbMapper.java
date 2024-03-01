package com.tdp.data.web.service;


import com.tdp.data.web.pojo.UrlDomainModel;
import com.tdp.data.web.pojo.UrlModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 数据库访问类
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
            " <if test=\"keyword_type != null and keyword_type != ''\"> and keyword_type in (${keywordTypes}) </if>" +
            "</where> LIMIT #{limitNum} " +
            "</script>")
    List<UrlDomainModel> selectAllByTag(@Param("tag") String tag, @Param("keywordTypes")String keywordTypes, @Param("limitNum") int limitNum);

    /**
     * 根据tag值和网站类型查询网站归属的类型
     * @param tag
     * @return
     */
    @Select("<script>" +
            " SELECT url_string FROM url_domain" +
            " <where>" +
            " <if test=\"tag != null and tag != ''\"> tag = #{tag}</if>" +
            " <if test=\"keywordType != null and keywordType != ''\">  AND keyword_type =#{keywordType} </if>" +
            " </where>" +
            " LIMIT #{limitNum}" +
            "</script>")
    List<UrlModel> selectAllUrlByTag(@Param("tag") String tag, @Param("keywordType")int keywordType, @Param("limitNum") int limitNum);
}
