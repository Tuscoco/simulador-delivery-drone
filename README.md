# Simulador de Entregas em Drone

---

## Visão Geral

O simulador-delivery-drone é uma API RESTful que gerencia:
- **Drones**: Frota de entrega com capacidade e autonomia
- **Pedidos**: Pacotes a entregar com peso, prioridade e localização
- **Entregas**: Rotas otimizadas que combinam múltiplos pedidos

### Características Principais

- **Otimização de Rota** - Utilizando a distância de Manhattan 
- **Seleção Inteligente de Drone** - Menor capacidade suficiente
- **Gerenciamento de Estados** - DISPONIVEL - PREPARANDO_ENTREGA - EM_VOO - CARREGANDO
- **Timestamps** - Rastreamento de início e conclusão  

---

## Recursos

### Endpoints Disponíveis

| Resource | Método | Endpoint | Descrição |
|----------|--------|----------|-----------|
| **Drones** | POST | `/drones` | Registrar novo drone |
| | GET | `/drones` | Listar todos os drones |
| | GET | `/drones/{id}` | Buscar drone por ID |
| | PUT | `/drones/{id}` | Atualizar drone |
| | DELETE | `/drones/{id}` | Deletar drone |
| | GET | `/drones/disponiveis` | Listar drones disponíveis |
| | GET | `/drones/capacidade?peso=5.0` | Filtrar por capacidade mínima |
| **Pedidos** | POST | `/pedidos` | Criar novo pedido |
| | GET | `/pedidos` | Listar todos os pedidos |
| | GET | `/pedidos/{id}` | Buscar pedido por ID |
| | GET | `/pedidos/prioridade?prioridade=ALTA` | Filtrar por prioridade |
| | PUT | `/pedidos/{id}` | Atualizar pedido |
| | DELETE | `/pedidos/{id}` | Deletar pedido |
| **Entregas** | POST | `/entregas` | Criar entrega (otimiza rota) |
| | GET | `/entregas` | Listar todas as entregas |
| | GET | `/entregas/{id}` | Buscar entrega por ID |
| | GET | `/entregas/status?status=PREPARANDO` | Filtrar por status |
| | PUT | `/entregas/{id}/iniciar` | Iniciar entrega |
| | PUT | `/entregas/{id}/concluir` | Concluir entrega |
| | DELETE | `/entregas/{id}` | Cancelar entrega |

---

## DRONES

### 1. Registrar Novo Drone

Cria um novo drone na frota.

```http
POST /api/v1/drones
Content-Type: application/json
```

**Requisição:**
```json
{
  "modelo": "DJI Phantom 4 Pro",
  "capacidadeCarga": 5.5,
  "autonomiaVoo": 30.0
}
```

**Parâmetros:**
| Campo | Tipo | Descrição | Exemplo |
|-------|------|-----------|---------|
| `modelo` | string | Nome/modelo do drone | "DJI Phantom 4 Pro" |
| `capacidadeCarga` | double | Capacidade em kg | 5.5 |
| `autonomiaVoo` | double | Autonomia máxima em km | 30.0 |

**Resposta (201 Created):**
```json
{
    "drone": {
        "autonomiaAtual": 30.0,
        "autonomiaVoo": 30.0,
        "capacidadeCarga": 5.5,
        "coordenadaAtual": {
            "x": 0,
            "y": 0
        },
        "entregaAtiva": null,
        "estado": "DISPONIVEL",
        "id": 1,
        "modelo": "DJI Phantom 4"
    }
}
```

**Campos de Resposta:**
| Campo | Tipo | Descrição |
|-------|------|-----------|
| `id` | long | ID único do drone |
| `modelo` | string | Modelo do drone |
| `capacidadeCarga` | double | Capacidade em kg |
| `autonomiaVoo` | double | Autonomia máxima em km |
| `autonomiaAtual` | double | Autonomia restante em km |
| `estado` | enum | Estado atual (DISPONIVEL, PREPARANDO_ENTREGA, EM_VOO, CARREGANDO) |
| `coordenadaAtual` | object | Localização (x, y) |
| `entregaAtiva` | object | Entrega em andamento (null se nenhuma) |

---

### 2. Listar Todos os Drones

