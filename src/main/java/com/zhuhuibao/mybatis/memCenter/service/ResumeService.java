package com.zhuhuibao.mybatis.memCenter.service;

import com.zhuhuibao.common.Response;
import com.zhuhuibao.common.constant.Constants;
import com.zhuhuibao.common.constant.MsgCodeConstant;
import com.zhuhuibao.mybatis.memCenter.entity.Resume;
import com.zhuhuibao.mybatis.memCenter.mapper.ResumeMapper;
import com.zhuhuibao.utils.MsgPropertiesUtils;
import com.zhuhuibao.utils.pagination.model.Paging;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.zhuhuibao.common.constant.ApiConstants;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by cxx on 2016/4/19 0019.
 */
@Service
@Transactional
public class ResumeService {
    private static final Logger log = LoggerFactory.getLogger(ResumeService.class);

    @Autowired
    private ResumeMapper resumeMapper;

    @Autowired
    ApiConstants apiConstants;

    /**
     * 发布简历
     */
    public Response setUpResume(Resume resume, String id){
        Response response = new Response();
        int isSetUp = resumeMapper.setUpResume(resume);
        try{
            if(isSetUp==1){
                Resume resume1 = resumeMapper.searchMyResume(id);
                response.setCode(200);
                response.setData(resume1.getId());
            }else {
                response.setMessage("发布失败");
            }
        }catch (Exception e){
            log.error("setUpResume error",e);
            e.printStackTrace();
        }
        return response;
    }

    /**
     * 查询我创建的简历
     */
    public Response searchMyResume(String id){
        Response response = new Response();
        Resume resume = resumeMapper.searchMyResume(id);
        Resume resume1 = resumeMapper.searchMyResumeAllInfo(id);
        if(resume1!=null){
            Integer i = 0;
            if(resume1.getJobExperience()!=null && !"".equals(resume1.getJobExperience())){
                i = i+1;
            }
            if(resume1.getEduExperience()!=null && !"".equals(resume1.getEduExperience())){
                i=i+1;
            }
            if(resume1.getProjectExperience()!=null && !"".equals(resume1.getProjectExperience())){
                i=i+1;
            }
            if(resume1.getAttach()!=null && !"".equals(resume1.getAttach())){
                i=i+1;
            }
            if(resume1.getPhoto()!=null && !"".equals(resume1.getPhoto())){
                i=i+1;
            }
            /*if(resume1.getExperienceYear()!=null && !"".equals(resume1.getExperienceYear())){
                i=i+1;
            }*/
            if(resume1.getCompany()!=null && !"".equals(resume1.getCompany())){
                i=i+1;
            }
            if(resume1.getPositionName()!=null && !"".equals(resume1.getPositionName())){
                i=i+1;
            }
            NumberFormat numberFormat = NumberFormat.getInstance();
            numberFormat.setMaximumFractionDigits(0);
            String result = numberFormat.format((float) (i+16) / (float) 23 * 100);
            String b = result + "%";
            Map map = new HashMap();
            map.put("info",resume);
            map.put("percent",b);
            response.setCode(200);
            response.setData(map);
        }else{
            response.setCode(400);
            response.setMessage("暂无简历");
        }
        return response;
    }

    /**
     * 查询我创建的简历的全部信息
     */
    public Response searchMyResumeAllInfo(String id){
        Response response = new Response();
        Resume resume = resumeMapper.searchMyResumeAllInfo(id);
        response.setCode(200);
        response.setData(resume);
        return response;
    }

    /**
     * 更新简历,刷新简历
     */
    public Response updateResume(Resume resume){
        Response response = new Response();
        int isUpdate = resumeMapper.updateResume(resume);
        try{
            if(isUpdate==1){
                response.setCode(200);
                response.setData(resume.getId());
            }else {
                response.setCode(400);
                response.setMessage("更新失败");
            }
        }catch (Exception e){
            log.error("updateResume error",e);
            e.printStackTrace();
        }
        return response;
    }

    /**
     * 预览简历
     */
    public Response previewResume(String id) throws Exception{
        Response response = new Response();
        try {
            Resume resume = resumeMapper.previewResume(id);
            response.setCode(200);
            response.setData(resume);
        }catch(Exception e)
        {
            throw e;
        }
        return response;
    }

    public Response findAllResume(Paging<Resume> pager, Map<String,Object> map)
    {
        Response response = new Response();
        try
        {
            List<Resume> resumeList = resumeMapper.findAllResume(pager.getRowBounds(),map);
            pager.result(resumeList);
            response.setData(pager);
        }
        catch(Exception e)
        {
            log.error("find all resume error!",e);
            response.setCode(MsgCodeConstant.response_status_400);
            response.setMsgCode(MsgCodeConstant.mcode_common_failure);
            response.setMessage((MsgPropertiesUtils.getValue(String.valueOf(MsgCodeConstant.mcode_common_failure))));
            return response;
        }
        return response;
    }

