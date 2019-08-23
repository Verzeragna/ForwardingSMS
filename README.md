# ForwardingSMS
ForwardingSMS it's app for forwarding incoming SMS. You create simple task in app and that task will work when the necessary conditions will be met. Every task contains name, sender number or name, reciever number and checkbox that turn yuor task on or off.

File name           | File content 
--------------------|--------------------------------------------------------------------------------------------------------
MainActivity.java   | It's the main app activity where display tasks.
AddActivity.java    | It's activity where user can edit/delete/create tasks.
DBHelper.java       | Class DBHelper create data base and manage it.
SMSReciever.java    | Class SMSReciever listen incoming SMS and performs actions according to the task.
