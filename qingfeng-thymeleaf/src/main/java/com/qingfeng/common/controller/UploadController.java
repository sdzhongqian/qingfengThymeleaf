package com.qingfeng.common.controller;

import com.qingfeng.base.controller.BaseController;
import com.qingfeng.common.service.UploadService;
import com.qingfeng.util.*;
import com.qingfeng.util.upload.FileType;
import com.qingfeng.util.upload.ParaUtil;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.List;

/**
 * @ProjectName UploadController
 * @author qingfeng
 * @version 1.0.0
 * @Description UploadController
 * @createTime 2021/12/26 0026 15:30
 */
@Controller
@RequestMapping(value = "/common/upload")
public class UploadController extends BaseController {

    @Autowired
    public UploadService uploadService;


    //=======================================================file 处理================================================

    @ResponseBody
    @RequestMapping(value = "/uploadFile", method = RequestMethod.POST)
    public void uploadFile(HttpServletRequest request, HttpServletResponse response ,HttpSession session, MultipartFile file) throws IOException {
        PageData pd = new PageData(request);
        Json json = new Json();
        if (null != file) {
            String myFileName = file.getOriginalFilename();// 文件原名称
            String fileSuffix = myFileName.substring(myFileName.lastIndexOf("."));//文件类型
            String fileType = myFileName.substring(myFileName.lastIndexOf(".")+1);//文件后缀
            //获取InputStream
            InputStream in = file.getInputStream();
            //根据文件头获取文件类型
            String type = FileType.getFileType(in);
            if(Verify.verifyIsNotNull(type)){
                if(FileType.uploadType.contains(","+type.toLowerCase()+",")&&FileType.uploadSuffixType.contains(","+fileType.toLowerCase()+",")){
                    //保存文件
                    String path = ParaUtil.common+ DateTimeUtil.getDate()+"/"+ GuidUtil.getGuid()+fileSuffix;
                    String savePath = ParaUtil.localName+path;
                    // 防止http头注入和路径遍历
                    if(FileUtil.http_header_manipulation(savePath) || FileUtil.directory_traversal_check(savePath)) {
                        throw new RuntimeException("文件名非法");
                    }
                    File files = new File(savePath);
                    if (!files.getParentFile().exists()){
                        files.getParentFile().mkdirs();
                    }
                    try {
                        file.transferTo(files);
                        pd.put("id", GuidUtil.getUuid());
                        pd.put("name",myFileName);
                        pd.put("desnames",myFileName);
                        pd.put("file_path",path);
                        pd.put("file_type",fileType);
                        pd.put("file_size",files.length());
                        pd.put("file_suffix",fileSuffix);

                        String time = DateTimeUtil.getDateTimeStr();
                        pd.put("create_time", time);
                        //处理数据权限
                        PageData user = (PageData) session.getAttribute("loginUser");
                        PageData organize = (PageData) session.getAttribute("loginOrganize");
                        pd.put("create_user",user.get("id"));
                        pd.put("create_organize",organize.get("organize_id"));
                        uploadService.saveFile(pd);
                        pd.put("show_file_path", ParaUtil.cloudfile+path);
                        json.setData(pd);
                        json.setSuccess(true);
                        json.setMsg("上传成功");
                        json.setFlag(ParaUtil.localName+path);
                    } catch (IOException e) {
                        json.setSuccess(false);
                        json.setMsg("上传失败");
                        e.printStackTrace();
                    }
                }else{
                    json.setSuccess(false);
                    json.setMsg("上传失败,文件格式不支持。");
                }
            }else{
                if(FileType.uploadSuffixType.contains(","+fileType+",")){
                    //保存文件
                    String path = ParaUtil.common+ DateTimeUtil.getDate()+"/"+ GuidUtil.getGuid()+fileSuffix;
                    String savePath = ParaUtil.localName+path;
                    // 防止http头注入和路径遍历
                    if(FileUtil.http_header_manipulation(savePath) || FileUtil.directory_traversal_check(savePath)) {
                        throw new RuntimeException("文件名非法");
                    }
                    File files = new File(savePath);
                    if (!files.getParentFile().exists()){
                        files.getParentFile().mkdirs();
                    }
                    try {
                        file.transferTo(files);
                        pd.put("id", GuidUtil.getUuid());
                        pd.put("name",myFileName);
                        pd.put("desnames",myFileName);
                        pd.put("file_path",path);
                        pd.put("file_type",fileType);
                        pd.put("file_size",files.length());
                        pd.put("file_suffix",fileSuffix);

                        String time = DateTimeUtil.getDateTimeStr();
                        pd.put("create_time", time);
                        //处理数据权限
                        PageData user = (PageData) session.getAttribute("loginUser");
                        PageData organize = (PageData) session.getAttribute("loginOrganize");
                        pd.put("create_user",user.get("id"));
                        pd.put("create_organize",organize.get("organize_id"));
                        uploadService.saveFile(pd);
                        pd.put("show_file_path", ParaUtil.cloudfile+path);
                        json.setData(pd);
                        json.setSuccess(true);
                        json.setMsg("上传成功");
                        json.setFlag(ParaUtil.localName+path);
                    } catch (IOException e) {
                        json.setSuccess(false);
                        json.setMsg("上传失败");
                        e.printStackTrace();
                    }
                }else{
                    json.setSuccess(false);
                    json.setMsg("上传失败,文件格式不支持。");
                }
            }

        }else{
            json.setSuccess(false);
            json.setMsg("文件为空");
        }
        this.writeJson(response,json);
    }