    /**
     * 我收到的简历
     */
    public Response receiveResume(Paging<Resume> pager, String id){
        Response response = new Response();
        List<Resume> resumeList = resumeMapper.findAllReceiveResume(pager.getRowBounds(),id);
        List list = new ArrayList();
        for(int i=0;i<resumeList.size();i++){
            Resume resume = resumeList.get(i);
            Map map = new HashMap();
            map.put(Constants.id,resume.getId());
            map.put(Constants.name,resume.getName());
            map.put(Constants.publishTime,resume.getPublishTime());
            map.put(Constants.realName,resume.getRealName());
            map.put(Constants.experienceYear,resume.getExperienceYear());
            list.add(map);
        }
        pager.result(list);
        response.setCode(200);
        response.setData(pager);
        return response;
    }

    /**
     * 导出简历
     */
    public Map<String,String> exportResume(String id){
        Map<String,String> resumeMap = new HashMap<String,String>();
        Resume resume = resumeMapper.previewResume(id);
        if(resume != null && resume.getAttach() != null)
        {
            String url = apiConstants.getUploadDoc()+"/job/"+resume.getAttach();
            resume.setAttach(url);
            resumeMap.put("title",resume.getTitle());
            resumeMap.put("name",resume.getRealName());
            resumeMap.put("sex",resume.getSex());
            resumeMap.put("marriage",resume.getMarriage());
            resumeMap.put("birthYear",resume.getBirthYear());
            resumeMap.put("education",resume.getEducation());
            resumeMap.put("liveArea",resume.getLiveArea());
            resumeMap.put("workYear",resume.getWorkYear());
            resumeMap.put("mobile",resume.getMobile());
            resumeMap.put("email",resume.getEmail());
            resumeMap.put("jobNature",resume.getJobNature());
            resumeMap.put("post",resume.getPost());
            resumeMap.put("jobArea",resume.getJobArea());
            resumeMap.put("hopeSalary",resume.getHopeSalary());
            resumeMap.put("status",resume.getStatus());
            resumeMap.put("eduExperience",resume.getEduExperience().replaceAll("<br/>","\n"));
            resumeMap.put("jobExperience",resume.getJobExperience().replaceAll("<br/>","\n"));
            resumeMap.put("projectExperience",resume.getProjectExperience().replaceAll("<br/>","\n"));
        }
        return resumeMap;
    }

    public String downloadBill(String id){
        Resume resume = resumeMapper.searchResumeById(id);
        String fileUrl = "";
        if(resume!=null){
            fileUrl = resume.getAttach();
        }
        return fileUrl;
    }

    /**
     * 根据会员ID查询出他自己的简历
     * @param createID
     * @return
     */
    public Resume queryResumeByCreateId(Long createID)
    {
        Resume resume = null;
        try
        {
            List<Resume> resumeList = resumeMapper.queryResumeByCreateId(createID);
            if(!resumeList.isEmpty())
            {
                resume = resumeList.get(0);
            }
        }catch(Exception e)
        {
            log.error("query resume by createID error!",e);
        }
        return resume;
    }

    /**
     * 最新求职
     * @param condition 查询条件
     * @return
     */
    public List queryLatestResume(Map<String,Object> condition) throws Exception{
        List list = new ArrayList();
        try {
            List<Resume> resumeList = resumeMapper.queryLatestResume(condition);

            for (int i = 0; i < resumeList.size(); i++) {
                Resume resume = resumeList.get(i);
                Map map = new HashMap();
                map.put(Constants.id, resume.getId());
                map.put(Constants.logo, resume.getPhoto());
                map.put(Constants.status, resume.getStatus());
                map.put(Constants.area, resume.getJobCity());
                map.put(Constants.name, resume.getRealName());
                map.put(Constants.position, resume.getPost());
                map.put(Constants.experienceYear, resume.getWorkYear());
                map.put(Constants.age, resume.getBirthYear());
                map.put(Constants.salary, resume.getHopeSalary());
                list.add(map);
            }
        }catch(Exception e)
        {
            log.error("query latest resume error!");
            throw e;
        }
        return list;
    }

    /**
     * 判断某个人是否已经创建简历
     * @param createID  创建者ID或者会员ID
     * @return true:存在，false:不存在
     */
    public Boolean isExistResume(Long createID)
    {
        log.info("isExistResume createID = "+createID);
        Boolean isExist = false;
        try {
            int count = resumeMapper.isExistResume(createID);
            if(count > 0)
            {
                isExist = true;
            }
        }catch(Exception e)
        {
            log.error("judge is exist resume error !!!");
            throw e;
        }
        return isExist;
    }
}
