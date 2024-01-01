# 

# **Project Overview**

This project is a Spring Boot application designed to interact with AWS services, specifically Amazon S3 for data storage and Amazon Athena for querying data. It provides a REST API to upload and read data to/from an S3 bucket, as well as to execute and fetch results from SQL queries using Athena.

## **Features**

### **S3 Operations with `S3Controller`**

- **Upload Data to S3**: The application allows uploading individual or bulk user data in JSON format to an S3 bucket. This is handled by the **`S3Controller`** class.
- **Read Data from S3**: Users can read data stored in the S3 bucket. The **`S3Controller`** class provides an endpoint to fetch data from a specified S3 object.

### **Athena Query Execution with `AthenaController`**

- **Execute SQL Queries on S3 Data**: The application can run SQL queries on data stored in S3 using AWS Athena. The **`AthenaController`** class handles the execution of these queries.
- **Fetch Query Results**: After executing a query, the application retrieves the results from Athena and presents them in a structured format.

## **Prerequisites**

- JDK (Java Development Kit) - Version 8 or higher.
- Maven or Gradle for managing dependencies.
- An AWS account with access to S3 and Athena.
- AWS CLI configured with credentials (AWS_ACCESS_KEY_ID and AWS_SECRET_ACCESS_KEY).

## **Configuration and Setup**

### **AWS Configuration**

- Set AWS credentials and S3 bucket name in environment variables:
    - **`AWS_ACCESS_KEY_ID`**
    - **`AWS_SECRET_ACCESS_KEY`**
    - **`S3_BUCKET_NAME`**

### **Running the Application**

1. Clone the repository and navigate to the project directory.
2. Build the application using Maven or Gradle.
3. Run the application using your IDE or command line.

## **Services and Controllers**

### **`AthenaService`**

- Connects to AWS Athena and executes SQL queries.
- Waits for query execution to complete and fetches the results.
- Parses the query results into a list of **`UserDetails`** objects.

### **`S3Service`**

- Handles the interaction with Amazon S3.
- Provides functionality to upload JSON content to an S3 bucket.
- Implements methods to read data from an S3 object.

### **`AthenaController`**

- Exposes an endpoint to execute SQL queries via Athena.
- Returns the query results as a list of **`UserDetails`**.

### **`S3Controller`**

- Provides endpoints for uploading and reading data to/from S3.
- Supports both single and bulk data uploads.
- Reads data from a specified S3 object.

## **Data Models**

### **`CreateUserRequest`**

- Represents the data structure for user details to be uploaded to S3.

### **`UserDetails`**

- Represents the data structure of user details fetched from Athena query results.

## **Usage**

- **Uploading Data to S3**: Use **`/api/s3/upload`** to upload a single user's data or **`/api/s3/bulk-upload`** for multiple users.
- **Reading Data from S3**: Send a GET request to **`/api/s3/read`** to retrieve data from the S3 bucket.
- **Executing Athena Queries**: Send a GET request to **`/api/athena/execute-query`** with the SQL query to execute a query and get results.

## **Conclusion**

This Spring Boot application is a powerful tool for interacting with AWS S3 and Athena, offering a convenient way to manage data storage and analysis in the cloud. Whether for individual use or as part of a larger system, it provides essential functionalities for handling data efficiently.
