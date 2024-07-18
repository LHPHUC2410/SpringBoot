package com.SB1.service;

import java.util.HashSet;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.SB1.dto.request.UserCreationRequest;
import com.SB1.dto.request.UserUpdateRequest;
import com.SB1.dto.response.UserResponse;
import com.SB1.entity.User;
import com.SB1.enums.Role;
import com.SB1.exception.AppException;
import com.SB1.exception.ErrorCode;
import com.SB1.mapper.UserMapper;
import com.SB1.repository.UserRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
//thay autowired
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {
	//@Autowired
	UserRepository userRepository;
	//@Autowired
	UserMapper userMapper;
	public User createUser(UserCreationRequest request)
	{
		if(userRepository.existsByUsername(request.getUsername())) 
		{
			throw new AppException(ErrorCode.USER_EXISTED);
		}
		
		User user = userMapper.toUser(request);
		
		PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
		user.setPassword(passwordEncoder.encode(request.getPassword()));
		
		HashSet<String> roles = new HashSet<String>();
		roles.add(Role.USER.name());
		user.setRoles(roles);
		
		//user.setUsername(request.getUsername());
		//user.setFirstName(request.getFirstName());
		//user.setLastName(request.getLastName());
		//user.setPassword(request.getPassword());
		
		return userRepository.save(user);
	}
	
	@PreAuthorize("hasRole('ADMIN')")   //quyen ADMIN moi duoc vao  // kiem tra co la ADMIN k? -> vao ham
	public List<UserResponse> getUsers() {
		return userRepository.findAll().stream().map(userMapper::toUserResponse).toList();
	}
	
	@PostAuthorize("returnObject.username==authentication.name")   // method thuc hien xong moi kiem tra
	public UserResponse getUser(String id) 
	{
		return userMapper.toUserResponse(userRepository.findById(id)
				.orElseThrow(()-> new RuntimeException("User not found")));
	}
	
	public UserResponse updateUser(String id, UserUpdateRequest request) {
		//User user = getUser(id);
		User user = userRepository.findById(id)
				.orElseThrow(()-> new RuntimeException("User not found"));
		
		userMapper.updateUser(user, request);
		//user.setPassword(request.getPassword());
		//user.setFirstName(request.getFirstName());
		//user.setLastName(request.getLastName());
		//return userRepository.save(user);
		
		return userMapper.toUserResponse(userRepository.save(user));
	}
	
	public void deleteUser(String userId) 
	{
		userRepository.deleteById(userId);
	}
	
	public UserResponse getInforUser(String username)
	{
		User user = userRepository.findByUsername(username).orElseThrow(
				() -> new AppException(ErrorCode.USER_NOT_EXISTED));
		return userMapper.toUserResponse(user);
	}
}
