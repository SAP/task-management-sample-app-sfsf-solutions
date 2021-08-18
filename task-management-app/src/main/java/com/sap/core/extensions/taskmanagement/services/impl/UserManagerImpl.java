package com.sap.core.extensions.taskmanagement.services.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.sap.cloud.sdk.cloudplatform.connectivity.HttpDestination;
import com.sap.cloud.sdk.datamodel.odata.client.exception.ODataServiceErrorException;
import com.sap.core.extensions.taskmanagement.vdm.namespaces.photo.Photo;
import com.sap.core.extensions.taskmanagement.vdm.namespaces.user.User;
import com.sap.core.extensions.taskmanagement.vdm.services.PhotoService;
import com.sap.core.extensions.taskmanagement.vdm.services.UserService;
import com.sap.core.extensions.taskmanagement.destination.DestinationProvider;
import com.sap.core.extensions.taskmanagement.dto.PhotoDTO;
import com.sap.core.extensions.taskmanagement.dto.UserDTO;
import com.sap.core.extensions.taskmanagement.services.UserInfo;
import com.sap.core.extensions.taskmanagement.services.UserManager;

@Component
class UserManagerImpl implements UserManager {

	private static final int LIVE_PROFILE_PHOTO_TYPE = 1;

	private final DestinationProvider destinationProvider;
	private final PhotoService photoService;
	private final UserService userService;
	private final UserInfo userInfo;

	@Autowired
	UserManagerImpl(DestinationProvider destinationProvider, PhotoService photoService, UserService userService,
			UserInfo userInfo) {
		this.destinationProvider = destinationProvider;
		this.photoService = photoService;
		this.userService = userService;
		this.userInfo = userInfo;
	}

	@Override
	public UserDTO getUserProfile() {
		return getUserProfile(null);
	}

	@Override
	public UserDTO getUserProfile(String userId) {
		if (userId == null) {
			userId = userInfo.getName();
		}

		User user = new User();
		Photo photo = new Photo();
		try {
			user = getUser(userId);
			photo = getPhoto(userId);
		} catch (ODataServiceErrorException e) {
			if (e.getHttpCode() != HttpStatus.NOT_FOUND.value()) {
				throw e;
			}
		}

		return toUserDTO(user, photo);
	}

	@Override
	public List<UserDTO> getUsersProfiles() {
		HttpDestination destination = destinationProvider.getDestination();
		return userService.getAllUser() //
				.select(User.DEFAULT_FULL_NAME) //
				.select(User.EMAIL) //
				.select(User.USER_ID) //
				.select(User.TITLE) //
				.select(User.DEPARTMENT) //
				.executeRequest(destination) //
				.stream() //
				.map(this::toDTO) //
				.collect(Collectors.toList()); //
	}

	@Override
	public PhotoDTO getUserPhoto(String userId) {
		Photo photo = getPhoto(userId);
		return toDTO(photo);
	}

	private User getUser(String userId) {
		HttpDestination destination = destinationProvider.getDestination();

		return userService.getUserByKey(userId).executeRequest(destination);
	}

	private Photo getPhoto(String userId) {
		HttpDestination destination = destinationProvider.getDestination();
		return photoService.getPhotoByKey(LIVE_PROFILE_PHOTO_TYPE, userId).executeRequest(destination);
	}

	private PhotoDTO toDTO(Photo photo) {
		return new PhotoDTO(photo.getPhoto());
	}

	private UserDTO toDTO(User user) {
		return toUserDTO(user, null);
	}

	private UserDTO toUserDTO(User user, Photo photo) {
		return new UserDTO(user.getUserId(), user.getTitle(), user.getEmail(), user.getDepartment(),
				user.getDefaultFullName(), fromDTO(photo));
	}

	private PhotoDTO fromDTO(Photo photo) {
		return new PhotoDTO(photo == null ? null : photo.getPhoto());
	}

}
