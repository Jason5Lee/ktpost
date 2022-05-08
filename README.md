# Ktpost REST API

The REST post service using vert.x, based on [the Domain Modeling idea from Scott Wlaschin](https://pragprog.com/titles/swdddf/domain-modeling-made-functional/)
and some of my immature ideas.

## Deployment

* Executes the `generate` task to generate the code that adds route setup for vert.x.
* Setup MySQL (or any compatible database such as MariaDB).
* Execute [setup.sql](./setup.sql) in the database.
* Configure the service based on [Vert.x Config](https://vertx.io/docs/vertx-config/java/). The available fields are
  * `secret`: key for encrypting JWT.
  * `mysql`: MySQL database connection URI.
  * `expireSecs`: The available duration of JWT token, in seconds.
  * `cost`: the cost of BCrypt to store the password.
* Deploy the verticle, e.g. execute the `run` task.

## Code Structure

This project utilizes code-as-documentation. The information contained in the code is as close as possible to the documentation.

* Using [the Vertical Slice Architecture](https://jimmybogard.com/vertical-slice-architecture/). Each package under `me.jason5lee.ktpost`
  except `common` represents a workflow and containing the related code.
* Under each workflow package
    * The file with the workflow name contains the domain model. They represent shared model for both programmers and non-programmers.
    * The file with the name `api.kt` includes the information of the API. This file can serve as API documentation. It includes
      * The API endpoint.
      * Authentication and authorization.
      * How the workflow input is built from the query, which shows the query format.
      * How the response is built from the workflow output, which shows the response format.

      If you are the client side that need to call the API
      or you are writing the API document you can refer to these code.
    * The file `Implementation.kt` includes the implementation details of the workflow, mostly the persistence part.
* Under the `common` package
    * `models.kt` includes the shared domain models.
    * `api.kt` includes the shared information of the API.
    * Some other common utilities.

## Failure Design

If an error does not infer the problem of the service itself, but which should be presented to the user and
let user solve it, it's called a failure. The example of a failure can be the invalid value for the certain field or not found.

We use `Result<T, F>` for failure, and exception for other errors.

### Value Invalid Design

Note: the invalidation talked about below is only for the failure. If an invalidation is caused by the
client wrong implementation instead of the user input (for example missing a field), the design below does not
apply. In that case a `400 Bad Request` will be responded.

The invalid values can be handled differently. Normally we want to respond a `422 Unprocessable Entity`,
but if the value is from the database we may just log and respond
internal error, or sometimes if the value is for finding something we may respond a `404 Not Found`.
