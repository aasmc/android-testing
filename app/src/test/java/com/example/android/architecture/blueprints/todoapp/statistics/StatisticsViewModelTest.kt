package com.example.android.architecture.blueprints.todoapp.statistics

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.android.architecture.blueprints.todoapp.MainCoroutineRule
import com.example.android.architecture.blueprints.todoapp.data.source.FakeTestRepository
import com.example.android.architecture.blueprints.todoapp.getOrAwaitNextValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is.`is`
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class StatisticsViewModelTest {

    /**
     * Runs all architecture components' background jobs on the same thread
     * This ensures that the tests results happen synchronously and in a repeatable order
     * However if you call LiveData.postValue() from the main thread, the documented precedence
     * might not be preserved.
     */
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    /**
     * Sets the main coroutine dispatcher for unit testing.
     */
    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    /**
     * Subject under test
     */
    private lateinit var statisticsViewModel: StatisticsViewModel

    /**
     * Use a fake repository to be injected into the viewModel
     */
    private lateinit var tasksRepository: FakeTestRepository

    @Before
    fun setUpStatisticsViewModel() {
        tasksRepository = FakeTestRepository()
        statisticsViewModel = StatisticsViewModel(tasksRepository)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun loadTasks_loading() {
        // Here we pause the dispatcher to be able to check values before the coroutine starts
        mainCoroutineRule.pauseDispatcher()
        // load the task in the view model
        statisticsViewModel.refresh()

        // Then progress indicator is shown
        assertThat(statisticsViewModel.dataLoading.getOrAwaitNextValue(), `is`(true))
        // Here we resume the coroutine
        mainCoroutineRule.resumeDispatcher()

        // then progress indicator is hidden
        assertThat(statisticsViewModel.dataLoading.getOrAwaitNextValue(), `is`(false))
    }

    @Test
    fun loadStatisticsWhenTasksAreUnavailable_callErrorToDisplay() {

        // make the repository return errors
        tasksRepository.setReturnError(true)
        statisticsViewModel.refresh()

        // Then empty and error LiveData are true which triggers an error message to be shown
        assertThat(statisticsViewModel.empty.getOrAwaitNextValue(), `is`(true))
        assertThat(statisticsViewModel.error.getOrAwaitNextValue(), `is`(true))
    }
}

















