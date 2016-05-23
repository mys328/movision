package com.zhuhuibao.business.job;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.zhuhuibao.common.constant.Constant;
import com.zhuhuibao.common.JsonResult;
import com.zhuhuibao.common.constant.JobConstant;
import com.zhuhuibao.common.util.ShiroUtil;
import com.zhuhuibao.mybatis.memCenter.entity.Job;
import com.zhuhuibao.mybatis.memCenter.entity.Resume;
import com.zhuhuibao.mybatis.memCenter.service.JobPositionService;
import com.zhuhuibao.mybatis.memCenter.service.JobRelResumeService;
import com.zhuhuibao.mybatis.memCenter.service.ResumeService;
import com.zhuhuibao.mybatis.oms.service.ChannelNewsService;
import com.zhuhuibao.mybatis.sitemail.entity.MessageText;
import com.zhuhuibao.mybatis.sitemail.service.SiteMailService;
import com.zhuhuibao.utils.DateUtils;
import com.zhuhuibao.utils.file.ExporDoc;
import com.zhuhuibao.utils.pagination.model.Paging;
import com.zhuhuibao.utils.pagination.util.StringUtils;
import org.apache.poi.hwpf.HWPFDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.*;

/**
 * Created by Administrator on 2016/4/21 0021.
 */
@RestController
@RequestMapping("/rest/jobsite")
@Api(value="Jobsite", description="人才网")
public class JobSiteController {
    private final static Logger log = LoggerFactory.getLogger(JobSiteController.class);

    @Autowired
    JobPositionService job;

    @Autowired
    ResumeService resume;

    @Autowired
    SiteMailService smService;

    @Autowired
    JobRelResumeService jrrService;

    @Autowired
    ChannelNewsService newsService;

    @RequestMapping(value="applyPosition", method = RequestMethod.POST)
    @ApiOperation(value="应聘职位",notes = "应聘职位",response = JsonResult.class)
    public JsonResult applyPosition(@ApiParam(value = "简历ID") @RequestParam String jobID,
                                    @ApiParam(value = "发布职位企业ID") @RequestParam String recID,
                                    @ApiParam(value = "职位标题") @RequestParam String messageText) throws JsonGenerationException, JsonMappingException, IOException
    {
        log.info("applay position recID = "+recID+" messageText ="+messageText);
        Long createid = ShiroUtil.getCreateID();
        JsonResult jsonResult = new JsonResult();
        if(createid != null)
        {
            Resume rme = resume.queryResumeByCreateId(createid);
            if(rme != null && rme.getId() != null) {
                Long resumeID = Long.valueOf(rme.getId());
                MessageText msgText = new MessageText();
                msgText.setSendID(createid);
                msgText.setRecID(Long.valueOf(recID));
                msgText.setMessageText(messageText);
                msgText.setTypeID(resumeID);
                msgText.setType(JobConstant.SITEMAIL_TYPE_JOB_ELEVEN);
                jsonResult = smService.addSiteMail(msgText);
                jrrService.insert(Long.valueOf(jobID), resumeID);
            }
        }
        else
        {
            jsonResult.setCode(400);
            jsonResult.setMessage("you are not login!!!!");
        }
        return jsonResult;
    }

