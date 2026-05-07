package com.attendance.service;

import com.attendance.dto.UserDto;
import com.attendance.entity.Role;
import com.attendance.entity.User;
import com.attendance.exception.ResourceNotFoundException;
import com.attendance.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public List<UserDto> getAllStudents() {
        return userRepository.findByRole(Role.STUDENT)
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    public List<UserDto> getAllFaculty() {
        return userRepository.findByRole(Role.FACULTY)
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    public UserDto getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
        return toDto(user);
    }

    private UserDto toDto(User u) {
        return UserDto.builder()
                .id(u.getId())
                .username(u.getUsername())
                .fullName(u.getFullName())
                .email(u.getEmail())
                .role(u.getRole().name())
                .build();
    }
}
