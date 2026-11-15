package br.com.rafaelhop.app_dynamodb

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(properties = ["app.dynamodb.initialize-table=false"])
class CrudDynamodbApplicationTests {
    @Test
    fun contextLoads() {
    }
}