    /**
     * 定义简历模板导出简历
     * @param req
     * @param response
     * @param resumeID 简历ID
     * @throws JsonGenerationException
     * @throws JsonMappingException
     * @throws IOException
     */
    @RequestMapping(value="exportResume", method = RequestMethod.GET)
    @ApiOperation(value="定义简历模板导出简历",notes = "定义简历模板导出简历")
    public void exportResume(HttpServletRequest req, HttpServletResponse response,
                             @ApiParam(value = "简历ID") @RequestParam Long resumeID) throws IOException
    {
        log.info("export resume id == "+resumeID);
        response.setDateHeader("Expires", 0);
        response.setHeader("Cache-Control",
                "no-store, no-cache, must-revalidate");
        response.addHeader("Cache-Control", "post-check=0, pre-check=0");
        response.setContentType("application/msword");
        try {
            String path = req.getSession().getServletContext().getRealPath("/");
            log.info("base path = "+path);
            Map<String, String> resumeMap = resume.exportResume(String.valueOf(resumeID));
            if (!resumeMap.isEmpty()) {
                response.setHeader("Content-disposition", "attachment; filename=\""
                        + URLEncoder.encode(resumeMap.get("title"), "UTF-8") + ".doc\""); //
                HWPFDocument document = ExporDoc.replaceDoc(path + "resumeTemplate.doc", resumeMap);
                ByteArrayOutputStream ostream = new ByteArrayOutputStream();
                document.write(ostream);
                ServletOutputStream stream = response.getOutputStream();
                stream.write(ostream.toByteArray());
                stream.flush();
                stream.close();
                stream.close();
            }
        }catch(IOException e){
                e.printStackTrace();
        }
    }

    @RequestMapping(value="queryCompanyInfo", method = RequestMethod.GET)
    @ApiOperation(value="公司详情",notes = "公司详情",response = JsonResult.class)
    public JsonResult queryCompanyInfo(@ApiParam(value = "创建者ID(会员ID)") @RequestParam(required = true) Long id) throws IOException
    {
        log.info("query company info id "+id);
        JsonResult jsonResult = job.queryCompanyInfo(id);
        return jsonResult;
    }

    @RequestMapping(value="queryAdvertisingPosition", method = RequestMethod.GET)
    @ApiOperation(value = "企业信息广告位",notes = "展示最新发布职位的企业信息，默认7条可配置",response = JsonResult.class)
    public JsonResult queryAdvertisingPosition() throws IOException
    {
        log.info("query advertising postion");
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("isDelete",0);
        map.put("count",7);
        JsonResult jsonResult = job.queryAdvertisingPosition(map);
        return jsonResult;
    }

    @RequestMapping(value="queryPositionInfoByID", method = RequestMethod.GET)
    @ApiOperation(value = "职位详情页面",notes = "职位搜索查看职位详情",response = JsonResult.class)
    public JsonResult queryPositionInfoByID(@ApiParam(value = "招聘职位ID") @RequestParam Long id) throws Exception
    {
        JsonResult jsonResult = job.queryPositionInfoByID(id);
        job.updateViews(id);
        return jsonResult;
    }

    @RequestMapping(value="queryOtherPosition", method = RequestMethod.GET)
    @ApiOperation(value = "职位详情中的其它职位",notes = "职位详情中的其它职位",response = JsonResult.class)
    public JsonResult queryOtherPosition(
                                   @ApiParam(value = "职位ID") @RequestParam(required = true) String jobID,
                                   @ApiParam(value="") @RequestParam() String createID,
                                   @ApiParam(value = "页码") @RequestParam(required = false)String pageNo,
                                   @ApiParam(value="每页显示的条数") @RequestParam(required = false) String pageSize) throws IOException
    {
        log.info("query position info by id");
        if (StringUtils.isEmpty(pageNo)) {
            pageNo = "1";
        }
        if (StringUtils.isEmpty(pageSize)) {
            pageSize = "10";
        }
        Paging<Job> pager = new Paging<Job>(Integer.valueOf(pageNo), Integer.valueOf(pageSize));
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("createID",createID);
        map.put("jobID",jobID);
        JsonResult jsonResult = new JsonResult();
        List<Job> jobList = job.findAllOtherPosition(pager,map);
        pager.result(jobList);
        jsonResult.setData(pager);
        return jsonResult;
    }

