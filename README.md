# Ktpost

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
  except `common` represents a workflow and contains the related code.
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

If an error does not infer the problem of the program itself, but which should be presented to the user and
let user solve it, it's called a failure. The example of a failure can be the invalid value for the certain field or not found.

It uses `Result<T, F>` to represent possible failure. It is like `Result<T>` in the standard library
except the failure is not limited to `Throwable` but a generics type `F`.
For the errors of the program, it uses exception.

### Invalidation Error Design

The workflow input uses the inline classes defined in `models.kt` which can only by constructed
after validation. The workflow itself only need to focus on valid value.

The invalidation can be caused by user providing an invalid input, wrong implementation of the client
and the invalid value in the persistence.
For example, a required field being missing in the request is an error caused by wrong implementation.
Since no matter what user enter, the client should always put it at the correct field.
An invalid value for a field, on the other hand, is an error caused by user input.

These three kinds of invalidation should be handled differently by client.
For invalid user input, the client should show the user why it's wrong and let user fix it.
For the other two kinds, it should just tell user that something is wrong and let user report this issue to developers.

If the invalidation is caused by wrong implementation, a `400 Bad Request` is responded.
If the invalidation is from the user input, it may respond a `422 Unprocessable Entity`,
or `404 Not Found` for query condition.
If the invalidation is from the databse, it responds a `500 Internal Server Error`.

## What you should NOT learn from this project.

* How it does code generation. It may not be a good way but just works for this sample.
* The authorization. It uses JWT token without refresh token.
* REST API design. I'm not an expert of it so maybe some APIs are not designed according to the standard.
