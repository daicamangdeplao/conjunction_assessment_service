# Conjunction Assessment Service

A Spring Boot microservice for evaluating satellite conjunction risks and computing collision probabilities between space objects.

## Overview

This service provides a REST API for Space Situational Awareness (SSA) operations, specifically focused on conjunction assessments. It evaluates potential collision risks between a primary space object (e.g., an active satellite) and a secondary object (e.g., space debris or another satellite).

The service performs asynchronous collision probability computations using orbit propagation and covariance analysis simulations.

## Technology Stack

* **Java 25** - Programming language
* **Spring Boot 4.0.2** - Application framework
* **Spring Web MVC** - REST API
* **Lombok** - Code generation (builders, getters, setters)
* **PostgreSQL** - Database (configured but not yet implemented)
* **Gradle** - Build tool

## Key Features

1. Submit conjunction assessments for two space objects
2. Asynchronous collision probability computation
3. Track assessment status (PENDING, RUNNING, COMPLETED, FAILED)
4. Retrieve individual assessments or list all assessments
5. Thread-safe concurrent processing

## Architecture
### Data Model

```
ConjunctionAssessment {
    Long id
    String primaryObjectId
    String secondaryObjectId
    AssessmentStatus status
    BigDecimal collisionProbability
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
