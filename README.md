Sistema de Aluguel de Carros 🚗
===============================

Sistema completo de aluguel de carros desenvolvido para a disciplina de Laboratório de Desenvolvimento de Software do curso de Engenharia de Software da PUC Minas, ministrada pelo Prof. Dr. João Paulo Carneiro Aramuni.

👥 Equipe de Desenvolvimento
----------------------------

*   **Bernardo de Resende**
    
*   **Flávio de Souza**
    
*   **João Marcelo Carvalho**
    
*   **Miguel Figueiredo**
    

📋 Sumário
----------

*   [Sobre o Projeto](https://claude.ai/chat/286d45eb-6d81-4c0a-8bb5-ecfe1c311405#sobre-o-projeto)
    
*   [Tecnologias Utilizadas](https://claude.ai/chat/286d45eb-6d81-4c0a-8bb5-ecfe1c311405#tecnologias-utilizadas)
    
*   [Arquitetura do Sistema](https://claude.ai/chat/286d45eb-6d81-4c0a-8bb5-ecfe1c311405#arquitetura-do-sistema)
    
*   [Banco de Dados na Nuvem](https://claude.ai/chat/286d45eb-6d81-4c0a-8bb5-ecfe1c311405#banco-de-dados-na-nuvem)
    
*   [Estrutura de Arquivos](https://claude.ai/chat/286d45eb-6d81-4c0a-8bb5-ecfe1c311405#estrutura-de-arquivos)
    
*   [Como Executar](https://claude.ai/chat/286d45eb-6d81-4c0a-8bb5-ecfe1c311405#como-executar)
    
*   [Funcionalidades](https://claude.ai/chat/286d45eb-6d81-4c0a-8bb5-ecfe1c311405#funcionalidades)
    
*   [Diagramas](https://claude.ai/chat/286d45eb-6d81-4c0a-8bb5-ecfe1c311405#diagramas)
    
*   [Demonstração](https://claude.ai/chat/286d45eb-6d81-4c0a-8bb5-ecfe1c311405#demonstração)
    

🎯 Sobre o Projeto
------------------

O **RentalCarSystem** é uma aplicação web full-stack para gerenciamento de aluguel de veículos, desenvolvida com foco em boas práticas de engenharia de software. O sistema oferece diferentes níveis de acesso (Cliente, Agente Empresa e Agente Banco), permitindo a gestão completa do ciclo de vida de solicitações de aluguel, desde a criação até a aprovação e execução.

### Principais Características

*   **Sistema de Autenticação JWT**: Segurança robusta com tokens de autenticação
    
*   **Múltiplos Perfis de Usuário**: Cliente, Agente Empresa e Agente Banco
    
*   **Gestão de Crédito**: Controle de limite de crédito para clientes
    
*   **Gerenciamento de Veículos**: CRUD completo de automóveis
    
*   **Fluxo de Aprovação**: Sistema de workflow para solicitações de aluguel
    
*   **Interface Responsiva**: Design moderno e adaptável a diferentes dispositivos
    

🛠 Tecnologias Utilizadas
-------------------------

### Backend

*   **Java 17**: Linguagem de programação principal
    
*   **Spring Boot 3.2.0**: Framework para desenvolvimento de aplicações Java
    
    *   Spring Web
        
    *   Spring Data JPA
        
    *   Spring Security
        
    *   Spring Validation
        
*   **PostgreSQL 42.7.3**: Banco de dados relacional
    
*   **JWT (jsonwebtoken 0.11.5)**: Autenticação e autorização
    
*   **Maven**: Gerenciamento de dependências e build
    
*   **Docker**: Containerização da aplicação
    

### Frontend

*   **Next.js 15**: Framework React para produção
    
*   **React 19**: Biblioteca para construção de interfaces
    
*   **TypeScript**: Superset JavaScript com tipagem estática
    
*   **Tailwind CSS**: Framework CSS utility-first
    
*   **Lucide React**: Biblioteca de ícones
    
*   **Vercel Analytics**: Monitoramento e análise
    

### Infraestrutura

*   **Docker & Docker Compose**: Orquestração de containers
    
*   **Microsoft Azure**: Hospedagem do banco de dados
    
*   **Vercel**: Hospedagem potencial do frontend
    

🏗 Arquitetura do Sistema
-------------------------

### Arquitetura Backend

O backend segue uma arquitetura em camadas (Layered Architecture) com separação clara de responsabilidades:

#### Camadas do Backend

1.  **Controllers**: Endpoints REST que recebem requisições HTTP
    
2.  **Services**: Implementação da lógica de negócio
    
3.  **Repositories**: Interface com o banco de dados usando Spring Data JPA
    
4.  **Models**: Entidades JPA e objetos de transferência de dados (DTOs)
    
5.  **Security**: Configuração de segurança, filtros JWT e autenticação
    

#### Padrões Utilizados

*   **DTO (Data Transfer Object)**: Separação entre entidades do banco e objetos de API
    
*   **Repository Pattern**: Abstração do acesso a dados
    
*   **Dependency Injection**: Gerenciamento de dependências pelo Spring
    
*   **RESTful API**: Seguindo princípios REST para os endpoints
    

### Arquitetura Frontend

O frontend utiliza uma arquitetura baseada em componentes com Next.js:

#### Estrutura do Frontend

1.  **Pages**: Componentes de página usando App Router do Next.js
    
2.  **Components**: Componentes reutilizáveis da UI
    
3.  **Services**: Camada de serviço para comunicação com a API
    
4.  **Contexts**: Gerenciamento de estado global (ex: AuthContext)
    
5.  **Types**: Definições de tipos TypeScript para type-safety
    

☁️ Banco de Dados na Nuvem
--------------------------

### Microsoft Azure PostgreSQL

O projeto utiliza **PostgreSQL hospedado no Microsoft Azure** para o banco de dados de produção.

#### Razões para a Escolha

1.  **Alta Disponibilidade**: Azure oferece SLA de 99.99% de uptime
    
2.  **Escalabilidade**: Fácil ajuste de recursos conforme demanda
    
3.  **Backups Automáticos**: Proteção de dados com backups gerenciados
    
4.  **Segurança**: Criptografia em repouso e em trânsito
    
5.  **Facilidade de Gerenciamento**: Interface intuitiva e ferramentas de monitoramento
    
6.  **Custo-Benefício**: Para aplicações educacionais, Azure oferece créditos gratuitos
    
7.  **Integração**: Excelente integração com ferramentas de CI/CD
    

#### Configuração

O banco de dados está configurado com:

*   **Versão**: PostgreSQL 42.7.3
    
*   **Região**: Configurável conforme necessidade
    
*   **Conexão Segura**: SSL/TLS habilitado
    
*   **Variáveis de Ambiente**: Credenciais gerenciadas via variáveis de ambiente


📁 Estrutura de Arquivos
------------------------

```
CarRentalSystem/
├── README.md
├── Codigo/
│   ├── docker-compose.yml
│   ├── docker-compose.dev.yml
│   ├── nginx.conf
│   ├── Makefile
│   ├── env.example
│   ├── SETUP-GUIA.md
│   ├── backend/
│   │   ├── src/
│   │   │   ├── main/
│   │   │   │   ├── java/
│   │   │   │   └── resources/
│   │   │   └── test/
│   │   ├── Dockerfile
│   │   ├── pom.xml
│   │   └── target/
│   └── frontend/
│       ├── app/
│       │   ├── login/
│       │   ├── register/
│       │   ├── car-selection/
│       │   ├── rental-requests/
│       │   ├── automobile-management/
│       │   ├── credit-management/
│       │   └── edit-profile/
│       ├── components/
│       ├── shared/
│       │   ├── config/
│       │   ├── contexts/
│       │   ├── interfaces/
│       │   ├── services/
│       │   ├── types/
│       │   └── utils/
│       ├── public/
│       ├── Dockerfile
│       ├── package.json
│       └── tsconfig.json
└── Documentação/
    ├── Diagrama de casos de uso.pdf
    ├── Diagrama de classes UML.pdf
    ├── Diagrama de Componentes.jpeg
    ├── Diagrama de Implantação.pdf
    ├── Diagrama de Pacotes.jpeg
    └── Histórias de Usuário.pdf
```

🚀 Como Executar
----------------

### Pré-requisitos

*   **Docker** (versão 20.10 ou superior)
*   **Docker Compose** (versão 2.0 ou superior)
*   **Git**
*   **Java 17** - apenas para desenvolvimento local
*   **Maven** - apenas para desenvolvimento local
*   **Node.js** (versão 18 ou superior) - apenas para desenvolvimento local

### Executando com Docker (Recomendado)

1. **Clone o repositório**
    ```sh
    git clone <url-do-repositorio>
    cd CarRentalSystem/Codigo
    ```

2. **Configure as variáveis de ambiente**
    
    Copie o arquivo exemplo e configure as variáveis:
    ```sh
    cp env.example backend/.env
    ```
    
    Edite o arquivo `backend/.env` com suas credenciais:
    ```env
    DB_HOST=seu-servidor-azure.postgres.database.azure.com
    DB_PORT=5432
    DB_NAME=rental_system
    DB_USER=seu_usuario
    DB_PASSWORD=sua_senha
    JWT_SECRET=sua_chave_secreta_jwt_muito_segura
    ```

3. **Execute a aplicação**
    ```sh
    docker-compose up -d
    ```

4. **Acesse a aplicação**
    *   Frontend: http://localhost:3000
    *   Backend API: http://localhost:8080
    *   Documentação API: http://localhost:8080/swagger-ui.html (se configurado)

### Executando Localmente (Desenvolvimento)

#### Backend (Spring Boot)

1. **Acesse a pasta backend**
    ```sh
    cd Codigo/backend
    ```

2. **Configure o arquivo `.env`**
    
    Crie o arquivo `.env` com as variáveis de ambiente necessárias (mesmo formato acima)

3. **Instale as dependências e execute**
    ```sh
    mvn clean install
    mvn spring-boot:run
    ```
    
    Ou usando o wrapper Maven:
    ```sh
    ./mvnw clean install
    ./mvnw spring-boot:run
    ```

#### Frontend (Next.js)

1. **Acesse a pasta frontend**
    ```sh
    cd Codigo/frontend
    ```

2. **Instale as dependências**
    ```sh
    npm install
    ```
    
    Ou usando pnpm (recomendado):
    ```sh
    pnpm install
    ```

3. **Execute o projeto**
    ```sh
    npm run dev
    ```
    
    Ou:
    ```sh
    pnpm dev
    ```

#### URLs de Acesso Local

*   **Frontend**: http://localhost:3000
*   **Backend**: http://localhost:8080

### Comandos Úteis

#### Docker

```sh
# Parar os containers
docker-compose down

# Ver logs em tempo real
docker-compose logs -f

# Rebuild dos containers
docker-compose up --build

# Executar apenas um serviço
docker-compose up backend
docker-compose up frontend
```

#### Desenvolvimento Local

```sh
# Backend - Executar testes
cd backend && mvn test

# Backend - Limpar e compilar
cd backend && mvn clean compile

# Frontend - Build de produção
cd frontend && npm run build

# Frontend - Executar testes
cd frontend && npm test

# Frontend - Linting
cd frontend && npm run lint
```

### Estrutura de Portas

| Serviço  | Porta Local | Porta Docker | Descrição |
|----------|-------------|--------------|-----------|
| Frontend | 3000        | 3000         | Interface do usuário |
| Backend  | 8080        | 8080         | API REST |
| Database | 5432        | 5432         | PostgreSQL |

### Solução de Problemas

#### Erro de Conexão com Banco

*   Verifique se as variáveis de ambiente estão corretas no arquivo `.env`
*   Confirme se o banco de dados Azure está acessível
*   Teste a conectividade: `telnet seu-host 5432`
*   Verifique se o firewall do Azure permite conexões externas

#### Erro de Porta em Uso

```sh
# Verificar qual processo está usando a porta
lsof -i :8080  # ou :3000

# Parar containers que podem estar rodando
docker-compose down

# Matar processo específico
kill -9 <PID>
```

#### Cache de Dependências

```sh
# Limpar cache Maven
cd backend && mvn clean

# Limpar cache NPM
cd frontend && npm ci

# Limpar cache pnpm
cd frontend && pnpm install --frozen-lockfile

# Remover node_modules e reinstalar
cd frontend && rm -rf node_modules && npm install
```

#### Problemas com Docker

```sh
# Limpar containers parados
docker system prune

# Rebuild forçando sem cache
docker-compose build --no-cache

# Ver logs detalhados de um serviço
docker-compose logs backend
docker-compose logs frontend
```

✨ Funcionalidades
-----------------

### Para Clientes

*   ✅ Registro e autenticação
    
*   ✅ Visualização de veículos disponíveis
    
*   ✅ Solicitação de aluguel
    
*   ✅ Acompanhamento de pedidos
    
*   ✅ Edição de perfil
    

### Para Agentes de Empresa

*   ✅ Gestão de veículos (CRUD completo)
    
*   ✅ Visualização de solicitações
    
*   ✅ Aprovação/rejeição de pedidos
    
*   ✅ Acompanhamento de contratos
    

### Para Agentes de Banco

*   ✅ Todas as funcionalidades de Agente Empresa
    
*   ✅ Gestão de limite de crédito
    
*   ✅ Criação de contratos de crédito
    
*   ✅ Análise financeira de clientes
    
*   ✅ Aprovação baseada em crédito disponível
    

📊 Diagramas
------------

### Diagrama de Casos de Uso

![Diagrama de Casos de Uso](Documentação/Diagrama%20de%20casos%20de%20uso.pdf)

**Localização**: `Documentação/Diagrama de casos de uso.pdf`

### Diagrama de Classes UML

![Diagrama de Classes](Documentação/Diagrama%20de%20classes%20UML.pdf)

**Localização**: `Documentação/Diagrama de classes UML.pdf`

### Diagrama de Componentes

![Diagrama de Componentes](Documentação/Diagrama%20de%20Componentes.jpeg)

**Localização**: `Documentação/Diagrama de Componentes.jpeg`

### Diagrama de Implantação

![Diagrama de Implantação](Documentação/Diagrama%20de%20Implantação.jpg)

**Localização**: `Documentação/Diagrama de Implantação.pdf`

### Diagrama de Pacotes

![Diagrama de Pacotes](Documentação/Diagrama%20de%20Pacotes.jpeg)

**Localização**: `Documentação/Diagrama de Pacotes.jpeg`

### Histórias de Usuário

![Histórias de Usuário](Documentação/Histórias%20de%20Usuário.pdf)

**Localização**: `Documentação/Histórias de Usuário.pdf`

🎬 Demonstração
---------------

### Fluxo de Cadastro e Login

_\[Espaço reservado para GIF demonstrando cadastro e login\]_

### Solicitação de Aluguel (Cliente)

_\[Espaço reservado para GIF demonstrando solicitação de aluguel\]_

### Aprovação de Pedido (Agente)

_\[Espaço reservado para GIF demonstrando aprovação de pedido\]_

### Gestão de Veículos

_\[Espaço reservado para GIF demonstrando gestão de veículos\]_

### Gestão de Crédito

_\[Espaço reservado para GIF demonstrando gestão de crédito\]_

🔒 Segurança
------------

O sistema implementa diversas medidas de segurança:

*   **JWT Authentication**: Tokens seguros para autenticação
    
*   **Password Hashing**: Senhas criptografadas com BCrypt
    
*   **CORS Configuration**: Controle de origens permitidas
    
*   **Role-Based Access Control**: Autorização baseada em perfis
    
*   **SQL Injection Prevention**: Uso de JPA/Hibernate com prepared statements
    
*   **HTTPS Support**: Suporte para conexões seguras
    

📝 Licença
----------

Este projeto foi desenvolvido para fins educacionais como parte da disciplina de Laboratório de Desenvolvimento de Software do curso de Engenharia de Software da PUC Minas.

👨‍🏫 Professor
---------------

**Prof. Dr. João Paulo Carneiro Aramuni**

*   Disciplina: Laboratório de Desenvolvimento de Software
    
*   Instituição: PUC Minas - Engenharia de Software
    

📞 Contato
----------

Para dúvidas ou sugestões sobre o projeto, entre em contato com a equipe de desenvolvimento através dos canais da universidade.

🙏 Agradecimentos
-----------------

Agradecemos ao Prof. Dr. João Paulo Aramuni pela orientação durante o desenvolvimento deste projeto, e à PUC Minas por proporcionar o ambiente e recursos necessários para o aprendizado prático de desenvolvimento de software.

**Desenvolvido com ❤️ pela equipe de Engenharia de Software da PUC Minas**