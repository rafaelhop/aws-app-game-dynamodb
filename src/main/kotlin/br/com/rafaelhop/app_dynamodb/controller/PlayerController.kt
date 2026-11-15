package br.com.rafaelhop.app_dynamodb.controller

import br.com.rafaelhop.app_dynamodb.controller.dto.ScoreDto
import br.com.rafaelhop.app_dynamodb.entity.PlayerHistory
import br.com.rafaelhop.app_dynamodb.service.PlayerHistoryService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/players")
class PlayerController(
    private val playerHistoryService: PlayerHistoryService,
) {
    @PostMapping("/{userName}/games")
    fun save(
        @PathVariable("userName") userName: String,
        @RequestBody scoreDto: ScoreDto,
    ): ResponseEntity<Void> {
        playerHistoryService.save(userName, scoreDto.score)
        return ResponseEntity.ok().build()
    }

    @GetMapping("/{userName}/games")
    fun list(
        @PathVariable("userName") userName: String,
    ): ResponseEntity<List<PlayerHistory>> = ResponseEntity.ok(playerHistoryService.list(userName))

    @GetMapping("/{userName}/games/{gameId}")
    fun findById(
        @PathVariable("userName") userName: String,
        @PathVariable("gameId") gameId: String,
    ): ResponseEntity<PlayerHistory> =
        playerHistoryService
            .findById(userName, gameId)
            ?.let { ResponseEntity.ok(it) }
            ?: ResponseEntity.notFound().build()

    @PutMapping("/{userName}/games/{gameId}")
    fun update(
        @PathVariable("userName") userName: String,
        @PathVariable("gameId") gameId: String,
        @RequestBody scoreDto: ScoreDto,
    ): ResponseEntity<Void> =
        if (playerHistoryService.update(userName, gameId, scoreDto.score)) {
            ResponseEntity.noContent().build()
        } else {
            ResponseEntity.notFound().build()
        }

    @DeleteMapping("/{userName}/games/{gameId}")
    fun delete(
        @PathVariable("userName") userName: String,
        @PathVariable("gameId") gameId: String,
    ): ResponseEntity<Void> =
        if (playerHistoryService.delete(userName, gameId)) {
            ResponseEntity.noContent().build()
        } else {
            ResponseEntity.notFound().build()
        }
}
