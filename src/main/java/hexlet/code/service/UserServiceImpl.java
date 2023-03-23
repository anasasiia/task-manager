package hexlet.code.service;

import hexlet.code.dto.UserDto;
import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public final class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    @Override
    public User createNewUser(final UserDto userDto) {
        final User user = new User();
        user.setEmail(userDto.getEmail());
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        return userRepository.save(user);
    }
    @Override
    public User updateUser(long id, UserDto userDto) {
        final User userUpdated = userRepository.findById(id).get();
        userUpdated.setFirstName(userDto.getFirstName());
        userUpdated.setLastName(userDto.getLastName());
        userUpdated.setEmail(userDto.getEmail());
        userUpdated.setPassword(passwordEncoder.encode(userDto.getPassword()));
        return userRepository.save(userUpdated);
    }
}
