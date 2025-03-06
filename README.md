# New-Account-PoC
How to run 

1. Pull all the code from git
2. Run the following command to start the containers（PostgreSQL, backend service, and Nginx.）:
   ```sh
   docker-compose up -d

   
3. restore my data table and test data into postgresql database by running the following commands

    ```sh
    # Copy the backup data file into the PostgreSQL container
    # Replace the local path with your actual backup file path
    docker cp /Users/yujm/dump-banking_core-202503051932.sql pg_db:/tmp/backup.sql

    # Enter the PostgreSQL container
    docker exec -it pg_db sh

    # Restore the data inside the container
    pg_restore -U postgres -d banking_core -v /tmp/backup.sql

4. How to access the system

   Use below url to access the portal: 
   
   http://localhost:8081

   Use below url to access the swagger api document:
   
   http://localhost:8080/swagger-ui/index.html



    