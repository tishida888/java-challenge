# Axa Java Challenge

### How to use this spring-boot project

- Install packages with `mvn package`
- Run `mvn spring-boot:run` for starting the application (or use your IDE)

Swagger URL has been changed as it's using v3
- Swagger UI : http://localhost:8080/swagger-ui/index.html
- H2 UI : http://localhost:8080/h2-console


### How to use

Client needs to be registered in Account table and only the user (who has account) can get token for authentication.
For testing purpose, the below accounts are pre-defined in table (defined in resources/sql/data.sql). 
Please use one of them to get token through Token API.

|name     |password    |admin  |
|:--------|:-----------|:------|
|admin1   |admin1      |true   |
|admin2   |admin2      |true   |
|admin3   |admin3      |true   |
|user1    |user1       |false  |
|user2    |user2       |false  |
|user3    |user3       |false  |


#### Token API :
In order to get token, please send POST request (like below) and you will get JSON content in response. 

  `curl -X POST "http://localhost:8080/api/v1/token?name=admin1&password=admin1" -H  "accept: */*" -d ""`

Response :
  {
    "token": "Bearer {Your Token}"
  }

#### Account API (admin role only) :
Only users who have admin role (admin=true) has access to the API. To get data through Account API, you need to set token in request header.
If you use admin role user, you can add/delete a new account.

  `curl -X GET "http://localhost:8080/api/v1/account" -H  "accept: application/json" -H  "Authorization: Bearer {YourToken}"`

Sadly still need to set 'Bearer ' in the token (I tried to remove 'Bearer' string, but couldn't find yet), so please note that the header has to be `Authorization: Bearer {YourToken}` format


#### Employee API (any user) :
Any user (even who does NOT have admin role) has access to Employee API. Token Authentication is same as Account API.

  `curl -X GET "http://localhost:8080/api/v1/employees" -H  "accept: application/json" -H  "Authorization: Bearer {YourToken}"`



### Changes
- Add Account logic. Setup Account DB, pre-defined accounts, Account API etc. This is for endpoint protection (i.e. only user who has account can access to API)
- Add cache logic on controll layer using ehcache.
- Add JWT token authentication (i.e. don't use session)
- Add logging in several places
- Add unit test on each layer (control/entity/service etc)


### What I would do if I had more time
- The code doesn't cover all cases of exceptions for API. Hence it just catches Exception and throwing ResponseStatusException with general message.
  Ideally it should have proper exception handling and return proper error message (with proper status code) to client.
- If exception handling is properly done, enhance swagger doc to make it clear for exceptions
- Also I would build a generic exception handler class in code and return to the client with better format of message
- It should have union of layers (control/repository/service) unit test using dummy data as well.
- Remoe 'Bearer ' string for authentication
- It depends on performance, but if I see bottleneck on DB io, I would have several threads to avoid perfromance issue
- 


#### My experience in Java
- I have Java experience from 2009 to 2015 (6 years) for trading application development.
- I didn't have spring boot experience. I learned several things from this challenge and I believe I will be able to catch up quickly.