    /**
     * @Description: delImg
     * @Param: [request, response, session]
     * @return: void
     * @Author: qingfeng
     * @Date: 2019-10-10 18:31
     */
    @RequestMapping(value = "/delFile", method = RequestMethod.GET)
    public void delFile(HttpServletRequest request, HttpServletResponse response , HttpSession session) throws Exception {
        PageData pd = new PageData(request);
        Json json = new Json();
        try {
            //查询信息
            String path = ParaUtil.localName+pd.getString("file_path");
            // 防止http头注入和路径遍历
            if(FileUtil.http_header_manipulation(path) || FileUtil.directory_traversal_check(path)) {
                throw new RuntimeException("文件名非法");
            }
            File pathFile = new File(path);
            pathFile.delete();
            pathFile.deleteOnExit();
            //删除数据
            uploadService.delFile(pd);
            json.setSuccess(true);
            json.setMsg("文件删除成功。");
        } catch (Exception ex) {
            json.setSuccess(false);
            json.setMsg("文件删除失败。");
            ex.printStackTrace();
        }
        this.writeJson(response,json);
    }


    @RequestMapping(value = "/downloadFile", method = RequestMethod.GET)
    public void downloadFile(HttpServletRequest request, HttpServletResponse response , HttpSession session) throws Exception {
        PageData pd = new PageData(request);
        FileUtil.downFile(response, ParaUtil.localName+pd.get("file_path").toString(),pd.get("name").toString());
    }

