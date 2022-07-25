# scala-slick-rest-postgresql
First Scala REST Api attempt for learning in a week. I used [Akka-Http](https://doc.akka.io/docs/akka-http/current/introduction.html), [Scala Slick](https://scala-slick.org/) as FRM for database connection and [PostgreSQL](https://www.postgresql.org/) with [Docker](https://www.docker.com/). 

## Build With
- [Scala](https://www.scala-lang.org/)
- [Akka](https://akka.io/)
- [Akka-Http](https://doc.akka.io/docs/akka-http/current/introduction.html)
- [Scala Slick](https://scala-slick.org/)
- [Docker](https://www.docker.com/)
- [PostgreSQL](https://www.postgresql.org/)

## Getting Started
### Prerequisites
- [Scala Version 2](https://www.scala-lang.org/)
- [Docker](https://www.docker.com/)
- [Git](https://git-scm.com/)

### Installation

1. Clone the repo and change the directory

```sh
git clone https://github.com/anilsenay/scala-slick-rest-postgresql.git
cd scala-slick-rest-postgresql
```

2. Run Docker Compose to setup and run PostgreSQL in a Docker container

```sh
docker compose up
```

3. When your docker is running and PostgreSQL is ready, compile and run the project

```sh
sbt run
```

### Configuration
#### _Change database credentials_
Change credentials in both `docker-compose.yml` and `src/main/resources/application.conf` files
#### _Change secret key for generating JWT_
Change `secretKey` in `src/main/resources/application.conf` file

## API REST Interface

Download Postman Collection: [GDrive](https://drive.google.com/file/d/1RRv9KwE2ULhnhLJ-hcusnSq7Y4sG6j3I/view?usp=sharing)

_You have to be authorized for access some endpoints by adding user's JWT token as Bearer to **Authorization** key in request header._

**AUTH**
- `POST /api/auth/login`
- `POST /api/auth/register`
- `POST /api/auth/validate`

**USER**
- `GET /api/user`
- `GET /api/user/{user_id}`
- `GET /api/user/{user_id}?address=true`
- `POST /api/user`
- `PUT /api/user/{user_id}`
- `DELETE /api/user/{user_id}`
- `POST /api/user/{user_id}/address`

**ADDRESS**
- `GET /api/address`
- `GET /api/address/{address_id}`
- `POST /api/address`
- `PUT /api/address/{address_id}`
- `DELETE /api/address/{address_id}`

**PRODUCT**
- `GET /api/product`
- `GET /api/product/{product_id}`
- `GET /api/product?sort=asc&category=Shoes&min=1000&max=1000&brand=adidas&sort=new&page=2`
- `POST /api/product`
- `POST /api/product/{product_id}/photo`
- `POST /api/product/{product_id}/size`
- `PUT /api/product/{product_id}`
- `DELETE /api/product/{product_id}`
- `DELETE /api/product/product_id/photo/{photo_index}`
- `DELETE /api/product/product_id/size/{size}`

**CATEGORY**
- `GET /api/category`
- `POST /api/category`
- `PUT /api/category/{category_id}`
- `DELETE /api/category/{category_id}`

**BRAND**
- `GET /api/brand`
- `POST /api/brand`
- `PUT /api/brand/{brand_id}`
- `DELETE /api/brand/{brand_id}`

**ORDER**
- `GET /api/order/{order_id}`
- `GET /api/order/{order_id}?user={user_id}`
- `POST /api/order`
- `PUT /api/order/{order_id}/{status}`
- `PUT /api/order/{order_id}/cancel`
- `DELETE /api/order/{order_id}`

## Future plans

- Applying best practices
- Implement Swagger
- ~~Implement authentication~~
- Implement a cache mechanism
- Better Error messages
- Create same project by using Akka Play framework & use Quill instead of Slick

## License

Distributed under the GPL License. See `LICENSE` for more information.

## Contact

[@anilsenay](https://twitter.com/anilsenay)

Project Link: [https://github.com/anilsenay/scala-slick-rest-postgresql](https://github.com/anilsenay/scala-slick-rest-postgresql)
