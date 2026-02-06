# Conjunction Assessment Service

A Spring Boot microservice for evaluating satellite conjunction risks and computing collision probabilities between space objects.

## Overview

This service provides a REST API for Space Situational Awareness (SSA) operations, specifically focused on conjunction assessments. It evaluates potential collision risks between a primary space object (e.g., an active satellite) and a secondary object (e.g., space debris or another satellite).

The service performs both synchronous and asynchronous collision probability computations using orbit propagation and covariance analysis simulations. It includes an automated routine screening scheduler that continuously monitors active satellites against all tracked space objects.

SSA architectures are traditionally pipeline-oriented (ingest → propagate → assess → disseminate)

## Technology Stack

* **Java 25** - Programming language
* **Spring Boot 4.0.2** - Application framework
* **Spring Web MVC** - REST API
* **Spring Data JPA** - Database access and ORM
* **Spring Scheduling** - Automated routine screening tasks
* **Lombok** - Code generation (builders, getters, setters, logging)
* **PostgreSQL** - Relational database with full persistence
* **SLF4J** - Logging framework
* **Gradle** - Build tool

## Key Features

1. **Automated Routine Screening**: Scheduled background task that continuously assesses all active satellites against all tracked space objects
2. **Space Object Management**: Comprehensive catalog of space objects with orbital parameters, operational status, and ephemeris data
3. **Flexible Assessment Retrieval**: Query assessments by primary object ID, with optional filtering by secondary object ID
4. **Dual Processing Modes**: Both synchronous and asynchronous collision probability computation
5. **PostgreSQL Persistence**: Full database persistence with indexed queries for optimal performance
6. **Profile-Based Configuration**: Development and production profiles with different execution strategies
7. **Manual Trigger Support**: Admin endpoint to manually trigger routine screening (dev profile)
8. **Status Tracking**: Monitor assessment lifecycle (PENDING, COMPLETED, FAILED)
9. **Thread-Safe Processing**: Concurrent and parallel processing with configurable time steps and priority levels

## Architecture
### Data Model

```
ConjunctionAssessment {
    Long id
    Long primaryObjectId (references SpaceObject.id)
    Long secondaryObjectId (references SpaceObject.id)
    AssessmentStatus status (PENDING | COMPLETED | FAILED)
    Double collisionProbability
    LocalDateTime requestedAt
    LocalDateTime completedAt
    Integer priorityLevel
    LocalDateTime windowStart
    LocalDateTime windowEnd
    Integer timeStepMinutes
}
```

## API Endpoints
### Submit Assessment

```http
POST /api/v1/assessments?primary={primaryObj}&secondary={secondaryObj}
```

* Creates a new conjunction assessment between two space objects.
* **Parameters:**
  * `primary` - ID of the primary (protected) space object
  * `secondary` - ID of the secondary (encounter) space object
* **Response:** `201 Created` with Location header pointing to the created resource

### Get All Assessments
```http
GET /api/v1/assessments
```
* Returns a list of all assessment IDs.
* **Response:** `200 OK` with JSON array of assessment IDs

### Get Assessment by
```http
GET /api/v1/assessments/{id}
```
* Retrieves a specific assessment with its current status and collision probability.
* **Response:** `200 OK` with ConjunctionAssessment JSON object

### Background Processing
The service includes a **RoutineScreeningScheduler** that:
* Runs automatically every 5 seconds (in non-dev profiles)
* Identifies all active satellites (operationalStatus = ACTIVE)
* Creates conjunction assessments between each active satellite and all other space objects
* Processes assessments in parallel for optimal performance
* Can be manually triggered via admin endpoint in dev mode

## Prerequisites
* Java 25 or higher
* Gradle (or use included wrapper)
* PostgreSQL (for future persistence)

## Setup & Running
### Build the project

```bash
./gradlew build
```

### Run the application
```bash
./gradlew bootRun
```
The service will start on the default Spring Boot port (8080).

## Usage Examples
### Submit a new assessment

```bash
curl -X POST "http://localhost:8080/api/v1/assessments?primary=SAT-001&secondary=DEBRIS-456" -i
```

### List all assessments

```bash
curl http://localhost:8080/api/v1/assessments
```

### Get specific assessment

```bash
curl http://localhost:8080/api/v1/assessments/123456789
```

Example response:
```json
{
"id": 123456789,
"primaryObjectId": "SAT-001",
"secondaryObjectId": "DEBRIS-456",
"status": "COMPLETED",
"collisionProbability": 0.000023456
}
```

## Current Limitations

* In-memory storage using ConcurrentHashMap (data is lost on restart)
* Collision probability computation is simulated (not real orbital mechanics)
* PostgreSQL dependency configured but not yet utilized
* No authentication or authorization
