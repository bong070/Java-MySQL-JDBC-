# Java-MySQL-JDBC-
This project is meant to implement AirBnB system.<br />
Purpose of this project is to get familiar with MySQL which is embedded with Java.

  * Assumptions (followed by reasons why): <br />
1.  Serviceadmin should prepare tables by requirements before users try to use.<br />
  a. Requirement can be changed by anytime from client. 
2.	Latitude and longitude does not have to precise with address.<br />
  a. It is really hard to get users to know their geographical coordinates.<br />
3.	Check-in time starts at 00:00 AM and Check-out time ends at 11:59:59 PM	<br />
  a. To prevent conflict between renters, since if current renter gets delayed on check-out time, next renter can be in the trouble.<br />
4.	Serviceadmin has authority for all operation.<br />
  a.	Serviceadmin should be the only mighty person who can edit tables. <br />
  b. Service admin can check all the tables as well.<br />
5.	User can choose whether or not to give system a user’s credit card information.<br />
  a.	Some user might not happy when they have to give credit card information when they are registering.<br />
6.	Credit Card validation has been confirmed and transaction has been processed at the time of booking.<br />
  a.	Assumption from handout.<br />
7.	Every input is validated.<br />
  a. This is a assumption for now but can be implemented in the future.<br />

System Limitations:<br />
1. System is not calculating exact age, person who has born before 1998 can be eligible (19+).<br />
2. System does not check format of user_input. User can apply any string into string type field or any integer into int type field.<br />
3. System does not check validity of user's credit card information.<br />
4. When user tries to search by city or address, this will only show matching list. Not adjacent.<br />
5. System now has text-based UI.<br />

System limitations above can be fixed easily if the time allows.
