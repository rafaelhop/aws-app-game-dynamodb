package br.com.rafaelhop.app_dynamodb.controller

import br.com.rafaelhop.app_dynamodb.controller.dto.ScoreDto
import br.com.rafaelhop.app_dynamodb.entity.PlayerHistory
import io.awspring.cloud.dynamodb.DynamoDbTemplate
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest

@RestController
@RequestMapping("/v1/players")
class PlayerController(
    private val dynamoDbTemplate: DynamoDbTemplate,
) {
    @PostMapping("/{userName}/games")
    fun save(
        @PathVariable("userName") userName: String,
        @RequestBody scoreDto: ScoreDto,
    ): ResponseEntity<Any> {
        val playerHistory = PlayerHistory.fromScoreDto(userName, scoreDto)
        dynamoDbTemplate.save(playerHistory)
        return ResponseEntity.ok().build()
    }

    @GetMapping("/{userName}/games")
    fun list(
        @PathVariable("userName") userName: String,
    ): ResponseEntity<List<PlayerHistory>> {
        val condition = QueryConditional.keyEqualTo { it.partitionValue(userName) }
        val query =
            QueryEnhancedRequest
                .builder()
                .queryConditional(condition)
                .build()

        val history = dynamoDbTemplate.query(query, PlayerHistory::class.java)
        return ResponseEntity.ok(history.items().toList())
    }

    @GetMapping("/{userName}/games/{gameId}")
    fun findById(
        @PathVariable("userName") userName: String,
        @PathVariable("gameId") gameId: String,
    ): ResponseEntity<PlayerHistory> {
        val condition = QueryConditional.keyEqualTo { it.partitionValue(userName).sortValue(gameId) }
        val query =
            QueryEnhancedRequest
                .builder()
                .queryConditional(condition)
                .build()

        val history = dynamoDbTemplate.query(query, PlayerHistory::class.java)
        return ResponseEntity.ok(history.items().first())
    }
}
