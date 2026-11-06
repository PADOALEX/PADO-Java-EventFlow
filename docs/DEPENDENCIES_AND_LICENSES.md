# Dependencies and license metadata

Source license: MIT, in `LICENSE`. This independent Java learning backend is conceptually based on the EventFlow domain and public HTTP contract. The original .NET repository and its attribution remain intact. No .NET source is included in this repository.

The parent POM is Spring Boot 4.0.9. Direct dependencies are the Spring MVC, Spring Data JPA, Bean Validation and Flyway starters, Flyway PostgreSQL support, the PostgreSQL JDBC driver and Spring Boot Test. `DEPENDENCY_TREE.txt` contains resolved versions, including transitive and test dependencies. `THIRD_PARTY_LICENSES.txt` was regenerated with `org.codehaus.mojo:license-maven-plugin:2.5.0:add-third-party` and contains metadata for 111 dependencies.

The runtime upgrade replaces vulnerable Spring 6, Jackson 2, Tomcat, JDBC and Log4j versions reported by the OSV audit. The resolved Spring 7.0.10 / Jackson 3.1.7 / Tomcat 11.0.27 dependency graph passed the real HTTP/PostgreSQL tests. CI audits all 111 resolved coordinates and fails on reported advisories; this is a scan-time result rather than a claim of complete security.

Spring/Jackson/Flyway/HikariCP and many support libraries list Apache-2.0; PostgreSQL JDBC lists BSD-2-Clause. Hibernate ORM lists LGPL-2.1; Logback lists dual EPL-2.0/LGPL-2.1-only choices in its POM metadata. JUnit lists EPL-2.0 and several Jakarta APIs list EPL/EDL alternatives. Preserve the exact component-specific terms and notices in the generated inventory; MIT for this project's code does not relicense third-party libraries.

Temurin/OpenJDK (GPLv2 with Classpath Exception), Maven (Apache-2.0), PostgreSQL (PostgreSQL License), the optional official container images and GitHub Actions are external build/runtime tooling with their own distribution notices. The source package includes none of the downloaded tool archives or Maven cache. Container builds are prepared but NOT RUN locally.

This is a checked dependency/POM-license inventory, not a comprehensive legal or vulnerability audit. Before redistributing a packaged JAR or image, retain applicable license/NOTICE files from the dependency distributions and re-evaluate any dependency changes. No claim of certification is made.
