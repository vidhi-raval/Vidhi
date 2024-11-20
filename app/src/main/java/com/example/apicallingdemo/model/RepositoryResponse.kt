package com.example.apicallingdemo.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity (tableName = "Repository")
data class Repository(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "full_name")
    val full_name: String,

    @ColumnInfo(name = "contributors_url")
    val contributors_url: String,

    @ColumnInfo(name = "description")
    val description: String?,

    @ColumnInfo(name = "stargazers_count")
    val stargazers_count: Int,

    @ColumnInfo(name = "watchers_count")
    val watchers_count: Int,

    var isExpanded: Boolean = false,

    @ColumnInfo(name = "owner")
    val owner: Owner,

    val lastFetched: Long
)

data class Owner(
    @ColumnInfo(name = "login")
    val login: String,
    @ColumnInfo(name = "avatar_url")
    val avatar_url: String
)

data class RepositoryResponse(
    val items: List<Repository>
)

@Entity(tableName = "contributors")
data class Contributors(
    @PrimaryKey val id: Int,
    val repoId: Int,
    @ColumnInfo(name = "avatar_url")
    val avatar_url: String
)
