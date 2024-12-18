package com.tdp.data.web.controller;

import com.tdp.data.web.pojo.UrlModel;
import com.tdp.data.web.service.*;
import com.tdp.data.web.utils.FileWriterUtils;
import com.tdp.data.web.utils.ZipUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.cursor.Cursor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 提供项目访问接口
 * @author admin
 */
@Controller
@Slf4j
public class WebController {


    @Autowired
    private RabbitUtils rabbitUtils;

    @Value("${file.log.path}")
    private String fileLogPath;

    @Value("${upload.file.timeout}")
    private int fileTimeOut;

    @Resource private DbService dbService;

    private static final Logger logger = LoggerFactory.getLogger(WebController.class);

    @GetMapping("/")
    public String upload(HttpServletRequest request, Model model) {
        logger.info("IP:{}=>访问了网站",request.getRemoteAddr());
        //显示当前文件夹中的文件列表
        File file = new File(fileLogPath);
        File[] files = file.listFiles(_file -> {
            if(System.currentTimeMillis() - _file.lastModified() > fileTimeOut * 3600000){
                return false;
            }
            return true;
        });
        if(Objects.nonNull(files) && files.length > 0){
            //按照时间的逆序进行排列
            Arrays.sort(files, (o1, o2) -> {
                long diff = o1.lastModified() - o2.lastModified();
                if(diff > 0){
                    return -1;
                } else if(diff == 0){
                    return 0;
                } else {
                    return 1;
                }
            });
            List<File> fileList = Arrays.asList(files);
            model.addAttribute("list", fileList);
        }


        Map<Boolean, String> checkedMap = new HashMap<>();
        checkedMap.put(true, "命中的列表");
        checkedMap.put(false, "非命中列表");
        model.addAttribute("checkedmap", checkedMap);

        KeywordEnum[] keywordEnums = KeywordEnum.values();

        Map<Integer, String> paramap = new HashMap<>();
        for (KeywordEnum keywordEnum: keywordEnums) {
            paramap.put(keywordEnum.index, keywordEnum.name);
        }
        model.addAttribute("paramap", paramap);

        String time = LocalDate.now().minusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        model.addAttribute("time", time);
        //显示MQ队列相关信息
        QueueModel queueModel = rabbitUtils.getQueueModel("wait_check");
        if(Objects.nonNull(queueModel)){
            model.addAttribute("queueModel", queueModel);
        }
        return "page";
    }

    /**
     * 获取命中的列表
     * @param tag tag值,一般为时间时间yyyy-MM-dd
     * @param keywordType
     * @param checked  是否是命中的列表
     * @param response
     */
    @RequestMapping("/getData")
    @ResponseBody
    public void getData(String tag, String keywordType,Boolean checked, HttpServletResponse response){

        // 获取系统的临时文件存储点
        String folder_tmp = System.getProperty("java.io.tmpdir");
        try {
            String responseFileName;
            if (StringUtils.isEmpty(tag)){
                responseFileName = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".zip";
            } else {
                responseFileName = tag + ".zip";
            }
            List<KeywordEnum> keywordEnumList = null;
            if (checked == null || checked){
                responseFileName = "Used_" + responseFileName;
                keywordEnumList =  Arrays.stream(KeywordEnum.values()).filter(t->t.checked).collect(Collectors.toList());
            } else {
                responseFileName = "UnUsed_" + responseFileName;
                keywordEnumList =  Arrays.stream(KeywordEnum.values()).filter(t->!t.checked).collect(Collectors.toList());
            }

            List<File> fileList = new ArrayList<>();
            if (StringUtils.isEmpty(keywordType)){
                // 未选择筛选类型
                for (int i=0;i<keywordEnumList.size(); i++){
                    String _filePath = folder_tmp + File.separator + keywordEnumList.get(i).name + ".txt";
                    boolean r = dbService.writeDomainByParameter(tag, keywordEnumList.get(i).index, null, _filePath);
                    if (r){
                        fileList.add(new File(_filePath));
                    }
                }
            } else {
                // 选择了筛选类型
                Map<Integer, String> paraMap = new HashMap<>(KeywordEnum.values().length);
                for (KeywordEnum keywordEnum: KeywordEnum.values()) {
                    paraMap.put(keywordEnum.index, keywordEnum.name);
                }
                Integer keywordTypeInteger = Integer.parseInt(keywordType);
                String keywordName = paraMap.get(keywordTypeInteger);
                String _filePath = folder_tmp + File.separator + keywordName + ".txt";
                boolean  r = dbService.writeDomainByParameter(tag, keywordTypeInteger, null, _filePath);
               if (r){
                   fileList.add(new File(_filePath));
               }
            }
            ZipUtils.listFileToZipStream(fileList, responseFileName, response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 获取下载文件
     * @param dateString 下载文件 yyyyMMdd
     * @param response 返回流
     * @throws IOException 异常
     */
    @GetMapping("/downloadLogFile")
    public void downloadLogFile(String dateString,HttpServletResponse response) throws IOException {
        // 指定要下载的文件
        //File file = ResourceUtils.getFile("classpath:2.zip");
        String fileNameStr = new StringBuffer(dateString).insert(4, "-").insert(7,"-").append(".json.gz").toString();
        String filePath = fileLogPath + fileNameStr;
        File file = ResourceUtils.getFile(filePath);
        //文件名编码，防止中文乱码
        String filename = URLEncoder.encode(file.getName(), "UTF-8");
        // 设置响应头信息
        response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
        // 内容类型为通用类型，表示二进制数据流
        response.setContentType("application/octet-stream");
        // 循环，边读取边输出，可避免大文件时OOM
        try (InputStream inputStream = new FileInputStream(file); OutputStream os = response.getOutputStream()) {
            byte[] bytes = new byte[1024];
            int readLength;
            while ((readLength = inputStream.read(bytes)) != -1) {
                os.write(bytes, 0, readLength);
            }
        }
    }

//    @Resource private DbMapper dbMapper;
//
//    @PostMapping("/upload")
//    @ResponseBody
//    public String upload(@RequestParam("file") MultipartFile file, Integer tag) {
//       // 获取上传文件名
//        String filename = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "_" + file.getOriginalFilename();
//        // 新建文件
//        File filepath = new File(fileLogPath, filename);
//        // 判断路径是否存在，如果不存在就创建一个
//        if (!filepath.getParentFile().exists()) {
//            filepath.getParentFile().mkdirs();
//        }
//        try {
//            String targetFilePath = fileLogPath + File.separator + filename;
//            // 写入文件
//            file.transferTo(new File(new File(fileLogPath).getAbsolutePath()+"/" + filename));
//            List<String> list = new ArrayList<>();
//            try(BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(targetFilePath)))){
//                String content = null;
//                while ((content = bufferedReader.readLine()) != null){
//                    list.add(content);
//                }
//            }
//            log.info("读取的行数为:{}", list.size());
//            list.forEach(t-> dbMapper.insertKeyWords(
//                    t, tag)
//            );
//        } catch (IOException e) {
//            e.printStackTrace();
//            return "文件" + filename + "上传出错!";
//        }
//        return "文件" + filename + "上传成功";
//    }





}
