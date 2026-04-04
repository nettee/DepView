# AGENTS.md

## Purpose
- This repository is a Java 8 Maven application that analyzes Java project dependencies and writes a static D3 visualization into `output/`.
- Main package: `me.nettee.depview`.
- Prefer small, targeted changes that preserve the existing structure and coding style.

## Repository layout
- `src/main/java/me/nettee/depview/main/`: entrypoints, config loading, orchestration, output generation.
- `src/main/java/me/nettee/depview/ast/`: AST parsing and visitors.
- `src/main/java/me/nettee/depview/model/`: dependency graph and domain model types.
- `src/main/java/me/nettee/depview/utils/`: filesystem and helper utilities.
- `src/main/resources/`: static assets copied into generated output.
- `src/test/java/`: JUnit tests.
- `conf-example/`: sample config files used to run the app.
- `bin/depview.py`: helper wrapper for `mvn spring-boot:run`.
- `bin/serve.py`: helper for serving generated output locally.
- `target/`, `output/`, and `log/` are generated artifacts; do not commit them.

## Environment expectations
- Use Java 8.
- Use Maven.
- The packaged artifact name is `target/depview-0.2-SNAPSHOT.jar`.
- Some tests assume local paths exist on the current machine; avoid rewriting them unless the task requires it.

## Build, run, and test commands

### Install / setup
- No separate install step beyond having Java 8 and Maven available.

### Build
- Full package build:
  - `mvn clean package`
- Faster build without tests:
  - `mvn -DskipTests package`

### Run
- Run packaged jar:
  - `java -jar target/depview-0.2-SNAPSHOT.jar <CONF_FILE>`
- Example:
  - `java -jar target/depview-0.2-SNAPSHOT.jar conf-example/depview-mac.conf`
- Run through Spring Boot Maven plugin:
  - `mvn spring-boot:run -Dspring-boot.run.arguments=<CONF_FILE>`
- Run through helper script:
  - `python3 bin/depview.py <CONF_FILE>`

### Test
- Run all tests:
  - `mvn test`
- Run a single test class:
  - `mvn -Dtest=FileFinderTest test`
- Run a single test method:
  - `mvn -Dtest=FileFinderTest#find_javaFile test`
- Verified in this repository:
  - `mvn -Dtest=FileFinderTest test`

### Lint / format / typecheck
- No dedicated lint, formatter, or separate typecheck configuration was found.
- There is no Checkstyle, Spotless, PMD, ErrorProne, or formatter config in the repo.
- Treat `mvn test` and `mvn package` as the primary verification commands.

### Output preview
- After generating output, serve it locally:
  - `python3 -m http.server 8000 --bind 127.0.0.1 -d output/<PROJECT_NAME>`
- Or use the helper script:
  - `bin/serve.py output/<PROJECT_NAME>`

## Change guidance for agents
- Keep patches minimal and repository-specific.
- Do not introduce new frameworks, build tools, or formatting systems unless explicitly requested.
- Do not rename packages, public classes, or config keys without checking all references.
- Preserve Java 8 compatibility.
- Prefer incremental edits over broad refactors.
- Avoid changing generated files in `target/` or `output/`.

## Code style

### Formatting
- Use 4-space indentation.
- Keep opening braces on the same line.
- Use blank lines to separate logical sections.
- Match the surrounding file’s spacing and wrapping instead of reformatting unrelated code.
- No repository-wide formatter is enforced, so avoid churn-only formatting changes.

### Imports
- Prefer explicit imports for new code.
- Existing code contains some wildcard imports; do not expand or normalize imports unless touching that area.
- Keep static imports grouped separately from normal imports.
- Maintain the file’s current import ordering style when editing.

### Types and Java usage
- Target Java 8 language features only.
- Prefer concrete, readable types at boundaries and standard collection interfaces (`List`, `Set`, `Map`) in signatures.
- Use `final` for fields and locals when it improves clarity and matches surrounding style.
- Keep generics explicit when they make the code easier to follow.
- Reuse existing libraries already in the project before adding new dependencies.
- Lombok is already used (`@Getter`, `@Setter`, `@NoArgsConstructor`); follow existing patterns where appropriate.

### Naming
- Classes: PascalCase.
- Methods and fields: camelCase.
- Constants / enum values: UPPER_SNAKE_CASE.
- Test methods currently use underscore-style names such as `find_javaFile`; keep existing test naming patterns in touched files.
- Respect the existing package root: `me.nettee.depview`.

### Control flow and structure
- Prefer short, direct methods with clear responsibilities.
- Use static factory methods where the existing code already does so.
- Prefer early returns for invalid states.
- Keep orchestration in `main/`, model logic in `model/`, and helpers in `utils/`.
- Avoid mixing unrelated concerns in the same class.

### Error handling
- Follow the local style: invalid input often throws `IllegalArgumentException`; invalid state often throws `IllegalStateException` or uses Guava preconditions.
- Prefer explicit failures over silently swallowing problems.
- If you touch code that currently uses `e.printStackTrace()`, only replace it when the task justifies the change and the new behavior is consistent with surrounding logging.
- Preserve user-visible behavior unless the task is specifically about error handling.

### Logging
- Use SLF4J (`Logger` / `LoggerFactory`) for application logging.
- Prefer parameterized logging over string concatenation.
- Keep log levels consistent with nearby code.

### Collections and streams
- Streams are used selectively; use them when they simplify the code.
- Do not force stream rewrites for imperative code that is already clear.
- Prefer straightforward collection construction and transformation.

### Tests
- Tests use JUnit 4 and AssertJ.
- Prefer adding or updating focused unit tests when changing behavior.
- Keep tests deterministic and local when possible.
- Be aware that the existing `FileFinderTest` relies on machine-specific paths; do not assume all environments mirror those paths unless verified.

## Configuration and runtime assumptions
- Runtime config is loaded from a HOCON file via Typesafe Config.
- Use `conf-example/` as the starting point for sample or manual test configs.
- The application writes generated assets under `output/<sanitized-project-name>/`.
- Static web assets are copied from `src/main/resources` into the generated output directory.

## Dependency guidance
- Existing key libraries include Spring Boot 2.1.7, Guava, Apache Commons Lang, Gson, Lombok, Eclipse JDT, and Typesafe Config.
- Do not upgrade dependencies or plugin versions unless requested.
- If a new dependency seems necessary, prefer discussing it first.

## Cursor / Copilot rules
- No `.cursor/rules/` directory was found.
- No `.cursorrules` file was found.
- No `.github/copilot-instructions.md` file was found.
- Therefore, this AGENTS.md is the primary agent instruction file in the repository.

## Suggested agent workflow
- First read `README.md` and `pom.xml` before making build or runtime changes.
- If changing execution flow, inspect `DepViewRunner`, `DepView`, and related model classes together.
- If changing file discovery behavior, inspect both `FileFinder` and `FileFinderTest`.
- Run the smallest relevant Maven test command first, then broader verification if needed.
- Summarize any inferred behavior in your final note when the repo does not define it explicitly.
