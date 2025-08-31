package user.service;

import user.dto.UserJoinRequestDTO;

public interface UserService {

    void createUser(UserJoinRequestDTO dto);
}