    /**
     * @Description: uploadthumbnailFile       上传图片并生成缩略图
     * @Param: [request, response, session]
     * @return: void
     * @Author: x
     * @Date: 2020-08-31 13:53
     */
    @RequestMapping(value = "/uploadthumbnailFile", method = RequestMethod.POST)
    public void uploadthumbnailFile(HttpServletRequest request, HttpServletResponse response ,HttpSession session, MultipartFile file) throws Exception {
        PageData pd = new PageData(request);
        Json json = new Json();
        String filename = file.getOriginalFilename();// 文件原名称
        String fileSuffix = filename.substring(filename.lastIndexOf("."));//文件类型
        String fileType = filename.substring(filename.lastIndexOf(".")+1);//文件后缀
        //获取InputStream
        InputStream in = file.getInputStream();
        //根据文件头获取文件类型
        String type = FileType.getFileType(in);
        if(FileType.uploadType.contains(","+type.toLowerCase()+",")&&FileType.uploadSuffixType.contains(","+fileType.toLowerCase()+",")){
            //保存文件
            String path = ParaUtil.common+ pd.get("type")+"/" + DateTimeUtil.getDate()+"/"+ GuidUtil.getGuid()+fileSuffix;
            String savePath = ParaUtil.localName+path;

            // 防止http头注入和路径遍历
            if(FileUtil.http_header_manipulation(savePath) || FileUtil.directory_traversal_check(savePath)) {
                throw new RuntimeException("文件名非法");
            }
            File files = new File(savePath);
            if (!files.getParentFile().exists()){
                files.getParentFile().mkdirs();
            }
            file.transferTo(files);
            pd.put("name",filename);
            pd.put("desnames",filename);
            pd.put("file_type",fileType);
            pd.put("file_size",files.length());
            pd.put("file_path",path);
            pd.put("show_path", ParaUtil.cloudfile+path);
            //处理中图middle_path
            String middle_path = ParaUtil.localName+ParaUtil.common+ pd.get("type")+"/" + DateTimeUtil.getDate()+"/"+ GuidUtil.getGuid()+fileSuffix;
            // 防止http头注入和路径遍历
            if(FileUtil.http_header_manipulation(middle_path) || FileUtil.directory_traversal_check(middle_path)) {
                throw new RuntimeException("文件名非法");
            }
            File toMiddleFile = new File(middle_path);
            if (!toMiddleFile.getParentFile().exists()){
                toMiddleFile.getParentFile().mkdirs();
            }
            Thumbnails.of(files)
                    .size(500, 500)
                    .toFile(toMiddleFile);
            pd.put("middle_path",middle_path);
            pd.put("showmiddle_path", ParaUtil.cloudfile+middle_path);
            json.setSuccess(true);
            json.setData(pd);
            json.setMsg("上传成功。");
        }else{
            json.setSuccess(false);
            json.setMsg("上传失败,文件格式不支持。");
        }
        this.writeJson(response,json);
    }


    //==========================================以下内容暂未用到==============================================================




    /**
     * @Description: uploadFile
     * @Param: [request, response, session, file]
     * @return: void
     * @Author: qingfeng
     * @Date: 2019-3-11 11:04
     */
    @RequestMapping(value = "/uploadOnlyLocalFile", method = RequestMethod.POST)
    public void uploadOnlyLocalFile(HttpServletRequest request, HttpServletResponse response , HttpSession session, MultipartFile file) throws Exception {
        PageData pd = new PageData(request);
        Json json = new Json();

        String filename = file.getOriginalFilename();// 文件原名称
        String fileSuffix = filename.substring(filename.lastIndexOf("."));//文件类型
        String fileType = filename.substring(filename.lastIndexOf(".")+1);//文件后缀
        //获取InputStream
        InputStream in = file.getInputStream();
        //根据文件头获取文件类型
        String type = FileType.getFileType(in);
        if(FileType.uploadType.contains(","+type.toLowerCase()+",")&&FileType.uploadSuffixType.contains(","+fileType.toLowerCase()+",")){
            //保存文件
            String path = ParaUtil.common+ DateTimeUtil.getDate()+"/"+ GuidUtil.getGuid()+fileSuffix;
            String savePath = ParaUtil.localName+path;
            // 防止http头注入和路径遍历
            if(FileUtil.http_header_manipulation(savePath) || FileUtil.directory_traversal_check(savePath)) {
                throw new RuntimeException("文件名非法");
            }
            File files = new File(savePath);
            if (!files.getParentFile().exists()){
                files.getParentFile().mkdirs();
            }
            file.transferTo(files);
            pd.put("name",filename);
            pd.put("desnames",filename);
            pd.put("file_path",path);
            pd.put("file_type",fileType);
            pd.put("file_size",files.length());
            pd.put("show_path", ParaUtil.cloudfile+path);
            json.setSuccess(true);
            json.setData(pd);
            json.setMsg("上传成功。");
        }else{
            json.setSuccess(false);
            json.setMsg("上传失败,文件格式不支持。");
        }
        this.writeJson(response,json);
    }

