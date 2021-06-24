package org.alsi.android.tvlaba.tv.tv.program

import android.annotation.SuppressLint
import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.leanback.widget.Presenter
import org.alsi.android.domain.tv.model.guide.CreditRole
import org.alsi.android.domain.tv.model.guide.TvProgramIssue
import org.alsi.android.presentationtv.model.TvProgramDetailsLiveData
import org.alsi.android.tvlaba.R
import org.alsi.android.tvlaba.databinding.TvProgramDetailsDescriptionExtendedBinding
import java.util.*
import kotlin.math.roundToInt

class TvProgramDetailsDescriptionPresenter(val context: Context): Presenter() {

    @SuppressLint("InflateParams")
    override fun onCreateViewHolder(parent: ViewGroup?): ViewHolder {
        val itemBinding = TvProgramDetailsDescriptionExtendedBinding.inflate(LayoutInflater.from(context))
        return MyViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, item: Any) = (holder as MyViewHolder).bind(item as TvProgramDetailsLiveData)

    inner class MyViewHolder(private val ib: TvProgramDetailsDescriptionExtendedBinding) : ViewHolder(ib.root) {
        fun bind(item: TvProgramDetailsLiveData) {
            val program = item.cursor!!.program
            val channel = item.cursor!!.channel
            program.let {

                // title
                ib.tvProgramDetailsPrimaryTitle.text = it?.title

                // subtitle: time & channel's title
                val programTime = it?.time?.shortString?:""
                val channelReferences = if (channel != null) context.getString(
                    R.string.statement_program_on_channel, channel.title) else ""
                ib.tvProgramDetailsSecondaryTitle.text = String.format("%s %s", programTime, channelReferences)

                // body
                ib.tvProgramDetailsDescriptionText.text = buildBodyText(program)

                // timeline
                ib.tvProgramDetailsProgress.progress = it?.time?.progress?:0

                // age limitation
                if (it?.ageGroup != null && it.ageGroup!! > 0)
                    ib.tvProgramDetailsBannerAgeLimitation.text = String.format("%s+", it.ageGroup)
                else ib.tvProgramDetailsBannerAgeLimitation.visibility = GONE

                // rates
                bindRate(ib.tvProgramDetailsBannerKinopoiskRate,
                    ib.tvProgramDetailsBannerKinopoiskLogo, it?.rateKinopoisk)
                bindRate(ib.tvProgramDetailsBannerImdbRate,
                    ib.tvProgramDetailsBannerImdbLogo, it?.rateKinopoisk)
            }
        }
    }


    /*
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(viewHolder: ViewHolder, item: Any) {
        val data = item as TvProgramDetailsLiveData
        val program = data.cursor!!.program
        val channel = data.cursor!!.channel
        with(viewHolder.view) {
            program.let {

                // title
                tvProgramDetailsPrimaryTitle.text = it?.title

                // subtitle: time & channel's title
                val programTime = it?.time?.shortString?:""
                val channelReferences = if (channel != null) context.getString(
                        R.string.statement_program_on_channel, channel.title) else ""
                tvProgramDetailsSecondaryTitle.text = "$programTime $channelReferences"

                // body
                tvProgramDetailsDescriptionText.text = buildBodyText(program)

                // timeline
                tvProgramDetailsProgress.progress = it?.time?.progress?:0

                // age limitation
                if (it?.ageGroup != null && it.ageGroup!! > 0)
                    tvProgramDetailsBannerAgeLimitation.text = "${it.ageGroup}+"
                else tvProgramDetailsBannerAgeLimitation.visibility = GONE

                // rates
                bindRate(tvProgramDetailsBannerKinopoiskRate,
                        tvProgramDetailsBannerKinopoiskLogo, it?.rateKinopoisk)
                bindRate(tvProgramDetailsBannerImdbRate,
                        tvProgramDetailsBannerImdbLogo, it?.rateKinopoisk)
            }
        }
    }

    */

    override fun onUnbindViewHolder(viewHolder: ViewHolder?) {
        // not applicable here
    }

    private fun buildBodyText(program: TvProgramIssue?): String? {
        program?: return null

        val bodyParts: MutableList<String?> = mutableListOf()
        listOf( program.description,
                buildCompactDigestExtract(program),
                buildCompactCreditList(program),
                buildProductionAndAwardsShort(program)
        ).forEach {
            it?.let { text -> bodyParts.add(text) }
        }

        return bodyParts.joinToString ("\n\n")
    }

    private fun buildCompactDigestExtract(program: TvProgramIssue): String? {
        val parts: MutableList<String?> = ArrayList()
        with (program) {
            releaseDates?.let { if (it.trim().isNotEmpty()) parts.add(it) }
            categoryNames?.let { if (it.isNotEmpty()) parts.add(it.joinToString(", ")) }
            countryNames?.let { if (it.isNotEmpty()) parts.add(it.joinToString(", ")) }
        }
        if (parts.isEmpty()) return null
        return parts.joinToString(", ")
    }

    private fun buildProductionAndAwardsShort(program: TvProgramIssue): String {
        val parts: MutableList<String?> = ArrayList()
        with(program) {
            production?.let { if (it.isNotEmpty()) parts.add(
                    context.getString(R.string.program_details_item_production, it)
            )}
            awards?.let { if (it.isNotEmpty()) parts.add(
                    context.getString(R.string.program_details_item_awards, it)
            )}
        }
        return parts.joinToString("\n\n").trim()
    }

    private fun buildCompactCreditList(program: TvProgramIssue): String? {
        if (program.credits.isNullOrEmpty()) return null
        val map: MutableMap<CreditRole, MutableList<String>> = mutableMapOf()
        program.credits!!.forEach { credit ->
            credit.role?.let { role ->
                credit.name?.let {
                    if(!map.containsKey(role)) map[role] = mutableListOf()
                    map[role]?.add(it)
                }
            }
        }
        if (map.isEmpty()) return null
        val titles = context.resources.getStringArray(R.array.program_digest_roles_program)
        val lines: MutableList<String?> = mutableListOf()
        for (role in map.keys) {
            val list = map[role] ?: continue
            lines.add(String.format("%s %s", titles[role.ordinal], TextUtils.join(", ", list)))
        }
        if (lines.isEmpty()) return null
        return lines.joinToString("\n")
    }


    private fun bindRate(textView: TextView, logoView: ImageView, rate: Float?) {
        if (rate != null && rate > 0) {
            textView.text = rate.roundToInt().toString()
            logoView.visibility = VISIBLE; textView.visibility = VISIBLE
        } else {
            textView.visibility = GONE; logoView.visibility = GONE
        }
    }
}