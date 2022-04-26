package com.qingfeng.common.controller;

import com.qingfeng.base.controller.BaseController;
import com.qingfeng.base.model.CommonConfig;
import com.qingfeng.common.service.AuthCertService;
import com.qingfeng.util.*;
import com.qingfeng.util.zip.ZipUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @ProjectName AuthCertController
 * @author Administrator
 * @version 1.0.0
 * @Description AuthCertController
 * @createTime 2021/8/11 14:34
 */
@Controller
@RequestMapping(value = "/common/authCert")
public class AuthCertController extends BaseController {

	@Autowired
	private AuthCertService authCertService;
	@Autowired
	private CommonConfig commonConfig;

	/**
	 * @title index
	 * @description 列表页面
	 * @author Administrator
	 * @updateTime 2021/8/11 14:34
	 */
	@RequestMapping(value = "/index", method = RequestMethod.GET)
	public String index(ModelMap map, HttpServletRequest request, HttpServletResponse response) throws IOException {
		PageData pd = new PageData(request);
		map.put("pd",pd);
		return "web/common/authCert/authCert_list";
	}

	/**
	 * @title findListPage
	 * @description 查询分页列表
	 * @author Administrator
	 * @updateTime 2021/8/11 14:35
	 */
	@RequestMapping(value = "/findListPage", method = RequestMethod.GET)
	public void findListPage(Page page, HttpServletRequest request, HttpServletResponse response, HttpSession session) throws IOException {
		PageData pd = new PageData(request);
		//处理分页
		if(Verify.verifyIsNotNull(pd.get("page"))){
			page.setIndex(Integer.parseInt(pd.get("page").toString()));
		}else{
			page.setIndex(1);
		}
		if(Verify.verifyIsNotNull(pd.get("limit"))){
			page.setShowCount(Integer.parseInt(pd.get("limit").toString()));
		}else{
			page.setShowCount(10);
		}
		page.setPd(pd);
		List<PageData> list = authCertService.findListPage(page);
		int num = authCertService.findListSize(page);
		Json json = new Json();
		json.setMsg("获取数据成功。");
		json.setCount(num);
		json.setData(list);
		json.setSuccess(true);
		this.writeJson(response,json);
	}

    /**
     * @title findList
     * @description 查询列表
     * @author Administrator
     * @updateTime 2021/8/11 14:35
     */
	@RequiresPermissions("authCertList")
    @RequestMapping(value = "/findList", method = RequestMethod.GET)
    public void findList(HttpServletRequest request, HttpServletResponse response) throws IOException  {
    	PageData pd = new PageData(request);

    	List<PageData> list = authCertService.findList(pd);
        Json json = new Json();
        json.setMsg("获取数据成功。");
        json.setData(list);
        json.setSuccess(true);
        this.writeJson(response,json);
    }

	/**
	 * @title findInfo
	 * @description 查询详情
	 * @author Administrator
	 * @updateTime 2021/8/11 14:36
	 */
	@RequiresPermissions("authCert:info")
	@RequestMapping(value = "/findInfo", method = RequestMethod.GET)
	public String findInfo(ModelMap map, HttpServletRequest request,HttpSession session)  {
		PageData pd = new PageData(request);
		PageData p = authCertService.findInfo(pd);
		map.addAttribute("p",p);
		map.put("pd",pd);
		return "web/common/authCert/authCert_info";
	}

	/**
	 * @title toAdd
	 * @description 跳转添加页面
	 * @author Administrator
	 * @updateTime 2021/8/11 14:36
	 */
	@RequiresPermissions("authCert:add")
	@RequestMapping(value = "/toAdd", method = RequestMethod.GET)
	public String toAdd(ModelMap map, HttpServletRequest request)  {
		PageData pd = new PageData(request);
		map.put("pd",pd);
		return "web/common/authCert/authCert_add";
	}

