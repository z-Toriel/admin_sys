package com.system.service.impl;

import com.system.entity.Poster;
import com.system.mapper.PosterMapper;
import com.system.service.PosterService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Byterain
 * @since 2023-01-15
 */
@Service
public class PosterServiceImpl extends ServiceImpl<PosterMapper, Poster> implements PosterService {

}