```http
GET /api/v1/drones
```

**Resposta (200 OK):**
```json
[
  {
      "drone": {
          "autonomiaAtual": 30.0,
          "autonomiaVoo": 30.0,
          "capacidadeCarga": 5.5,
          "coordenadaAtual": {
              "x": 0,
              "y": 0
          },
          "entregaAtiva": null,
          "estado": "DISPONIVEL",
          "id": 1,
          "modelo": "DJI Phantom 4"
      }
  },
  {
      "drone": {
          "autonomiaAtual": 40.0,
          "autonomiaVoo": 40.0,
          "capacidadeCarga": 10.0,
          "coordenadaAtual": {
              "x": 0,
              "y": 0
          },
          "entregaAtiva": null,
          "estado": "DISPONIVEL",
          "id": 2,
          "modelo": "DJI Phantom 5"
      }
  }
]
```

---

### 3. Buscar Drone por ID

```http
GET /api/v1/drones/1
```

**Resposta (200 OK):**
```json
{
    "drone": {
        "autonomiaAtual": 30.0,
        "autonomiaVoo": 30.0,
        "capacidadeCarga": 5.5,
        "coordenadaAtual": {
            "x": 0,
            "y": 0
        },
        "entregaAtiva": null,
        "estado": "DISPONIVEL",
        "id": 1,
        "modelo": "DJI Phantom 4"
    }
}
```

**Resposta de Erro (404 Not Found):**
```json
{
  "timestamp": "2025-12-12T10:30:45",
  "status": 404,
  "erro": "Drone Nao Encontrado",
  "mensagem": "Drone de id 1 não encontrado!"
}
```

---

### 4. Atualizar Drone

```http
PUT /api/v1/drones/1
Content-Type: application/json
```

**Requisição:**
```json
{
  "modelo": "DJI Phantom 4 Pro Max",
  "capacidadeCarga": 6.0,
  "autonomiaVoo": 35.0
}
```

**Resposta (200 OK):**
```json
{
    "drone": {
        "autonomiaAtual": 30.0,
        "autonomiaVoo": 30.0,
        "capacidadeCarga": 5.5,
        "coordenadaAtual": {
            "x": 0,
            "y": 0
        },
        "entregaAtiva": null,
        "estado": "DISPONIVEL",
        "id": 1,
        "modelo": "DJI Phantom 4"
    }
}
```

**Resposta de Erro (404 Not Found):**
```json
{
  "timestamp": "2025-12-12T10:30:45",
  "status": 404,
  "erro": "Drone Nao Encontrado",
  "mensagem": "Drone de id 1 não encontrado!"
}
```

---

### 5. Deletar Drone

```http
DELETE /api/v1/drones/1
```

**Resposta (204 No Content):**
```
(Sem corpo de resposta)
```

**Resposta de Erro (404 Not Found):**
```json
{
  "timestamp": "2025-12-12T10:30:45",
  "status": 404,
  "erro": "Drone Nao Encontrado",
  "mensagem": "Drone de id 1 não encontrado!"
}
```

---

### 6. Listar Drones Disponíveis

Retorna apenas drones com estado `DISPONIVEL`.

```http
GET /api/v1/drones/disponiveis
```

**Resposta (200 OK):**
```json
[
    {
        "drone": {
            "autonomiaAtual": 30.0,
            "autonomiaVoo": 30.0,
            "capacidadeCarga": 5.0,
            "coordenadaAtual": {
                "x": 0,
                "y": 0
            },
            "estado": "DISPONIVEL",
            "id": 1,
            "modelo": "DJI Phantom 4"
        }
    },
    {
        "drone": {
            "autonomiaAtual": 30.0,
            "autonomiaVoo": 30.0,
            "capacidadeCarga": 10.0,
            "coordenadaAtual": {
                "x": 0,
                "y": 0
            },
            "estado": "DISPONIVEL",
            "id": 2,
            "modelo": "DJI Phantom 4"
        }
    }
]
```

---

### 7. Filtrar Drones por Capacidade

Retorna drones com capacidade >= peso especificado.

```http
GET /api/v1/drones/capacidade?peso=5.0
```

