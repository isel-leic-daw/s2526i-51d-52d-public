# Command line reference

## Build images

* `./gradlew buildImageJvm` - builds the JVM image with agenda backend HTTP API
* `./gradlew buildImagePostgres` - builds the Postgres image
* `./gradlew buildImageUbuntu` - builds an image with Ubuntu and the DIG DNS tool
* `./gradlew buildImageAll` - builds all images

## Start and stop services

* `./gradlew allUp` - starts all services.
* `./gradlew allDown` - stops all services.

## Ubuntu

* `docker exec -ti ubuntu bash` - open shell on contained running Ubuntu.
* `dig agenda-jvm` - uses `dig` to resolve the addresses for the `agenda-jvm` hostname.


