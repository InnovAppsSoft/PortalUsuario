package com.marlon.portalusuario.feature.balancemanagement.view

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.preference.PreferenceManager
import com.marlon.portalusuario.R
import com.marlon.portalusuario.databinding.FragmentCuentasBinding
import com.marlon.portalusuario.feature.balancemanagement.core.extensions.toSizeString
import com.marlon.portalusuario.perfil.ImageSaver
import com.marlon.portalusuario.perfil.PerfilActivity
import cu.suitetecsa.sdk.sim.model.SimCard
import cu.suitetecsa.sdk.ussd.uitls.toTimeString
import java.time.LocalDateTime


@RequiresApi(Build.VERSION_CODES.O)
class BalanceManagementFragment : Fragment() {
    private val viewModel by activityViewModels<BalanceManagementViewModel>()

    private lateinit var binding: FragmentCuentasBinding

    private lateinit var spBalance: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    private lateinit var spProfile: SharedPreferences

    // preference dualSim
    private lateinit var simCards: List<SimCard>
    private var simCardSelected: SimCard? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentCuentasBinding.inflate(layoutInflater)

        viewModel.simCards.observe(viewLifecycleOwner) { simCardList ->
            simCards = simCardList
        }

        viewModel.currentSimCard.observe(viewLifecycleOwner) { simCard ->
            simCardSelected = simCard
            simCard?.let { current ->
                if (simCards.size > 1) {
                    binding.checkSimDual.visibility = View.VISIBLE
                    binding.checkSimDual.isChecked = current.slotIndex == 1
                    binding.checkSimDual.setOnCheckedChangeListener { _, isChecked ->
                        val futureCurrentSimCard = simCards.first { it.slotIndex == if (isChecked) 0 else 1 }
                        viewModel.changeCurrentSimCard(futureCurrentSimCard)
                    }
                }
            }
        }

        spProfile = requireActivity().getSharedPreferences("profile", Context.MODE_PRIVATE)

        // SharedPreferences para guardar datos de cuentas
        spBalance = requireActivity().getSharedPreferences("cuentas", Context.MODE_PRIVATE)
        editor = spBalance.edit()