	/**
	 * @title save
	 * @description 保存
	 * @author Administrator
	 * @updateTime 2021/8/11 14:36
	 */
	@RequiresPermissions("authCert:add")
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public void save(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws IOException  {
		PageData pd = new PageData(request);
		System.out.println("------------------------");
		System.out.println(pd);
		//主键id
		String id = GuidUtil.getUuid();
		pd.put("id", id);
		String time = DateTimeUtil.getDateTimeStr();
		pd.put("create_time", time);
		pd.put("status","1");
		pd.put("type","0");
		pd.put("limit_num","0");
		//处理数据权限
		PageData user = (PageData) session.getAttribute("loginUser");
		PageData organize = (PageData) session.getAttribute("loginOrganize");
		pd.put("create_user",user.get("id"));
		pd.put("create_organize",organize.get("organize_id"));

		authCertService.save(pd);
		Json json = new Json();
		json.setSuccess(true);
		json.setMsg("操作成功。");
		this.writeJson(response,json);
	}


	/**
	 * @Description: toUpdate
	 * @Param: [map, request]
	 * @return: java.lang.String
	 * @Author: anxingtao
	 * @Date: 2020-9-22 22:47
	 */
	@RequiresPermissions("authCert:edit")
	@RequestMapping(value = "/toUpdate", method = RequestMethod.GET)
	public String toUpdate(ModelMap map, HttpServletRequest request)  {
		PageData pd = new PageData(request);
		PageData p = authCertService.findInfo(pd);
		map.put("p",p);
		return "web/common/authCert/authCert_update";
	}

	/**
	 * @title update
	 * @description 更新数据
	 * @author Administrator
	 * @updateTime 2021/8/11 14:37
	 */
	@RequiresPermissions("authCert:edit")
	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public void update(HttpServletRequest request, HttpServletResponse response,HttpSession session) throws IOException  {
		PageData pd = new PageData(request);

		String time = DateTimeUtil.getDateTimeStr();
		pd.put("create_time", time);
		pd.put("update_time", time);
		PageData user = (PageData) session.getAttribute("loginUser");
		pd.put("update_user",user.get("id"));
		authCertService.update(pd);
		Json json = new Json();
		json.setSuccess(true);
		json.setMsg("操作成功。");
		this.writeJson(response,json);
	}

	/**
	 * @title del
	 * @description 删除
	 * @author Administrator
	 * @updateTime 2021/8/11 14:36
	 */
	@RequiresPermissions("authCert:del")
	@RequestMapping(value = "/del", method = RequestMethod.GET)
	public void del(HttpServletRequest request, HttpServletResponse response) throws IOException  {
		PageData pd = new PageData(request);
		String[] ids = pd.get("ids").toString().split(",");
		authCertService.del(ids);
		Json json = new Json();
		json.setSuccess(true);
		json.setMsg("操作成功。");
		this.writeJson(response,json);
	}


	/**
	 * @title exportCert
	 * @description 导出证书
	 * @author Administrator
	 * @updateTime 2021/8/11 15:46
	 */
	@RequiresPermissions("authCert:exportCert")
	@RequestMapping(value = "/exportCert", method = RequestMethod.GET)
	public void exportCert(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws Exception {
		PageData pd = new PageData(request);
		PageData p = authCertService.findInfo(pd);
		String content = p.get("title")+"#"+p.get("start_time")+"#"+p.get("end_time")+"#"+p.get("ip_address")+"#"+p.get("mac_address")+"#"+p.get("other_limit");
		System.out.println(content);
		System.out.println(commonConfig.getPublicKey());
		String license = RSAUtils.encrypt(content,commonConfig.getPublicKey());
		// 创建根节点
		Element root = new Element("QingFeng");
		// 为根节点设置属性
		root.setAttribute("version", p.get("version_num").toString());
		// 创建Document对象，并为其设置根节点
		Document document = new Document(root);

		Element title = new Element("Title");
		Element sdate = new Element("SDate");
		Element edate = new Element("EDate");
		Element ip = new Element("IP");
		Element mac = new Element("MAC");
		Element olimit = new Element("OLimit");
		Element licenseKeys = new Element("LicenseKeys");

		title.addContent(p.get("title").toString());
		sdate.addContent(p.get("start_time").toString());
		edate.addContent(p.get("end_time").toString());
		ip.addContent(p.get("ip_address")+"");
		mac.addContent(p.get("mac_address")+"");
		olimit.addContent(p.get("other_limit")+"");
		licenseKeys.addContent(license);

		root.addContent(title);
		root.addContent(sdate);
		root.addContent(edate);
		root.addContent(ip);
		root.addContent(mac);
		root.addContent(olimit);
		root.addContent(licenseKeys);
		// 创建XMLOutputter对象
		XMLOutputter outputter = new XMLOutputter();
		//创建Format对象（自动缩进、换行）
		Format format = Format.getPrettyFormat();
		//为XMLOutputter设置Format对象
		outputter.setFormat(format);
		// 将Document转换成XML
		String tempPath = "D:\\home\\template\\QingfengLicense.xml";
//		String tempPath = session.getServletContext().getRealPath("/") + "/template/QingfengLicense.xml";
		outputter.output(document, new FileOutputStream(new File(tempPath)));
//		String toPath = session.getServletContext().getRealPath("/") + "/template/QingfengLicense.zip";
		/** 测试压缩方法2  */
//		List<File> fileList = new ArrayList<>();
//		fileList.add(new File(tempPath));
//		FileOutputStream fos2 = new FileOutputStream(new File(toPath));
//		ZipUtils.toZip(fileList, fos2);
		FileUtil.downFile(response, tempPath, "QingfengLicense.xml");
//		File file = new File(toPath);
//		file.delete();
//		file.deleteOnExit();
	}

	public static void downFile(HttpServletResponse response, String filePath,
								String filename) throws Exception {
		File tempFile = new File(filePath);
		System.out.println(tempFile.exists());
		if (tempFile.exists()) {
			response.reset();
			response.setContentType("bin");//
			filename = new String(filename.getBytes(), "ISO-8859-1");
			response.addHeader("Content-Disposition", "attachment;filename="
					+ filename);
			FileInputStream fis = new FileInputStream(tempFile);
			OutputStream os = response.getOutputStream();
			byte[] bb = new byte[1024*8];
			int i = 0;
			while ((i = fis.read(bb)) > 0) {
				os.write(bb, 0, i);
			}
			os.close();
			os.flush();
			fis.close();
		}
	}

}
