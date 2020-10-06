package com.example.android.architecture.blueprints.todoapp.data.source.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.example.android.architecture.blueprints.todoapp.data.Task
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
@SmallTest
class TasksDaoTest {

    /**
     * Rule that ensures that each task runs synchronously using Architecture Components
     */
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: ToDoDatabase

    @Before
    fun initDb() {
        database = Room.inMemoryDatabaseBuilder(
                ApplicationProvider.getApplicationContext(),
                ToDoDatabase::class.java
        ).allowMainThreadQueries().build()
    }

    @After
    fun closeDb() = database.close()

    @Test
    fun insertTaskAndGetById() = runBlockingTest {
        // GIVEN - Insert a task
        val task = Task ("title", "description")
        database.taskDao().insertTask(task)

        // WHEN  - Get the task from the DB by ID
        val loaded = database.taskDao().getTaskById(task.id)

        // THEN - assert that the loaded data contains expected values
        assertThat(loaded as Task, notNullValue())
        assertThat(loaded.id, `is`(task.id))
        assertThat(loaded.title, `is`(task.title))
        assertThat(loaded.description, `is`(task.description))
        assertThat(loaded.isCompleted, `is`(task.isCompleted))
    }

    @Test
    fun updateTaskAndGetById() = runBlockingTest {
        val original = Task("title", "description", true)

        database.taskDao().insertTask(original)

        // WHEN updating a task in the DB
        val newTask = Task("new title", "new description", false, original.id)
        database.taskDao().updateTask(newTask)

        // THEN the loaded data contains the expected values
        val loaded = database.taskDao().getTaskById(original.id)
        assertThat(loaded as Task, notNullValue())
        assertThat(loaded.id, `is`(original.id))
        assertThat(loaded.title, `is`(newTask.title))
        assertThat(loaded.description, `is`(newTask.description))
        assertThat(loaded.isCompleted, `is`(newTask.isCompleted))
    }

}