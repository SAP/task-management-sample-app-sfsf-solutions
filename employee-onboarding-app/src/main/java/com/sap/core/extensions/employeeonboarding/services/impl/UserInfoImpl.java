package com.sap.core.extensions.employeeonboarding.services.impl;

import org.springframework.stereotype.Component;

import com.sap.cloud.security.xsuaa.token.SpringSecurityContext;
import com.sap.core.extensions.employeeonboarding.services.UserInfo;

@Component
class UserInfoImpl implements UserInfo {

	public String getName() {
		return SpringSecurityContext.getToken().getLogonName();
	}
}
