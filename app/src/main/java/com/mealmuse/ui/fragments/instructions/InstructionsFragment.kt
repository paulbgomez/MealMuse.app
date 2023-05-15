package com.mealmuse.ui.fragments.instructions

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import com.mealmuse.databinding.FragmentInstructionsBinding
import com.mealmuse.util.Constants.Companion.RECIPE_RESULT_KEY
import com.mealmuse.util.retrieveParcelable

/**
 * Fragmento que muestra las instrucciones de una receta.
 */
class InstructionsFragment : Fragment() {

    private var _binding: FragmentInstructionsBinding? = null
    private val binding get() = _binding!!

    /**
     * Infla el diseño de este fragmento y carga las instrucciones de la receta en un WebView.
     *
     * @param inflater El inflater utilizado para inflar el diseño del fragmento.
     * @param container El contenedor padre en el cual el diseño del fragmento será colocado.
     * @param savedInstanceState El estado previamente guardado del fragmento, si existe.
     * @return La vista inflada del fragmento.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentInstructionsBinding.inflate(inflater, container, false)

        val args = arguments
        val myBundle: com.mealmuse.models.Result? = args?.retrieveParcelable(RECIPE_RESULT_KEY)

        if (myBundle != null) {
            binding.instructionsWebView.webViewClient = object : WebViewClient() {}
            val websiteUrl: String = myBundle.sourceUrl
            binding.instructionsWebView.loadUrl(websiteUrl)
        }

        return binding.root
    }

    /**
     * Limpia el binding cuando el fragmento es destruido.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}