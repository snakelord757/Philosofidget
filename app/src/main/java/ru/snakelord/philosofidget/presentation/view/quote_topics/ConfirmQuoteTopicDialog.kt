package ru.snakelord.philosofidget.presentation.view.quote_topics

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import ru.snakelord.philosofidget.R

class ConfirmQuoteTopicDialog : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val index = requireArguments().getInt(TOPIC_INDEX)
        val topic = resources.getStringArray(R.array.quote_topics)[index]
        return AlertDialog.Builder(requireContext())
            .setTitle(R.string.quote_topic_confirm_title)
            .setMessage(getString(R.string.quote_topic_confirm_message, topic))
            .setNegativeButton(android.R.string.cancel, null)
            .setPositiveButton(R.string.quote_topic_confirm) { _, _ ->
                QuoteTopicSelection.select(requireContext(), index)
                parentFragmentManager.popBackStack()
            }
            .create()
    }

    companion object {
        const val TAG = "ConfirmQuoteTopicDialog"
        private const val TOPIC_INDEX = "topic_index"

        fun newInstance(index: Int) = ConfirmQuoteTopicDialog().apply {
            arguments = bundleOf(TOPIC_INDEX to index)
        }
    }
}
