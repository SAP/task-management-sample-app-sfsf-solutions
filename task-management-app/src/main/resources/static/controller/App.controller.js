sap.ui.define([
	"sap/ui/core/mvc/Controller",
	"sap/m/MessageBox",
	"sap/ui/model/json/JSONModel",
	"ext/samples/taskmanagement/util/Config",
	"ext/samples/taskmanagement/util/StatusFormatter",
], function (Controller, MessageBox, JSONModel, Config, StatusFormatter) {
	"use strict";

	return Controller.extend("ext.samples.taskmanagement.controller.App", {

		onBeforeRendering: function () { },

		onInit: function () {
			this.fetchCurrentUser();
			
			this.fetchOnboardingRequests();

			this.subscribeForRequestCreated();
		},

		subscribeForRequestCreated: function (params) {
			sap.ui.getCore().getEventBus().subscribe("ext.samples.taskmanagement", "requestCreated", function (chanel, event, eventData) {
				var requestsModel = this.getView().getModel("requests");
				var requests = requestsModel.getProperty("/onboardRequests");
				var shallowRequestsCopy = requests.concat([]);
				shallowRequestsCopy.push(eventData);
				requestsModel.setProperty("/onboardRequests", shallowRequestsCopy);
			}, this);
		},

		fetchCurrentUser: function() {
			function setUserModel(data) {
				this.getView().setModel(new JSONModel(data), "user");
			};

			function showGetCurrentUserError() {
				var messageBundle = this.getView().getModel("i18n").getResourceBundle();
				MessageBox.error(messageBundle.getText("couldNotGetCurrentUserError"));
			}

			jQuery.ajax({
				method: "GET",
				url: Config.serviceUrl + "/users/current",
				context: this,
				beforeSend: this.showBusy("profile")
			}).done(setUserModel)
				.fail(showGetCurrentUserError)
				.always(this.hideBusy("profile"));
		},

		fetchOnboardingRequests: function() {
			function setRequestsModel(data) {
				this.getView().setModel(new JSONModel(data), "requests");
			};

			function notifyRequestsLoaded(data) {
				sap.ui.getCore().getEventBus().publish("ext.samples.taskmanagement", "requestsLoaded", data);
			}

			function showGetRequestsError() {
				var messageBundle = this.getView().getModel("i18n").getResourceBundle();
				MessageBox.error(messageBundle.getText("couldNotGetRequestsError"));
			}

			jQuery.ajax({
				method: "GET",
				url: Config.serviceUrl + "/onboardings",
				context: this,
				beforeSend: this.showBusy("table")
			}).done(setRequestsModel, notifyRequestsLoaded)
			.fail(showGetRequestsError)
			.always(this.hideBusy("table"));
		},

		showBusy: function (controlId) {
			return function (jqXHR, settings) {
				this.byId(controlId).setBusy(true);
			};
		},

		hideBusy: function (controlId) {
			return function () {
				this.byId(controlId).setBusy(false);
			};
		},

		showDetails: function (evt) {
			var listItem = evt.getSource();
			var bindingContext = listItem.getBindingContext("requests");

			var detailsView = this.byId('detailsView');

			detailsView.getModel("details").setData({
				employeeId: bindingContext.getProperty("toDoEntryV2/subjectId"),
			});

			detailsView.byId('detailsPopover').openBy(evt.getSource());
		},

		deleteRequest: function (evt) {
			var listItem = evt.getSource();
			var bindingContext = listItem.getBindingContext("requests");
			var requestId = bindingContext.getProperty("toDoEntryV2/toDoEntryId");
			var requestName = bindingContext.getProperty("toDoEntryV2/toDoEntryName");
			var userId = bindingContext.getProperty("toDoEntryV2/subjectId");

			var resourceBundle = this.getView().getModel("i18n").getResourceBundle();
			var confirmationMessage = resourceBundle.getText("deleteRequestConfirmation", [requestName]);
			MessageBox.confirm(confirmationMessage, this.submitRequestDeletion.bind(this, requestId, userId));
		},

		submitRequestDeletion: function (requestId, userId, oAction) {
			if(oAction !== MessageBox.Action.OK) {
				return;
			}

			function showDeleteRequestError() {
				var resourceBundle = this.getView().getModel("i18n").getResourceBundle();
				MessageBox.error(resourceBundle.getText("couldNotDeleteRequestError"));
			}

			function handleDeleteRequestSuccess(data, textStatus, jqXHR) {
				if (jqXHR.status !== 204) {
					showDeleteRequestError.bind(this)();
					return;
				}

				var requestsModel = this.getView().getModel("requests");
				var requests = requestsModel.getProperty("/onboardRequests");
				var newRequests = requests.filter(function (r) {
					return r.toDoEntryV2.toDoEntryId !== requestId;
				});
				requestsModel.setProperty("/onboardRequests", newRequests);

				sap.ui.getCore().getEventBus().publish("ext.samples.taskmanagement", "requestDeleted", userId);
			}

			jQuery.ajax({
				method: "DELETE",
				url: Config.serviceUrl + "/onboardings/" + requestId,
				context: this,
				beforeSend: this.showBusy("table")
			}).done(handleDeleteRequestSuccess)
				.fail(showDeleteRequestError)
				.always(this.hideBusy("table"));
		},

		completeRequest: function (evt) {
			var listItem = evt.getSource();
			var bindingContext = listItem.getBindingContext("requests");
			var requestId = bindingContext.getProperty("toDoEntryV2/toDoEntryId");
			var requestName = bindingContext.getProperty("toDoEntryV2/toDoEntryName");

			var resourceBundle = this.getView().getModel("i18n").getResourceBundle();
			var confirmationMessage = resourceBundle.getText("completeRequestConfirmation", [requestName]);
			MessageBox.confirm(confirmationMessage, this.submitRequestCompletion.bind(this, requestId));
		},

		submitRequestCompletion: function (requestId, oAction) {
			if(oAction !== MessageBox.Action.OK) {
				return;
			}

			function showCompleteRequestError() {
				var resourceBundle = this.getView().getModel("i18n").getResourceBundle();
				MessageBox.error(resourceBundle.getText("couldNotCompleteRequestError"));
			}

			function handleCompleteRequestSuccess(data, textStatus, jqXHR) {
				if (jqXHR.status !== 204) {
					showCompleteRequestError.bind(this)();
					return;
				}

				var requestsModel = this.getView().getModel("requests");
				var requests = requestsModel.getProperty("/onboardRequests");
				requests.forEach(function (r, i) {
					if(r.toDoEntryV2.toDoEntryId === requestId) {
						requestsModel.setProperty("/onboardRequests/" + i + "/toDoEntryV2/status", StatusFormatter.getCompletedStatus());
					}
				});
			}

			jQuery.ajax({
				method: "PATCH",
				url: Config.serviceUrl + "/onboardings/" + requestId + "/complete",
				context: this,
				beforeSend: this.showBusy("table")
			}).done(handleCompleteRequestSuccess)
				.fail(showCompleteRequestError)
				.always(this.hideBusy("table"));
		},

		newRequest: function (evt) {
			var newRequestButton = evt.getSource();
			var selectionView = this.byId("selectionView");
			selectionView.byId('selectionPopover').openBy(newRequestButton);
		},

		formatStatusText: function (status) {
			return StatusFormatter.formatText(status);
		},

		formatStatusState: function (status) {
			return StatusFormatter.formatState(status);
		},

		formatStatusVisibility: function (status) {
			return StatusFormatter.isCompleted(status);
		},
		
		formatCompleteActionVisibility: function(status) {
			return !StatusFormatter.isCompleted(status);
		}

	});

});
