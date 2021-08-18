package com.sap.core.extensions.taskmanagement.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sap.core.extensions.taskmanagement.dto.PhotoDTO;
import com.sap.core.extensions.taskmanagement.dto.UserDTO;
import com.sap.core.extensions.taskmanagement.dto.UsersDTO;
import com.sap.core.extensions.taskmanagement.services.UserManager;

@RestController
@RequestMapping("/v1/users")
public class UserController {

	private final UserManager userManager;

	@Autowired
	public UserController(UserManager userManager) {
		this.userManager = userManager;
	}

	@GetMapping(value = "/current")
	public ResponseEntity<UserDTO> fetchCurrentUserProfile() {
		UserDTO currentUser = userManager.getUserProfile();

		return ResponseEntity.ok(currentUser);
	}

	@GetMapping(value = "/{userId}")
	public ResponseEntity<UserDTO> fetchUserProfile(@PathVariable(name = "userId") String userId) {
		UserDTO user = userManager.getUserProfile(userId);

		return ResponseEntity.ok(user);
	}

	@GetMapping(value = "/{userId}/photo")
	public ResponseEntity<PhotoDTO> fetchUserPhoto(@PathVariable(name = "userId") String userId) {
		PhotoDTO photo = userManager.getUserPhoto(userId);

		return ResponseEntity.ok(photo);
	}

	@GetMapping
	public ResponseEntity<UsersDTO> fetchAllUsers() {
		List<UserDTO> users = userManager.getUsersProfiles();

		return ResponseEntity.ok(new UsersDTO(users));
	}

}
