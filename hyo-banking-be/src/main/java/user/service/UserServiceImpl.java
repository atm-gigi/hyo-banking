package user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import user.domain.User;
import user.dto.UserJoinRequestDTO;
import user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public void createUser(UserJoinRequestDTO dto) {
        User user = User.builder()
                .loginId(dto.getLoginId())
                .name(dto.getName())
                .password(dto.getPassword())
                .phone(dto.getPhone())
                .build();

        userRepository.save(user);
    }
}
