package org.koin.koincomponent

import org.junit.Test
import org.koin.core.KoinComponent
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.inject
import org.koin.core.logger.Level
import org.koin.dsl.bind
import org.koin.dsl.module

class TODOAppTest {

    val todoAppModule = module {
        single { TasksView() } bind TasksContract.View::class
        single { TasksPresenter(get()) as TasksContract.Presenter }
    }

    val repositoryModule = module {
        single("remoteDataSource") { FakeTasksRemoteDataSource() as TasksDataSource }
        single("localDataSource") { TasksLocalDataSource() as TasksDataSource }
        single {
            TasksRepository(
                get("remoteDataSource"),
                get("localDataSource")
            )
        } bind TasksDataSource::class
    }

    interface TasksContract {
        interface View
        interface Presenter
    }

    class TasksView : KoinComponent, TasksContract.View {
        val taskPreenter by inject<TasksContract.Presenter>()
    }

    class TasksPresenter(val tasksRepository: TasksRepository) : TasksContract.Presenter
    interface TasksDataSource
    class FakeTasksRemoteDataSource : TasksDataSource
    class TasksLocalDataSource : TasksDataSource
    class TasksRepository(
        val remoteDataSource: TasksDataSource,
        val localDatasource: TasksDataSource
    ) : TasksDataSource

    @Test
    fun `should create all components`() {
        val koinApp = startKoin {
            logger(Level.DEBUG)
            modules(todoAppModule, repositoryModule)
        }
        val koin = koinApp.koin

        val view = koin.get<TasksView>()
        println("-> ${view.taskPreenter}")
        stopKoin()
    }
}