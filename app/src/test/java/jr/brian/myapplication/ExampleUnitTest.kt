package jr.brian.myapplication

import jr.brian.myapplication.data.model.repository.Repository
import jr.brian.myapplication.viewmodel.MainViewModel
import org.junit.Test

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