        // Observar el estado del balance principal
        viewModel.mainBalance.observe(viewLifecycleOwner) {
            it?.credit?.let { credit ->
                binding.llCredit.visibility = View.VISIBLE
                binding.textCuentasSaldo.text = getString(R.string.credit_placeholder, credit)
                binding.textCuentasVenceSim.text = it.activeUntil
            }

            it?.data?.data?.let {  data ->
                binding.llDataVoice.visibility = View.VISIBLE
                binding.cardData.visibility = View.VISIBLE
                binding.textCuentasDatos.text = data.toSizeString()
                it.data.remainingDays?.let { days ->
                    binding.llDataRemainingDays.visibility = View.VISIBLE
                    binding.textCuentasVenceDatos.text = String().format("%d dias", days)
                }
            } ?: kotlin.run {
                binding.cardData.visibility = View.GONE
            }

            it?.data?.dataLte?.let { dataLte ->
                binding.llSmsLte.visibility = View.VISIBLE
                binding.cardDataLte.visibility = View.VISIBLE
                binding.textCuentasDatosLte.text = dataLte.toSizeString()
                it.data.remainingDays?.let { days ->
                    binding.llDataRemainingDays.visibility = View.VISIBLE
                    binding.textCuentasVenceDatos.text = String().format("%d dias", days)
                }
            } ?: kotlin.run {
                binding.cardDataLte.visibility = View.GONE
            }

            it?.mailData?.data?.let {  dataMail ->
                binding.cardDataMail.visibility = View.VISIBLE
                binding.textCuentasMensajeria.text = dataMail.toSizeString()
                it.mailData.remainingDays?.let { days ->
                    binding.textCuentasVenceMensajeria.text = String().format("%d dias", days)
                }
            } ?: kotlin.run {
                binding.cardDataMail.visibility = View.GONE
            }

            it?.dailyData?.data?.let {  dataDaily ->
                binding.cardDataDaily.visibility = View.VISIBLE
                binding.textCuentasDiaria.text = dataDaily.toSizeString()
                it.mailData.remainingDays?.let { days ->
                    binding.textCuentasVenceDiaria.text = String().format("%d dias", days)
                }
            } ?: kotlin.run {
                binding.cardDataDaily.visibility = View.GONE
            }

            it?.voice?.mainVoice?.let { voice ->
                binding.llDataVoice.visibility = View.VISIBLE
                binding.cardVoice.visibility = View.VISIBLE
                binding.textCuentasMinutos.text = voice.toTimeString()
                it.voice.remainingDays?.let { days ->
                    binding.textCuentasVenceMinSms.text = String().format("%d dias", days)
                }
            } ?: kotlin.run {
                binding.cardVoice.visibility = View.GONE
            }

            it?.sms?.mainSms?.let { sms ->
                binding.llSmsLte.visibility = View.VISIBLE
                binding.cardSms.visibility = View.VISIBLE
                binding.textCuentasMensajes.text = String().format("%d sms", sms)
                it.sms.remainingDays?.let { days ->
                    binding.llSmsLte.visibility = View.VISIBLE
                    binding.textCuentasVenceMinSms.text = String().format("%d dias", days)
                }
            } ?: kotlin.run {
                binding.cardSms.visibility = View.GONE
            }

            if (binding.cardData.visibility == View.GONE && binding.cardVoice.visibility == View.GONE){
                binding.llDataVoice.visibility = View.GONE
            }

            if (binding.cardDataLte.visibility == View.GONE && binding.cardSms.visibility == View.GONE){
                binding.llSmsLte.visibility = View.GONE
            }

            if (binding.cardDataDaily.visibility == View.GONE && binding.cardDataLte.visibility == View.GONE){
                binding.llDataRemainingDays.visibility = View.GONE
            }
        }


        viewModel.bonusBalance.observe(viewLifecycleOwner) {
            it?.dataCu?.bonusDataCuCount?.let { dataCu ->
                binding.textCuentasDatosNacionales.text = dataCu.toSizeString()
            }
        }

        binding.editar.setOnClickListener {
            startActivity(
                Intent(
                    context,
                    PerfilActivity::class.java
                )
            )
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            // Show an alert dialog to the user that the feature is not supported
            showFeatureUnsupportedAlert()
        }

