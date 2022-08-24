# Monolithic Sample App

## How to run
- Start database

       docker-compose -f docker/database.yaml up -d
- Start keyloak for Oauth2 Server
    
      docker-compose -f docker/keycloak.yaml up -d
- Start backend app
      
      cd backend && ./mvnw spring-boot:run
## Documents

[Backend development doc](backend/README.md)
