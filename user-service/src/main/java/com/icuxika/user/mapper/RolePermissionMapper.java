package com.icuxika.user.mapper;

import com.icuxika.framework.mybatis.mapper.MyBaseMapper;
import com.icuxika.framework.object.modules.user.entity.RolePermission;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface RolePermissionMapper extends MyBaseMapper<RolePermission> {
}
