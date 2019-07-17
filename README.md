# rest-api-java-maven-docker
## RESTful API
The API will follow all the correct guidelines that currently exist for a RESTful API. Here are some of them:
- A URL identifies a resource.
- URLs include nouns, not verbs.
- Use of plural nouns for consistency.
- Use HTTP verbs (GET, POST, PUT, DELETE) according with [HTTP/1.1](http://www.w3.org/Protocols/rfc2616/rfc2616-sec9.html) standard.
- Use of version in the URL, example http(s)://api.brenosalles.com/v1/path/to/resource.
## HTTP Verbs
Bellow is an example that shows how the API will behave and the vebs that it will use.

| HTTP METHOD | GET | POST | DELETE |
| --- | --- | --- | --- |
| CRUD OP | READ | CREATE/UPDATE | REMOVE |
| /dogs | List all dogs | Create new dog | Error |
| /dogs/1 | Info about dog 1 | Update info of dog 1 | Remove dog 1 |
## Usage
### List all messages
**Definition:** `GET /greetings`

**Response**:
- `500 - Internal Server Error`
- `400 - Bad User Request`
- `200 - OK`

**Response Body (Success):**:
```json
[
	{
        "10": {
            "date": "2019-07-17 15:37:48.873",
            "message": "bJmmCHfVV"
        }
    },
	{
        "28": {
            "date": "2019-07-17 15:47:26.316",
            "message": "LfqAnimt"
        }
    },
    {
        "44": {
            "date": "2019-07-17 15:47:47.067",
            "message": "yPIVIDVc"
        }
    }
]
```

**Response Body (Error):**
```json
{
    "message": "Error motive."
}
```
### Create a message
**Definition:** `POST /greetings`

**Response**:
- `500 - Internal Server Error`
- `400 - Bad User Request`
- `200 - OK`

**Response Body (Success):**:
```json
{
    "10": {
        "date": "2019-07-17 15:37:48.873",
        "message": "bJmmCHfVV"
    }
}
```

**Response Body (Error):**
```json
{
    "message": "Error motive."
}
```

### List Specific message
**Definition:** `GET /greetings/(:id)`

**Response:**
- `500 - Internal Server Error`
- `404 - Not Found`
- `400 - Bad User Request`
- `200 - OK`

**Response Body (Success):**
```json
{
    "10": {
        "date": "2019-07-17 15:37:48.873",
        "message": "bJmmCHfVV"
    }
}
```

**Response Body (Error):**
```json
{
    "message": "Error motive."
}
```

### Update Specific message
**Definition:** `PUT /greetings/(:id)`

**Response:**
- `500 - Internal Server Error`
- `404 - Not Found`
- `400 - Bad User Request`
- `200 - OK`

**Response Body (Success):**
```json
{
    "10": {
        "date": "2019-07-17 15:37:48.873",
        "message": "bJmmCHfVV"
    }
}
```

**Response Body (Error):**
```json
{
    "message": "Error motive."
}
```

### Delete Specific message
**Definition:** `DELETE /greetings/(:id)`

**Response:**
- `500 - Internal Server Error`
- `404 - Not Found`
- `400 - Bad User Request`
- `200 - OK`

**Response Body (Success):**
```json
{
    "10": "Delete Successful."
}
```

**Response Body (Error):**
```json
{
    "message": "Error motive."
}
```

## Requirements (No Docker)
- Java (+11)
- Maven (+3)

## Install (No Docker)
- Navigate to folder
- mvn clean install
- mvn dependency:resolve
- mvn verify
- java -jar ./target/rest-api-0.0.1-SNAPSHOT-jar-with-dependencies.jar

*Note: Will listen on port 4567*

## Requirements (Docker)
- Docker

## Install (Docker)
- Navigate to folder
- docker build -t rest-api .
- docker run -p 5000:4567 rest-api

*Note: Will listen on port 5000*
