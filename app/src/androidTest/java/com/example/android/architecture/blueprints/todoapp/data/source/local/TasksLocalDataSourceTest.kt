package com.example.android.architecture.blueprints.todoapp.data.source.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.example.android.architecture.blueprints.todoapp.data.Result
import com.example.android.architecture.blueprints.todoapp.data.Task
import com.example.android.architecture.blueprints.todoapp.data.succeeded
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is.`is`
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@MediumTest
class TasksLocalDataSourceTest {

    /**
     * Ensures that all tasks are run synchronously using Architecture Components
     */
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var localDataSource: TasksLocalDataSource
    private lateinit var database: ToDoDatabase

    @Before
    fun initSource() {
        database = Room.inMemoryDatabaseBuilder(
                ApplicationProvider.getApplicationContext(),
                ToDoDatabase::class.java
        ).allowMainThreadQueries().build()

        localDataSource = TasksLocalDataSource(
                database.taskDao(),
                Dispatchers.Main
        )
    }

    @After
    fun closeDb() = database.close()

    // todo Replace runBlocking with runBlockingTest once the issue
    // github.com/Kotlin/kotlinx.coroutines/issues/1204 is resolved
    @Test
    fun saveTask_RetrieveTask() = runBlocking {
        // GIVEN - a new task is saved in the database
        val newTask = Task("title", "description", false)
        localDataSource.saveTask(newTask)

        // WHEN task is retrieved by id
        val loaded = localDataSource.getTask(newTask.id)

        // THEN same task is returned
        assertThat(loaded.succeeded, `is`(true))
        loaded as Result.Success
        assertThat(loaded.data.title, `is`(newTask.title))
        assertThat(loaded.data.description, `is`(newTask.description))
        assertThat(loaded.data.isCompleted, `is`(newTask.isCompleted))
    }

    @Test
    fun completeTask_retrieveTaskIsCompleted() = runBlocking {

        val incomplete = Task("title", "desc", false)
        localDataSource.saveTask(incomplete)
        // GIVEN Task is completed
        localDataSource.completeTask(incomplete)

        // WHEN the task is loaded
        val loaded = localDataSource.getTask(incomplete.id)

        // THEN it is updated
        assertThat(loaded.succeeded, `is`(true))
        loaded as Result.Success
        assertThat(loaded.data.title, `is`(incomplete.title))
        assertThat(loaded.data.description, `is`(incomplete.description))
        assertThat(loaded.data.isCompleted, `is`(true))
    }

}