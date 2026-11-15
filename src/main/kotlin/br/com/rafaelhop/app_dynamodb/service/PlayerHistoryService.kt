package br.com.rafaelhop.app_dynamodb.service

import br.com.rafaelhop.app_dynamodb.entity.PlayerHistory
import br.com.rafaelhop.app_dynamodb.repository.PlayerHistoryRepository
import org.springframework.stereotype.Service

@Service
class PlayerHistoryService(
    private val playerHistoryRepository: PlayerHistoryRepository,
) {
    fun save(
        userName: String,
        score: Double,
    ) {
        playerHistoryRepository.save(PlayerHistory.create(userName, score))
    }

    fun list(userName: String): List<PlayerHistory> = playerHistoryRepository.findAllByUserName(userName)

    fun findById(
        userName: String,
        gameId: String,
    ): PlayerHistory? = playerHistoryRepository.findByUserNameAndGameId(userName, gameId)

    fun update(
        userName: String,
        gameId: String,
        score: Double,
    ): Boolean {
        val history = playerHistoryRepository.findByUserNameAndGameId(userName, gameId) ?: return false
        history.score = score
        playerHistoryRepository.save(history)
        return true
    }

    fun delete(
        userName: String,
        gameId: String,
    ): Boolean {
        val history = playerHistoryRepository.findByUserNameAndGameId(userName, gameId) ?: return false
        playerHistoryRepository.delete(history)
        return true
    }
}
