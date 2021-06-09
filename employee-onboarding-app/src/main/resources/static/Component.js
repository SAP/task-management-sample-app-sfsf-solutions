sap.ui.define([
	"sap/ui/core/UIComponent",
	"sap/ui/model/resource/ResourceModel"
], function(UIComponent, ResourceModel) {
	"use strict";

	return UIComponent.extend("ext.samples.employeeonboarding.Component", {

		metadata : {
			manifest : "json"
		},

		init: function () {
			UIComponent.prototype.init.apply(this, arguments);

			this.setI18nModel();
			this.setDocumentTitle();
		},

		setI18nModel: function () {
			let i18nModel = new ResourceModel({ bundleUrl: "i18n/message-bundle.properties" })
			this.setModel(i18nModel, "i18n");
		},

		setDocumentTitle: function () {
			let messages = this.getModel("i18n").getResourceBundle();
			document.title = messages.getText("appTitle");
		}

	});

});
