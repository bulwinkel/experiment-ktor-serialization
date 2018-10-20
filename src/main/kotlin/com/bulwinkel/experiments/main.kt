package com.bulwinkel.experiments

import io.ktor.client.HttpClient
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.request.get
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.internal.ArrayListSerializer
import kotlin.reflect.KClass
import kotlin.system.exitProcess

@Serializable
data class ChangeStatus(
    val additions: Int,
    val deletions: Int,
    val total: Int
)

@Serializable
data class User(
    val avatar_url: String,
    val events_url: String,
    val followers_url: String,
    val following_url: String,
    val gists_url: String,
    val gravatar_id: String,
    val html_url: String,
    val id: String,
    val login: String,
    val node_id: String,
    val organizations_url: String,
    val received_events_url: String,
    val repos_url: String,
    val site_admin: String,
    val starred_url: String,
    val subscriptions_url: String,
    val type: String,
    val url: String
)

@Serializable
data class Gist(
    val change_status: ChangeStatus,
    val url: String,
    val committed_at: String,
    val user: User,
    val version: String
)

suspend fun main() {
    val gistSerializer = Gist.serializer()
    val li: KSerializer<List<Gist>> = ArrayListSerializer(gistSerializer)

    val client = HttpClient {
        install(JsonFeature) {
            serializer = KotlinxSerializer().apply {
                setMapper(List::class as KClass<List<Gist>>, li)
                setMapper(Gist::class, gistSerializer)
            }
        }
    }

    val gists = client.get<List<Gist>>("https://api.github.com/gists/b1c371d447df071b90fe6e331d8b7d98/commits")
    println("gists = $gists")

    println("all additions = ${gists.fold(0) { acc, gist -> acc + gist.change_status.additions }}")
    exitProcess(0)
}