# 🍔 Delivery System - Checkpoint Diamante 05

> **FIAP - Java Advanced (2º Semestre)**  
> Sistema de pedidos de delivery distribuído baseado em arquitetura de microsserviços.

---

## 👥 Integrantes da Equipe

| Nome | RM | Turma | GitHub | LinkedIn |
| :--- | :---: | :---: | :--- | :--- |
| **Enzo Okuizumi** | 561432 | 2TDSPG | [EnzoOkuizumiFiap](https://github.com/EnzoOkuizumiFiap) | [Enzo Okuizumi](https://www.linkedin.com/in/enzo-okuizumi-b60292256/) |
| **Lucas Barros Gouveia** | 566422 | 2TDSPG | [LuzBGouveia](https://github.com/LuzBGouveia) | [Lucas Barros Gouveia](https://www.linkedin.com/in/lucas-barros-gouveia-09b147355/) |
| **Milton Marcelino** | 564836 | 2TDSPG | [MiltonMarcelino](https://github.com/MiltonMarcelino) | [Milton Marcelino](http://linkedin.com/in/milton-marcelino-250298142) |

---

## 🏛️ Arquitetura do Sistema

O projeto é estruturado em um monorepo composto por 4 microsserviços Gradle e containers Docker:

* **`eureka-server` (:8761):** Service Registry & Discovery onde todos os serviços se registram dinamicamente.
* **`order-service` (:8080):** Serviço principal. Gerencia o cardápio, reserva de estoque concorrente com Lock Pessimista, Rate Limiting (Bucket4j), produtor de avaliações via RabbitMQ e assistente conversacional via Spring AI.
* **`payment-service` (:8081 / :8082):** Serviço de pagamento simulado rodando em duas instâncias (com falha simulada de ~50%) para demonstrar Load Balancing e Retry com backoff exponencial.
* **`review-service` (:8083):** Consumidor RabbitMQ com técnica de backpressure (buffer concorrente em memória e flush periódico a cada 5s para o banco H2) e ranking dos pratos.
* **`RabbitMQ (Docker)` (:5672 / :15672):** Message Broker rodando a imagem `rabbitmq:4-management`.

```
                    ┌─────────────────────────┐
                    │      eureka-server      │
                    │         (:8761)         │
                    └───────────▲─────────────┘
                                │ Registro & Descoberta
             ┌──────────────────┼──────────────────┐
             │                  │                  │
    ┌────────┴────────┐         │         ┌────────┴────────┐
    │  order-service  │         │         │ review-service  │
    │     (:8080)     │         │         │     (:8083)     │
    └────┬─────────┬──┘         │         └────────▲────────┘
         │         │            │                  │
LB + Retry         │ Publica (AMQP)         Consome (AMQP)
         ▼         │            │                  │
┌────────────────┐ └──────────┐ │ ┌────────────────┴────────┐
│ payment-service│            ▼ ▼ ▼                         │
│ (:8081 / :8082)│       ┌──────────────┐                   │
└────────────────┘       │   RabbitMQ   ├───────────────────┘
                         │   (:5672)    │
                         └──────────────┘
```

---

## 🚀 Como Executar o Projeto

### Pré-requisitos
* **Java 25** instalado e configurado
* **Docker** e **Docker Compose** em execução
* Variável de ambiente com a chave da IA configurada (opcional, para o assistente):
  ```bash
  export GEMINI_API_KEY="sua-chave-aqui"
  # No Windows PowerShell:
  $env:GEMINI_API_KEY="sua-chave-aqui"
  ```

---

### Passo a Passo de Execução

#### 1. Iniciar a Infraestrutura (RabbitMQ)
Na raiz do repositório, execute:
```bash
docker compose up -d
```
> O painel de gerenciamento do RabbitMQ estará disponível em: `http://localhost:15672` (usuário: `guest`, senha: `guest`).

#### 2. Subir o Service Registry (`eureka-server`)
Em um novo terminal:
```bash
cd eureka-server
./gradlew bootRun
```
> Painel do Eureka disponível em: `http://localhost:8761`

#### 3. Subir as duas instâncias do `payment-service`
Abra dois terminais separados para rodar as instâncias:

* **Instância 1 (porta 8081):**
  ```bash
  cd payment-service
  ./gradlew bootRun
  ```
* **Instância 2 (porta 8082):**
  ```bash
  cd payment-service
  ./gradlew bootRun --args='--server.port=8082'
  ```

#### 4. Subir o `order-service`
Em um novo terminal:
```bash
cd order-service
./gradlew bootRun
```

#### 5. Subir o `review-service`
Em um novo terminal:
```bash
cd review-service
./gradlew bootRun
```

---

## 📡 Contrato de Rotas & Endpoints

### `order-service` (:8080)

| Método | Rota | Corpo da Requisição | Status de Resposta | Descrição |
| :--- | :--- | :--- | :--- | :--- |
| **GET** | `/dishes` | — | `200 OK` | Lista todos os pratos do cardápio |
| **GET** | `/dishes/{id}` | — | `200 OK` / `404 Not Found` | Detalhes de um prato específico |
| **POST** | `/orders` | `{"dishId": 1, "quantity": 2}` | `201 Created`<br>`400 Bad Request`<br>`404 Not Found`<br>`409 Conflict`<br>`429 Too Many Requests`<br>`502 Bad Gateway` | Criação de pedido com lock pessimista, rate limit e pagamento |
| **GET** | `/orders/{id}` | — | `200 OK` / `404 Not Found` | Consulta detalhes de um pedido |
| **POST** | `/reviews` | `{"dishId": 1, "rating": 5, "comment": "Ótimo!"}` | `202 Accepted`<br>`400 Bad Request`<br>`404 Not Found` | Publica avaliação assíncrona na fila do RabbitMQ |
| **POST** | `/assistant` | `{"question": "Tem opção vegana?"}` | `200 OK` (`{"answer": "..."}`) | Consulta ao assistente de IA baseado no cardápio |

### `payment-service` (:8081 e :8082) - *Interno*

| Método | Rota | Corpo da Requisição | Respostas | Descrição |
| :--- | :--- | :--- | :--- | :--- |
| **POST** | `/payments` | `{"amount": 79.80}` | `200 OK`: `{"status": "APPROVED", "instance": 8081}`<br>`500 Internal Server Error`: Falha simulada | Chamada interna via Load Balancer |

### `review-service` (:8083)

| Método | Rota | Respostas | Descrição |
| :--- | :--- | :--- | :--- |
| **GET** | `/reviews/ranking` | `200 OK`: `[{"dishId": 1, "dishName": "House Burger", "average": 4.6, "count": 128}]` | Ranking consolidado dos pratos ordenados por média |

---

## 🎯 Padrões e Funcionalidades Implementadas

* **Prevenção de Race Conditions com Lock Pessimista:** Utilização de `@Lock(LockModeType.PESSIMISTIC_WRITE)` na busca do prato no banco H2 com `@Transactional`. Em cenário de 50 requisições simultâneas para um prato com 10 unidades de estoque, exatamente 10 pedidos são confirmados e o estoque finaliza rigorosamente em 0.
* **Resiliência e Load Balancing:** Cliente `PaymentClient` configurado com `RestTemplate` `@LoadBalanced` (chamando `http://PAYMENT-SERVICE/payments`), alternando entre as instâncias 8081 e 8082. Anotado com `@Retryable` configurando backoff exponencial (`delay = 500ms`, `multiplier = 2`, `jitter = 200ms`, `maxRetries = 3`). Se todas as tentativas falharem, a transação sofre rollback automático (estoque permanece intacto) e devolve HTTP `502 Bad Gateway`.
* **Mensageria com RabbitMQ:** Producer em `order-service` publica eventos JSON em `TopicExchange` (`delivery.exchange`) com routing key `reviews.new` para a fila durável `reviews.queue`, respondendo `202 Accepted` sem travar a requisição.
* **Backpressure e Processamento em Lote:** Consumer em `review-service` acumula avaliações em um `ConcurrentHashMap<Long, ReviewAccumulator>` em memória. Um scheduler `@Scheduled(fixedDelay = 5_000)` realiza o flush em lote a cada 5 segundos para a entidade `ReviewSummary` no banco H2, aliviando a carga do banco.
* **Assistente Inteligente (Spring AI):** Implementado com `ChatClient`, prompt engineering com persona de atendente em português, injeção dinâmica do cardápio atualizado do banco e recusa educada para temas fora do escopo do restaurante.
* **Bônus de Rate Limiting (+10 Pontos):** Implementado com **Bucket4j** (Token Bucket) no `order-service`, limitando o endpoint `POST /orders` a no máximo 20 requisições por segundo. Requisições excedentes são barradas com HTTP `429 Too Many Requests` e corpo `{"error": "Rate limit exceeded. Maximum 20 requests per second."}`.
* **Padronização Global de Erros:** Todas as exceções 4xx e 502 são interceptadas pelo `GlobalExceptionHandler`, garantindo o formato estrito exigido pelo App React Native: `{"error": "mensagem"}`.

---

## 🧪 Execução de Testes Automatizados

Para executar os testes unitários e de integração via Gradle:

```bash
# Testes do order-service (incluindo teste do Rate Limiter Bucket4j)
cd order-service
./gradlew test
```
