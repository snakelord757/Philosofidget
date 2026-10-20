package ru.snakelord.philosofidget.presentation.view.quote_topics

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import ru.snakelord.philosofidget.R
import ru.snakelord.philosofidget.databinding.FragmentQuoteTopicsBinding

class QuoteTopicsFragment : Fragment(R.layout.fragment_quote_topics) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val binding = FragmentQuoteTopicsBinding.bind(view)
        val topics = resources.getStringArray(R.array.quote_topics)
        binding.topicsList.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_single_choice, topics)
        binding.topicsList.setItemChecked(QuoteTopicSelection.getIndex(requireContext()), true)
        binding.backButton.setOnClickListener { parentFragmentManager.popBackStack() }
        binding.topicsList.setOnItemClickListener { _, _, position, _ ->
            // Keep the check mark on the confirmed selection until the dialog is accepted.
            binding.topicsList.setItemChecked(QuoteTopicSelection.getIndex(requireContext()), true)
            if (parentFragmentManager.findFragmentByTag(ConfirmQuoteTopicDialog.TAG) == null) {
                ConfirmQuoteTopicDialog.newInstance(position).show(parentFragmentManager, ConfirmQuoteTopicDialog.TAG)
            }
        }
    }
}