    /**
     * @Description: delFile
     * @Param: [request, response, session]
     * @return: void
     * @Author: qingfeng
     * @Date: 2019-3-11 11:04
     */
    @RequestMapping(value = "/delOnlyLocalFile", method = RequestMethod.GET)
    public void delOnlyLocalFile(HttpServletRequest request, HttpServletResponse response , HttpSession session) throws Exception {
        PageData pd = new PageData(request);

        String savePath = ParaUtil.localName;
        String path = savePath+pd.get("file_path");
        // 防止http头注入和路径遍历
        if(FileUtil.http_header_manipulation(path) || FileUtil.directory_traversal_check(path)) {
            throw new RuntimeException("文件名非法");
        }
        File files = new File(path);
        files.delete();
        files.deleteOnExit();
        Json json = new Json();
        json.setSuccess(true);
        json.setMsg("文件删除成功。");
        this.writeJson(response,json);
    }


    /**
     * @Description: downloadFile
     * @Param: [request, response, session]
     * @return: void
     * @Author: qingfeng
     * @Date: 2019-3-11 11:05
     */
//    @RequestMapping(value = "/downloadFile", method = RequestMethod.GET)
//    public void downloadFile(HttpServletRequest request, HttpServletResponse response , HttpSession session) throws Exception {
//        PageData pd = new PageData(request);
////        String savePath = ParaUtil.xmAddress;
//        String savePath= request.getSession().getServletContext().getRealPath("/");
//        System.out.println("##################:"+savePath+pd.get("file_path").toString());
//        FileUtil.downFile(response,savePath+pd.get("file_path").toString(),pd.get("name").toString());
//    }


    /**
     * @Description: uploadFile
     * @Param: [request, response, session, file]
     * @return: void
     * @Author: qingfeng
     * @Date: 2018-12-6 13:44
     */
    @ResponseBody
    @RequestMapping(value = "/uploadLocalFile", method = RequestMethod.POST)
    public void uploadLocalFile(HttpServletRequest request, HttpServletResponse response , HttpSession session, MultipartFile file) throws Exception {
        PageData pd = new PageData(request);
        Json json = new Json();

        String filename = file.getOriginalFilename();// 文件原名称
        String fileSuffix = filename.substring(filename.lastIndexOf("."));//文件类型
        String fileType = filename.substring(filename.lastIndexOf(".")+1);//文件后缀
        //获取InputStream
        InputStream in = file.getInputStream();
        //根据文件头获取文件类型
        String type = FileType.getFileType(in);
        if(FileType.uploadType.contains(","+type.toLowerCase()+",")&&FileType.uploadSuffixType.contains(","+fileType.toLowerCase()+",")){
            //保存文件
            File directory = new File("");// 参数为空
            String savePath = directory.getCanonicalPath();
            String path = savePath+"/upload/commomFile/"+ DateTimeUtil.getDate()+"/"+ GuidUtil.getGuid()+fileSuffix;
            // 防止http头注入和路径遍历
            if(FileUtil.http_header_manipulation(path) || FileUtil.directory_traversal_check(path)) {
                throw new RuntimeException("文件名非法");
            }
            File files = new File(path);
            if (!files.getParentFile().exists()){
                files.getParentFile().mkdirs();
            }
            file.transferTo(files);
            pd.put("name",filename);
            pd.put("desnames",filename);
            pd.put("file_path",path);
            pd.put("file_type",fileType);
            pd.put("file_size",files.length());
            pd.put("obj_id","");
            pd.put("file_suffix",fileSuffix);
            pd.put("source",pd.get("type"));
            pd.put("order_by","1");

            PageData user = (PageData) session.getAttribute("loginUser");
            PageData organize = (PageData) session.getAttribute("loginOrganize");
            pd.put("create_user",user.get("id"));
            pd.put("create_organize",organize.get("organize_id"));
            pd.put("create_time", DateTimeUtil.getDateTimeStr());
            uploadService.saveFile(pd);
            json.setSuccess(true);
            json.setData(pd);
            json.setMsg(pd.get("id")+"#"+path+"#"+filename);
        }else{
            json.setSuccess(false);
            json.setMsg("上传失败,文件格式不支持。");
        }
        this.writeJson(response,json);
    }


