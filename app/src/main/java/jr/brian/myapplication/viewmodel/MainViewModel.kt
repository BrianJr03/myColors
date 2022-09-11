package jr.brian.myapplication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jr.brian.myapplication.data.model.remote.MyColorResponse
import jr.brian.myapplication.data.model.repository.Repository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val repository: Repository) : ViewModel() {
    val flowResponse = MutableStateFlow<MyColorResponse?>(null)
    val loading = MutableStateFlow(false)

    fun getColors(color: String, numOfColors: Int) = viewModelScope.launch {
        loading.emit(true)
        flowResponse.apply {
            value = null
            emit(repository.getColors(color, numOfColors))
        }
        loading.emit(false)
    }
}