package com.icuxika.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.icuxika.framework.object.modules.user.entity.Permission;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PermissionMapper extends BaseMapper<Permission> {
}
