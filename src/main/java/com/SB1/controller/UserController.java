package com.SB1.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.SB1.dto.request.ApiResponse;
import com.SB1.dto.request.UserCreationRequest;
import com.SB1.dto.request.UserUpdateRequest;
import com.SB1.dto.response.UserResponse;
import com.SB1.entity.User;
import com.SB1.repository.UserRepository;
import com.SB1.service.UserService;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
	@Autowired
	private UserService userService;
	
	@PostMapping
	public ApiResponse<User> createUser(@RequestBody @Valid UserCreationRequest request) {
		ApiResponse<User> apiResponse = new ApiResponse<User>();
		//apiResponse.setMessage("success");
		apiResponse.setResult(userService.createUser(request));
		return apiResponse;
	}
	
	@GetMapping("/my-info")
	public ApiResponse<UserResponse> getInfor() {
		var authentication = SecurityContextHolder.getContext().getAuthentication();  //lay thong tin nguoi dang dang nhap
		//log.warn(authentication.getName());
		var username = authentication.getName();
		UserResponse userResponse = userService.getInforUser(username);
		ApiResponse<UserResponse> apiResponse = new ApiResponse<UserResponse>();
		apiResponse.setResult(userResponse);
		return apiResponse;
	}
	
	@GetMapping
	public ApiResponse<List<UserResponse>> getUsers() {
		var authentication = SecurityContextHolder.getContext().getAuthentication();   //lay thong tin nguoi dang dang nhap
		
		ApiResponse<List<UserResponse>> apiResponse = new ApiResponse<List<UserResponse>>();
		apiResponse.setResult(userService.getUsers());
		return apiResponse;
	}
	
	@GetMapping("/{userId}")
	public UserResponse getUser(@PathVariable("userId") String userId )
	{
		return userService.getUser(userId);
	}
	
	@PutMapping("/{userid}")
	public UserResponse update(@PathVariable("userid") String userid, @RequestBody UserUpdateRequest request)
	{
		return userService.updateUser(userid, request);
	}
	
	@DeleteMapping("{userId}")
	public String deleteUser(@PathVariable("userId") String userId) {
		userService.deleteUser(userId);
		return "User has been deleted";
	}
}
