package com.example.btlmad.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

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
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class ReportFragment extends Fragment {

    private Spinner spinnerType;
    private MaterialButton btnLargeFiles;
    private RecyclerView recyclerView;

    private DBHelper dbHelper;
    private DocumentAdapter documentAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_report, container, false);

        spinnerType = view.findViewById(R.id.spinnerTypeReport);
        btnLargeFiles = view.findViewById(R.id.btnLargeFiles);
        recyclerView = view.findViewById(R.id.recyclerViewReport);

        dbHelper = new DBHelper(getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        setupSpinner();

        btnLargeFiles.setOnClickListener(v -> {
            // Tìm tài liệu có kích thước > 1MB
            List<Document> list = dbHelper.getDocumentsLargerThan1MB();
            updateList(list);
        });

        return view;
    }

    private void setupSpinner() {
        List<DocumentType> types = dbHelper.getAllDocumentTypes();

        List<DocumentType> spinnerList = new ArrayList<>();
        spinnerList.add(new DocumentType(-1, "--- Chọn loại tài liệu ---", ""));
        spinnerList.addAll(types);

        ArrayAdapter<DocumentType> adapter = new ArrayAdapter<>(
                requireContext(), android.R.layout.simple_spinner_item, spinnerList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(adapter);

        spinnerType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                DocumentType selected = (DocumentType) parent.getItemAtPosition(position);
                if (selected.getId() != -1) {
                    List<Document> list = dbHelper.getDocumentsByType(selected.getId());
                    updateList(list);
                } else {
                    updateList(new ArrayList<>());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void updateList(List<Document> documents) {
        if (documentAdapter == null) {
            documentAdapter = new DocumentAdapter(documents, null);
            recyclerView.setAdapter(documentAdapter);
        } else {
            documentAdapter.setDocumentList(documents);
        }
    }
}
