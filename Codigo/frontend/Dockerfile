# Multi-stage build para desenvolvimento com hot reload
FROM node:18-alpine AS base

# Instalar dependências necessárias
RUN apk add --no-cache libc6-compat

WORKDIR /app

# Copiar arquivos de dependências
COPY package*.json ./
COPY pnpm-lock.yaml* ./

# Instalar dependências
RUN npm install -g pnpm && pnpm install --frozen-lockfile

# Copiar código fonte
COPY . .

# Expor porta
EXPOSE 3000

# Configurar variáveis de ambiente para desenvolvimento
ENV NODE_ENV=development
ENV NEXT_PUBLIC_API_BASE_URL=http://localhost:8080/api

# Comando para desenvolvimento com hot reload
CMD ["pnpm", "run", "dev"]

# Stage para produção (opcional)
FROM node:18-alpine AS production

RUN apk add --no-cache libc6-compat

WORKDIR /app

# Copiar arquivos de dependências
COPY package*.json ./
COPY pnpm-lock.yaml* ./

# Instalar dependências de produção
RUN npm install -g pnpm && pnpm install --frozen-lockfile --prod

# Copiar código fonte
COPY . .

# Build da aplicação
RUN pnpm run build

# Expor porta
EXPOSE 3000

# Configurar variáveis de ambiente para produção
ENV NODE_ENV=production
ENV NEXT_PUBLIC_API_BASE_URL=http://backend:8080/api

# Comando para produção
CMD ["pnpm", "run", "start"]
