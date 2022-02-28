package com.example.fastdfs.controller;

import com.example.fastdfs.pojo.FastDFSFile;
import com.example.fastdfs.utils.FastDFSClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.io.InputStream;

/**
 * 文件上传controller
 */
@Controller
public class FileController {

    private static Logger logger = LoggerFactory.getLogger(FileController.class);

    /**
     * 上传文件
     * @param file
     * @param redirectAttributes
     * @return
     */
    @PostMapping("/upload")
    public String fileUpload(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes){
        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "请选择一个文件上传");
            return "redirect:uploadStatus";
        }
        try {
            //上传文件，拿到返回的文件路径
            String path = saveFile(file);
            redirectAttributes.addFlashAttribute("message", "上传成功"
                    +file.getOriginalFilename());

            //打印路径
            redirectAttributes.addFlashAttribute("path", "路径："+path);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:uploadStatus";

    }

    /**
     * 页面跳转
     * @return
     */
    @GetMapping("/uploadStatus")
    public String uploadStatus(){
        return "uploadStatus";
    }
    /**
     * 页面跳转
     * @return
     */
    @GetMapping("/upload")
    public String upload(){
        return "upload";
    }

    public String saveFile(MultipartFile multipartFile) throws Exception {
        String[] fileAbsolutePath = {};
        String fileName = multipartFile.getOriginalFilename();
        String ext = fileName.substring(fileName.lastIndexOf(".") + 1);
        byte[] file_buff = null;
        InputStream inputStream = multipartFile.getInputStream();
        if (inputStream != null) {
            int len1 = inputStream.available();
            file_buff = new byte[len1];
            inputStream.read(file_buff);
        }
        inputStream.close();
        FastDFSFile file = new FastDFSFile(fileName,file_buff,ext);
        fileAbsolutePath = FastDFSClient.upload(file);
        if (fileAbsolutePath==null){
            logger.error("上传失败");
        }
        String path = FastDFSClient.getTrackerUrl()+fileAbsolutePath[0]+"/"+fileAbsolutePath[1];
        return path;
    }
}