    @RequestMapping(value="queryPublishPositionByID", method = RequestMethod.GET)
    @ApiOperation(value="查询某个企业发布的职位",notes = "查询某个企业发布的职位",response = JsonResult.class)
    public JsonResult queryPublishPositionByID(
                                   @ApiParam(value = "企业ID") @RequestParam String enterpriseID,
                                   @ApiParam(value= "城市code") @RequestParam(required = false) String city,
                                   @ApiParam(value= "名称") @RequestParam(required = false) String name,
                                   String pageNo,String pageSize) throws IOException
    {
        log.info("query position info by id");
        if (StringUtils.isEmpty(pageNo)) {
            pageNo = "1";
        }
        if (StringUtils.isEmpty(pageSize)) {
            pageSize = "10";
        }
        Paging<Job> pager = new Paging<Job>(Integer.valueOf(pageNo), Integer.valueOf(pageSize));
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("createID",enterpriseID);
        map.put("city",city);
        map.put("name",name);
        JsonResult jsonResult = new JsonResult();
        List<Job> jobList = job.findAllOtherPosition(pager,map);
        pager.result(jobList);
        jsonResult.setData(pager);
        return jsonResult;
    }

    @RequestMapping(value="queryAllPosition", method = RequestMethod.GET)
    @ApiOperation(value="职位搜索",notes = "职位频道页搜索",response = JsonResult.class)
    public JsonResult queryAllPosition(@ApiParam(value = "公司名称或企业名称") @RequestParam(required = false) String name,
                                       @ApiParam(value = "省代码") @RequestParam(required = false) String province,
                                       @ApiParam(value = "市代码") @RequestParam(required = false) String city,
                                       @ApiParam(value = "区代码") @RequestParam(required = false) String area,
                                       @ApiParam(value = "企业规模") @RequestParam(required = false) String employeeNumber,
                                       @ApiParam(value = "企业性质") @RequestParam(required = false) String enterpriseType,
                                       @ApiParam(value = "发布时间") @RequestParam(required = false) String days,
                                       @ApiParam(value = "薪资") @RequestParam(required = false) String salary,
                                       @ApiParam(value = "职位类别") @RequestParam(required = false) String positionType,
                                       @ApiParam(value = "页码") @RequestParam(required = false) String pageNo,
                                       @ApiParam(value = "每页显示的数目") @RequestParam(required = false) String pageSize) throws IOException
    {
        JsonResult jsonResult = new JsonResult();
        log.info("query position info by id");
        if (StringUtils.isEmpty(pageNo)) {
            pageNo = "1";
        }
        if (StringUtils.isEmpty(pageSize)) {
            pageSize = "10";
        }
        Map<String,Object> map = new HashMap<String,Object>();
        if(name != null && !"".equals(name))
        {
            map.put("name",name.replace("_","\\_"));
        }
        map.put("positionType",positionType);
        map.put("city",city);
        map.put("employeeNumber",employeeNumber);
        map.put("enterpriseType",enterpriseType);
        //发布时间一周内，一天内
        if(days != null && !"".equals(days)) {
            Date date = DateUtils.date2Sub(new Date(), 5, -Integer.parseInt(days));
            String publishTime = DateUtils.date2Str(date, "yyyy-MM-dd");
            map.put("publishTime", publishTime);
        }
        map.put("salary",salary);
        Paging<Job> pager = new Paging<Job>(Integer.valueOf(pageNo), Integer.valueOf(pageSize));
        List<Job> jobList = job.findAllOtherPosition(pager,map);
        pager.result(jobList);
        jsonResult.setData(pager);
        return jsonResult;
    }

