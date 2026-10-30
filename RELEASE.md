# Release process

1. Update the `maven-artifactVersion` in `gradle/libs.versions.toml`.
2. Update the `CHANGELOG.md`.
3. Push the changes to the repo.
4. Run `./gradlew clean`.
5. Run `./gradlew publishAndReleaseToMavenCentral`.
6. Create a GitHub release with the contents of the `CHANGELOG.md`.
