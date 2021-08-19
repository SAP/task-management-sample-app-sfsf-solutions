sap.ui.define([
	"sap/ui/core/mvc/Controller",
	"sap/m/MessageBox",
	"sap/ui/model/json/JSONModel",
	"ext/samples/taskmanagement/util/Config"
], function (Controller, MessageBox, JSONModel, Config) {
	"use strict";

	return Controller.extend("ext.samples.taskmanagement.controller.UserSelection", {

		onInit: function () {
			this.getView().setModel(new JSONModel(), "userSelection");

			sap.ui.getCore().getEventBus().subscribe("ext.samples.taskmanagement", "requestsLoaded", this.fetchUsers, this);
			sap.ui.getCore().getEventBus().subscribe("ext.samples.taskmanagement", "requestDeleted", this.addUser, this);
		},

		addUser: function(chanel, event, userId) {
			function addUserToModel(data) {
				var userSelectionModel = this.getView().getModel("userSelection");
				var users = userSelectionModel.getProperty("/users");
				var shallowUsersCopy = users.concat([]);
				shallowUsersCopy.push(data);
				userSelectionModel.setProperty("/users", shallowUsersCopy);
			};

			function showGetDetailsError() {
				var messageBundle = this.getView().getModel("i18n").getResourceBundle();
				MessageBox.error(messageBundle.getText("couldNotGetEmployeeDetailsError"));
			}

			jQuery.ajax({
				method: "GET",
				url: Config.serviceUrl + "/users/" + userId,
				context: this,
				beforeSend: this.showBusy("selectionPopover")
			}).done(addUserToModel)
			.fail(showGetDetailsError)
			.always(this.hideBusy("selectionPopover"));
		},
		
		fetchUsers: function (chanel, event, eventData) {
			function fillUsersModel(data) {
				var nonOnboardedUsers = data.users.filter(function (u) {
					return eventData.onboardRequests.every(function (r) {
						return r.toDoEntryV2.subjectId !== u.userId;
					}); 
				});
				data.users = nonOnboardedUsers;
				this.getView().getModel("userSelection").setData(data);
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
			}).done(fillUsersModel)
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
					return u.userId !== data.toDoEntryV2.subjectId;
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
