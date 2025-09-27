# 🚀 Guia de Configuração - Car Rental System

Este guia te ajudará a configurar o projeto **Car Rental System** em qualquer sistema operacional.

## 📋 Pré-requisitos

- **Git** instalado
- **Docker** e **Docker Compose** instalados
- **Node.js 18+** (apenas se quiser rodar sem Docker)
- **Java 17+** (apenas se quiser rodar sem Docker)

---

## 🐧 **LINUX (Ubuntu/Debian)**

### **1. Instalar Docker:**
```bash
# Atualizar sistema
sudo apt update

# Instalar Docker
sudo apt install -y docker.io docker-compose

# Iniciar Docker
sudo systemctl start docker
sudo systemctl enable docker

# Verificar instalação
docker --version
docker-compose --version
```

### **2. Configurar usuário Docker:**
```bash
# Criar grupo docker (se não existir)
sudo groupadd docker

# Adicionar usuário ao grupo
sudo usermod -aG docker $USER

# Aplicar mudanças
newgrp docker

# Testar sem sudo
docker run hello-world
```

### **3. Clonar e configurar projeto:**
```bash
# Clonar repositório
git clone <seu-repositorio>
cd CarRentalSystem/Codigo

# Dar permissões ao Maven Wrapper
chmod +x backend/mvnw

# Executar setup automático
./setup-docker.sh
```

### **4. Iniciar aplicação:**
```bash
# Usar Makefile (recomendado)
make quick-start

# Ou Docker Compose direto
docker-compose -f docker-compose.dev.yml up --build
```

---

## 🪟 **WINDOWS**