**Parâmetros Query:**
| Parâmetro | Tipo | Descrição |
|-----------|------|-----------|
| `peso` | double | Peso mínimo em kg |

**Resposta (200 OK):**
```json
[
    {
        "drone": {
            "autonomiaAtual": 30.0,
            "autonomiaVoo": 35.0,
            "capacidadeCarga": 11.0,
            "coordenadaAtual": {
                "x": 0,
                "y": 0
            },
            "entregaAtiva": null,
            "estado": "DISPONIVEL",
            "id": 1,
            "modelo": "DJI Phantom 12 Pro"
        }
    }
]
```

---

## PEDIDOS

### 1. Criar Novo Pedido

Cria um novo pedido para entrega.

```http
POST /api/v1/pedidos
Content-Type: application/json
```

**Requisição:**
```json
{
  "pesoPacote": 2.5,
  "prioridade": "ALTA",
  "descricao": "Entrega urgente de documentos",
  "coordenadaDestino": {
    "x": 15,
    "y": 25
  }
}
```

**Parâmetros:**
| Campo | Tipo | Descrição |
|-------|------|-----------|
| `pesoPacote` | double | Peso em kg |
| `prioridade` | enum | ALTA, MEDIA, BAIXA |
| `descricao` | string | Descrição do pacote |
| `coordenadaDestino.x` | int | Coordenada X |
| `coordenadaDestino.y` | int | Coordenada Y |

**Resposta (201 Created):**
```json
{
    "pedido": {
        "coordenadaDestino": {
            "x": 5,
            "y": 10
        },
        "dataPedido": "2025-12-12T07:45:13.293734249",
        "descricao": "Entrega urgente",
        "entrega": null,
        "id": 2,
        "pesoPacote": 2.5,
        "prioridade": "ALTA"
    }
}
```

---

### 2. Listar Todos os Pedidos

```http
GET /api/v1/pedidos
```

**Resposta (200 OK):**
```json
[
    {
        "pedido": {
            "coordenadaDestino": {
                "x": 10,
                "y": 20
            },
            "dataPedido": "2025-12-12T07:09:30.889219",
            "descricao": "Entrega urgente",
            "entrega": null,
            "id": 1,
            "pesoPacote": 2.5,
            "prioridade": "ALTA"
        }
    },
    {
        "pedido": {
            "coordenadaDestino": {
                "x": 20,
                "y": 10
            },
            "dataPedido": "2025-12-12T07:09:53.706821",
            "descricao": "Entrega urgente",
            "entrega": null,
            "id": 2,
            "pesoPacote": 5.0,
            "prioridade": "BAIXA"
        }
    }
]
```

---

### 3. Buscar Pedido por ID

```http
GET /api/v1/pedidos/1
```

**Resposta (200 OK):**
```json
{
    "pedido": {
        "coordenadaDestino": {
            "x": 20,
            "y": 10
        },
        "dataPedido": "2025-12-12T07:09:53.706821",
        "descricao": "Entrega urgente",
        "entrega": null,
        "id": 1,
        "pesoPacote": 5.0,
        "prioridade": "BAIXA"
    }
}
```

**Resposta de Erro (404 Not Found):**
```json
{
    "timestamp": "2025-12-12T09:24:10.716840335",
    "status": 404,
    "erro": "Pedido Nao Encontrado",
    "mensagem": "Pedido de id 1 não encontrado!"
}
```

---

### 4. Filtrar Pedidos por Prioridade

```http
GET /api/v1/pedidos/prioridade?prioridade=ALTA
```

**Parâmetros Query:**
| Parâmetro | Tipo | Descrição | Valores |
|-----------|------|-----------|--------|
| `prioridade` | enum | Filtro de prioridade | ALTA, MEDIA, BAIXA |

**Resposta (200 OK):**
```json
[
    {
        "pedido": {
            "coordenadaDestino": {
                "x": 10,
                "y": 20
            },
            "dataPedido": "2025-12-12T07:09:30.889219",
            "descricao": "Entrega urgente",
            "entrega": null,
            "id": 1,
            "pesoPacote": 2.5,
            "prioridade": "ALTA"
        }
    },
    {
        "pedido": {
            "coordenadaDestino": {
                "x": 20,
                "y": 10
            },
            "dataPedido": "2025-12-12T07:09:53.706821",
            "descricao": "Entrega urgente",
            "entrega": null,
            "id": 2,
            "pesoPacote": 5.0,
            "prioridade": "ALTA"
        }
    }
]
```

