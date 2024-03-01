package com.tdp.data.web.controller;

import com.tdp.data.web.pojo.UrlModel;
import com.tdp.data.web.service.DbMapper;
import com.tdp.data.web.service.KeywordEnum;
import com.tdp.data.web.service.QueueModel;
import com.tdp.data.web.service.RabbitUtils;
import com.tdp.data.web.utils.FileWriterUtils;
import com.tdp.data.web.utils.ZipUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 提供项目访问接口
 * @author admin
 */
@Controller
public class WebController {


    @Autowired
    private RabbitUtils rabbitUtils;

    @Value("${upload.target.folder}")
    private String targetFolder;

    @Value("${upload.file.timeout}")
    private int fileTimeOut;

    @Resource
    private DbMapper dbMapper;

    private static final Logger logger = LoggerFactory.getLogger(WebController.class);

    @GetMapping("/")
    public String upload(HttpServletRequest request, Model model) {
        logger.info("IP:{}=>访问了网站",request.getRemoteAddr());
        //显示当前文件夹中的文件列表
        File file = new File(targetFolder);
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


        KeywordEnum[] keywordEnums = KeywordEnum.values();

        Map<Integer, String> paramap = new HashMap<>();
        for (KeywordEnum keywordEnum: keywordEnums) {
            paramap.put(keywordEnum.index, keywordEnum.name);
        }
        model.addAttribute("paramap", paramap);

        String time = LocalDate.now().minusDays(1).format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        model.addAttribute("time", time);
        //显示MQ队列相关信息
        QueueModel queueModel = rabbitUtils.getQueueModel("wait_check");
        if(Objects.nonNull(queueModel)){
            model.addAttribute("queueModel", queueModel);
        }
        return "page";
    }

    @RequestMapping("/getData")
    @ResponseBody
    public void getData(String tag, String keywordType, HttpServletResponse response){

        // 获取系统的临时文件存储点
        String folder_tmp = System.getProperty("java.io.tmpdir");

        try {
            String responseFileName;
            if (StringUtils.isEmpty(tag)){
                responseFileName = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".zip";
            } else {
                responseFileName = tag + ".zip";
            }

            KeywordEnum[] keywordEnums = KeywordEnum.values();

            List<File> fileList = new ArrayList<>();

            if (StringUtils.isEmpty(keywordType)){
                // 未选择筛选类型
                for (int i=0;i<keywordEnums.length; i++){
                    List<UrlModel> _list = dbMapper.selectAllUrlByTag(tag, keywordEnums[i].index, Integer.MAX_VALUE);
                    if(_list.size() == 0){
                        continue;
                    }
                    String _filePath = folder_tmp + File.separator + keywordEnums[i].name + ".txt";
                    FileWriterUtils.writeFile(_list, _filePath);
                    fileList.add(new File(_filePath));
                }
            } else {
                // 选择了筛选类型
                Map<Integer, String> paraMap = new HashMap<>(keywordEnums.length);
                for (KeywordEnum keywordEnum: keywordEnums) {
                    paraMap.put(keywordEnum.index, keywordEnum.name);
                }

                Integer keywordTypeInteger = Integer.parseInt(keywordType);
                String keywordName = paraMap.get(keywordTypeInteger);
                List<UrlModel> _list = dbMapper.selectAllUrlByTag(tag, keywordTypeInteger, Integer.MAX_VALUE);

                String _filePath = folder_tmp + File.separator + keywordName + ".txt";
                FileWriterUtils.writeFile(_list, _filePath);
                fileList.add(new File(_filePath));
            }
            ZipUtils.listFileToZipStream(fileList, responseFileName, response);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
    }

//    @PostMapping("/upload")
//    @ResponseBody
//    public String upload(@RequestParam("file") MultipartFile file) {
//       // 获取上传文件名
//        String filename = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "_" + file.getOriginalFilename();
//        // 新建文件
//        File filepath = new File(targetFolder, filename);
//        // 判断路径是否存在，如果不存在就创建一个
//        if (!filepath.getParentFile().exists()) {
//            filepath.getParentFile().mkdirs();
//        }
//        try {
//            // 写入文件
//            file.transferTo(new File(targetFolder + File.separator + filename));
//        } catch (IOException e) {
//            e.printStackTrace();
//            return "文件" + filename + "上传出错!";
//        }
//        return "文件" + filename + "上传成功";
//    }





}
