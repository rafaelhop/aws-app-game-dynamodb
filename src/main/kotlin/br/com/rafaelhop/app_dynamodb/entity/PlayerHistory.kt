package br.com.rafaelhop.app_dynamodb.entity

import br.com.rafaelhop.app_dynamodb.controller.dto.ScoreDto
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey
import java.time.Instant
import java.util.UUID

@DynamoDbBean
data class PlayerHistory(
    @get:DynamoDbPartitionKey
    @get:DynamoDbAttribute("username")
    var userName: String? = null,
    @get:DynamoDbSortKey
    @get:DynamoDbAttribute("game_id")
    var gameId: UUID? = null,
    @get:DynamoDbAttribute("score")
    var score: Double? = null,
    @get:DynamoDbAttribute("created_at")
    var createdAt: Instant? = null,
) {
    companion object {
        fun fromScoreDto(
            userName: String,
            scoreDto: ScoreDto,
        ): PlayerHistory =
            PlayerHistory(
                userName = userName,
                gameId = UUID.randomUUID(),
                score = scoreDto.score,
                createdAt = Instant.now(),
            )
    }
}