---

### 5. Atualizar Pedido

```http
PUT /api/v1/pedidos/1
Content-Type: application/json
```

**Requisição:**
```json
{
  "pesoPacote": 3.0,
  "prioridade": "MEDIA",
  "descricao": "Entrega com prioridade reduzida",
  "coordenadaDestino": {
    "x": 20,
    "y": 30
  }
}
```

**Resposta (200 OK):**
```json
{
    "pedido": {
        "coordenadaDestino": {
            "x": 10,
            "y": 20
        },
        "dataPedido": "2025-12-12T07:09:53.706821",
        "descricao": "Entrega urgente",
        "entrega": null,
        "id": 2,
        "pesoPacote": 2.5,
        "prioridade": "MEDIA"
    }
}
```

**Resposta de Erro (404 Not Found):**
```json
{
    "timestamp": "2025-12-12T09:24:10.716840335",
    "status": 404,
    "erro": "Pedido Nao Encontrado",
    "mensagem": "Pedido de id 1 não encontrado!"
}
```

---

### 6. Deletar Pedido

```http
DELETE /api/v1/pedidos/1
```

**Resposta (204 No Content):**
```
(Sem corpo de resposta)
```

**Resposta de Erro (404 Not Found):**
```json
{
    "timestamp": "2025-12-12T09:24:10.716840335",
    "status": 404,
    "erro": "Pedido Nao Encontrado",
    "mensagem": "Pedido de id 1 não encontrado!"
}
```

---

## ENTREGAS

### 1. Criar Entrega (Otimiza Rota Automaticamente)

Cria uma nova entrega com múltiplos pedidos. O sistema **automaticamente**:
- Seleciona o drone ideal
- Otimiza a rota
- Calcula distância total e tempo estimado
- Define estado como PREPARANDO

```http
POST /api/v1/entregas
Content-Type: application/json
```

**Requisição:**
```json
{
  "pedidoIds": [1, 2, 3]
}
```

**Parâmetros:**
| Campo | Tipo | Descrição |
|-------|------|-----------|
| `pedidoIds` | array[long] | IDs dos pedidos a entregar |

**Resposta (201 Created):**
```json
{
    "id": 1,
    "distanciaTotal": 56.0,
    "tempoEstimado": 1.12,
    "status": "PREPARANDO",
    "dataHoraInicio": null,
    "dataHoraConclusao": null,
    "droneId": 1,
    "droneModelo": "DJI Phantom 4",
    "pedidoIds": [
        1,
        2,
        3
    ]
}
```

**Campos de Resposta:**
| Campo | Tipo | Descrição |
|-------|------|-----------|
| `id` | long | ID único da entrega |
| `distanciaTotal` | double | Distância total em km (Manhattan) |
| `tempoEstimado` | double | Tempo estimado em horas (50 km/h) |
| `status` | enum | Estado (PREPARANDO, ENTREGANDO, CONCLUIDA, RETORNANDO) |
| `dataHoraInicio` | timestamp | Quando iniciou (null se não começou) |
| `dataHoraConclusao` | timestamp | Quando terminou (null se em andamento) |
| `droneId` | long | ID do drone selecionado |
| `droneModelo` | string | Modelo do drone |
| `pedidoIds` | array[long] | IDs dos pedidos em ordem otimizada |

> **Nota:** Os pedidoIds retornados estão na **ordem otimizada** da rota!

---

### 2. Listar Todas as Entregas

```http
GET /api/v1/entregas
```

**Resposta (200 OK):**
```json
[
    {
        "id": 1,
        "distanciaTotal": 30.0,
        "tempoEstimado": 0.6,
        "status": "PREPARANDO",
        "dataHoraInicio": null,
        "dataHoraConclusao": null,
        "droneId": 1,
        "droneModelo": "DJI Phantom 4",
        "pedidoIds": [1, 2, 3]
    },
    {
        "id": 2,
        "distanciaTotal": 40.0,
        "tempoEstimado": 1.6,
        "status": "PREPARANDO",
        "dataHoraInicio": null,
        "dataHoraConclusao": null,
        "droneId": 2,
        "droneModelo": "DJI Phantom 4",
        "pedidoIds": [4, 5, 6]
    }
]
```

