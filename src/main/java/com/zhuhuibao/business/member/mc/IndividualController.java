package com.zhuhuibao.business.member.mc;

import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.zhuhuibao.common.Response;
import com.zhuhuibao.common.constant.MemberConstant;
import com.zhuhuibao.common.constant.MsgCodeConstant;
import com.zhuhuibao.common.util.ShiroUtil;
import com.zhuhuibao.exception.AuthException;
import com.zhuhuibao.mybatis.memCenter.entity.*;
import com.zhuhuibao.mybatis.memCenter.service.MemInfoCheckService;
import com.zhuhuibao.mybatis.memCenter.service.MemRealCheckService;
import com.zhuhuibao.mybatis.memCenter.service.MemberService;
import com.zhuhuibao.shiro.realm.ShiroRealm;
import com.zhuhuibao.utils.MsgPropertiesUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 个人资料维护
 * Created by cxx on 2016/6/17 0017.
 */
@RestController
@RequestMapping("/rest/member/mc/individual")
public class IndividualController {

    @Autowired
    private MemberService memberService;

    @Autowired
    MemInfoCheckService memInfoCheckService;

    @Autowired
    MemRealCheckService memRealCheckService;



    @ApiOperation(value = "工作类别", notes = "工作类别", response = Response.class)
    @RequestMapping(value = "sel_workTypeList", method = RequestMethod.GET)
    public Response workTypeList()  {
        Response result = new Response();
        List<WorkType> workType = memberService.findIndividualWorkTypeList();
        result.setData(workType);
        return result;
    }

    @ApiOperation(value = "工作单位类别", notes = "工作单位类别", response = Response.class)
    @RequestMapping(value = "sel_identityList", method = RequestMethod.GET)
    public Response identityList()  {
        Response result = new Response();
        List<Identity> identity = memberService.findIdentityList();
        result.setData(identity);
        return result;
    }

    @ApiOperation(value = "查询个人基本信息", notes = "查询个人基本信息", response = Response.class)
    @RequestMapping(value ={ "base/sel_mem_basic_info","sel_mem_basic_info"}, method = RequestMethod.GET)
    public Response basicInfo() {
        Response result = new Response();
        Long memberId = ShiroUtil.getCreateID();
        if(memberId!=null){
            MemInfoCheck  member = memInfoCheckService.findMemById(String.valueOf(memberId));
            Map map = new HashMap();
            map.put("nickname",member.getNickname());
            map.put("sex",member.getSex());
            map.put("personCompanyType",member.getPersonCompanyType());
            map.put("personCompanyTypeName",member.getPersonCompanyTypeName());
            map.put("workType",member.getWorkType());
            map.put("workTypeName",member.getWorkTypeName());
            map.put("province",member.getProvince());
            map.put("provinceName",member.getProvinceName());
            map.put("headShot",member.getHeadShot());
            map.put("city",member.getCity());
            map.put("cityName",member.getCityName());
            map.put("area",member.getArea());
            map.put("areaName",member.getAreaName());
            map.put("address",member.getAddress());
            map.put("fixedTelephone",member.getFixedTelephone());
            map.put("fixedMobile",member.getFixedMobile());
            map.put("QQ",member.getQQ());
            map.put("status",member.getStatus());
            map.put("reason",member.getReason());
            result.setData(map);
        }else {
            throw new AuthException(MsgCodeConstant.un_login, MsgPropertiesUtils.getValue(String.valueOf(MsgCodeConstant.un_login)));
        }
        return result;
    }