    @RequestMapping(value="findAllResume", method = RequestMethod.GET)
    @ApiOperation(value="人才库搜索",notes = "人才库搜索",response = JsonResult.class)
    public JsonResult findAllResume(@ApiParam(value = "简历名称") @RequestParam(required = false) String title,
                                    @ApiParam(value = "期望工作城市") @RequestParam(required = false) String jobCity,
                                    @ApiParam(value = "工作年限前") @RequestParam(required = false) String expYearBefore,
                                    @ApiParam(value = "工作年限后") @RequestParam(required = false) String expYearBehind,
                                    @ApiParam(value = "学历") @RequestParam(required = false) String education,
                                    @ApiParam(value = "职位类别") @RequestParam(required = false) String positionType,
                                    @ApiParam(value = "页码") @RequestParam(required = false) String pageNo,
                                    @ApiParam(value = "每页显示的数目") @RequestParam(required = false) String pageSize) throws IOException
    {
        log.info("find all resume!!");
        JsonResult jsonResult = new JsonResult();
        if (StringUtils.isEmpty(pageNo)) {
            pageNo = "1";
        }
        if (StringUtils.isEmpty(pageSize)) {
            pageSize = "10";
        }
        Paging<Resume> pager = new Paging<Resume>(Integer.valueOf(pageNo), Integer.valueOf(pageSize));
        Map<String, Object> map = new HashMap<String, Object>();
        if(title != null && !"".equals(title))
        {
            map.put("title",title.replace("_","\\_"));
        }
        map.put("jobCity",jobCity);
        map.put("expYearBefore",expYearBefore);
        map.put("expYearBehind",expYearBehind);
        map.put("education",education);
        map.put("isPublic","1");
        map.put("status",JobConstant.JOB_MEMBER_STATUS_LOGOUT);
        if(positionType != null && positionType.length() > 0)
        {
            String[] positionTypes = positionType.split(",");
            List<String> positionList = Arrays.asList(positionTypes);
            map.put("positionList",positionList);
        }
        jsonResult = resume.findAllResume(pager, map);
        jsonResult.setData(pager);
        return jsonResult;
    }

    /**
     * 查询推荐感兴趣的职位
     */
    @RequestMapping(value = "queryRecommendPosition", method = RequestMethod.GET)
    @ApiOperation(value = "根据当前职位类别查询出其它公司的职位",notes = "查找按照最新时间排序",response = JsonResult.class)
    public JsonResult queryRecommendPosition(@ApiParam(value = "职位ID") @RequestParam String jobID,
                                             @ApiParam(value="职位类别ID") @RequestParam(required = false) String postID) throws IOException {
        //查询不同公司发布的相同职位
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("postID",postID);
        map.put("count",JobConstant.JOB_RECOMMEND_COUNT);
        map.put("delete",JobConstant.JOB_DELETE_ZERO);
        map.put("jobID",jobID);
        JsonResult jsonResult = job.searchSamePosition(map);
        return jsonResult;
    }

    /**
     * 热门招聘
     */
    @RequestMapping(value = "queryHotPosition", method = RequestMethod.GET)
    @ApiOperation(value = "人才网首页热门招聘 默认9个",notes = "人才网首页热门招聘 默认9个",response = JsonResult.class)
    public JsonResult queryHotPosition() throws Exception {
        Map<String,Object> condition = new HashMap<String,Object>();
        condition.put("count",JobConstant.JOB_HOTPOSITION_COUNT_NINE);
        condition.put("delete",JobConstant.JOB_DELETE_ZERO);
        condition.put("salary",JobConstant.JOB_TYPE_SALARY);
        condition.put("status",JobConstant.JOB_MEMBER_STATUS_LOGOUT);
        List jobList = job.queryHotPosition(condition);
        JsonResult jsonResult = new JsonResult();
        jsonResult.setData(jobList);
        return jsonResult;
    }

    /**
     * 最新求职
     */
    @RequestMapping(value = "queryLatestResume", method = RequestMethod.GET)
    @ApiOperation(value = "人才网首页最新求职 默认9个",notes = "人才网首页最新求职    默认9个",response = JsonResult.class)
    public JsonResult queryLatestResume() throws Exception {
        Map<String,Object> condition = new HashMap<String,Object>();
        condition.put("count",JobConstant.JOB_RESUME_LATEST_COUNT_NINE);
        condition.put("public",JobConstant.JOB_RESUME_STATUS_PUBLIC);
        condition.put("status",JobConstant.JOB_MEMBER_STATUS_LOGOUT);
        JsonResult jsonResult = new JsonResult();
        List resumeList = resume.queryLatestResume(condition);
        jsonResult.setData(resumeList);
        return jsonResult;
    }