---

### 3. Buscar Entrega por ID

```http
GET /api/v1/entregas/1
```

**Resposta (200 OK):**
```json
{
    "id": 1,
    "distanciaTotal": 30.0,
    "tempoEstimado": 0.6,
    "status": "PREPARANDO",
    "dataHoraInicio": null,
    "dataHoraConclusao": null,
    "droneId": 2,
    "droneModelo": "DJI Phantom 4",
    "pedidoIds": [1, 2, 3]
}
```

**Resposta de Erro (404 Not Found):**
```json
{
    "timestamp": "2025-12-12T09:33:08.033613855",
    "status": 404,
    "erro": "Entrega Nao Encontrada",
    "mensagem": "Entrega de id 1 não encontrada!"
}
```

---

### 4. Filtrar Entregas por Status

```http
GET /api/v1/entregas/status?status=PREPARANDO
```

**Parâmetros Query:**
| Parâmetro | Tipo | Descrição | Valores |
|-----------|------|-----------|--------|
| `status` | enum | Filtro de status | PREPARANDO, ENTREGANDO, RETORNANDO, CONCLUIDA |

**Resposta (200 OK):**
```json
[
    {
        "id": 1,
        "distanciaTotal": 30.0,
        "tempoEstimado": 0.6,
        "status": "PREPARANDO",
        "dataHoraInicio": null,
        "dataHoraConclusao": null,
        "droneId": 2,
        "droneModelo": "DJI Phantom 4",
        "pedidoIds": [1, 2, 3]
    }
]
```

---

### 5. Iniciar Entrega

Muda status de `PREPARANDO` → `ENTREGANDO` e drone de `DISPONIVEL` → `EM_VOO`.

```http
PUT /api/v1/entregas/1/iniciar
```

**Resposta (200 OK):**
```json
{
    "id": 1,
    "distanciaTotal": 30.0,
    "tempoEstimado": 0.6,
    "status": "ENTREGANDO",
    "dataHoraInicio": "2025-12-12T07:42:03.117052258",
    "dataHoraConclusao": null,
    "droneId": 1,
    "droneModelo": "DJI Phantom 4",
    "pedidoIds": [1, 2, 3]
}
```

**Estado do Drone após iniciar:**
```
ANTES: DISPONIVEL
DEPOIS: EM_VOO
```

**Resposta de Erro (404 Not Found):**
```json
{
    "timestamp": "2025-12-12T09:33:08.033613855",
    "status": 404,
    "erro": "Entrega Nao Encontrada",
    "mensagem": "Entrega de id 1 não encontrada!"
}
```

---

### 6. Concluir Entrega

Muda status de `ENTREGANDO` → `CONCLUIDA`, drone volta a `DISPONIVEL`.

```http
PUT /api/v1/entregas/1/concluir
```

**Resposta (200 OK):**
```json
{
    "id": 1,
    "distanciaTotal": 30.0,
    "tempoEstimado": 0.6,
    "status": "CONCLUIDA",
    "dataHoraInicio": "2025-12-12T07:42:03.117052",
    "dataHoraConclusao": "2025-12-12T07:42:08.74416025",
    "droneId": 1,
    "droneModelo": "DJI Phantom 4",
    "pedidoIds": [1, 2, 3]
}
```

**Resposta de Erro (404 Not Found):**
```json
{
    "timestamp": "2025-12-12T09:33:08.033613855",
    "status": 404,
    "erro": "Entrega Nao Encontrada",
    "mensagem": "Entrega de id 1 não encontrada!"
}
```

---

### 7. Cancelar Entrega

Só é possível cancelar entregas em status `PREPARANDO`.  
Retorna drone para `DISPONIVEL`.

```http
DELETE /api/v1/entregas/1
```

**Resposta (204 No Content):**
```
(Sem corpo de resposta)
```

