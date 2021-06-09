package com.sap.core.extensions.employeeonboarding.services;

import java.util.List;

import com.sap.core.extensions.employeeonboarding.dto.PhotoDTO;
import com.sap.core.extensions.employeeonboarding.dto.UserDTO;

public interface UserManager {

	UserDTO getUserProfile();

	List<UserDTO> getUsersProfiles();

	UserDTO getUserProfile(String userId);

	PhotoDTO getUserPhoto(String userId);

}
