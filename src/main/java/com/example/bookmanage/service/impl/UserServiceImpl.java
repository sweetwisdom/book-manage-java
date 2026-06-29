package com.example.bookmanage.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.bookmanage.model.dto.UserDTO;
import com.example.bookmanage.model.entity.User;
import com.example.bookmanage.model.mapper.UserMapper;
import com.example.bookmanage.service.UserService;
import com.example.bookmanage.model.vo.UserVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
