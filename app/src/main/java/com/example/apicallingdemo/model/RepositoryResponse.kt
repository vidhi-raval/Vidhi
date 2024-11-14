package com.example.apicallingdemo.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity (tableName = "Repository")
data class Repository(
    @PrimaryKey val id: Int,
    val node_id: String,
    val name: String,
    val full_name: String,
    val html_url: String,
    val contributors_url: String,
    val description: String?,
    val stargazers_count: Int,
    val watchers_count: Int,
    val forks_count: Int,
    val watchers: Int,
    var isExpanded: Boolean = false,
    val owner: Owner,
    val lastFetched: Long
)

data class Owner(
    val login: String,
    val avatar_url: String
)

data class RepositoryResponse(
    val items: List<Repository>
)

data class Contributor(
    val id: Int,
    val avatar_url: String,
)
