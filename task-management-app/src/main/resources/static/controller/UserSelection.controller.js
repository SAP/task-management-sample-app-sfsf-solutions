sap.ui.define([
	"sap/ui/core/mvc/Controller",
	"sap/m/MessageBox",
	"sap/ui/model/json/JSONModel",
	"ext/samples/taskmanagement/util/Config"
], function (Controller, MessageBox, JSONModel, Config) {
	"use strict";

	return Controller.extend("ext.samples.taskmanagement.controller.UserSelection", {

		onInit: function () {
			sap.ui.getCore().getEventBus().subscribe("ext.samples.taskmanagement", "requestsLoaded", this.fetchUsers, this);
			sap.ui.getCore().getEventBus().subscribe("ext.samples.taskmanagement", "requestDeleted", this.addUser, this);
		},

		addUser: function(chanel, event, eventData) {
			var users = this.getView().getModel("userSelection").getProperty("/users");
			var shallowUsersCopy = users.concat([]);
			shallowUsersCopy.push(eventData);
			this.getView().getModel("userSelection").setProperty("/users", shallowUsersCopy);
		},
		
		fetchUsers: function (chanel, event, eventData) {
			function setUsersModel(data) {
				var nonOnboardedUsers = data.users.filter(function (u) {
					return eventData.onboardRequests.every(function (r) {
						return r.user.userId !== u.userId;
					}); 
				});
				data.users = nonOnboardedUsers;
				this.getView().setModel(new JSONModel(data), "userSelection");
			};

			function showUserFetchError() {
				var messageBundle = this.getView().getModel("i18n").getResourceBundle();
				MessageBox.error(messageBundle.getText("couldNotFetchUsersError"));
			};

			jQuery.ajax({
				method: "GET",
				url: Config.serviceUrl + "/users",
				context: this,
				beforeSend: this.showBusy("selectionPopover")
			}).done(setUsersModel)
				.fail(showUserFetchError)
				.always(this.hideBusy("selectionPopover"));	
		},

		createRequest: function (evt) {
			var listItem = evt.getSource();
			var bindingContext = listItem.getBindingContext("userSelection");
			var user = bindingContext.getObject("");

			function showCreateRequestError() {
				var messageBundle = this.getView().getModel("i18n").getResourceBundle();
				MessageBox.error(messageBundle.getText("couldNotCreateRequestError"));
			};

			function handleCreateRequestSuccess(data, textStatus, jqXHR) {
				if (jqXHR.status !== 201) {
					showCreateRequestError.bind(this)();
					return;
				}

				var usersModel = this.getView().getModel("userSelection");
				var users = usersModel.getProperty("/users");
				var filteredUsers = users.filter(function (u) {
					return u.userId !== data.user.userId;
				});
				usersModel.setProperty("/users", filteredUsers);

				sap.ui.getCore().getEventBus().publish("ext.samples.taskmanagement", "requestCreated", data);
			}

			jQuery.ajax({
				method: "POST",
				url: Config.serviceUrl + "/onboardings",
				contentType: Config.jsonMediaType,
				context: this,
				beforeSend: this.showBusy("selectionPopover"),
				data: JSON.stringify({
					"user": user
				})
			}).done(handleCreateRequestSuccess)
				.fail(showCreateRequestError)
				.always(this.hideBusy("selectionPopover"));;
		},

		showBusy: function (controlId) {
			return function () {
				this.byId(controlId).setBusy(true);
			};
		},

		hideBusy: function (controlId) {
			return function () {
				this.byId(controlId).setBusy(false);
			};
		}

	});
});