        return binding.root
    }

    private fun showFeatureUnsupportedAlert() {
        val featureUnsupportedDialog = AlertDialog.Builder(requireActivity())
        featureUnsupportedDialog.setTitle("Advertencia")
        featureUnsupportedDialog.setMessage(R.string.advertencia)
        featureUnsupportedDialog.setPositiveButton("Ok", null)
        featureUnsupportedDialog.create().show()
    }

    override fun onViewCreated(arg0: View, arg1: Bundle?) {
        super.onViewCreated(arg0, arg1)

        // Mostrar saludo con el nombre del usuario
        displayGreeting()
        sayHello()

        // Perfil en el header
        setProfilePicture()


        // Ver bonos promocionales
        binding.buttonCuentasBono.setOnClickListener {
            val promo = spBalance.getString("bonos", null)
            val alertdialogo = AlertDialog.Builder(requireActivity())
            alertdialogo.setMessage(promo)
            alertdialogo.setPositiveButton("Ok", null)
            alertdialogo.create().show()
        }

        // SwipeRefresh
        binding.swipeRefresh.setOnRefreshListener {
            // Progress dialog
            val progressBar = ProgressBar(requireContext(), null, android.R.attr.progressBarStyleHorizontal)
            progressBar.max = MAX_PROGRESS

            val dialogBuilder = AlertDialog.Builder(requireContext())
            dialogBuilder.setView(progressBar)
            dialogBuilder.setTitle(getString(R.string.balance_updating_string))
            dialogBuilder.setCancelable(false)

            val progressDialog = dialogBuilder.create()
            progressDialog.show()

            // UPDATE
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
            viewModel.getBalances {
                progressDialog.setTitle(it)
            }
        }
    }

    private fun setProfilePicture() {
        val load = ImageSaver(context)
            .setFileName("IMG.png")
            .setDirectoryName("PortalUsuario")
            .load()
        if (load == null) {
            binding.imgDrawerPerfil.setImageResource(R.drawable.portal)
        } else {
            binding.imgDrawerPerfil.setImageBitmap(load)
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
        binding.textSaludoPerfil.text = getString(
            when {
                currentTime.hour < NOON -> R.string.title_good_morning
                currentTime.hour in NOON..EVENING_END -> R.string.title_good_afternoon
                else -> R.string.title_good_night
            }
        )
    }

    private fun displayGreeting() {
        binding.textname.text =
            spProfile.getString("nombre", "")?.takeIf { it.isNotEmpty() }
                ?: getString(R.string.user_string)
        binding.textPhoneNumber.text =
            spProfile.getString("numero", "")?.takeIf { it.isNotEmpty() }
                ?: getString(R.string.phone_number_string)
        binding.textEmail.text =
            spProfile.getString("nauta", "")?.takeIf { it.isNotEmpty() }
                ?: getString(R.string.mail_nauta_string)
    }

    override fun onResume() {
        super.onResume()
        if (view == null) return
        requireView().isFocusableInTouchMode = true
        requireView().requestFocus()
        requireView().setOnKeyListener { _, keyCode, event ->
            event.action == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK
        }
        displayGreeting()

        // saldo movil
        binding.textCuentasSaldo.text = spBalance.getString("saldo", "0.00 CUP")

        // vence saldo movil
        binding.textCuentasVenceSim.text = spBalance.getString("vence_saldo", "Expira: 00/00/00")

        // minutos
        binding.textCuentasMinutos.text = spBalance.getString("minutos", "00:00:00")

        // mensajes
        binding.textCuentasMensajes.text = spBalance.getString("sms", "0")

        // vence sms y voz
        binding.textCuentasVenceMinSms.text = spBalance.getString("vence_sms", "0 días")

        // bonos en promo
        val promo = spBalance.getString("bonos", null)
        binding.buttonCuentasBono.visibility =
            if (!promo.isNullOrEmpty()) View.VISIBLE else View.GONE

        // datos nacionales
        binding.textCuentasDatosNacionales.text = spBalance.getString("datos_nacionales", "0 MB")

        // paquetes
        binding.textCuentasDatos.text = spBalance.getString("paquete", "0 MB")

        // lte
        binding.textCuentasDatosLte.text = spBalance.getString("lte", "0 MB")

        // vence datos
        binding.textCuentasVenceDatos.text = spBalance.getString("vence_datos", "0 días")

        // mensajeria y vencimiento
        binding.textCuentasMensajeria.text = spBalance.getString("mensajeria", "0 MB")
        binding.textCuentasVenceMensajeria.text =
            spBalance.getString("vence_mensajeria", "0 días")

        // diaria y vencimiento
        binding.textCuentasDiaria.text = spBalance.getString("diaria", "0 MB")
        binding.textCuentasVenceDiaria.text = spBalance.getString("vence_diaria", "0 horas")

        // update hora
        binding.textHoraUpdate.text =
            getString(R.string.last_update_placeholder, spBalance.getString("hora", "00:00"))
    }

    companion object {
        private const val NOON = 12
        private const val EVENING_END = 19
        private const val MAX_PROGRESS = 100
        private const val REQUEST_CODE = 234

        val simSlotName = arrayOf(
            "extra_asus_dial_use_dualsim",
            "com.android.phone.extra.slot",
            "slot",
            "simslot",
            "sim_slot",
            "subscription",
            "Subscription",
            "phone",
            "com.android.phone.DialingMode",
            "simSlot",
            "slot_id",
            "simId",
            "simnum",
            "phone_type",
            "slotId",
            "slotIdx"
        )
    }
}