# Development Environment

## Introduction

This guide ensures all the tools required for the first part of the course are installed and working properly on the student's machines.

## Steps

- Make sure the following tools are installed:
  - [Git client](https://git-scm.com/downloads)
  - Java 21
  - IntelliJ
  - Docker Desktop
  - See [course tooling](https://github.com/isel-leic-daw/s2526i-51d-52d-public/wiki/tools)

- Clone the course repository:
  `git clone git@github.com:isel-leic-daw/s2526i-51d-52d-public.git`

- Move into the project folder:
  `cd s2526i-51d-52d-public`

- Run the `check` task to:
  - Fetch dependencies
  - Compile source code
  - Run tests
  - Check code style

  `./gradlew check`

- Update the course repository:
  `git pull`

- Open the project in IntelliJ:
  - Open `s2526i-51d-52d-public/settings.gradle.kts` as a project

- Run all programs in each module.

- Run all tests in each module.

- Start a PostgreSQL container:
  `docker run --name postgres-container -p 5432:5432 -e POSTGRES_PASSWORD=password -d postgres`

  This command:
  - Names the container `postgres-container`
  - Maps port 5432 to localhost
  - Sets the password to `password`
  - Runs in detached mode
  - Uses the official [Postgres image](https://hub.docker.com/_/postgres)

- Confirm the container is running:
  `docker ps --all`

- Use a PostgreSQL client to connect to the RDBMS

- Open a shell inside the container:
  `docker exec -ti postgres-container bash`

- Run the `psql` client inside the container:
  `psql postgres postgres`

- If you see:
  `postgres=#`
  then you're successfully connected.

Let me know if you'd like this saved as a file or styled for a specific platform like GitHub or Notion!