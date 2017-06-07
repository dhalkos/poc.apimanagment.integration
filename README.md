# Spring-Boot Camel Bank Rest API

This project exposes 3 REST APIs which return mocked responses

### Get Account Balance
	path:'/camel/api/{ACCOUNT_ID}/balance':
    method: 'get'      
    parameters:
        - in: path
          name: ACCOUNT_ID
          description: The account id
          required: true
          type: string
    responses:
        '200':
			description: Success
			schema:
				$ref: '#/definitions/AccountDetailsJSON'
			
### Get Account Transactions	
	path:'/camel/api/{ACCOUNT_ID}/transactions':
    method: 'get'      
	parameters:
		- in: path
		  name: ACCOUNT_ID
		  description: The account id
		  required: true
		  type: string
		- name: obp_from_date
		  type: string
		  required: true
		  in: header
		  description: "Date of the oldest transaction registered.  (Date format parameter \"yyyy-MM-dd'T'HH:mm:ss.SSS'Z'\" (2014-07-01T00:00:00.000Z)"
		- name: obp_to_date
		  type: string
		  required: true
		  in: header
		  description: "Date of the newest transaction registered (Date format parameter \"yyyy-MM-dd'T'HH:mm:ss.SSS'Z'\" (2014-07-01T00:00:00.000Z)"
	responses:
		'200':
			description: Success
			schema:
				$ref: '#/definitions/TransactionsJSON'
				
### Transfer Ammount from Account
	path:'/camel/api/{ACCOUNT_ID}/transfer':
    method: 'post' 
	parameters:
        - in: body
          name: body
          description: BANK_BODY
          required: true
          schema:
            $ref: '#/definitions/TransactionRequestBodySEPAJSON'
        - in: path
          name: ACCOUNT_ID
          description: The account id
          required: true
          type: string
      responses:
        '201':
          description: Success
          schema:
            $ref: '#/definitions/TransactionResponseJSON'
				
### Building

The project can be built with

    mvn clean install


### Running the example locally

The project can be run locally using the following Maven goal:

    mvn spring-boot:run


### Running the example in Kubernetes

It is assumed a running Kubernetes platform is already running. If not you can find details how to [get started](http://fabric8.io/guide/getStarted/index.html).

Assuming your current shell is connected to Kubernetes or OpenShift so that you can type a command like

```
kubectl get pods
```

or for OpenShift

```
oc get pods
```

Then the following command will package your app and run it on Kubernetes:

```
mvn fabric8:run
```

To list all the running pods:

    oc get pods

Then find the name of the pod that runs this quickstart, and output the logs from the running pods with:

    oc logs <name of pod>

You can also use the [fabric8 developer console](http://fabric8.io/guide/console.html) to manage the running pods, and view logs and much more.


### More details

You can find more details about running this [quickstart](http://fabric8.io/guide/quickstarts/running.html) on the website. This also includes instructions how to change the Docker image user and registry.

