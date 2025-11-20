# 🧠 Humanamente API

## 📖 Sobre o Projeto

O **Humanamente** é uma plataforma inovadora que utiliza Inteligência Artificial de forma inversa: em vez de substituir o ser humano, ela identifica e preserva as tarefas que devem permanecer essencialmente humanas em cada profissão.

### 🎯 Objetivo

Transformar a IA em uma ferramenta de **empatia e valorização do ser humano**, ajudando pessoas e organizações a redesenharem o trabalho de forma mais humana, justa e sustentável.

### 🔍 Como Funciona

1. **Análise de Cargo**: O usuário insere informações sobre uma profissão ou descrição detalhada das atividades
2. **Classificação Inteligente**: A IA analisa e classifica tarefas em três categorias:

   - 🧑 **HUMAN**: Tarefas que devem permanecer humanas (empatia, criatividade, ética)
   - 🤝 **HYBRID**: Tarefas que podem ser auxiliadas por automação
   - 🤖 **AUTOMATED**: Tarefas que podem ser automatizadas com segurança

3. **Recomendações Personalizadas**: Para tarefas com alto potencial de automação, o sistema:
   - Identifica o nível de impacto (HIGH, MEDIUM, LOW)
   - Recomenda habilidades emergentes (upskilling)
   - Sugere cursos e áreas de requalificação

### 💡 Diferenciais

- **Preservação do Fator Humano**: Valoriza aspectos como empatia, criatividade e julgamento ético
- **Plano de Desenvolvimento**: Não apenas identifica riscos, mas oferece caminhos de evolução
- **Análise Contextual**: Considera a natureza e o contexto de cada profissão
- **Foco em Pessoas**: Prioriza o crescimento profissional e a adaptação ao mercado

---

## 🏗️ Arquitetura do Projeto

```
humanamente-java-api/
│
├── src/
│   ├── main/
│   │   ├── java/com/humanamente/api/
│   │   │   ├── config/              # Configurações (Security, CORS, Auth)
│   │   │   │   ├── AuthFilter.java
│   │   │   │   ├── CorsConfig.java
│   │   │   │   └── SecurityConfig.java
│   │   │   │
│   │   │   ├── controller/          # Endpoints REST
│   │   │   │   ├── AuthController.java
│   │   │   │   ├── UserController.java
│   │   │   │   ├── TaskController.java
│   │   │   │   ├── AnalysisController.java
│   │   │   │   └── ValidationExceptionHandler.java
│   │   │   │
│   │   │   ├── dto/                 # Data Transfer Objects
│   │   │   │   ├── LoginRequest.java
│   │   │   │   ├── RegisterRequest.java
│   │   │   │   ├── TokenDTO.java
│   │   │   │   └── ApiResponse.java
│   │   │   │
│   │   │   ├── model/               # Entidades JPA
│   │   │   │   ├── User.java
│   │   │   │   ├── Analysis.java
│   │   │   │   ├── Task.java
│   │   │   │   └── Recommendation.java
│   │   │   │
│   │   │   ├── repository/          # Repositórios JPA
│   │   │   │   ├── UserRepository.java
│   │   │   │   ├── AnalysisRepository.java
│   │   │   │   ├── TaskRepository.java
│   │   │   │   └── RecommendationRepository.java
│   │   │   │
│   │   │   ├── service/             # Lógica de Negócio
│   │   │   │   ├── AuthService.java
│   │   │   │   ├── UserService.java
│   │   │   │   ├── TaskService.java
│   │   │   │   ├── AnalysisService.java
│   │   │   │   ├── TokenService.java
│   │   │   │   └── MessageService.java
│   │   │   │
│   │   │   └── HumanamenteApiApplication.java
│   │   │
│   │   └── resources/
│   │       ├── db/migration/        # Scripts Flyway
│   │       │   ├── V1__create_tables.sql
│   │       │   └── V2__insert_users.sql
│   │       │
│   │       ├── messages_pt_BR.properties
│   │       ├── messages_en_US.properties
│   │       └── application.properties
│   │
│   └── test/                        # Testes unitários e integração
│
├── Dockerfile                       # Multi-stage build com JRE
├── compose.yaml                     # Docker Compose (PostgreSQL + RabbitMQ)
├── build.gradle                     # Dependências e build
└── README.md
```

---

## 🚀 Tecnologias Utilizadas

### Backend

- **Java 21** - Linguagem de programação
- **Spring Boot** - Framework principal
- **Spring Security** - Autenticação e autorização JWT
- **Spring Data JPA** - ORM para banco de dados
- **PostgreSQL** - Banco de dados relacional
- **Flyway** - Migrations e versionamento de DB

### IA e Análise

- **Groq API** - Modelo de IA (Llama 3.3 70B)
- **Spring AI** - Integração com modelos de linguagem

### Cache e Mensageria

- **Caffeine** - Cache em memória
- **RabbitMQ** - Mensageria assíncrona

