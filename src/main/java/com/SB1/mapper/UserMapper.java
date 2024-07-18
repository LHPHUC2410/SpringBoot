package com.SB1.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import com.SB1.dto.request.UserCreationRequest;
import com.SB1.dto.request.UserUpdateRequest;
import com.SB1.dto.response.UserResponse;
import com.SB1.entity.User;

@Mapper(componentModel = "Spring")
public interface UserMapper {
	User toUser(UserCreationRequest request);
	UserResponse toUserResponse(User request);
	void updateUser(@MappingTarget User user, UserUpdateRequest request);
}
