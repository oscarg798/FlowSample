package com.oscarg798

import app.cash.turbine.test
import com.oscarg798.list.GetPeople
import com.oscarg798.list.ListViewModel
import com.oscarg798.list.Refresh
import dagger.internal.Beta
import io.mockk.called
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.util.UUID
import kotlin.random.Random


class ListViewModelTest {

    private val getPeople: GetPeople = mockk()
    private val refresh: Refresh = mockk()

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: ListViewModel


    @Before
    fun setup(){
        Dispatchers.setMain(testDispatcher)
        viewModel = ListViewModel (getPeople, refresh, corutineDispatcherProvider = object : CorutineDispatcherProvider {
            override val io: CoroutineDispatcher
            get() = testDispatcher

        })
    }

    @After
    fun tearDown(){
        Dispatchers.resetMain()
    }


    @Test
   fun `when viewmodel initializated then pright states emitted with people and loading`() = runTest {
       val people = createPeople()
       val peopleFlow = flowOf(people)

       every { getPeople() } answers  { peopleFlow}

       viewModel.state.test {
           Assert.assertEquals(
               ListViewModel.State(
                   loading = false,
                   people = null,
                   isRefreshing = false
               ),
               awaitItem()
           )
            Assert.assertEquals(
                ListViewModel.State(
                    loading = true,
                    people = null,
                    isRefreshing = false
                ),
                awaitItem()
            )
           Assert.assertEquals(
               ListViewModel.State(
                   loading = false,
                   people = people,
                   isRefreshing = false
               ),
               awaitItem()
           )
       }

        verify(exactly = 1) {
            getPeople()
            refresh wasNot called
        }


   }

    @Test
    fun `when refresh is called then people refreshed`() = runTest{
        val people = createPeople()
        val peopleFlow = MutableStateFlow(listOf<People>())

        every { getPeople() } answers  { peopleFlow}
        coEvery { refresh() }  answers {
            peopleFlow.value = people
        }

        viewModel.refresh()

        viewModel.state.test {
            Assert.assertEquals(
                ListViewModel.State(
                    loading = false,
                    people = null,
                    isRefreshing = false
                ),
                awaitItem()
            )
            Assert.assertEquals(
                ListViewModel.State(
                    loading = true,
                    people = null,
                    isRefreshing = false
                ),
                awaitItem()
            )
            Assert.assertEquals(
                ListViewModel.State(
                    loading = false,
                    people = listOf(),
                    isRefreshing = false
                ),
                awaitItem()
            )
            Assert.assertEquals(
                ListViewModel.State(
                    loading = false,
                    people = listOf(),
                    isRefreshing = true
                ),
                awaitItem()
            )
            Assert.assertEquals(
                ListViewModel.State(
                    loading = false,
                    people = people,
                    isRefreshing = false
                ),
                awaitItem()
            )
        }

        coVerify(exactly = 1) {
            getPeople()
            refresh()
        }

    }

    @Test
    fun `when add clicked then navigate to add emitted`() = runTest{
        val people = createPeople()
        val peopleFlow = flowOf(people)

        every { getPeople() } answers  { peopleFlow}

        viewModel.onAddPressed()

        viewModel.event.test {
            Assert.assertEquals(
                ListViewModel.Event.NavigateToAdd,
                awaitItem()
            )
        }
    }

    private fun createPeople() = (0 until Random.nextInt(1,5)).map {
        People(
            id = UUID.randomUUID(),
            name = UUID.randomUUID().toString(),
            lastname = UUID.randomUUID().toString(),
            email = UUID.randomUUID().toString()
        )
    }
}
