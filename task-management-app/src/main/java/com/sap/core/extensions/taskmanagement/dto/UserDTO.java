package com.sap.core.extensions.taskmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(doNotUseGetters = true, callSuper = true)
public class UserDTO {

	private String userId;
	private String title;
	private String email;
	private String department;
	private String defaultFullName;
	private PhotoDTO photo;

}
