# Java-MySQL-JDBC-
This project is meant to implement AirBnB system.
Purpose of this project is to get familiar with MySQL which is embedded with Java.

Assumptions (followed by reasons why):
1.	Serviceadmin should prepare tables by requirements before users try to use.
-	Requirement can be changed by anytime from client.
2.	Latitude and longitude does not have to precise with address.
-	It is really hard to get users to know their geographical coordinates.
3.	Check-in time starts at 00:00 AM and Check-out time ends at 11:59:59 PM	
-	To prevent conflict between renters, since if current renter gets delayed on check-out time, next renter can be in the trouble.
4.	Serviceadmin has authority for all operation.
-	Serviceadmin should be the only mighty person who can edit tables. Service admin can check all the tables as well.
5.	User can choose whether or not to give system a user’s credit card information.
-	Some user might not happy when they have to give credit card information when they are registering.
6.	Credit Card validation has been confirmed and transaction has been processed at the time of booking.
-	Assumption from handout.
7.	Every input is validated
- This is a assumption for now but can be implemented in the future.

System Limitations:
1. System is not calculating exact age, person who has born before 1998 can be eligible (19+).
2. System does not check format of user_input. User can apply any string into string type field or any integer into int type field.
3. System does not check validity of user's credit card information.
4. When user tries to search by city or address, this will only show matching list. Not adjacent.

System limitations above can be fixed easily if the time allows.
