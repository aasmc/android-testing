package com.example.android.architecture.blueprints.todoapp.tasks

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.android.architecture.blueprints.todoapp.Event
import com.example.android.architecture.blueprints.todoapp.MainCoroutineRule
import com.example.android.architecture.blueprints.todoapp.R
import com.example.android.architecture.blueprints.todoapp.data.Task
import com.example.android.architecture.blueprints.todoapp.data.source.FakeTestRepository
import com.example.android.architecture.blueprints.todoapp.getOrAwaitNextValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class TasksViewModelTest {


    private lateinit var taskRepository: FakeTestRepository
    /**
     * Runs all architecture components' background jobs on the same thread
     * This ensures that the tests results happen synchronously and in a repeatable order
     * However if you call LiveData.postValue() from the main thread, the documented precedence
     * might not be preserved.
     */
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineDispatcher = MainCoroutineRule()

    private lateinit var tasksViewModel: TasksViewModel

    @ExperimentalCoroutinesApi
    @Before
    fun setUpViewModel() {
        // set the testDispatcher as the main dispatcher
        taskRepository = FakeTestRepository()
        val task1 = Task("Title1", "Description1")
        val task2 = Task("Title2", "Description2", true)
        val task3 = Task("Title3", "Description3", true)
        taskRepository.addTasks(task1, task2, task3)
        tasksViewModel = TasksViewModel(taskRepository)

    }

    @Test
    fun `addNewTask sets new task event`() {
        // When adding a new task
        tasksViewModel.addNewTask()
        // Then the new task event is triggered
        val value = tasksViewModel.newTaskEvent.getOrAwaitNextValue()

        assertThat(value.getContentIfNotHandled(), (not(nullValue())))
    }

    @Test
    fun `setFilterAllTasks makes addTasks view visible`() {
        // When
        tasksViewModel.setFiltering(TasksFilterType.ALL_TASKS)
        // Then
        val value = tasksViewModel.tasksAddViewVisible.getOrAwaitNextValue()
        assertThat(value, `is`(true))
    }

    @Test
    fun completeTask_dataAndSnackBarUpdated() {
        // create an active task and add it to the repository
        val task = Task("Title", "Description")
        taskRepository.addTasks(task)

        // mark the task as complete task
        tasksViewModel.completeTask(task, true)

        // verify that the task is completed
        assertThat(taskRepository.tasksServiceData[task.id]?.isCompleted, `is`(true))

        // assert that the snackBar has been updated with the correct text
        val snackBar: Event<Int> = tasksViewModel.snackbarText.getOrAwaitNextValue()
        assertThat(snackBar.getContentIfNotHandled(), `is`(R.string.task_marked_complete))
    }

}