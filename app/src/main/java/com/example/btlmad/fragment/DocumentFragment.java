package com.example.btlmad.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.btlmad.R;
import com.example.btlmad.adapter.DocumentAdapter;
import com.example.btlmad.database.DBHelper;
import com.example.btlmad.model.Document;
import com.example.btlmad.model.DocumentType;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class DocumentFragment extends Fragment {

    private RecyclerView recyclerView;
    private FloatingActionButton fabAdd;
    private DBHelper dbHelper;
    private DocumentAdapter documentAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_document, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewDocument);
        fabAdd = view.findViewById(R.id.fabAddDocument);
        dbHelper = new DBHelper(getContext());

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        loadData();

        fabAdd.setOnClickListener(v -> showAddEditDialog(null));
        return view;
    }

    private void loadData() {
        List<Document> list = dbHelper.getAllDocuments();
        if (documentAdapter == null) {
            documentAdapter = new DocumentAdapter(list, new DocumentAdapter.OnItemClickListener() {
                @Override
                public void onEditClick(Document document) {
                    showAddEditDialog(document);
                }

                @Override
                public void onDeleteClick(Document document) {
                    showDeleteConfirmDialog(document);
                }
            });
            recyclerView.setAdapter(documentAdapter);
        } else {
            documentAdapter.setDocumentList(list);
        }
    }

    private void showDeleteConfirmDialog(Document document) {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa tài liệu \"" + document.getName() + "\"?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    dbHelper.deleteDocument(document.getId());
                    loadData();
                    Toast.makeText(getContext(), "Đã xóa tài liệu", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void showAddEditDialog(Document document) {
        View dialogView = LayoutInflater.from(getContext())
                .inflate(R.layout.dialog_add_document, null);

        EditText edtDocName = dialogView.findViewById(R.id.edtDocName);
        EditText edtDocLink = dialogView.findViewById(R.id.edtDocLink);
        EditText edtDocSize = dialogView.findViewById(R.id.edtDocSize);
        AutoCompleteTextView spinnerType = dialogView.findViewById(R.id.spinnerDocType);

        List<DocumentType> types = dbHelper.getAllDocumentTypes();
        if (types.isEmpty()) {
            Toast.makeText(getContext(), "Vui lòng thêm Loại tài liệu trước!", Toast.LENGTH_SHORT).show();
            return;
        }

        ArrayAdapter<DocumentType> typeAdapter = new ArrayAdapter<>(
                requireContext(), android.R.layout.simple_dropdown_item_1line, types);
        spinnerType.setAdapter(typeAdapter);

        boolean isEdit = (document != null);
        if (isEdit) {
            edtDocName.setText(document.getName());
            edtDocLink.setText(document.getDownloadLink());
            edtDocSize.setText(String.valueOf(document.getSizeKb()));
            for (DocumentType t : types) {
                if (t.getId() == document.getTypeId()) {
                    spinnerType.setText(t.getName(), false);
                    break;
                }
            }
        }

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(isEdit ? "Sửa tài liệu" : "Thêm tài liệu")
                .setView(dialogView)
                .setPositiveButton("Lưu", (dialog, which) -> {
                    String name = edtDocName.getText().toString().trim();
                    String link = edtDocLink.getText().toString().trim();
                    String sizeStr = edtDocSize.getText().toString().trim();
                    String selectedTypeName = spinnerType.getText().toString();

                    if (TextUtils.isEmpty(name)) {
                        Toast.makeText(getContext(), "Tên tài liệu không được để trống!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    double sizeKb = 0;
                    try {
                        if (!sizeStr.isEmpty()) sizeKb = Double.parseDouble(sizeStr);
                    } catch (NumberFormatException e) {
                        Toast.makeText(getContext(), "Kích thước không hợp lệ!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    DocumentType selectedType = null;
                    for (DocumentType t : types) {
                        if (t.getName().equals(selectedTypeName)) {
                            selectedType = t;
                            break;
                        }
                    }

                    if (selectedType == null) {
                        Toast.makeText(getContext(), "Vui lòng chọn loại tài liệu!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (isEdit) {
                        document.setName(name);
                        document.setDownloadLink(link);
                        document.setSizeKb(sizeKb);
                        document.setTypeId(selectedType.getId());
                        dbHelper.updateDocument(document);
                        Toast.makeText(getContext(), "Đã cập nhật tài liệu", Toast.LENGTH_SHORT).show();
                    } else {
                        dbHelper.addDocument(new Document(name, selectedType.getId(), link, sizeKb));
                        Toast.makeText(getContext(), "Đã thêm tài liệu", Toast.LENGTH_SHORT).show();
                    }
                    loadData();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
}
