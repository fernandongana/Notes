package co.mz.noteApp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.preference.Preference

class DefinicoesFragment : Fragment() {

    private var preferenceChangeListener: Preference.OnPreferenceChangeListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().supportFragmentManager
            .beginTransaction()
            .replace(
                R.id.settings,
                PreferenceFragment()
            )
            .commit()

      //  Toast.makeText(requireActivity(), "Started", Toast.LENGTH_SHORT).show()

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (container != null) {
            container.removeAllViews();
        }
        return inflater.inflate(R.layout.fragment_definicoes, container, false)
    }
}