package com.padi.newcompose.ui.screens.list

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.padi.newcompose.api.AtlasApi
import com.padi.newcompose.api.service.GeneralRetrofit
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jsoup.Jsoup
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@HiltViewModel
class ListViewModel @Inject constructor(
    private val application: Application,
    ) : ViewModel() {
    data class State(
        val list: List<ListData> = emptyList(),
        val isLoading: Boolean = false,
        val error: String? = null
    )

    private val _state = MutableStateFlow(State())
    val state: StateFlow<State> = _state.asStateFlow()
    fun getAtlasList(type: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            runCatching {
                val url =
                    AtlasApi.BASE_URL + URLEncoder.encode(type, StandardCharsets.UTF_8.toString())
                val call = GeneralRetrofit.instance.request(url).await()
                val str = call.string()
                val doc = Jsoup.parse(str)
                val cardSelectTr = doc.select("#CardSelectTr")
                val tr = cardSelectTr.select("tr")
                val list = mutableListOf<ListData>()
                tr.forEach { item ->
                    val imgElement = item.selectFirst("img")
                    val imgUrl = imgElement?.attr("src")
                    val a = item.selectFirst("a")
                    val title = a?.attr("title")
                    val jumpUrl = a?.attr("href")
                    if (title != null && imgUrl != null && jumpUrl != null) {
                        list.add(ListData(title, imgUrl, "https://wiki.biligame.com$jumpUrl"))
                    }
                }
                _state.update {
                    it.copy(
                        list = list, isLoading = false, error = null
                    )
                }
            }.onFailure { e ->
                println(e.message)
                _state.update {
                    it.copy(
                        isLoading = false, error = e.message
                    )
                }
            }
        }
    }
}

data class ListData(
    val title: String, val imgUrl: String, val jumpUrl: String
)