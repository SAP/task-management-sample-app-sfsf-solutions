package com.sap.core.extensions.employeeonboarding.rest;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sap.core.extensions.employeeonboarding.dto.PhotoDTO;
import com.sap.core.extensions.employeeonboarding.dto.UserDTO;
import com.sap.core.extensions.employeeonboarding.dto.UsersDTO;
import com.sap.core.extensions.employeeonboarding.services.UserManager;

@RestController
@RequestMapping("/v1/users")
public class UserController {

	private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

	private final UserManager userManager;

	@Autowired
	public UserController(UserManager userManager) {
		this.userManager = userManager;
	}

	@GetMapping(value = "/current")
	public ResponseEntity<UserDTO> fetchCurrentUserProfile() {
		UserDTO currentUser = userManager.getUserProfile();

		LOGGER.debug("User: {}", currentUser);
		return ResponseEntity.ok(currentUser);
	}

	@GetMapping(value = "/{userId}")
	public ResponseEntity<UserDTO> fetchUserProfile(@PathVariable(name = "userId") String userId) {
		UserDTO user = userManager.getUserProfile(userId);

		LOGGER.debug("User: {}", user);
		return ResponseEntity.ok(user);
	}

	@GetMapping(value = "/{userId}/photo")
	public ResponseEntity<PhotoDTO> fetchUserPhoto(@PathVariable(name = "userId") String userId) {
		PhotoDTO photo = userManager.getUserPhoto(userId);

		LOGGER.debug("Photo for user {} retrieved successfully: {}", userId);
		return ResponseEntity.ok(photo);
	}

	@GetMapping
	public ResponseEntity<UsersDTO> fetchAllUsers() {
		List<UserDTO> users = userManager.getUsersProfiles();

		LOGGER.debug("Fetched users: {}", users);
		return ResponseEntity.ok(new UsersDTO(users));
	}

}
