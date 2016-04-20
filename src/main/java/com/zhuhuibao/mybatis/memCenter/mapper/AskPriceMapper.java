package com.zhuhuibao.mybatis.memCenter.mapper;

import com.zhuhuibao.common.AskPriceBean;
import com.zhuhuibao.common.AskPriceResultBean;
import com.zhuhuibao.common.AskPriceSearchBean;
import com.zhuhuibao.mybatis.memCenter.entity.AskPrice;
import org.apache.ibatis.session.RowBounds;

import java.util.List;

public interface AskPriceMapper {

    int saveAskPrice(AskPrice record);

    AskPriceBean queryAskPriceByID(String id);

    List<AskPriceResultBean> findAll(AskPriceSearchBean askPriceSearch);

    List<AskPriceResultBean> findAllByPager1(RowBounds rowBounds, AskPriceSearchBean askPriceSearch);

    List<AskPrice> find();

}