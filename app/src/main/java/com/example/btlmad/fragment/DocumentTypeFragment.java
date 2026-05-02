package com.example.btlmad.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.btlmad.R;
import com.example.btlmad.adapter.DocumentTypeAdapter;
import com.example.btlmad.database.DBHelper;
import com.example.btlmad.model.DocumentType;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class DocumentTypeFragment extends Fragment {

    private RecyclerView recyclerView;
    private FloatingActionButton fabAdd;
    private DBHelper dbHelper;
    private DocumentTypeAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_doc_type, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewDocType);
        fabAdd = view.findViewById(R.id.fabAddDocType);
        dbHelper = new DBHelper(getContext());

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        loadData();

        fabAdd.setOnClickListener(v -> showAddEditDialog(null));
        return view;
    }

    private void loadData() {
        List<DocumentType> list = dbHelper.getAllDocumentTypes();
        if (adapter == null) {
            adapter = new DocumentTypeAdapter(list, new DocumentTypeAdapter.OnItemClickListener() {
                @Override
                public void onEditClick(DocumentType type) {
                    showAddEditDialog(type);
                }

                @Override
                public void onDeleteClick(DocumentType type) {
                    showDeleteConfirmDialog(type);
                }
            });
            recyclerView.setAdapter(adapter);
        } else {
            adapter.setTypeList(list);
        }
    }

    private void showDeleteConfirmDialog(DocumentType type) {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa loại tài liệu \"" + type.getName() + "\"?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    dbHelper.deleteDocumentType(type.getId());
                    loadData();
                    Toast.makeText(getContext(), "Đã xóa loại tài liệu", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void showAddEditDialog(DocumentType type) {
        View dialogView = LayoutInflater.from(getContext())
                .inflate(R.layout.dialog_add_doc_type, null);
        EditText edtName = dialogView.findViewById(R.id.edtTypeName);
        EditText edtDesc = dialogView.findViewById(R.id.edtTypeDesc);

        boolean isEdit = (type != null);
        if (isEdit) {
            edtName.setText(type.getName());
            edtDesc.setText(type.getDescription());
        }

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(isEdit ? "Sửa loại tài liệu" : "Thêm loại tài liệu")
                .setView(dialogView)
                .setPositiveButton("Lưu", (dialog, which) -> {
                    String name = edtName.getText().toString().trim();
                    String desc = edtDesc.getText().toString().trim();
                    if (name.isEmpty()) {
                        Toast.makeText(getContext(), "Tên loại không được để trống!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (isEdit) {
                        type.setName(name);
                        type.setDescription(desc);
                        dbHelper.updateDocumentType(type);
                        Toast.makeText(getContext(), "Đã cập nhật", Toast.LENGTH_SHORT).show();
                    } else {
                        dbHelper.addDocumentType(new DocumentType(name, desc));
                        Toast.makeText(getContext(), "Đã thêm loại tài liệu", Toast.LENGTH_SHORT).show();
                    }
                    loadData();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
}
