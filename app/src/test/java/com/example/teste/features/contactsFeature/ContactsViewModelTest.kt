package com.example.teste.features.contactsFeature

import com.example.teste.InstantExecutorExtension
 import com.example.teste.data.Repository
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verifyOrder
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith


@ExtendWith(InstantExecutorExtension::class)
internal class ContactsViewModelTest {

    private val repository = mockk<Repository>(relaxed = true)
    private lateinit var viewModel: ContactsViewModel

    @BeforeEach
    fun setup() {
        viewModel = ContactsViewModel(repository)

        clearAllMocks()
    }

    @Test
    fun `test request`() {
        every { viewModel.requestUsers() } answers {}
        viewModel.requestUsers()
        viewModel.repository.getUser(viewModel.listUser)
        viewModel.repository.remoteDataSource.requestListUser(viewModel.listUser)
        verifyOrder {
            viewModel.requestUsers()
            viewModel.repository.getUser(viewModel.listUser)
            viewModel.repository.remoteDataSource.requestListUser(viewModel.listUser)
        }
    }

}