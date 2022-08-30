package jr.brian.myapplication.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import jr.brian.myapplication.model.remote.MyColorResponse
import jr.brian.myapplication.model.repository.Repository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val repository: Repository) : ViewModel() {
    val flowResponse = MutableStateFlow<MyColorResponse?>(null)

    val colorsLiveData = MutableLiveData<MyColorResponse>()
    var compositeDisposable: CompositeDisposable = CompositeDisposable()

    fun getColors(color: String, numOfColors: Int) = viewModelScope.launch {
        flowResponse.emit(repository.getColors(color, numOfColors))
    }

    fun getColorsRx(color: String, numOfColors: Int) {
        repository.getColorsRx(color, numOfColors)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { colorsLiveData.postValue(it) },
                { Log.i("TAG", it.message ?: "error") })
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }
}