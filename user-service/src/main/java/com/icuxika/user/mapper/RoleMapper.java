package com.icuxika.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.icuxika.framework.object.modules.user.entity.Role;
import com.icuxika.framework.object.modules.user.vo.RoleInfoVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface RoleMapper extends BaseMapper<Role> {

    List<RoleInfoVO> selectRoleJoinedWithPermission(Long id);
}
