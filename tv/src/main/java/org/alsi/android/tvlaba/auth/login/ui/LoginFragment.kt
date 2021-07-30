package org.alsi.android.tvlaba.auth.login.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment.findNavController
import dagger.android.support.AndroidSupportInjection
import org.alsi.android.presentation.auth.login.model.LoginViewModel
import org.alsi.android.presentation.state.Resource
import org.alsi.android.presentation.state.ResourceState.*
import org.alsi.android.tvlaba.R
import org.alsi.android.tvlaba.databinding.LoginFragmentBinding
import org.alsi.android.tvlaba.tv.injection.ViewModelFactory
import javax.inject.Inject

class LoginFragment : Fragment(R.layout.login_fragment) {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var loginViewModel : LoginViewModel

    private var _vb: LoginFragmentBinding? = null
    private val vb get() = _vb!!

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        super.onCreate(savedInstanceState)

        loginViewModel = ViewModelProvider(this, viewModelFactory).get(LoginViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _vb = LoginFragmentBinding.inflate(inflater, container, false)

        vb.loginButtonSubmit.setOnClickListener {
            loginViewModel.login(
                vb.loginPinCodeEdit.text.toString(),
                vb.loginPasswordEdit.text.toString()
            )
        }
        return vb.root
    }

    override fun onStart() {
        super.onStart()
        loginViewModel.liveData.observe(this, this::handleLoginResult)
    }

    private fun handleLoginResult(resource: Resource<Unit>) {

        when(resource.status) {
            LOADING -> {}
            SUCCESS -> findNavController(this).navigate(R.id.actionGlobalOnLogIn)
            ERROR -> Toast.makeText(context, resource.message, Toast.LENGTH_LONG).show()

        }
    }
}