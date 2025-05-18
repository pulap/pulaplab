# Auth

Auth is an experimental authentication and authorization system, originally developed as a playground while exploring how challenging it would be to implement such features in Clojure. The project began as part of an early prototype for Pulap, a real estate manager, but has since evolved into its own standalone lab. 

Although the main Pulap application will be implemented using a different tech stack, working with Clojure again has proven to be a rewarding experience. The initial effort invested in building an authentication and authorization system was especially gratifying, and the plan is to continue developing and refining this system incrementally.

> **Note:** The repository was previously named `pulaplab`. GitHub should automatically redirect operations from the old location.

> **Namespace Note:** The codebase still reflects its origins, but as the project evolves, expect the structure and naming (such as namespaces) to better match its current focus.

## Getting Started

1. Install [Clojure CLI tools](https://clojure.org/guides/getting_started).
2. Run the app:
   ```bash
   clj -M:run
   ```

## Running Tests

To run the tests:
```bash
clojure -M:test
```