    @ApiOperation(value = "个人基本资料保存", notes = "个人基本资料保存", response = Response.class)
    @RequestMapping(value = "base/upd_mem_basic_info", method = RequestMethod.POST)
    public Response upd_mem_basic_info(@ModelAttribute MemInfoCheck member)  {
        Response result = new Response();
        Long memberId = ShiroUtil.getCreateID();
        ShiroRealm.ShiroUser loginMember = ShiroUtil.getMember();
        if(memberId!=null){
            member.setId(memberId);
            //基本资料待审核
            //基本资料待审核
            if(loginMember.getStatus() != MemberConstant.MemberStatus.WJH.intValue()
                    ||loginMember.getStatus() != MemberConstant.MemberStatus.ZX.intValue()){
                member.setStatus(MemberConstant.MemberStatus.WSZLDSH.intValue());
            }
            memInfoCheckService.update(member);
            Member mem = memberService.findMemById(String.valueOf(memberId));
            Subject currentUser = SecurityUtils.getSubject();
            Session session = currentUser.getSession(false);
            if (session != null) {
                ShiroRealm.ShiroUser principal = (ShiroRealm.ShiroUser) session.getAttribute("member");
                principal.setStatus(Integer.parseInt(mem.getStatus()));
                session.setAttribute("member", principal);
            }
        }else {
            throw new AuthException(MsgCodeConstant.un_login, MsgPropertiesUtils.getValue(String.valueOf(MsgCodeConstant.un_login)));
        }
        return result;
    }


    @ApiOperation(value = "查询个人实名信息", notes = "查询个人实名信息", response = Response.class)
    @RequestMapping(value ={ "sel_mem_realName_info","real/sel_mem_realName_info"}, method = RequestMethod.GET)
    public Response realNameInfo() {
        Response result = new Response();
        Long memberId = ShiroUtil.getCreateID();
        if(memberId!=null){
            MemRealCheck member = memRealCheckService.findMemById(String.valueOf(memberId));
            Map map = new HashMap();
            map.put("personRealName",member.getPersonRealName());
            map.put("personIdentifyCard",member.getPersonIdentifyCard());
            map.put("personIDFrontImgUrl",member.getPersonIDFrontImgUrl());
            map.put("personIDBackImgUrl",member.getPersonIDBackImgUrl());
            map.put("status",member.getStatus());
            map.put("reason",member.getReason());
            result.setData(map);
        }else {
            throw new AuthException(MsgCodeConstant.un_login, MsgPropertiesUtils.getValue(String.valueOf(MsgCodeConstant.un_login)));
        }
        return result;
    }


    @ApiOperation(value = "个人实名认证保存", notes = "个人实名认证保存", response = Response.class)
    @RequestMapping(value = {"upd_mem_realName_info","real/upd_mem_realName_info"}, method = RequestMethod.POST)
    public Response upd_mem_realName_info(@RequestParam String personRealName,
                                          @RequestParam String personIdentifyCard,
                                          @RequestParam String personIDFrontImgUrl,
                                          @RequestParam String personIDBackImgUrl)  {
        Response result = new Response();
        Long memberId = ShiroUtil.getCreateID();
        ShiroRealm.ShiroUser loginMember = ShiroUtil.getMember();
        MemRealCheck member = new MemRealCheck();
        if(memberId!=null){
            member.setId(memberId);
            //实名认证待审核
            if(loginMember.getStatus() != MemberConstant.MemberStatus.WJH.intValue()
                    ||loginMember.getStatus() != MemberConstant.MemberStatus.ZX.intValue()){
                member.setStatus(MemberConstant.MemberStatus.SMRZDSH.intValue());
            }
            member.setPersonRealName(personRealName);
            member.setPersonIdentifyCard(personIdentifyCard);
            member.setPersonIDFrontImgUrl(personIDFrontImgUrl);
            member.setPersonIDBackImgUrl(personIDBackImgUrl);
            memRealCheckService.update(member);
            Member mem = memberService.findMemById(String.valueOf(memberId));
            Subject currentUser = SecurityUtils.getSubject();
            Session session = currentUser.getSession(false);
            if (session != null) {
                ShiroRealm.ShiroUser principal = (ShiroRealm.ShiroUser) session.getAttribute("member");
                principal.setStatus(Integer.parseInt(mem.getStatus()));
                session.setAttribute("member", principal);
            }
        }else {
            throw new AuthException(MsgCodeConstant.un_login, MsgPropertiesUtils.getValue(String.valueOf(MsgCodeConstant.un_login)));
        }
        return result;
    }

