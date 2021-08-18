package com.sap.core.extensions.taskmanagement.services;

import java.util.List;

import com.sap.core.extensions.taskmanagement.dto.PhotoDTO;
import com.sap.core.extensions.taskmanagement.dto.UserDTO;

public interface UserManager {

	UserDTO getUserProfile();

	List<UserDTO> getUsersProfiles();

	UserDTO getUserProfile(String userId);

	PhotoDTO getUserPhoto(String userId);

}
