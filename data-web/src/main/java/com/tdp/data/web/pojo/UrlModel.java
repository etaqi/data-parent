package com.tdp.data.web.pojo;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class UrlModel {

    @ExcelProperty(value = "网址", index = 0)
    private String url_string;
}
