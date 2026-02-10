# Variables
DC = docker-compose
MAVEN = ./mvnw

.PHONY: help up down restart logs clean build install

help: ## Show this help message
	@echo 'Usage:'
	@echo '  make [target]'
	@echo ''
	@echo 'Targets:'
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | sort | awk 'BEGIN {FS = ":.*?## "}; {printf "  \033[36m%-20s\033[0m %s\n", $$1, $$2}'

up: ## Start the application and database (dettached mode)
	$(DC) up -d

down: ## Stop the application and database
	$(DC) down

restart: down up ## Restart the application and database

logs: ## Follow logs
	$(DC) logs -f

build: ## Build the application with Maven
	$(MAVEN) clean package -DskipTests

rebuild: build ## Rebuild and restart the container
	$(DC) up -d --build api

clean: down ## Stop services and remove volumes (WARNING: Data loss)
	$(DC) down -v

install: ## Install dependencies (Maven)
	$(MAVEN) install -DskipTests

sql-shell: ## Access the database via psql inside the container
	docker exec -it betterdle-postgres psql -U betterdle -d betterdle
