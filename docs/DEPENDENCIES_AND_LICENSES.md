# Dependencies and license metadata

Source license: MIT, in `LICENSE`. This independent Java learning backend is conceptually based on the EventFlow domain and public HTTP contract. The original .NET repository and its attribution remain intact. No .NET source is included in this repository.

The parent POM is Spring Boot 3.5.16. Direct dependencies are Spring Web, Spring Data JPA, Bean Validation, Flyway core/PostgreSQL support, the PostgreSQL JDBC driver and Spring Boot Test. `DEPENDENCY_TREE.txt` contains the resolved versions, including transitive and test dependencies. `THIRD_PARTY_LICENSES.txt` was generated with `org.codehaus.mojo:license-maven-plugin:2.5.0:add-third-party` and contains metadata for 94 dependencies. No unlabelled dependency was reported.

Spring/Jackson/Flyway/HikariCP and many support libraries list Apache-2.0; PostgreSQL JDBC lists BSD-2-Clause. Hibernate ORM lists LGPL-2.1; Logback lists dual EPL-2.0/LGPL-2.1-only choices in its POM metadata. JUnit lists EPL-2.0 and several Jakarta APIs list EPL/EDL alternatives. Preserve the exact component-specific terms and notices in the generated inventory; MIT for this project's code does not relicense third-party libraries.

Temurin/OpenJDK (GPLv2 with Classpath Exception), Maven (Apache-2.0), PostgreSQL (PostgreSQL License), the optional official container images and GitHub Actions are external build/runtime tooling with their own distribution notices. The source package includes none of the downloaded tool archives or Maven cache. Container builds are prepared but NOT RUN locally.

This is a checked dependency/POM-license inventory, not a comprehensive legal or vulnerability audit. Before redistributing a packaged JAR or image, retain applicable license/NOTICE files from the dependency distributions and re-evaluate any dependency changes. No claim of certification is made.
