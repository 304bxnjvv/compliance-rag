# Plan de trabajo — MVP `compliance-rag`

Estado: 🚧 en desarrollo. Stack: Java 17 · Spring Boot 4.1 · Spring AI 2.0 · DeepSeek + Gemini.

## Fase 0 — Setup ✅
- [x] Scaffold Spring Boot + Maven
- [x] Dependencias Spring AI (DeepSeek chat + Gemini embeddings)
- [x] Configuración (`application.yml`, `.env.example`, `run.ps1`)
- [x] Compila (build verde)
- [x] Repo público en GitHub
- [ ] Arranca en localhost con claves reales *(pendiente: requiere tus API keys)*

## Fase 1 — Ingesta de documentos ✅ (TDD)
- [x] `PdfDocumentParser`: extrae texto de PDF por página con metadata (PDFBox)
- [x] `IngestionService`: orquesta parser → vector store, registra documentos
- [x] `VectorStoreConfig`: SimpleVectorStore con embeddings de Gemini
- [x] Endpoints `POST /documents` y `GET /documents`
- [x] 7 tests verdes (servicio, parser, controller)
- **Hecho:** subes un PDF y queda indexado. ✅

## Fase 2 — RAG / preguntas con citas (TDD) 👈 siguiente
- [ ] `RagService`: embedding de la pregunta → top-K → prompt + contexto → DeepSeek
- [ ] Respuesta **con citas** (documento + página)
- [ ] Endpoint `POST /ask`
- [ ] Tests (prompt armado + parseo)
- **Hecho cuando:** preguntas algo y responde fundamentado solo en los docs, citando la fuente.

## Fase 3 — Pulido y presentación
- [ ] Sub-chunking de páginas largas (TokenTextSplitter)
- [ ] Persistir el índice a archivo (no re-indexar al reiniciar)
- [ ] Manejo de errores (`@ControllerAdvice`) + validaciones
- [ ] Swagger / OpenAPI
- [ ] README profesional con diagrama y ejemplos
- **Hecho cuando:** alguien clona, sigue el README y lo corre sin ayuda.

## Fase 4 — pgvector real (post-MVP)
- [ ] Migrar a Supabase (Postgres + pgvector)
- **Hecho cuando:** el índice vive en un vector DB real.
