package com.example.btlmad.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.btlmad.R;
import com.example.btlmad.model.DocumentType;

import java.util.List;

public class DocumentTypeAdapter extends RecyclerView.Adapter<DocumentTypeAdapter.TypeViewHolder> {

    private List<DocumentType> typeList;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onEditClick(DocumentType type);
        void onDeleteClick(DocumentType type);
    }

    public DocumentTypeAdapter(List<DocumentType> typeList, OnItemClickListener listener) {
        this.typeList = typeList;
        this.listener = listener;
    }

    public void setTypeList(List<DocumentType> typeList) {
        this.typeList = typeList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TypeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_doc_type, parent, false);
        return new TypeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TypeViewHolder holder, int position) {
        DocumentType type = typeList.get(position);
        holder.tvTypeName.setText(type.getName());
        String desc = (type.getDescription() != null && !type.getDescription().isEmpty())
                ? type.getDescription() : "Không có mô tả";
        holder.tvTypeDesc.setText(desc);

        if (listener == null) {
            holder.btnEdit.setVisibility(View.GONE);
            holder.btnDelete.setVisibility(View.GONE);
        } else {
            holder.btnEdit.setVisibility(View.VISIBLE);
            holder.btnDelete.setVisibility(View.VISIBLE);
            holder.btnEdit.setOnClickListener(v -> listener.onEditClick(type));
            holder.btnDelete.setOnClickListener(v -> listener.onDeleteClick(type));
        }
    }

    @Override
    public int getItemCount() {
        return typeList != null ? typeList.size() : 0;
    }

    static class TypeViewHolder extends RecyclerView.ViewHolder {
        TextView tvTypeName, tvTypeDesc;
        ImageView btnEdit, btnDelete;

        public TypeViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTypeName = itemView.findViewById(R.id.tvTypeName);
            tvTypeDesc = itemView.findViewById(R.id.tvTypeDesc);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
