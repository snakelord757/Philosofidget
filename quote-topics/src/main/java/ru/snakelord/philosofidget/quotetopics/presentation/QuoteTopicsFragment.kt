package ru.snakelord.philosofidget.quotetopics.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.snakelord.philosofidget.quotetopics.R

class QuoteTopicsFragment : Fragment() {
    private val viewModel: QuoteTopicsViewModel by viewModel()
    private var topicList: ListView? = null
    private var backButton: Button? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val context = requireContext()
        val padding = (16 * resources.displayMetrics.density).toInt()
        return LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(padding, padding, padding, padding)
            addView(Button(context).apply {
                setText(R.string.quote_topics_back)
                backButton = this
            })
            addView(TextView(context).apply {
                setText(R.string.quote_topics_title)
                textSize = 24f
                setPadding(0, padding, 0, padding)
                ViewCompat.setAccessibilityHeading(this, true)
            })
            addView(ListView(context).apply {
                topicList = this
            }, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f))
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        childFragmentManager.setFragmentResultListener(
            TopicConfirmationDialog.RESULT_KEY, viewLifecycleOwner
        ) { _, result ->
            val topicId = result.getString(TopicConfirmationDialog.TOPIC_ID)
            if (topicId != null && viewModel.selectTopic(topicId)) {
                parentFragmentManager.popBackStack()
            }
        }
        backButton?.setOnClickListener { parentFragmentManager.popBackStack() }
        topicList?.adapter = ArrayAdapter(
            requireContext(), android.R.layout.simple_list_item_1, viewModel.topics.map { it.title }
        )
        topicList?.setOnItemClickListener { _, _, position, _ ->
            if (!childFragmentManager.isStateSaved &&
                childFragmentManager.findFragmentByTag(CONFIRMATION_TAG) == null
            ) {
                val topic = viewModel.topics[position]
                TopicConfirmationDialog.newInstance(topic.id, topic.title)
                    .showNow(childFragmentManager, CONFIRMATION_TAG)
            }
        }
    }

    override fun onDestroyView() {
        topicList?.onItemClickListener = null
        topicList?.adapter = null
        backButton?.setOnClickListener(null)
        topicList = null
        backButton = null
        super.onDestroyView()
    }

    private companion object {
        const val CONFIRMATION_TAG = "quote_topic_confirmation"
    }
}
