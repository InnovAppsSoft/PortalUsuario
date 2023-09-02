package com.marlon.portalusuario.feature.balancemanagement.framework.view

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.marlon.portalusuario.R
import com.marlon.portalusuario.databinding.FragmentCuentasBinding
import com.marlon.portalusuario.feature.balancemanagement.framework.view.extensions.hiddenUnnecessaryShowingViews
import com.marlon.portalusuario.feature.balancemanagement.framework.view.extensions.observeIn
import com.marlon.portalusuario.feature.balancemanagement.framework.view.extensions.updateCreditView
import com.marlon.portalusuario.feature.balancemanagement.framework.view.extensions.updateDataDailyView
import com.marlon.portalusuario.feature.balancemanagement.framework.view.extensions.updateDataLteView
import com.marlon.portalusuario.feature.balancemanagement.framework.view.extensions.updateDataMailView
import com.marlon.portalusuario.feature.balancemanagement.framework.view.extensions.updateDataView
import com.marlon.portalusuario.feature.balancemanagement.framework.view.extensions.updateSmsView
import com.marlon.portalusuario.feature.balancemanagement.framework.view.extensions.updateVoiceView
import com.marlon.portalusuario.feature.profile.util.ImageSaver
import com.marlon.portalusuario.feature.profile.presentation.ProfileFragment
import cu.suitetecsa.sdk.sim.model.SimCard
import cu.suitetecsa.sdk.ussd.uitls.toSizeString
import java.time.LocalDateTime

private const val NOON = 12
private const val EVENING_END = 19
private const val MAX_PROGRESS = 100
private const val REQUEST_CODE = 234

@RequiresApi(Build.VERSION_CODES.O)
class BalanceManagementFragment : Fragment() {
    private val viewModel by activityViewModels<BalanceManagementViewModel>()
    private lateinit var binding: FragmentCuentasBinding

    private lateinit var simCards: List<SimCard>
    private var simCardSelected: SimCard? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentCuentasBinding.inflate(layoutInflater)

        viewModel.simCards.observeIn(viewLifecycleOwner) { simCards = it }

        viewModel.currentSimCard.observeIn(viewLifecycleOwner) { simCard ->
            simCardSelected = simCard
            simCard?.let { current ->
                if (simCards.size > 1) {
                    binding.checkSimDual.visibility = View.VISIBLE
                    binding.checkSimDual.isChecked = current.slotIndex == 1
                    binding.checkSimDual.setOnCheckedChangeListener { _, isChecked ->
                        val futureCurrentSimCard =
                            simCards.first { it.slotIndex == if (isChecked) 0 else 1 }
                        viewModel.changeCurrentSimCard(futureCurrentSimCard)
                    }
                }
            }
        }

        viewModel.mainBalance.observeIn(viewLifecycleOwner) { balances ->
            balances?.let {
                val currentBalance = it.firstOrNull {
                        balance -> balance.id == simCardSelected?.serialNumber
                }
                currentBalance?.let { balance ->
                    binding.updateCreditView(balance)
                    binding.updateDataView(balance)
                    binding.updateDataLteView(balance)
                    binding.updateDataMailView(balance)
                    binding.updateDataDailyView(balance)
                    binding.updateVoiceView(balance)
                    binding.updateSmsView(balance)
                    binding.hiddenUnnecessaryShowingViews()
                }
            }
        }


        viewModel.bonusBalance.observeIn(viewLifecycleOwner) { balances ->
            balances?.let {
                val currentBalance = it.firstOrNull {
                    balance -> balance.id == simCardSelected?.serialNumber
                }
                currentBalance?.let { balance ->
                    balance.dataCu.bonusDataCuCount?.let { dataCu ->
                        binding.textCuentasDatosNacionales.text = dataCu.toSizeString()
                    }
                }
            }
        }

        binding.editar.setOnClickListener {
            startActivity(
                Intent(
                    context,
                    ProfileFragment::class.java
                )
            )
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            showFeatureUnsupportedAlert()
        }

        return binding.root
    }

    private fun showFeatureUnsupportedAlert() {
        val featureUnsupportedDialog = AlertDialog.Builder(requireActivity())
        featureUnsupportedDialog.setTitle(getString(R.string.warning_string))
        featureUnsupportedDialog.setMessage(R.string.advertencia)
        featureUnsupportedDialog.setPositiveButton("Ok", null)
        featureUnsupportedDialog.create().show()
    }

    override fun onViewCreated(arg0: View, arg1: Bundle?) {
        super.onViewCreated(arg0, arg1)

        viewModel.profilePreferences.observeIn(viewLifecycleOwner) { spProfile ->
            binding.textname.text = spProfile?.name.takeIf { !it.isNullOrEmpty() } ?: getString(R.string.user_string)
            binding.textPhoneNumber.text = spProfile?.phoneNumber
                .takeIf { !it.isNullOrEmpty() } ?: getString(R.string.phone_number_string)
            binding.textEmail.text = spProfile?.mail
                .takeIf { !it.isNullOrEmpty() } ?: getString(R.string.mail_nauta_string)
        }
        sayHello()

        setProfilePicture()

        binding.swipeRefresh.setOnRefreshListener {
            val progressBar =
                ProgressBar(requireContext(), null, android.R.attr.progressBarStyleHorizontal)
            progressBar.max = MAX_PROGRESS

            val dialogBuilder = AlertDialog.Builder(requireContext())
            dialogBuilder.setView(progressBar)
            dialogBuilder.setTitle(getString(R.string.balance_updating_string))
            dialogBuilder.setCancelable(false)

            val progressDialog = dialogBuilder.create()
            progressDialog.show()

            if (
                ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.CALL_PHONE
                ) !=
                PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.CALL_PHONE),
                    REQUEST_CODE
                )
                return@setOnRefreshListener
            }
            viewModel.updateBalances {
                progressDialog.setTitle(it)
            }
        }
    }

    private fun setProfilePicture() {
        val load = ImageSaver(context)
            .setFileName(getString(R.string.profile_picture_path))
            .setDirectoryName(getString(R.string.app_name))
            .load()
        load?.let {
            binding.imgDrawerPerfil.setImageBitmap(it)
        } ?: run {
            binding.imgDrawerPerfil.setImageResource(R.drawable.portal)
        }
    }

    private fun sayHello() {
        val currentTime = LocalDateTime.now()
        binding.imgGreetings.setImageResource(
            when {
                currentTime.hour < NOON -> R.drawable.baseline_light_mode_24
                currentTime.hour in NOON..EVENING_END -> R.drawable.outline_brightness_4_24
                else -> R.drawable.outline_dark_mode_24
            }
        )
        binding.profileGreetingString.text = getString(
            when {
                currentTime.hour < NOON -> R.string.title_good_morning
                currentTime.hour in NOON..EVENING_END -> R.string.title_good_afternoon
                else -> R.string.title_good_night
            }
        )
    }
}