    @ApiOperation(value = "个人资质保存", notes = "个人资质保存", response = Response.class)
    @RequestMapping(value ={ "add_certificate","cert/add_certificate"}, method = RequestMethod.POST)
    public Response add_certificate(@ApiParam(value = "证书编号")@RequestParam String certificate_number,
                                    @ApiParam(value = "证书id")@RequestParam String certificate_id,
                                    @RequestParam(required = false) String certificate_grade,
                                    @ApiParam(value = "资质名称")@RequestParam String certificate_name,
                                    @ApiParam(value = "资质证书图片url")@RequestParam String certificate_url)  {
        Response result = new Response();
        CertificateRecord record = new CertificateRecord();
        Long memberId = ShiroUtil.getCreateID();
        if(memberId!=null){
            record.setMem_id(String.valueOf(memberId));
            record.setType("3");
            record.setCertificate_id(certificate_id);
            record.setCertificate_name(certificate_name);
            record.setCertificate_grade(certificate_grade);
            record.setCertificate_number(certificate_number);
            record.setCertificate_url(certificate_url);
            memberService.saveCertificate(record);
        }else {
            throw new AuthException(MsgCodeConstant.un_login, MsgPropertiesUtils.getValue(String.valueOf(MsgCodeConstant.un_login)));
        }
        return result;
    }

    @ApiOperation(value = "个人资质编辑", notes = "个人资质编辑", response = Response.class)
    @RequestMapping(value = {"upd_certificate","cert/upd_certificate"}, method = RequestMethod.POST)
    public Response update_certificate(@RequestParam String id,
                                       @RequestParam String certificate_number,
                                       @RequestParam String certificate_id,
                                       @RequestParam String certificate_name,
                                       @RequestParam(required = false) String certificate_grade,
                                       @RequestParam String certificate_url)  {
        Response result = new Response();
        CertificateRecord record = new CertificateRecord();
        Long memberId = ShiroUtil.getCreateID();
        if(memberId!=null){
            record.setId(id);
            record.setCertificate_id(certificate_id);
            record.setCertificate_name(certificate_name);
            record.setCertificate_number(certificate_number);
            record.setCertificate_url(certificate_url);
            record.setCertificate_grade(certificate_grade);
            record.setStatus("0");
            memberService.updateCertificate(record);
        }else {
            throw new AuthException(MsgCodeConstant.un_login, MsgPropertiesUtils.getValue(String.valueOf(MsgCodeConstant.un_login)));
        }
        return result;
    }

    @ApiOperation(value = "个人资质查询", notes = "个人资质查询", response = Response.class)
    @RequestMapping(value ={ "sel_certificate","cert/sel_certificate"}, method = RequestMethod.GET)
    public Response sel_certificate()  {
        Response result = new Response();
        Long memberId = ShiroUtil.getCreateID();
        if(memberId!=null){
            CertificateRecord record = new CertificateRecord();
            record.setMem_id(String.valueOf(memberId));
            record.setIs_deleted(0);
            record.setType("3");
            List<CertificateRecord> list = memberService.certificateSearch(record);
            result.setData(list);
        }else {
            throw new AuthException(MsgCodeConstant.un_login, MsgPropertiesUtils.getValue(String.valueOf(MsgCodeConstant.un_login)));
        }
        return result;
    }

    @ApiOperation(value = "个人资质类型", notes = "个人资质类型", response = Response.class)
    @RequestMapping(value ={ "sel_certificateList","cert/sel_certificateList"}, method = RequestMethod.GET)
    public Response certificateList(@ApiParam(value = "个人type:3")@RequestParam String type)  {
        Response response = new Response();
        List list = memberService.findCertificateList(type);
        response.setData(list);
        return response;
    }
}
