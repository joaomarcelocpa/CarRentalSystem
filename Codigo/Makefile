# Makefile para gerenciar o projeto Car Rental System

.PHONY: help build up down restart logs clean dev prod test

# Comandos padrão
help: ## Mostra esta ajuda
	@echo "Comandos disponíveis:"
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | sort | awk 'BEGIN {FS = ":.*?## "}; {printf "\033[36m%-20s\033[0m %s\n", $$1, $$2}'

# Desenvolvimento
dev: ## Inicia os serviços em modo desenvolvimento
	docker compose -f docker-compose.dev.yml up --build

dev-detached: ## Inicia os serviços em modo desenvolvimento (background)
	docker compose -f docker-compose.dev.yml up --build -d

dev-logs: ## Mostra logs dos serviços de desenvolvimento
	docker compose -f docker-compose.dev.yml logs -f

dev-down: ## Para os serviços de desenvolvimento
	docker compose -f docker-compose.dev.yml down

# Produção
prod: ## Inicia os serviços em modo produção
	docker compose --profile production up --build -d

prod-logs: ## Mostra logs dos serviços de produção
	docker compose --profile production logs -f

prod-down: ## Para os serviços de produção
	docker compose --profile production down

# Comandos gerais
build: ## Constrói as imagens Docker
	docker compose build

up: ## Inicia todos os serviços
	docker compose up -d

down: ## Para todos os serviços
	docker compose down

restart: ## Reinicia todos os serviços
	docker compose restart

logs: ## Mostra logs de todos os serviços
	docker compose logs -f

# Limpeza
clean: ## Remove containers, volumes e imagens não utilizados
	docker-compose down --volumes --remove-orphans
	docker system prune -f
	docker volume prune -f

clean-all: ## Remove tudo (containers, volumes, imagens e networks)
	docker-compose down --volumes --remove-orphans
	docker system prune -af
	docker volume prune -f
	docker network prune -f

# Testes
test: ## Executa testes
	docker-compose exec backend ./mvnw test
	docker-compose exec frontend npm test

# Comandos específicos
backend-logs: ## Mostra logs apenas do backend
	docker-compose logs -f backend

frontend-logs: ## Mostra logs apenas do frontend
	docker-compose logs -f frontend

backend-shell: ## Abre shell no container do backend
	docker-compose exec backend /bin/bash

frontend-shell: ## Abre shell no container do frontend
	docker-compose exec frontend /bin/sh

# Status
status: ## Mostra status dos containers
	docker-compose ps

health: ## Verifica saúde dos serviços
	@echo "Verificando backend..."
	@curl -f http://localhost:8080/api/test/public || echo "Backend não está respondendo"
	@echo "Verificando frontend..."
	@curl -f http://localhost:3000 || echo "Frontend não está respondendo"

# Desenvolvimento rápido
quick-start: ## Inicia rapidamente para desenvolvimento
	@echo "Iniciando serviços de desenvolvimento..."
	docker-compose -f docker-compose.dev.yml up --build -d
	@echo "Aguardando serviços ficarem prontos..."
	@sleep 30
	@make health
	@echo "Serviços iniciados!"
	@echo "Frontend: http://localhost:3000"
	@echo "Backend: http://localhost:8080"
	@echo "H2 Console: http://localhost:8080/h2-console"
