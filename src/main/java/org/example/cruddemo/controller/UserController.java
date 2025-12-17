package org.example.cruddemo.controller;

import org.example.cruddemo.dto.UserDTO;
import org.example.cruddemo.service.UserService;
import org.example.cruddemo.vo.UserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * User Controller
 * 接收 HTTP 请求，调用 Service 层
 */
@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 新增用户
     * POST /users
     * @param userDTO 请求体参数
     * @return 创建成功的用户
     */
    @PostMapping
    public ResponseEntity<UserVO> addUser(@RequestBody UserDTO userDTO) {
        // TODO: 调用 userService.createUser(userDTO)
        // TODO: 返回 ResponseEntity.ok(result)
        throw new UnsupportedOperationException("Method not implemented yet");
    }

    /**
     * 删除用户
     * DELETE /users/{id}
     * @param id 用户ID
     * @return 无内容
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        // TODO: 调用 userService.deleteUser(id)
        // TODO: 返回 ResponseEntity.noContent().build()
        throw new UnsupportedOperationException("Method not implemented yet");
    }

    /**
     * 修改用户
     * PUT /users
     * @param userDTO 请求体参数 (需包含ID)
     * @return 修改后的用户
     */
    @PutMapping
    public ResponseEntity<UserVO> updateUser(@RequestBody UserDTO userDTO) {
        // TODO: 调用 userService.updateUser(userDTO)
        // TODO: 返回 ResponseEntity.ok(result)
        throw new UnsupportedOperationException("Method not implemented yet");
    }

    /**
     * 查询用户详情
     * GET /users/{id}
     * @param id 用户ID
     * @return 用户详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserVO> getUser(@PathVariable Long id) {
        // TODO: 调用 userService.getUser(id)
        // TODO: 返回 ResponseEntity.ok(result)
        throw new UnsupportedOperationException("Method not implemented yet");
    }

    /**
     * 分页查询用户列表
     * GET /users?page=1&size=10
     * @param page 页码，默认为1
     * @param size 每页数量，默认为10
     * @return 用户列表
     */
    @GetMapping
    public ResponseEntity<List<UserVO>> listUsers(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        // TODO: 调用 userService.getUserList(page, size)
        // TODO: 返回 ResponseEntity.ok(result)
        throw new UnsupportedOperationException("Method not implemented yet");
    }
}
