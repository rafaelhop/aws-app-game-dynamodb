package br.com.rafaelhop.app_dynamodb.controller

import br.com.rafaelhop.app_dynamodb.controller.dto.ScoreDto
import br.com.rafaelhop.app_dynamodb.entity.PlayerHistory
import io.awspring.cloud.dynamodb.DynamoDbTemplate
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import software.amazon.awssdk.enhanced.dynamodb.Key
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
    ): ResponseEntity<Void> {
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
        val history = loadHistory(userName, gameId)

        return history?.let { ResponseEntity.ok(it) } ?: ResponseEntity.notFound().build()
    }

    @PutMapping("/{userName}/games/{gameId}")
    fun update(
        @PathVariable("userName") userName: String,
        @PathVariable("gameId") gameId: String,
        @RequestBody scoreDto: ScoreDto,
    ): ResponseEntity<Void> {
        val history = loadHistory(userName, gameId) ?: return ResponseEntity.notFound().build()

        history.score = scoreDto.score
        dynamoDbTemplate.save(history)
        return ResponseEntity.noContent().build()
    }

    @DeleteMapping("/{userName}/games/{gameId}")
    fun delete(
        @PathVariable("userName") userName: String,
        @PathVariable("gameId") gameId: String,
    ): ResponseEntity<Void> {
        val history = loadHistory(userName, gameId) ?: return ResponseEntity.notFound().build()

        dynamoDbTemplate.delete(history)
        return ResponseEntity.noContent().build()
    }

    private fun loadHistory(
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
}
