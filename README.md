Design:
- One? thread per synchronization job
- Synchronization job state kept in the target database 
- Standalone web application - spring boot
- REST API for reading statuses and providing input (e.g. new docs transformation method)  

Technology outline:
- LightCouch for storing/retreiving docs and getting changes updates  
- Jolt for json to json tranformation
- Spring MVC for servlet controller layer 

Description:
- We wait for changes
- When change arrive we simply read the doc id and revision 
- We download new version, translate it and store instead of existing one (this step and the above can be combined by using inclueDocs switch)

Issues:
- What the service does when it fails to process changes 
	- stops and waits for data to be corrected 
		- does not move revision marker 
	- polls changes whenever they are available 
		- moves revision marker 
		- if it is unable to process change event (schema validation fails) it stores the state (marker) and adds id to a blacklist
		- proceeds further with documents that are not on the black list  
		- should be rethought 
			- service is "always" in a state in which we ahve some failing changes (docs on blacklist) and some succeeding 
			- continuous processing of the "blacklist" should be planned 
			 
- What the service does when the schema changes 
	- it stops synchronization waiting for the request 
	