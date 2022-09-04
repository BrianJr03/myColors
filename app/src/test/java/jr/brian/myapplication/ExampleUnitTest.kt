package jr.brian.myapplication

import androidx.compose.runtime.collectAsState
import jr.brian.myapplication.model.remote.ApiService
import jr.brian.myapplication.model.repository.Repository
import jr.brian.myapplication.viewmodel.MainViewModel
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class ExampleUnitTest {
    @Mock
    lateinit var repo: Repository

    lateinit var viewModel: MainViewModel

    @Before
    fun init() {
        viewModel = MainViewModel(repo)
    }

    @Test
    fun addition_isCorrect() {
        print(viewModel.flowResponse.value)
    }
}