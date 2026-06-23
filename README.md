# 🛡️ compliance-rag — Asistente de Normativa con IA (RAG)

![Java](https://img.shields.io/badge/Java-17-orange) ![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.1-green) ![Spring AI](https://img.shields.io/badge/Spring%20AI-2.0-brightgreen) ![Tests](https://img.shields.io/badge/tests-11%20passing-success)

Asistente backend que responde preguntas en lenguaje natural sobre documentos
(regulaciones, políticas, manuales) **citando la fuente exacta** y basándose
**únicamente** en los documentos cargados — sin inventar.

Aplica el patrón **RAG** (Retrieval-Augmented Generation) con **Java 17 + Spring
Boot 4 + Spring AI**, usando **DeepSeek** como LLM y **Google Gemini** para los
embeddings. El vector store es en memoria (diseñado para migrar a pgvector sin
tocar la lógica).

---

## 🎬 Demo

```bash
# 1) Subir un documento (PDF)
curl -F "file=@norma.pdf" http://localhost:8080/documents          # -> 201 Created

# 2) Preguntar sobre él
curl -X POST http://localhost:8080/ask \
     -H "Content-Type: application/json" \
     -d '{"question":"¿Qué exige la norma sobre datos personales?"}'
```

Respuesta:

```json
{
  "answer": "La norma exige cifrar los datos personales sensibles del cliente, tanto en tránsito como en reposo...",
  "citations": [ { "source": "norma.pdf", "page": 4 } ]
}
```

---

## 🧱 Arquitectura

```
                         ┌──────────────── INGESTA ────────────────┐
   PDF  ──>  PdfDocumentParser ──>  fragmentos + metadata  ──>  VectorStore
            (PDFBox, por página)     (source, page)            (embeddings Gemini)
                                                                     │
                         ┌──────────────── CONSULTA ───────────────┐ │
   Pregunta ──> embedding ──> similarity search (top-K) ──> Prompt + contexto
                                                                     │
                                                          DeepSeek (LLM)
                                                                     │
                                                   Respuesta + citas (fuente, página)
```

| Componente | Responsabilidad |
|-----------|-----------------|
| `PdfDocumentParser` | Extrae texto del PDF por página, con metadata de origen |
| `IngestionService` | Orquesta parseo → indexado, registra documentos |
| `VectorStoreConfig` | Bean del vector store + modelo de embeddings |
| `RagService` | Recupera contexto, arma el prompt, llama al LLM, devuelve citas |
| `DocumentController` / `AskController` | Endpoints REST |

---

## 🛠️ Stack

- **Java 17**, **Spring Boot 4.1**, **Maven**
- **Spring AI 2.0** — orquestación de LLM, embeddings y vector store
- **DeepSeek** (`deepseek-chat`) — LLM de respuestas
- **Google Gemini** (`gemini-embedding-001`) — embeddings
- **Apache PDFBox 3** — lectura de PDFs
- **JUnit 5 + Mockito + AssertJ** — tests (TDD)

---

## ✅ Requisitos

- **Java 17** (`java -version`)
- **Maven** (`mvn -version`)
- Una **API key de DeepSeek** y una **API key de Google Gemini** (ambas con plan gratuito o de bajo costo)

---

## ⚙️ Configuración

1. Copia la plantilla de variables de entorno:
   ```bash
   cp .env.example .env
   ```
2. Edita `.env` y completa tus dos claves:
   ```env
   DEEPSEEK_API_KEY=sk-...        # https://platform.deepseek.com
   GEMINI_API_KEY=...             # https://aistudio.google.com/app/apikey
   ```
   > El archivo `.env` está en `.gitignore`: **nunca se sube al repositorio**.

---

## ▶️ Cómo ejecutarlo

**Windows (PowerShell)** — carga el `.env` y arranca:
```powershell
./run.ps1
```

**Manual (cualquier SO):**
```bash
# exporta las variables y arranca
export DEEPSEEK_API_KEY=sk-...
export GEMINI_API_KEY=...
mvn spring-boot:run
```

La API queda disponible en **http://localhost:8080**.

📖 **Documentación interactiva (Swagger UI):** http://localhost:8080/swagger-ui/index.html

---

## 📡 Uso de la API

### `POST /documents` — Subir e indexar un PDF
```bash
curl -F "file=@mi-documento.pdf" http://localhost:8080/documents
```
Respuesta: `201 Created`

### `GET /documents` — Listar documentos indexados
```bash
curl http://localhost:8080/documents
```
```json
["norma.pdf", "politica-privacidad.pdf"]
```

### `POST /ask` — Preguntar con respuesta citada
```bash
curl -X POST http://localhost:8080/ask \
     -H "Content-Type: application/json" \
     -d '{"question":"¿Cuánto tiempo se conservan los datos?"}'
```
```json
{
  "answer": "Los datos se conservan por un máximo de 5 años...",
  "citations": [ { "source": "norma.pdf", "page": 2 } ]
}
```

---

## 🗂️ Estructura del proyecto

```
src/main/java/cl/brodriguez/compliancerag/
├── ingestion/   PdfDocumentParser · IngestionService · DocumentParser
├── rag/         RagService · RagAnswer · Citation
├── web/         DocumentController · AskController · AskRequest
└── config/      VectorStoreConfig
```

---

## 🧪 Tests

```bash
mvn test
```
**11 tests** (parser, servicio de ingesta, RAG y controllers), escritos con **TDD**
(RED → GREEN → REFACTOR). Las claves de las APIs no son necesarias para correr los
tests (usan mocks).

---

## 🧯 Troubleshooting

- **`model ... is not found for embedContent`**: tu cuenta de Gemini puede exponer
  un modelo de embeddings distinto. Lista los disponibles con
  `GET https://generativelanguage.googleapis.com/v1beta/models?key=TU_KEY` y ajusta
  `spring.ai.google.genai.embedding.text.model` en `application.yml`.
- **`Connection refused` al consultar `/ask`**: asegúrate de haber subido al menos un
  documento con `POST /documents` antes de preguntar.

---

## 🗺️ Roadmap

- [x] Fase 1 — Ingesta de PDFs (parseo, embeddings, indexado)
- [x] Fase 2 — RAG con citas (`/ask`)
- [ ] Fase 3 — Manejo de errores, Swagger/OpenAPI, persistencia del índice
- [ ] Fase 4 — Migración a **pgvector** (Postgres) como vector store real

Ver [docs/PLAN.md](docs/PLAN.md) para el detalle.

---

> Proyecto de portafolio — backend Java + integración de IA (LLM, embeddings, RAG).
