package ru.snakelord.philosofidget.quotetopics.presentation

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import ru.snakelord.philosofidget.quotetopics.data.QuoteTopicsRepository
import ru.snakelord.philosofidget.quotetopics.domain.ConfirmTopicSelection

class QuoteTopicsFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        childFragmentManager.setFragmentResultListener(CONFIRMED, this) { _, result ->
            val id = result.getString(TOPIC_ID) ?: return@setFragmentResultListener
            if (ConfirmTopicSelection(QuoteTopicsRepository(requireContext()))(id)) {
                parentFragmentManager.popBackStack()
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, state: Bundle?): View {
        val context = requireContext()
        val topics = QuoteTopicsRepository(context).topics()
        val padding = (16 * resources.displayMetrics.density).toInt()
        return LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            addView(TextView(context).apply {
                text = "Темы цитат"
                textSize = 24f
                setPadding(padding, padding, padding, padding)
            }, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT))
            addView(ListView(context).apply {
                adapter = ArrayAdapter(context, android.R.layout.simple_list_item_1, topics.map { it.title })
                setOnItemClickListener { _, _, position, _ ->
                    if (childFragmentManager.findFragmentByTag(DIALOG_TAG) == null) {
                        val topic = topics[position]
                        ConfirmTopicDialog.newInstance(topic.id, topic.title).show(childFragmentManager, DIALOG_TAG)
                    }
                }
            }, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f))
        }
    }

    companion object {
        internal const val CONFIRMED = "quote_topic_confirmed"
        internal const val TOPIC_ID = "topic_id"
        private const val DIALOG_TAG = "confirm_quote_topic"
    }
}

class ConfirmTopicDialog : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        AlertDialog.Builder(requireContext())
            .setTitle("Выбор темы")
            .setMessage("Выбрать тему «${requireArguments().getString(TITLE)}»?")
            .setNegativeButton("Отмена", null)
            .setPositiveButton("Выбрать") { _, _ ->
                parentFragmentManager.setFragmentResult(QuoteTopicsFragment.CONFIRMED, Bundle().apply {
                    putString(QuoteTopicsFragment.TOPIC_ID, requireArguments().getString(QuoteTopicsFragment.TOPIC_ID))
                })
            }
            .create()

    companion object {
        private const val TITLE = "topic_title"
        fun newInstance(id: String, title: String) = ConfirmTopicDialog().apply {
            arguments = Bundle().apply {
                putString(QuoteTopicsFragment.TOPIC_ID, id)
                putString(TITLE, title)
            }
        }
    }
}
