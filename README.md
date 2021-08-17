# Sample Extension Application for Onboarding Employees in SAP SuccessFactors

## Introduction

You can extend the functionality of your SAP SuccessFactors system to manage your business partners. To do that, you can use the Sample Extension Application for Onboarding Employees in SAP SuccessFactors, deploy it in a subaccount in SAP Business Technology Platform (SAP BTP) but have it fully integrated in your SAP SuccessFactors system. Using this application, you can:

* Move the employees in your company from one job title to another
* Approve the transfer of employees between postions and departments

![](SAP-SuccessFactors-Employee-Onboarding.png)

## Prerequisites

There are several components and authorizations that you and/or your team members need.

**Tools**

* [JDK 7 or later](http://www.oracle.com/technetwork/java/javase/downloads/jre7-downloads-1880261.html)
* [Maven 3.0.x](http://maven.apache.org/docs/3.0.5/release-notes.html)
* [Cloud Foundry Command Line Interface (cf CLI)](https://help.sap.com/viewer/65de2977205c403bbc107264b8eccf4b/Cloud/en-US/4ef907afb1254e8286882a2bdef0edf4.html?q=cf%20CLI)
* [git](https://git-scm.com/download/)

**On SAP BTP side:**

* You have either an [enterprise](https://help.sap.com/viewer/65de2977205c403bbc107264b8eccf4b/Cloud/en-US/171511cc425c4e079d0684936486eee6.html) or a [trial](https://help.sap.com/viewer/65de2977205c403bbc107264b8eccf4b/Cloud/en-US/046f127f2a614438b616ccfc575fdb16.html) global account in SAP BTP.
* You have an S-user or P-user (if you are using an enterprise global account), and a trial user (if you are using a trial account). See [User and Member Management](https://help.sap.com/viewer/65de2977205c403bbc107264b8eccf4b/Cloud/en-US/cc1c676b43904066abb2a4838cbd0c37.html?q=user).
* You are an administrator of the global account where you want to register your SAP SuccessFactors system.
* You enable the Cloud Foundry capabilities for your subaccount in SAP BTP.
* Check which feature set you are using. See [Cloud Management Tools — Feature Set Overview](https://help.sap.com/viewer/65de2977205c403bbc107264b8eccf4b/Cloud/en-US/caf4e4e23aef4666ad8f125af393dfb2.html?q=feature%20set).

**On SAP SuccessFactors side:**

* You have a dedicated SAP SuccessFactors company instance.
* To configure the integration on the SAP SuccessFactors system side, you need a user with permissions to access SAP SuccessFactors Provisioning

## Process

### 1. Connect the SAP SuccessFactors system you want to extend with the corresponding global account in SAP BTP

To do that, have to register your SAP SuccessFactors system in your global account in SAP BTP. During this process, an integration token is created and then used by the SAP SuccessFactors system tenant administrator to configure the integration on the SAP SuccessFactors system side.
See [Register an SAP SuccessFactors System in a Global Account in SAP BTP](https://help.sap.com/viewer/65de2977205c403bbc107264b8eccf4b/Cloud/en-US/e956ba209f30447cb55140e38c15e345.html). 

### 2. Make the SAP SuccessFactors system accessible in the subaccount in SAP BTP in which you want to build your extension application.

To do so, you configure the entitlements and assign the corresponding quota and the `api-access` service plan to the subaccount where the extension applications will reside for the system you registered in the previous step.
See [Configure the Entitlements for the Subaccount in SAP BTP](https://help.sap.com/viewer/65de2977205c403bbc107264b8eccf4b/Cloud/en-US/65ad330d11ac49a196948aa8db6470fb.html).

### 3. Clone the Sample Extension Application for Onboarding Employees in SAP SuccessFactors from GitHub

1. To clone the GitHub repository, use this command:

      ```
      git clone https://github.com/SAP/task-management-sample-app-sfsf-solutions
      ```

2. In the root of the project, there's a file called `sap-successfactors-extensibility.json` which contains the name of the SAP SuccessFactors system which you'll connect to. You need to replace the value of the **"systemName"** parameter with the name of the SAP SuccessFactors system you registered in **step 1. Connect the SAP SuccessFactors system you want to extend with the corresponding global account in SAP BTP**. 


3. In the root of the project, there's a file called `vars.yml`. You need to replace the values of these parameters:

* **ID**: this is your user in SAP BTP. It's either an S-user, a P-user, or a trial user. 
* **LANDSCAPE_APPS_DOMAIN**: this is the API endpoint of your subaccount in SAP BTP. See [Log On to the Cloud Foundry Environment Using the Cloud Foundry Command Line Interface](https://help.sap.com/viewer/65de2977205c403bbc107264b8eccf4b/Cloud/en-US/7a37d66c2e7d401db4980db0cd74aa6b.html).

4. To build the appliation, use this command in a console started from the root folder of the project:

      ```
      mvn clean install
      ```

### 4. Configure the Entitlements to the Cloud Foundry Runtime

1. Log on to the SAP BTP cockpit as Cloud Foundry administrator.
2. Go to your global account.
3. [Feature Set A]: From the navigation area, choose **Entitlements** > **Subaccount Assignments** and enter your subaccount.

   [Feature Set B]: From the navigation area, choose **Entitlements** > **Entity Assignments**.

4. If there is no entry for the Cloud Foundry runtime, choose **Configure Entitlements** and **Add Service Plans**.
5. In the following popup, proceed as follows:

    1. Choose **Cloud Foundry Runtime**.
    2. Under **Available Service Plans**, select the checkbox **MEMORY**.
    3. Choose **Add 1 Service Plan**.
  
6. Choose **+** to add at least 1 to the subaccount.
7. Choose **Save**.

### 5. Create a Destination Service Instance

You have to create a service instance of the Destination service using the `lite` service plan.

1. To log on to the cf CLI, use this command:

      ```
      cf login -a https://api.cf.sap.hana.ondemand.com
      ```
  
  where https://api.cf.sap.hana.ondemand.com is the API endpoint of the subaccount. See [Log On to the Cloud Foundry Environment Using the Cloud Foundry Command Line Interface](https://help.sap.com/viewer/65de2977205c403bbc107264b8eccf4b/Cloud/en-US/7a37d66c2e7d401db4980db0cd74aa6b.html).

2. To navigate to your space, type in the console the number that corresponds to your Org in the list with Orgs that is displayed after you log on to cf CLI. 
  
3. To create the Destination service instance, use this command:

      ```
      cf create-service destination lite destination
      ```

### 6. Create an SAP SuccessFactors Extensibility Service Instance to Consume the SAP SuccessFactors APIs

You have to create a service instance of the SAP SuccessFactors Extensibility service using the `api-access` service plan.

During the service instance creation, an HTTP destination on a subaccount level is automatically generated in this subaccount. It contains all instance binding properties which are sufficient to establish connection to the SAP SuccessFactors system. When creating the service instance, you configure the communication arrangement and the authentication type for the connection in a JSON file. 
  
In cf CLI, use this command to create a service instance of the SAP SuccessFactors Extensibility service:

```
cf create-service sap-successfactors-extensibility api-access sap-successfactors-extensibility -c sap-successfactors-extensibility.json
```
      
### 7. Create a Service Instance of the Authorization and Trust Management (XSUAA) Service

In cf CLI, use this command to create a service instance of the Authorization and Trust Management (XSUAA) service:

```
cf create-service xsuaa application xsuaa -c xsuaa.json
```

### 8. Build, Deploy and Run the Application

These are the steps you need to follow to get the Sample Extension Application for Onboarding Employees in SAP SuccessFactors, deploy it and run it:

1. In the cf CLI, navigate to the root folder of the project using the command:

      ```
      cd <root_project_folder>
      ```

2. In the cf CLI push the `vars.yml` file using this command:

      ```
      cf push --vars-file vars.yml
      ```

3. To run the application, copy and paste this URL in a browser:

      ```
      http://employee-onboarding-demo-web<your-user>.cfapps.sap.hana.ondemand.com/index.html
      ```
   
   where **<your-user>** is your S-user, P-user, or trial user.
   
## Summary
