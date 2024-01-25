package com.icuxika.framework.mybatis.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MyBaseMapper<T> extends BaseMapper<T> {

    int insertBatchSomeColumn(@Param("list") List<T> batchList);
}
