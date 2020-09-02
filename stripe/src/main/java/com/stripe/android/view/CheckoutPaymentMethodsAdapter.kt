package com.stripe.android.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.stripe.android.R
import com.stripe.android.databinding.LayoutCheckoutPaymentMethodItemBinding
import com.stripe.android.model.PaymentMethod
import java.lang.IllegalStateException

internal class CheckoutPaymentMethodsAdapter(val paymentMethods: List<PaymentMethod>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var selectedPaymentMethodId: String? = null

    private fun updateSelectedPaymentMethod(position: Int) {
        val currentlySelectedPosition = paymentMethods.indexOfFirst {
            it.id == selectedPaymentMethodId
        }
        if (currentlySelectedPosition != position) {
            // selected a new Payment Method
            notifyItemChanged(currentlySelectedPosition)
            notifyItemChanged(position)
            selectedPaymentMethodId = paymentMethods.getOrNull(position)?.id
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        // TODO: Support AddCard, GooglePay
        return when (ViewType.values()[viewType]) {
            ViewType.Card -> CardViewHolder(parent)
            else -> throw IllegalStateException("Unsupported view type")
        }
    }

    override fun getItemCount(): Int {
        return paymentMethods.size
    }

    override fun getItemViewType(position: Int): Int {
        // TODO: Support AddCard, GooglePay
        return ViewType.Card.ordinal
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is CardViewHolder) {
            val paymentMethod = paymentMethods[position]
            holder.setPaymentMethod(paymentMethod)
            holder.setSelected(paymentMethod.id == selectedPaymentMethodId)
            holder.itemView.setOnClickListener {
                updateSelectedPaymentMethod(holder.adapterPosition)
            }
        }
    }

    private class CardViewHolder(private val binding: LayoutCheckoutPaymentMethodItemBinding) : RecyclerView.ViewHolder(binding.root) {
        constructor(parent: ViewGroup) : this(
            LayoutCheckoutPaymentMethodItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )

        fun setPaymentMethod(method: PaymentMethod) {
            val card = method.card!!
            // TODO: Get updated card brand icons
            binding.brandIcon.setImageResource(card.brand.icon)
            binding.cardNumber.text = binding.root.context
                .getString(R.string.checkout_payment_method_item_card_number, card.last4)
        }

        fun setSelected(selected: Boolean) {
            binding.checkIcon.visibility = if (selected) View.VISIBLE else View.GONE
        }
    }

    private enum class ViewType {
        Card,
        AddCard,
        GooglePay
    }
}
