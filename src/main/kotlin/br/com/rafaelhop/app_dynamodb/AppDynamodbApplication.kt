package br.com.rafaelhop.app_dynamodb

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class CrudDynamodbApplication

fun main(args: Array<String>) {
    runApplication<CrudDynamodbApplication>(*args)
}
