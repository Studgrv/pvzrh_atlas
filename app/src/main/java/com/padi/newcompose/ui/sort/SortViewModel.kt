package com.padi.newcompose.ui.sort

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.padi.newcompose.api.AtlasApi
import com.padi.newcompose.api.service.GeneralRetrofit
import com.padi.newcompose.ui.screens.list.ListData
import com.padi.newcompose.ui.screens.list.ListViewModel.State
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jsoup.Jsoup


data class TabData(val title: String)

data class ObjectData(val name: String, val imgUrl: String, val jumpUrl: String)

@HiltViewModel
class SortViewModel @Inject constructor(
    private val application: Application,
) : ViewModel() {
    data class State(
        val plantTabList: List<TabData> = emptyList(),
        val zombiesTabList: List<TabData> = emptyList(),
        val zombiesList: List<List<ObjectData>> = emptyList(),
        val plantList: List<List<ObjectData>> = emptyList(),

        val isLoading: Boolean = false,
        val error: String? = null
    )

    private val _state = MutableStateFlow(State())
    var state = _state.asStateFlow()

    fun getAllData() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            runCatching {
                val url = AtlasApi.BASE_URL
                val call = GeneralRetrofit.instance.request(url).await()
                val str = call.string()
                val doc = Jsoup.parse(str)
                val col = doc.selectFirst(".col-sm-9")
                val biliBox = col?.select(".BILIBILI-BOX")
                val plantTabList = mutableListOf<TabData>()
                val zombiesTabList = mutableListOf<TabData>()
                biliBox?.forEachIndexed { index, element ->
                    val items = element.select(".tab-panel")
                    items.forEach { item ->
                        val title = item.text()
                        if (index == 0) {
                            plantTabList.add(TabData(title))
                        } else if (index == 1) {
                            zombiesTabList.add(TabData(title))
                        }
                    }

                }

                val container = col?.select(".resp-tabs-container")
                val plantList = mutableListOf<List<ObjectData>>()
                val zombiesList = mutableListOf<List<ObjectData>>()

                container?.forEachIndexed { index, element ->
                    val items = element.select(".resp-tab-content")
                    items.forEach { item ->
                        val images = item.select("img")
                        val dataList = mutableListOf<ObjectData>()
                        images.forEach { img ->
                            val jumpUrl =
                                "https://wiki.biligame.com${img.parent()?.attr("href") ?: ""}"
                            val name = img.attr("alt")
                            val urlImg = img.attr("src")
                            dataList.add(ObjectData(name, urlImg, jumpUrl))
                        }
                        if (index == 0) {
                            plantList.add(dataList)
                        } else if (index == 1) {
                            zombiesList.add(dataList)
                        }
                    }
                }


                _state.update {
                    it.copy(
                        plantTabList = plantTabList,
                        zombiesTabList = zombiesTabList,
                        plantList = plantList,
                        zombiesList = zombiesList,
                        isLoading = false,
                        error = null
                    )
                }
            }.onFailure { e ->
                _state.update {
                    it.copy(
                        isLoading = false, error = e.message
                    )
                }
            }
        }
    }
}

