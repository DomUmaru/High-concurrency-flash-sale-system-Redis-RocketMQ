package org.example.cruddemo.service.impl;

import org.example.cruddemo.dto.UserDTO;
import org.example.cruddemo.mapper.UserMapper;
import org.example.cruddemo.service.UserService;
import org.example.cruddemo.vo.UserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * User Service 实现类
 * 处理具体的业务逻辑，如参数校验、DTO/VO转换、调用Mapper
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public UserVO createUser(UserDTO userDTO) {
        // TODO: 1. 将 UserDTO 转换为 User Entity
        // TODO: 2. 补全 Entity 的其他字段 (如 createTime)
        // TODO: 3. 调用 userMapper.insert(user)
        // TODO: 4. 将插入后的 User Entity 转换为 UserVO 返回
        throw new UnsupportedOperationException("Method not implemented yet");
    }

    @Override
    public void deleteUser(Long id) {
        // TODO: 调用 userMapper.deleteById(id)
        throw new UnsupportedOperationException("Method not implemented yet");
    }

    @Override
    public UserVO updateUser(UserDTO userDTO) {
        // TODO: 1. 检查用户是否存在 (可选)
        // TODO: 2. 将 UserDTO 转换为 User Entity
        // TODO: 3. 调用 userMapper.update(user)
        // TODO: 4. 返回更新后的数据
        throw new UnsupportedOperationException("Method not implemented yet");
    }

    @Override
    public UserVO getUser(Long id) {
        // TODO: 1. 调用 userMapper.selectById(id)
        // TODO: 2. 如果为空，抛出异常或返回 null
        // TODO: 3. 将 User Entity 转换为 UserVO (注意隐藏密码)
        // TODO: 4. 返回 UserVO
        throw new UnsupportedOperationException("Method not implemented yet");
    }

    @Override
    public List<UserVO> getUserList(int page, int size) {
        // TODO: 1. 计算 offset = (page - 1) * size
        // TODO: 2. 调用 userMapper.selectList(offset, size)
        // TODO: 3. 将 List<User> 转换为 List<UserVO>
        // TODO: 4. 返回 List<UserVO>
        throw new UnsupportedOperationException("Method not implemented yet");
    }
}
