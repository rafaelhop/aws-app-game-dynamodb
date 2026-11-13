package br.com.rafaelhop.app_dynamodb.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import java.net.URI

@Configuration
class DynamodbConfig {
    @Bean
    fun dynamodbClient(): DynamoDbClient =
        DynamoDbClient
            .builder()
            .region(Region.SA_EAST_1)
            .credentialsProvider(
                StaticCredentialsProvider.create(
                    AwsBasicCredentials.create("test", "test"),
                ),
            ).endpointOverride(URI.create("http://localhost:4566"))
            .build()
}
