# 🛡️ compliance-rag — Asistente de Normativa con IA (RAG)

Asistente backend que responde preguntas en lenguaje natural sobre documentos normativos
(regulaciones, políticas, manuales), **citando la fuente exacta** y basándose únicamente
en los documentos cargados — sin inventar.

Construido con **Java 17 + Spring Boot 4 + Spring AI**, aplicando el patrón **RAG**
(Retrieval-Augmented Generation).

> Proyecto de portafolio enfocado en backend + integración de IA en producción.

## ✨ Qué hace

1. Cargas documentos (PDF) → el sistema los divide, genera *embeddings* y los indexa.
2. Haces una pregunta → busca los fragmentos más relevantes y se los pasa al LLM como contexto.
3. Responde fundamentado **con citas** (documento y página de origen).

## 🧱 Arquitectura

```
PDF → [Ingesta] → chunking → embeddings (Gemini) → Vector Store
                                                        |
Pregunta → embedding → busqueda top-K → prompt + contexto → LLM (DeepSeek) → Respuesta + citas
```

| Capa | Responsabilidad |
|------|-----------------|
| `IngestionService` | Parsea PDF, divide en fragmentos, genera embeddings, indexa |
| `RagService` | Recupera el contexto relevante y genera la respuesta con citas |
| Controllers REST | Exponen `/documents` y `/ask` |

## 🛠️ Stack

- **Java 17**, **Spring Boot 4.1**, **Maven**
- **Spring AI 2.0** (orquestación de LLM, embeddings y vector store)
- **DeepSeek** (`deepseek-chat`) como LLM de chat
- **Google Gemini** (`text-embedding-004`) para embeddings (free tier)
- Vector store **en memoria** (diseñado para migrar a pgvector sin tocar la lógica)

## 🚀 Cómo correrlo

1. Requisitos: **Java 17** y **Maven**.
2. Configura tus claves:
   ```bash
   cp .env.example .env
   ```
   Edita `.env` con tu `DEEPSEEK_API_KEY` y `GEMINI_API_KEY`
   (esta última gratis en https://aistudio.google.com/app/apikey).
3. Arranca:
   ```powershell
   ./run.ps1
   ```
   La API queda en `http://localhost:8080`.

## 📡 Endpoints

| Método | Ruta | Descripción |
|--------|------|-------------|
| `POST` | `/documents` | Sube e indexa un PDF |
| `GET`  | `/documents` | Lista documentos indexados |
| `POST` | `/ask` | Pregunta y recibe respuesta con citas |

## 📌 Estado

🚧 En desarrollo — ver [docs/PLAN.md](docs/PLAN.md) para el roadmap por fases.
