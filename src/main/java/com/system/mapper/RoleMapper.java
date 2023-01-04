package com.system.mapper;

import com.system.entity.Role;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author Byterain
 * @since 2022-11-29
 */
@Repository
public interface RoleMapper extends BaseMapper<Role> {
    public List<Role> listByUserId(Long userid);

}
