package com.example.btlmad.adapter;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.btlmad.R;
import com.example.btlmad.model.Document;

import java.util.List;

public class DocumentAdapter extends RecyclerView.Adapter<DocumentAdapter.DocViewHolder> {

    private List<Document> documentList;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onEditClick(Document document);
        void onDeleteClick(Document document);
    }

    public DocumentAdapter(List<Document> documentList, OnItemClickListener listener) {
        this.documentList = documentList;
        this.listener = listener;
    }

    public void setDocumentList(List<Document> documentList) {
        this.documentList = documentList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public DocViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_document, parent, false);
        return new DocViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DocViewHolder holder, int position) {
        Document doc = documentList.get(position);
        holder.tvDocName.setText(doc.getName());
        holder.tvDocSize.setText("Kích thước: " + doc.getFormattedSize());

        String link = doc.getDownloadLink();
        if (link != null && !link.isEmpty()) {
            holder.tvDocLink.setText(link);
            holder.tvDocLink.setVisibility(View.VISIBLE);
            holder.tvDocLink.setOnClickListener(v -> {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                    v.getContext().startActivity(intent);
                } catch (Exception ignored) {}
            });
        } else {
            holder.tvDocLink.setVisibility(View.GONE);
        }

        if (listener == null) {
            holder.btnEdit.setVisibility(View.GONE);
            holder.btnDelete.setVisibility(View.GONE);
        } else {
            holder.btnEdit.setVisibility(View.VISIBLE);
            holder.btnDelete.setVisibility(View.VISIBLE);
            holder.btnEdit.setOnClickListener(v -> listener.onEditClick(doc));
            holder.btnDelete.setOnClickListener(v -> listener.onDeleteClick(doc));
        }
    }

    @Override
    public int getItemCount() {
        return documentList != null ? documentList.size() : 0;
    }

    static class DocViewHolder extends RecyclerView.ViewHolder {
        TextView tvDocName, tvDocSize, tvDocLink;
        ImageView btnEdit, btnDelete;

        public DocViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDocName = itemView.findViewById(R.id.tvDocName);
            tvDocSize = itemView.findViewById(R.id.tvDocSize);
            tvDocLink = itemView.findViewById(R.id.tvDocLink);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
