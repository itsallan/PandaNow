package io.dala.pandanow.data.source


import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.dala.pandanow.domain.models.Playlist
import io.dala.pandanow.domain.models.VideoHistoryItem
import java.util.UUID

interface PlaylistDataSource {
    fun createPlaylist(name: String, videos: List<VideoHistoryItem>): Playlist
    fun getPlaylists(): List<Playlist>
    fun getPlaylistById(id: String): Playlist?
    fun deletePlaylist(id: String)
    fun updateVideos(playlistId: String, newVideos: List<VideoHistoryItem>)
}

class PlaylistLocalDataSource(
    context: Context,
    private val gson: Gson
) : PlaylistDataSource {

    private val playlistPreferences: SharedPreferences = context.getSharedPreferences("Playlists", Context.MODE_PRIVATE)
    private val PLAYLISTS_KEY = "playlist_list"

    private fun savePlaylistList(list: List<Playlist>) {
        val json = gson.toJson(list)
        playlistPreferences.edit().putString(PLAYLISTS_KEY, json).apply()
    }

    override fun getPlaylists(): List<Playlist> {
        val json = playlistPreferences.getString(PLAYLISTS_KEY, null)
        return if (json != null) {
            val type = object : TypeToken<List<Playlist>>() {}.type
            gson.fromJson(json, type) ?: emptyList()
        } else {
            emptyList()
        }
    }

    override fun getPlaylistById(id: String): Playlist? {
        return getPlaylists().firstOrNull { it.id == id }
    }

    override fun createPlaylist(name: String, videos: List<VideoHistoryItem>): Playlist {
        val newPlaylist = Playlist(
            id = UUID.randomUUID().toString(),
            name = name,
            videos = videos,
            createdTimestamp = System.currentTimeMillis()
        )
        val list = getPlaylists().toMutableList()
        list.add(0, newPlaylist)
        savePlaylistList(list)
        return newPlaylist
    }

    override fun deletePlaylist(id: String) {
        val list = getPlaylists().toMutableList()
        list.removeAll { it.id == id }
        savePlaylistList(list)
    }

    override fun updateVideos(playlistId: String, newVideos: List<VideoHistoryItem>) {
        val list = getPlaylists().toMutableList()
        val index = list.indexOfFirst { it.id == playlistId }

        if (index != -1) {
            list[index] = list[index].copy(videos = newVideos)
            savePlaylistList(list)
        }
    }
}