    /**
     * 最新招聘（按分类一起查询）
     */
    @RequestMapping(value = "queryLatestJob", method = RequestMethod.GET)
    @ApiOperation(value = "人才网首页最新招聘",notes = "人才网首页最新招聘",response = JsonResult.class)
    public JsonResult queryLatestJob() throws Exception {
        JsonResult jsonResult = new JsonResult();
        List list = job.queryLatestJob(5);
        jsonResult.setData(list);
        return jsonResult;

    }

    /**
     * 名企招聘
     */
    @RequestMapping(value = "greatCompanyPosition", method = RequestMethod.GET)
    @ApiOperation(value = "广告位名企招聘",notes = "广告位名企招聘",response = JsonResult.class)
    public JsonResult greatCompanyPosition() throws Exception {
        JsonResult jsonResult = new JsonResult();
        List enterpriseList = job.greatCompanyPosition();
        jsonResult.setData(enterpriseList);
        return jsonResult;
    }

    @RequestMapping(value = "querySimilarCompany", method = RequestMethod.GET)
    @ApiOperation(value = "相似企业",notes = "相似企业",response = JsonResult.class)
    public JsonResult querySimilarCompany(String id) throws IOException {
        JsonResult jsonResult = job.querySimilarCompany(id,4);
        return jsonResult;
    }

    @RequestMapping(value = "queryEnterpriseHotPosition", method = RequestMethod.GET)
    @ApiOperation(value="热门职位", notes="查询名企发布的热门职位", response=JsonResult.class)
    public JsonResult queryEnterpriseHotPosition() throws IOException {
        JsonResult jsonResult = new JsonResult();
        Map<String,Object> map = new HashMap<String,Object>();
        //“1”推荐企业.
        map.put("recommend", JobConstant.JOB_RECOMMEND_TRUE);
        map.put("count",JobConstant.JOB_HOTPOSITION_COUNT_EIGHT);
        map.put("status",JobConstant.JOB_MEMBER_STATUS_LOGOUT);
        List<Job> jobList = job.queryEnterpriseHotPosition(map);
        jsonResult.setData(jobList);
        return jsonResult;
    }

    @RequestMapping(value="isExistResume",method=RequestMethod.GET)
    @ApiOperation(value = "判断是否已经创建简历",notes = "判断是否已经创建简历",response=JsonResult.class)
    public JsonResult isExistResume() throws Exception
    {
        JsonResult jsonResult = new JsonResult();
        Long createID = ShiroUtil.getCreateID();
        if(createID != null) {
            Boolean isExist = resume.isExistResume(createID);
            jsonResult.setData(isExist);
        }
        return jsonResult;
    }

    @RequestMapping(value="queryUnreadMsgCount",method = RequestMethod.GET)
    @ApiOperation(value="未读消息数目",notes = "人才网未读消息数目",response = JsonResult.class)
    public JsonResult queryUnreadMsgCount()
    {
        JsonResult jsonResult = new JsonResult();
        Long receiveID = ShiroUtil.getCreateID();
        if(receiveID != null) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("recID", receiveID);
            map.put("type", JobConstant.SITEMAIL_TYPE_JOB_ELEVEN);
            map.put("status", Constant.MAILSITE_STATUS_UNREAD);
            jsonResult.setData(smService.queryUnreadMsgCount(map));
        }
        return jsonResult;
    }

    @RequestMapping(value="isExistApplyPosition",method = RequestMethod.GET)
    @ApiOperation(value="查看此职位是否已被同一个人申请，10天后可以再次申请",notes = "1:已申请，0：未申请",response = JsonResult.class)
    public JsonResult isExistApplyPosition(@ApiParam(value = "职位ID") @RequestParam String JobID,
                                           @ApiParam(value = "简历ID") @RequestParam String resumeID) throws Exception {
        JsonResult jsonResult = new JsonResult();
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("jobID",JobID);
        map.put("resumeID",resumeID);
        Integer count = jrrService.isExistApplyPosition(map);
        jsonResult.setData(count);
        return jsonResult;
    }
}
