package com.SB1.dto.request;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserCreationRequest {
	private String username;
	
	@Size(min = 8, message = "PASSWORD_INVALID")
	private String password;
	private String firstName;
	private String lastName;
	
}
