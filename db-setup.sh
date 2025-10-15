#!/bin/bash

# Database setup script for eshop
# This script helps create or drop the eshop database in the postgres-infra container

set -e

CONTAINER_NAME="postgres-infra"
DB_NAME="eshop"
DB_USER="postgres"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Function to check if container is running
check_container() {
    if ! docker ps --format '{{.Names}}' | grep -q "^${CONTAINER_NAME}$"; then
        echo -e "${RED}Error: Container '${CONTAINER_NAME}' is not running.${NC}"
        echo "Please start the container first using:"
        echo "  cd /path/to/db-samples && docker compose up -d postgres-infra"
        exit 1
    fi
}

# Function to check if database exists
db_exists() {
    docker exec -it ${CONTAINER_NAME} psql -U ${DB_USER} -d postgres -tAc "SELECT 1 FROM pg_database WHERE datname='${DB_NAME}'" | grep -q 1
}

# Function to create database
create_db() {
    echo -e "${YELLOW}Creating database '${DB_NAME}'...${NC}"
    
    if db_exists; then
        echo -e "${YELLOW}Database '${DB_NAME}' already exists.${NC}"
        read -p "Do you want to drop and recreate it? (y/N): " -n 1 -r
        echo
        if [[ $REPLY =~ ^[Yy]$ ]]; then
            drop_db
        else
            echo -e "${GREEN}Keeping existing database.${NC}"
            return 0
        fi
    fi
    
    docker exec -it ${CONTAINER_NAME} psql -U ${DB_USER} -d postgres -c "CREATE DATABASE ${DB_NAME};"
    docker exec -it ${CONTAINER_NAME} psql -U ${DB_USER} -d postgres -c "GRANT ALL PRIVILEGES ON DATABASE ${DB_NAME} TO ${DB_USER};"
    
    echo -e "${GREEN}Database '${DB_NAME}' created successfully!${NC}"
}

# Function to drop database
drop_db() {
    echo -e "${YELLOW}Dropping database '${DB_NAME}'...${NC}"
    
    if ! db_exists; then
        echo -e "${YELLOW}Database '${DB_NAME}' does not exist.${NC}"
        return 0
    fi
    
    # Terminate all connections to the database
    docker exec -it ${CONTAINER_NAME} psql -U ${DB_USER} -d postgres -c "SELECT pg_terminate_backend(pg_stat_activity.pid) FROM pg_stat_activity WHERE pg_stat_activity.datname = '${DB_NAME}' AND pid <> pg_backend_pid();"
    
    docker exec -it ${CONTAINER_NAME} psql -U ${DB_USER} -d postgres -c "DROP DATABASE IF EXISTS ${DB_NAME};"
    
    echo -e "${GREEN}Database '${DB_NAME}' dropped successfully!${NC}"
}

# Function to show database info
show_info() {
    echo -e "${YELLOW}Database Information:${NC}"
    echo "Container: ${CONTAINER_NAME}"
    echo "Database: ${DB_NAME}"
    echo "User: ${DB_USER}"
    echo ""
    
    if db_exists; then
        echo -e "${GREEN}Status: Database exists${NC}"
        echo ""
        echo "Connection details:"
        echo "  Host: localhost"
        echo "  Port: 5432"
        echo "  Database: ${DB_NAME}"
        echo "  User: ${DB_USER}"
        echo "  JDBC URL: jdbc:postgresql://localhost:5432/${DB_NAME}"
    else
        echo -e "${RED}Status: Database does not exist${NC}"
    fi
}

# Function to open psql shell
open_shell() {
    echo -e "${YELLOW}Opening psql shell for database '${DB_NAME}'...${NC}"
    
    if ! db_exists; then
        echo -e "${RED}Error: Database '${DB_NAME}' does not exist.${NC}"
        echo "Please create it first using: $0 create"
        exit 1
    fi
    
    docker exec -it ${CONTAINER_NAME} psql -U ${DB_USER} -d ${DB_NAME}
}

# Main script
show_usage() {
    echo "Usage: $0 {create|drop|recreate|info|shell}"
    echo ""
    echo "Commands:"
    echo "  create    - Create the eshop database"
    echo "  drop      - Drop the eshop database"
    echo "  recreate  - Drop and recreate the eshop database"
    echo "  info      - Show database information and status"
    echo "  shell     - Open psql shell for the eshop database"
    echo ""
}

# Check if container is running
check_container

# Parse command
case "${1:-}" in
    create)
        create_db
        ;;
    drop)
        drop_db
        ;;
    recreate)
        drop_db
        create_db
        ;;
    info)
        show_info
        ;;
    shell)
        open_shell
        ;;
    *)
        show_usage
        exit 1
        ;;
esac

exit 0