### **1. Instalar Docker Desktop:**
1. Baixe o [Docker Desktop for Windows](https://www.docker.com/products/docker-desktop/)
2. Execute o instalador
3. Reinicie o computador
4. Abra o Docker Desktop e aguarde inicializar

### **2. Instalar Git (se não tiver):**
1. Baixe o [Git for Windows](https://git-scm.com/download/win)
2. Execute o instalador com as opções padrão

### **3. Configurar projeto:**
```powershell
# Abrir PowerShell ou CMD como Administrador

# Clonar repositório
git clone <seu-repositorio>
cd CarRentalSystem\Codigo

# Verificar Docker
docker --version
docker-compose --version
```

### **4. Iniciar aplicação:**
```powershell
# Usar Makefile (se tiver make instalado)
make quick-start

# Ou Docker Compose direto
docker-compose -f docker-compose.dev.yml up --build
```

### **5. Alternativa sem Makefile:**
```powershell
# Criar script PowerShell equivalente
docker-compose -f docker-compose.dev.yml up --build -d
Start-Sleep 30
curl http://localhost:8080/api/test/public
curl http://localhost:3000
```

---

## 🍎 **macOS**

### **1. Instalar Docker Desktop:**
```bash
# Via Homebrew (recomendado)
brew install --cask docker

# Ou baixe manualmente:
# https://www.docker.com/products/docker-desktop/
```

### **2. Instalar Homebrew (se não tiver):**
```bash
/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"
```

### **3. Configurar projeto:**
```bash
# Clonar repositório
git clone <seu-repositorio>
cd CarRentalSystem/Codigo

# Dar permissões
chmod +x backend/mvnw

# Verificar Docker
docker --version
docker-compose --version
```

### **4. Iniciar aplicação:**
```bash
# Usar Makefile
make quick-start

# Ou Docker Compose direto
docker-compose -f docker-compose.dev.yml up --build
```

---

## 🔧 **Configuração Manual (Sem Docker)**

Se preferir rodar sem Docker, siga estes passos:

### **1. Backend (Spring Boot):**
```bash
# Instalar Java 17+
# Ubuntu/Debian:
sudo apt install openjdk-17-jdk

# macOS:
brew install openjdk@17

# Windows: Baixar do Oracle ou usar Chocolatey
choco install openjdk17

# Configurar JAVA_HOME
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64  # Linux
export JAVA_HOME=/opt/homebrew/opt/openjdk@17        # macOS
set JAVA_HOME=C:\Program Files\Java\jdk-17          # Windows

# Executar backend
cd backend
./mvnw spring-boot:run
```

### **2. Frontend (Next.js):**
```bash
# Instalar Node.js 18+
# Ubuntu/Debian:
curl -fsSL https://deb.nodesource.com/setup_18.x | sudo -E bash -
sudo apt-get install -y nodejs

# macOS:
brew install node

# Windows: Baixar do nodejs.org

# Executar frontend
cd frontend
npm install
npm run dev
```

---

## 🌐 **URLs da Aplicação**

Após iniciar com sucesso, acesse:

- **Frontend**: http://localhost:3000
- **Backend API**: http://localhost:8080
- **API Test**: http://localhost:8080/api/test/public
- **H2 Console**: http://localhost:8080/h2-console (desenvolvimento)

---

## 🛠️ **Comandos Úteis**

### **Docker:**
```bash
# Ver status dos containers
docker-compose ps

# Ver logs
docker-compose logs -f

# Parar tudo
docker-compose down

# Limpar tudo
docker-compose down --volumes --remove-orphans
docker system prune -f
```

### **Makefile (Linux/macOS):**
```bash
make help           # Ver todos os comandos
make dev            # Desenvolvimento
make prod           # Produção
make logs           # Ver logs
make clean          # Limpar containers
make health         # Verificar saúde dos serviços
```

---

## 🔍 **Troubleshooting**

### **Problemas Comuns:**

#### **1. Docker não funciona sem sudo (Linux):**
```bash
sudo usermod -aG docker $USER
newgrp docker
# Ou faça logout/login
```

#### **2. Porta já em uso:**
```bash
# Linux/macOS:
sudo lsof -i :3000
sudo lsof -i :8080

# Windows:
netstat -ano | findstr :3000
netstat -ano | findstr :8080
```

#### **3. Permissão negada no mvnw (Linux/macOS):**
```bash
chmod +x backend/mvnw
```

#### **4. Docker Desktop não inicia (Windows/macOS):**
- Verifique se a virtualização está habilitada no BIOS
- Reinicie o Docker Desktop
- Verifique se não há antivírus bloqueando

#### **5. Erro de memória (Docker):**
```bash
# Aumentar memória no Docker Desktop
# Settings > Resources > Memory: 4GB+
```

---

## 📱 **Comandos Rápidos por Sistema**

### **Linux:**
```bash
sudo apt update && sudo apt install -y docker.io docker-compose
sudo systemctl start docker
sudo usermod -aG docker $USER && newgrp docker
git clone <repo> && cd CarRentalSystem/Codigo
chmod +x backend/mvnw && make quick-start
```

### **Windows (PowerShell):**
```powershell
# Instalar Docker Desktop manualmente
git clone <repo>
cd CarRentalSystem\Codigo
docker-compose -f docker-compose.dev.yml up --build
```

### **macOS:**
```bash
brew install --cask docker
git clone <repo> && cd CarRentalSystem/Codigo
chmod +x backend/mvnw && make quick-start
```

---

## ✅ **Verificação Final**

Para verificar se tudo está funcionando:

```bash
# 1. Verificar se containers estão rodando
docker-compose ps

# 2. Testar endpoints
curl http://localhost:8080/api/test/public
curl http://localhost:3000

# 3. Acessar aplicação no navegador
# Frontend: http://localhost:3000
# Backend: http://localhost:8080
```

---

## 🆘 **Suporte**

Se encontrar problemas:

1. **Verifique os logs**: `docker-compose logs`
2. **Consulte este guia**: Procure na seção de troubleshooting
3. **Limpe e reinicie**: `make clean && make dev`
4. **Verifique requisitos**: Docker, Git, permissões

---

**🎯 Desenvolvido para funcionar em qualquer sistema operacional!**
