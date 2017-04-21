package com.movision.mybatis.pointRecord.service;

import com.movision.mybatis.pointRecord.entity.PointRecord;
import com.movision.mybatis.pointRecord.mapper.PointRecordMapper;
import com.movision.utils.pagination.model.Paging;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * @Author zhuangyuhao
 * @Date 2017/2/28 19:59
 */
@Service
@Transactional
public class PointRecordService {

    private static Logger log = LoggerFactory.getLogger(PointRecordService.class);

    @Autowired
    private PointRecordMapper pointRecordMapper;

    public int addPointRecord(PointRecord pointRecord) {
        try {
            log.info("增加积分记录");
            return pointRecordMapper.insertSelective(pointRecord);
        } catch (Exception e) {
            log.error("增加积分记录失败", e);
            throw e;
        }
    }

    public List<PointRecord> queryAllMyPointRecord(int userid) {
        try {
            log.info("查询我的所有积分记录");
            return pointRecordMapper.queryMyAllTypePoint(userid);
        } catch (Exception e) {
            log.error("查询我的所有积分记录失败", e);
            throw e;
        }
    }

    public List<PointRecord> queryMyTodayPoint(int userid) {
        try {
            log.info("查询今日的我的积分记录");
            return pointRecordMapper.queryMyTodayPoint(userid);
        } catch (Exception e) {
            log.error("查询今日的我的积分记录失败", e);
            throw e;
        }
    }


    public int queryMyTodayPointSum(int userid) {
        try {
            log.info("查询今日的我的积分记录");
            return pointRecordMapper.queryMyTodayPointSum(userid);
        } catch (Exception e) {
            log.error("查询今日的我的积分记录失败", e);
            throw e;
        }
    }

    public int queryIsSignToday(int userid) {
        try {
            log.info("查询今天是否有签到记录");
            return pointRecordMapper.queryIsSignToday(userid);
        } catch (Exception e) {
            log.error("查询今天是否有签到记录失败", e);
            throw e;
        }
    }

    public List<Map> findAllMyPointList(Paging<Map> pointRecordPaging, Map map) {
        try {
            log.info("查询我的积分明细");
            return pointRecordMapper.findAllMyPointRecord(pointRecordPaging.getRowBounds(), map);
        } catch (Exception e) {
            log.error("查询我的积分明细失败", e);
            throw e;
        }
    }

    public void inserRecord(Map<String, Object> parammap) {
        try {
            log.info("插入积分消费流水记录");
            pointRecordMapper.inserRecord(parammap);
        } catch (Exception e) {
            log.error("插入积分消费流水记录失败");
            throw e;
        }
    }

    public PointRecord selectFinishPersonDataPointRecord(Integer id) {
        try {
            log.info("查询当前用户的完善个人资料的积分记录");
            return pointRecordMapper.selectFinishPersonDataPointRecord(id);
        } catch (Exception e) {
            log.error("查询当前用户的完善个人资料的积分记录失败", e);
            throw e;
        }
    }


}
