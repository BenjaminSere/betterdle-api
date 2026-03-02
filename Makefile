# Variables
DC = docker compose

# Extraction des arguments pour la commande logs
ifeq (logs,$(firstword $(MAKECMDGOALS)))
  LOG_ARGS := $(wordlist 2,$(words $(MAKECMDGOALS)),$(MAKECMDGOALS))
  ifeq ($(LOG_ARGS),)
    LOG_ARGS = api
  endif
  $(eval $(LOG_ARGS):;@:)
endif

.PHONY: help start stop clean test logs

help: ## Affiche ce message d'aide
	@echo 'Usage:'
	@echo '  make [target]'
	@echo ''
	@echo 'Targets:'
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | sort | awk 'BEGIN {FS = ":.*?## "}; {printf "  \033[36m%-20s\033[0m %s\n", $$1, $$2}'

start: ## Démarrer l'API et la base de données
	$(DC) up -d --build

test: ## Lancer les tests avec Docker (image Maven)
	docker run --rm -v "$(CURDIR)":/app -w /app maven:3.9.3-eclipse-temurin-17 mvn test

stop: ## Stopper les conteneurs
	$(DC) stop

down: stop ## Stopper et supprimer les conteneurs
	$(DC) down

clean: down ## Supprimer les conteneurs, les volumes de données et les images
	$(DC) down -v --rmi all

logs: ## Afficher les logs en direct (par défaut: api). Usage: make logs [service]
	$(DC) logs -f $(LOG_ARGS)
