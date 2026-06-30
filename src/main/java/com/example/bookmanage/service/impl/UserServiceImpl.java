package com.example.bookmanage.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.bookmanage.common.response.PageResponse;
import com.example.bookmanage.model.dto.UserDTO;
import com.example.bookmanage.model.dto.UserQueryDTO;
import com.example.bookmanage.model.entity.User;
import com.example.bookmanage.model.mapper.UserMapper;
import com.example.bookmanage.service.UserService;
import com.example.bookmanage.model.vo.UserVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户Service实现类 - 业务逻辑实现
 */
@Service
public class UserServiceImpl  extends ServiceImpl<UserMapper,User> implements  UserService {



    @Override
    public List<UserVO> getAllUsers() {
        List<User> users = baseMapper.selectList(null);
        return users.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public PageResponse<UserVO> getUserPage(UserQueryDTO queryDTO) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(queryDTO.getName())) {
            queryWrapper.like(User::getName, queryDTO.getName());
        }
        queryWrapper.orderByDesc(User::getId);

        Page<User> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        Page<User> userPage = baseMapper.selectPage(page, queryWrapper);
        List<UserVO> records = userPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        return new PageResponse<>(
                records,
                userPage.getTotal(),
                userPage.getCurrent(),
                userPage.getSize(),
                userPage.getPages()
        );
    }

    @Override
    public UserVO getUserById(Long id) {
        User user = baseMapper.selectById(id);
        if (user == null) {
            return null;
        }
        return convertToVO(user);
    }

    @Override
    @Transactional
    public UserVO createUser(UserDTO userDTO) {
        User user = new User();
        // user.setName(userDTO.getName());
        // user.setAge(userDTO.getAge());
        BeanUtils.copyProperties(userDTO,user);
        this.save(user);
        return convertToVO(user);
    }

    /**
     * Entity -> VO 转换
     */
    private UserVO convertToVO(User user) {
        UserVO vo = new UserVO();
        vo.setId(user.getId());
        vo.setName(user.getName());
        vo.setAge(user.getAge());
        return vo;
    }
}
