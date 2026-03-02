# Variables
DC = docker compose

# Extraction des arguments pour les commandes
SUPPORTED_COMMANDS := start stop down clean logs
ifneq ($(filter $(firstword $(MAKECMDGOALS)),$(SUPPORTED_COMMANDS)),)
  SVC := $(wordlist 2,$(words $(MAKECMDGOALS)),$(MAKECMDGOALS))
  ifeq ($(SVC),)
    SVC = api
  endif
  $(eval $(SVC):;@:)
endif

.PHONY: help start stop down clean test logs

help: ## Affiche ce message d'aide
	@echo 'Usage:'
	@echo '  make [target] [service]'
	@echo ''
	@echo 'Targets:'
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | sort | awk 'BEGIN {FS = ":.*?## "}; {printf "  \033[36m%-20s\033[0m %s\n", $$1, $$2}'

start: ## Démarrer un service (par défaut: api, ou 'all' pour tout)
ifeq ($(SVC),all)
	$(DC) up -d --build
else
	$(DC) up -d --build $(SVC)
endif

test: ## Lancer les tests avec Docker (image Maven)
	docker run --rm -v "$(CURDIR)":/app -w /app maven:3.9.3-eclipse-temurin-17 mvn test

stop: ## Stopper un service (par défaut: api, ou 'all' pour tout)
ifeq ($(SVC),all)
	$(DC) stop
else
	$(DC) stop $(SVC)
endif

down: ## Stopper et supprimer un service (par défaut: api, ou 'all' pour tout)
ifeq ($(SVC),all)
	$(DC) down
else
	$(DC) rm -s -f $(SVC)
endif

clean: ## Supprimer et nettoyer (par défaut: api, ou 'all' pour tout)
ifeq ($(SVC),all)
	$(DC) down -v --rmi all
else
	$(DC) rm -s -v -f $(SVC)
endif

logs: ## Afficher les logs en direct (par défaut: api, ou 'all' pour tout)
ifeq ($(SVC),all)
	$(DC) logs -f
else
	$(DC) logs -f $(SVC)
endif
