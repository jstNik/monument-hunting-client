package com.example.monument_hunting.domain.api_domain

import com.example.monument_hunting.domain.Catalog
import com.example.monument_hunting.domain.Monument
import com.example.monument_hunting.domain.Player
import com.example.monument_hunting.domain.Region
import com.example.monument_hunting.domain.Riddle
import com.example.monument_hunting.domain.Zone
import com.google.gson.annotations.SerializedName

data class ServerData(
    @SerializedName("player")
    var player: _Player = _Player(),
    @SerializedName("player_riddles")
    var playerRiddles: List<_PlayerRiddles> = listOf(),
    @SerializedName("regions")
    var regions: List<_Region> = listOf(),
    @SerializedName("riddles")
    var riddles: List<_Riddle> = listOf(),
    @SerializedName("zones")
    var zones: List<_Zone> = listOf()
){


    fun toCatalog(): Catalog{
        val riddlesCompleted = playerRiddles.map { it.riddleId }
        val riddles = riddles.groupBy({ it.id }, { Riddle(it.id, it.name, it.body, riddlesCompleted.contains(it.id)) })
        val monuments = this.riddles.groupBy(
            { it.monument.zoneId }, {
                Monument(
                    it.monument.id,
                    it.monument.name,
                    it.monument.position,
                    riddles.getOrDefault(it.id, listOf(Riddle())).singleOrNull() ?: Riddle(),
                    it.monument.category
                )
            }
        )
        val zones = zones.groupBy(
            { it.regionId }, {
                Zone(
                    it.id,
                    it.name,
                    monuments.getOrDefault(it.id, listOf(Monument())).singleOrNull() ?: Monument(),
                    it.coordinates.map { c -> c.latLng })
            }
        )
        val regions = regions.map{
            Region(
                it.id,
                it.name,
                it.coordinates.map{ b -> b.latLng },
                zones.getOrDefault(it.id, listOf()),
                it.color
            )
        }
        val player = Player(
            player.id,
            player.username
        )
        val catalog = Catalog(player, regions)
        return catalog
    }

}