### Infraestrutura

- **Docker & Docker Compose** - Containerização
- **Gradle** - Gerenciamento de dependências
- **Eclipse Temurin JRE 21** - Runtime Java

---

## 📦 Instalação e Execução

### Pré-requisitos

- Docker e Docker Compose instalados
- Variáveis de ambiente configuradas

### 1. Clone o repositório

```bash
git clone https://github.com/MUKINH4/humanamente-java-api.git
cd humanamente-java-api
```

### 2. Configure as variáveis de ambiente

Crie um arquivo `.env` na raiz do projeto:

```env
# Groq API
GROQ_API_KEY=sua-chave-groq-api

# JWT
JWT_SECRET=sua-chave-secreta-jwt-super-segura

# Spring Profile
SPRING_PROFILES_ACTIVE=dev
```

### 3. Inicie os containers

```bash
docker-compose up --build
```

A aplicação estará disponível em: `http://localhost:8080`

### 4. Usuários pré-cadastrados

Após a primeira execução, o sistema cria automaticamente:

| Tipo  | Email                 | Senha    | Role  |
| ----- | --------------------- | -------- | ----- |
| Admin | admin@humanamente.com | admin123 | ADMIN |
| User  | user@humanamente.com  | user123  | USER  |

---

## 📡 Endpoints da API

### Autenticação

#### `POST /auth/register`

Registra um novo usuário.

**Request:**

```json
{
  "username": "joao",
  "email": "joao@email.com",
  "password": "senha123",
  "confirmPassword": "senha123"
}
```

**Response:** `201 Created`

```
Usuário criado com sucesso
```

#### `POST /auth/login`

Realiza login e retorna token JWT.

**Request:**

```json
{
  "email": "admin@humanamente.com",
  "password": "admin123"
}
```

**Response:** `200 OK`

```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "username": "admin"
}
```

### Usuários

#### `GET /users`

Lista todos os usuários (requer autenticação).

**Headers:**

```
Authorization: Bearer {token}
```

**Response:** `200 OK`

```json
{
  "message": "Usuários encontrados com sucesso",
  "data": [...]
}
```

#### `GET /users/{id}`

Busca usuário por ID.

#### `PUT /users/{id}`

Atualiza dados do usuário.

#### `DELETE /users/{id}`

Remove usuário.

#### `POST /users/change-password`

Altera senha do usuário autenticado.

### Tarefas

#### `POST /tasks`

Cria uma nova tarefa de análise.

#### `GET /tasks/{id}`

Busca tarefa por ID.

#### `GET /tasks`

Lista todas as tarefas.

#### `GET /tasks/analysis/{analysisId}`

Lista tarefas de uma análise específica.

#### `GET /tasks/classification?classification=HUMAN`

Filtra tarefas por classificação.

#### `PUT /tasks/{id}`

Atualiza tarefa.

#### `DELETE /tasks/{id}`

Remove tarefa.

---

## 🗄️ Modelo de Dados

### Users (Usuários)

```sql
- id (VARCHAR 36, PK)
- username (VARCHAR 100, UNIQUE)
- email (VARCHAR 150, UNIQUE)
- password (VARCHAR 255)
- role (VARCHAR 20) -- ADMIN, USER
```

### Analysis (Análises)

```sql
- id (BIGSERIAL, PK)
- user_id (VARCHAR 36, FK)
- job_title (VARCHAR 200)
- analysis_date (TIMESTAMP)
- overall_score (DOUBLE) -- 0-100
- ai_recommendation (TEXT)
```

### Task (Tarefas)

```sql
- id (BIGSERIAL, PK)
- analysis_id (BIGINT, FK)
- description (TEXT)
- human_core_score (DOUBLE) -- 0-100
- classification (VARCHAR 20) -- HUMAN, HYBRID, AUTOMATED
- reason (TEXT)
```

### Recommendation (Recomendações)

```sql
- id (BIGSERIAL, PK)
- task_id (BIGINT, FK)
- up_skill (VARCHAR 150)
- course_suggestion (VARCHAR 200)
- impact_level (VARCHAR 20) -- HIGH, MEDIUM, LOW
```

---

## 🔐 Segurança

- **JWT Authentication**: Tokens stateless com expiração configurável
- **BCrypt Password Hashing**: Senhas armazenadas com criptografia forte
- **CORS Configurado**: Controle de origem de requisições
- **Spring Security**: Proteção de endpoints e autorização por roles
- **Dockerfile com usuário não-root**: Execução segura de containers

---

## 🌍 Internacionalização

A API suporta múltiplos idiomas através de `messages.properties`:

- 🇧🇷 Português (pt_BR) - Padrão
- 🇺🇸 Inglês (en_US)

Para mudar a língua adicione o parâmetro: <code>?lang=</code> com a língua que deseja mudar na URL da requisição. Ex: <code>https://localhost:8080/auth/login?lang=pt_BR</code>