**Resposta de Erro (404 Not Found):**
```json
{
    "timestamp": "2025-12-12T09:33:08.033613855",
    "status": 404,
    "erro": "Entrega Nao Encontrada",
    "mensagem": "Entrega de id 1 não encontrada!"
}
```

**Resposta de Erro (400 Bad Request):**
```json
{
  "timestamp": "2025-12-12T11:10:00",
  "status": 400,
  "erro": "Erro de Cancelamento",
  "mensagem": "Apenas entregas em preparação podem ser canceladas!"
}
```

---

## Códigos de Status HTTP

| Código | Significado | Cenários |
|--------|-------------|----------|
| **200** | OK | Requisição bem-sucedida (GET, PUT) |
| **201** | Created | Recurso criado com sucesso (POST) |
| **204** | No Content | Recurso deletado com sucesso (DELETE) |
| **400** | Bad Request | Erro de validação ou lógica de negócio |
| **404** | Not Found | Recurso não encontrado |
| **500** | Internal Server Error | Erro no servidor |

---

## Tratamento de Erros

### Formato Padrão de Erro

```json
{
  "timestamp": "2025-12-12T11:10:00",
  "status": 400,
  "erro": "Tipo de Erro",
  "mensagem": "Descrição detalhada do erro"
}
```

### Erros Comuns

#### 404 - Recurso Não Encontrado
```json
{
  "timestamp": "2025-12-12T11:10:00",
  "status": 404,
  "erro": "Drone Nao Encontrado",
  "mensagem": "Drone de id 999 não encontrado!"
}
```

#### 404 - Pedido Não Encontrado
```json
{
  "timestamp": "2025-12-12T11:10:00",
  "status": 404,
  "erro": "Pedido Nao Encontrado",
  "mensagem": "Pedido de id 999 não encontrado!"
}
```

#### 404 - Entrega Não Encontrada
```json
{
  "timestamp": "2025-12-12T11:10:00",
  "status": 404,
  "erro": "Entrega Nao Encontrada",
  "mensagem": "Entrega de id 999 não encontrada!"
}
```

#### 400 - Nenhum Drone Disponível
```json
{
  "timestamp": "2025-12-12T11:10:00",
  "status": 400,
  "erro": "Nenhum drone disponível",
  "mensagem": "Nenhum drone disponível no momento para a entrega!"
}
```

#### 400 - Cancelamento Inválido
```json
{
  "timestamp": "2025-12-12T11:10:00",
  "status": 400,
  "erro": "Erro de Cancelamento",
  "mensagem": "Apenas entregas em preparação podem ser canceladas!"
}
```

---

## Notas Importantes

### Cálculos de Distância
- Usa **Distância Manhattan**, NÃO Euclidiana
- Apropriada para cidades com ruas em grade (X, Y)
- Mais realista que linha reta

### Seleção de Drone
- Prioriza **menor capacidade suficiente** (eficiência)
- NÃO leva em consideração distância até o pedido

### Autonomia
- Consumida apenas ao **concluir** a entrega

### Timestamps
- `dataHoraInicio`: Preenchida ao iniciar entrega
- `dataHoraConclusao`: Preenchida ao concluir entrega
- `dataPedido`: Preenchida automaticamente ao criar pedido

---

## Dependências e Execução

### Pré-requisitos

Antes de executar o projeto, certifique-se de ter instalado:

| Software | Versão Mínima | Descrição |
|----------|---------------|-----------|
| **Java JDK** | 21+ | Java Development Kit |
| **Maven** | 3.8+ | Gerenciador de dependências |
| **Git** | 2.0+ | Controle de versão |

### Dependências do Projeto

O projeto utiliza as seguintes dependências principais:

#### Spring Framework
- **Spring Boot Starter Web**
- **Spring Boot Starter Data JPA**

#### Banco de Dados
- **H2 Database**
- **Hibernate**

### Como Executar

### 1. Clonar o Repositório
```bash
git clone https://github.com/Tuscoco/simulador-delivery-drone.git
cd simulador-delivery-drone
```

### 2. Compilar o Projeto
```bash
mvn clean install
```

### 3. Executar a Aplicação
```bash
mvn spring-boot:run
```

---

**Documentação Completa - API Simulador de Entregas em Drone**  
Versão 1.0.0 | Dezembro de 2025
