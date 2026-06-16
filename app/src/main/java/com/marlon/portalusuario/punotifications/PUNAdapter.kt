package com.marlon.portalusuario.punotifications

import android.os.Build
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.marlon.portalusuario.R
import com.marlon.portalusuario.util.Util

class PUNAdapter(
    private val notificationsList: List<PUNotification>,
    private val context: android.content.Context,
) : RecyclerView.Adapter<PUNAdapter.PUNViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): PUNViewHolder =
        PUNViewHolder(
            LayoutInflater.from(context).inflate(R.layout.post_item, parent, false),
        )

    override fun onBindViewHolder(
        holder: PUNViewHolder,
        position: Int,
    ) {
        val pun = notificationsList[position]
        val title = pun.title
        if (title.isNotEmpty()) {
            holder.titleTv.text = title
        } else {
            holder.titleTv.text = "Portal Usuario anuncia...!"
        }

        val details = pun.text
        if (details.isNotEmpty()) {
            holder.detailsTv.text = details
        } else {
            holder.detailsTv.text = "Sin detalles"
        }

        val date = Util.long2Date(pun.date)
        if (date != null) {
            holder.dateTv.text = pun.dateToString()
        } else {
            holder.dateTv.text = "Sin fecha"
        }

        val image = pun.image
        if (image.isNotEmpty()) {
            Glide
                .with(context)
                .load(image)
                .centerCrop()
                .into(holder.circleImage)
            Glide
                .with(context)
                .load(image)
                .centerCrop()
                .into(holder.bigImage)
        }

        holder.arrowBtn.setOnClickListener(
            RequireApiImpl(
                onApi26 = {
                    if (holder.expandableView.visibility == View.GONE) {
                        TransitionManager.beginDelayedTransition(holder.cardView, AutoTransition())
                        holder.expandableView.visibility = View.VISIBLE
                        holder.circleImageContainer.visibility = View.GONE
                        holder.arrowBtn.rotation = 180f
                    } else {
                        TransitionManager.beginDelayedTransition(holder.cardView, AutoTransition())
                        holder.circleImageContainer.visibility = View.VISIBLE
                        holder.arrowBtn.rotation = 0f
                        holder.expandableView.visibility = View.GONE
                    }
                },
                onPreApi26 = {
                    if (holder.expandableView.visibility == View.GONE) {
                        holder.expandableView.visibility = View.VISIBLE
                        holder.circleImageContainer.visibility = View.GONE
                        holder.arrowBtn.rotation = 180f
                    } else {
                        holder.circleImageContainer.visibility = View.VISIBLE
                        holder.arrowBtn.rotation = 0f
                        holder.expandableView.visibility = View.GONE
                    }
                },
            ),
        )
    }

    override fun getItemCount(): Int = notificationsList.size

    inner class PUNViewHolder(
        itemView: View,
    ) : RecyclerView.ViewHolder(itemView) {
        val circleImage: ImageView = itemView.findViewById(R.id.circleImage)
        val titleTv: TextView = itemView.findViewById(R.id.title)
        val dateTv: TextView = itemView.findViewById(R.id.date)
        val detailsTv: TextView = itemView.findViewById(R.id.description)
        val arrowBtn: Button = itemView.findViewById(R.id.arrowBtn)
        val expandableView: ConstraintLayout = itemView.findViewById(R.id.expandableView)
        val cardView: CardView = itemView.findViewById(R.id.cardView)
        val bigImage: ImageView = itemView.findViewById(R.id.big_image)
        val circleImageContainer: CardView = itemView.findViewById(R.id.view)
    }
}

private class RequireApiImpl(
    private val onApi26: () -> Unit,
    private val onPreApi26: () -> Unit,
) : View.OnClickListener {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onClick(v: View) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            onApi26()
        } else {
            onPreApi26()
        }
    }
}