    /**
     * @Description: delFile
     * @Param: [request, response, session]
     * @return: void
     * @Author: qingfeng
     * @Date: 2018-12-6 13:44
     */
    @ResponseBody
    @RequestMapping(value = "/delLocalFile", method = RequestMethod.GET)
    public void delLocalFile(HttpServletRequest request, HttpServletResponse response , HttpSession session) throws Exception {
        PageData pd = new PageData(request);

        File directory = new File("");// 参数为空
        String savePath = directory.getCanonicalPath();
        String path = savePath+pd.get("file_path");
        // 防止http头注入和路径遍历
        if(FileUtil.http_header_manipulation(path) || FileUtil.directory_traversal_check(path)) {
            throw new RuntimeException("文件名非法");
        }
        File files = new File(path);
        files.delete();
        files.deleteOnExit();
        uploadService.delFile(pd);

        Json json = new Json();
        json.setSuccess(true);
        json.setMsg("文件删除成功。");
        this.writeJson(response,json);
    }


    /**
     * @Description: 删除附件
     * @Param: [pd]
     * @return: void
     * @Author: qingfeng
     * @Date: 2018-12-8 10:39
     */
    public void delFiles(PageData pd) throws Exception {
        File directory = new File("");// 参数为空
        String savePath = directory.getCanonicalPath();
        String path = savePath+pd.get("file_path");
        // 防止http头注入和路径遍历
        if(FileUtil.http_header_manipulation(path) || FileUtil.directory_traversal_check(path)) {
            throw new RuntimeException("文件名非法");
        }
        File files = new File(path);
        files.delete();
        files.deleteOnExit();
        uploadService.delFile(pd);
    }


    /**
     * @Description: downloadFile
     * @Param: [request, response, session]
     * @return: void
     * @Author: qingfeng
     * @Date: 2018-12-6 13:44
     */
    @ResponseBody
    @RequestMapping(value = "/downloadLocalFile", method = RequestMethod.GET)
    public void downloadLocalFile(HttpServletRequest request, HttpServletResponse response , HttpSession session) throws Exception {
        PageData pd = new PageData(request);
        File directory = new File("");// 参数为空
        String savePath = directory.getCanonicalPath();
        FileUtil.downFile(response,savePath+pd.get("file_path").toString(),pd.get("name").toString());
    }



    //=======================================================file 处理================================================

    /**
     * @Description: findFileList
     * @Param: [request, response, session]
     * @return: void
     * @Author: qingfeng
     * @Date: 2020-11-1 14:19
     */
    @RequestMapping(value = "/findFileList", method = RequestMethod.GET)
    public void findFileList(HttpServletRequest request, HttpServletResponse response , HttpSession session) throws Exception {
        PageData pd = new PageData(request);

//        pd.put("idList", Arrays.asList(pd.get("ids").toString().split(",")));
        List<PageData> findFileList = uploadService.findFileList(pd);
        Json json = new Json();
        json.setData(findFileList);
        json.setSuccess(true);
        json.setMsg("文件获取成功。");
        this.writeJson(response,json);
    }

}
