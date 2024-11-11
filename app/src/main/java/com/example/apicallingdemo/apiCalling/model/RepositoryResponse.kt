package com.example.apicallingdemo.apiCalling.model

data class Repository(
    val id: Int,
    val node_id: String,
    val name: String,
    val full_name: String,
    val html_url: String,
    val description: String?,
    val stargazers_count: Int,
    val watchers_count: Int,
    val forks_count: Int,
    val watchers: Int,
    var isExpanded: Boolean = false,
    val owner: Owner
)

data class Owner(
    val login: String,
    val avatar_url: String
)

data class RepositoryResponse(
    val items: List<Repository>
)
