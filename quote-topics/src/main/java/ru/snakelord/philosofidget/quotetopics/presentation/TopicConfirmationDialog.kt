package ru.snakelord.philosofidget.quotetopics.presentation

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import ru.snakelord.philosofidget.quotetopics.R

class TopicConfirmationDialog : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.quote_topics_confirm_title)
            .setMessage(getString(R.string.quote_topics_confirm_message, requireArguments().getString(TOPIC_TITLE)))
            .setNegativeButton(R.string.quote_topics_cancel, null)
            .setPositiveButton(R.string.quote_topics_confirm) { _, _ ->
                parentFragmentManager.setFragmentResult(RESULT_KEY, Bundle().apply {
                    putString(TOPIC_ID, requireArguments().getString(TOPIC_ID))
                })
            }
            .create()

    companion object {
        const val RESULT_KEY = "quote_topic_confirmed"
        const val TOPIC_ID = "quote_topic_id"
        private const val TOPIC_TITLE = "quote_topic_title"

        fun newInstance(id: String, title: String) = TopicConfirmationDialog().apply {
            arguments = Bundle().apply {
                putString(TOPIC_ID, id)
                putString(TOPIC_TITLE, title)
            }
        }
    }
}
