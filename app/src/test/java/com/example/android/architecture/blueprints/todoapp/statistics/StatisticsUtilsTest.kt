package com.example.android.architecture.blueprints.todoapp.statistics

import com.example.android.architecture.blueprints.todoapp.data.Task
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Assert.assertEquals
import org.junit.Test

class StatisticsUtilsTest {

    /**
     * If there're no completed tasks and one active task,
     * then there're 100% active tasks and 0% completed tasks
     */
    @Test
    fun `getActiveAndCompletedStats no completed returns zero hundred`() {
        val tasks = listOf<Task>(
                Task(title = "title", description = "description", isCompleted = false)
        )

        val result = getActiveAndCompletedStats(tasks)

        assertThat(result.completedTasksPercent, `is` (0f))
        assertThat(result.activeTasksPercent, `is`(100f))
    }

    /**
     * If there're 2 completed tasks and 3 active tasks,
     * then there're 40% completed and 60% active tasks
     */
    @Test
    fun `getActiveAndCompletedStats tasks return forty and sixty`() {
        val tasks = listOf<Task>(
                Task(title = "title", description = "description", isCompleted = false),
                Task(title = "title1", description = "description1", isCompleted = false),
                Task(title = "title2", description = "description2", isCompleted = false),
                Task(title = "title3", description = "description3", isCompleted = true),
                Task(title = "title4", description = "description4", isCompleted = true)
        )

        val result = getActiveAndCompletedStats(tasks)

        assertEquals(40f, result.completedTasksPercent)
        assertEquals(60f, result.activeTasksPercent)
    }

    /**
     * If there're no completed tasks and no active task,
     * then there'is 0% active tasks and 0% completed tasks
     */
    @Test
    fun `getActiveAndCompletedStats no completed returns zero zero`() {
        val tasks = emptyList<Task>()

        val result = getActiveAndCompletedStats(tasks)

        assertEquals(0f, result.completedTasksPercent)
        assertEquals(0f, result.activeTasksPercent)
    }

    /**
     * If list of tasks is null
     * then there's 0% active tasks and 0% completed tasks
     */
    @Test
    fun `getActiveAndCompletedStats no completed returns zero zero when null`() {
        val tasks = null

        val result = getActiveAndCompletedStats(tasks)

        assertEquals(0f, result.completedTasksPercent)
        assertEquals(0f, result.activeTasksPercent)
    }


}