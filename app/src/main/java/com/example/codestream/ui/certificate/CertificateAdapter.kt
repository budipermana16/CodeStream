package com.example.codestream.ui.certificate

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.codestream.data.model.Certificate
import com.example.codestream.databinding.ItemCertificateBinding

class CertificateAdapter(
    private val onClick: (Certificate) -> Unit
) : RecyclerView.Adapter<CertificateAdapter.VH>() {

    private val items = mutableListOf<Certificate>()

    fun submit(list: List<Certificate>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    inner class VH(private val binding: ItemCertificateBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Certificate) {
            // pastikan id ini ada di item_certificate.xml:
            binding.tvTitle.text = item.courseTitle
            binding.tvIssuedAt.text = "Issued: ${item.issuedAt}"
            binding.root.setOnClickListener { onClick(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemCertificateBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(items[position])
    override fun getItemCount(): Int = items.size
}
