package br.com.rafaelhop.app_dynamodb.repository

import br.com.rafaelhop.app_dynamodb.entity.PlayerHistory
import io.awspring.cloud.dynamodb.DynamoDbTemplate
import org.springframework.stereotype.Repository
import software.amazon.awssdk.enhanced.dynamodb.Key
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest

@Repository
class PlayerHistoryRepository(
    private val dynamoDbTemplate: DynamoDbTemplate,
) {
    fun save(history: PlayerHistory) {
        dynamoDbTemplate.save(history)
    }

    fun findAllByUserName(userName: String): List<PlayerHistory> {
        val query =
            QueryEnhancedRequest
                .builder()
                .queryConditional(QueryConditional.keyEqualTo { it.partitionValue(userName) })
                .build()

        return dynamoDbTemplate.query(query, PlayerHistory::class.java).items().toList()
    }

    fun findByUserNameAndGameId(
        userName: String,
        gameId: String,
    ): PlayerHistory? =
        dynamoDbTemplate.load(
            Key
                .builder()
                .partitionValue(userName)
                .sortValue(gameId)
                .build(),
            PlayerHistory::class.java,
        )

    fun delete(history: PlayerHistory) {
        dynamoDbTemplate.delete(history)
    }
}
