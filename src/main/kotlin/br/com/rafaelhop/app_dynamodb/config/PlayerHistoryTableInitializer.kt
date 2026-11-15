package br.com.rafaelhop.app_dynamodb.config

import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.AttributeDefinition
import software.amazon.awssdk.services.dynamodb.model.BillingMode
import software.amazon.awssdk.services.dynamodb.model.CreateTableRequest
import software.amazon.awssdk.services.dynamodb.model.DescribeTableRequest
import software.amazon.awssdk.services.dynamodb.model.KeySchemaElement
import software.amazon.awssdk.services.dynamodb.model.KeyType
import software.amazon.awssdk.services.dynamodb.model.ResourceInUseException
import software.amazon.awssdk.services.dynamodb.model.ResourceNotFoundException
import software.amazon.awssdk.services.dynamodb.model.ScalarAttributeType

@Component
@ConditionalOnProperty(
    prefix = "app.dynamodb",
    name = ["initialize-table"],
    havingValue = "true",
    matchIfMissing = true,
)
class PlayerHistoryTableInitializer(
    private val dynamoDbClient: DynamoDbClient,
) : ApplicationRunner {
    override fun run(args: ApplicationArguments) {
        val tableExists =
            try {
                dynamoDbClient.describeTable(
                    DescribeTableRequest.builder().tableName(TABLE_NAME).build(),
                )
                true
            } catch (_: ResourceNotFoundException) {
                false
            }

        if (!tableExists) {
            try {
                dynamoDbClient.createTable(createTableRequest())
            } catch (_: ResourceInUseException) {
                // Another application instance created the table after the describe call.
            }
        }

        dynamoDbClient
            .waiter()
            .waitUntilTableExists(DescribeTableRequest.builder().tableName(TABLE_NAME).build())
    }

    private fun createTableRequest(): CreateTableRequest =
        CreateTableRequest
            .builder()
            .tableName(TABLE_NAME)
            .keySchema(
                KeySchemaElement
                    .builder()
                    .attributeName("username")
                    .keyType(KeyType.HASH)
                    .build(),
                KeySchemaElement
                    .builder()
                    .attributeName("game_id")
                    .keyType(KeyType.RANGE)
                    .build(),
            ).attributeDefinitions(
                AttributeDefinition
                    .builder()
                    .attributeName("username")
                    .attributeType(ScalarAttributeType.S)
                    .build(),
                AttributeDefinition
                    .builder()
                    .attributeName("game_id")
                    .attributeType(ScalarAttributeType.S)
                    .build(),
            ).billingMode(BillingMode.PAY_PER_REQUEST)
            .build()

    private companion object {
        const val TABLE_NAME = "player_history"
    }
}
