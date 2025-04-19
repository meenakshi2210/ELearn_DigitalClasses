package com.digital.classes.fragments;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.digital.classes.R;

import java.io.File;
import java.io.IOException;

public class ChapterFragment extends Fragment {

    private PDFView pdfView;
    StorageReference storageRef = FirebaseStorage.getInstance().getReference();
    private String chapterName;
    private String className;
    private String bookName;
    private String type;
    private ProgressBar progressBar;
    private String chapterNo;
    private TextView textView;


    public ChapterFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chapter, container, false);
        pdfView = view.findViewById(R.id.pdfView);
        progressBar = view.findViewById(R.id.progress_bar);
        textView = view.findViewById(R.id.text);
        chapterName = getArguments().getString("chapter");
        className = getArguments().getString("classroom");
        bookName = getArguments().getString("book");
        type = getArguments().getString("type");


        chapterNo = chapterName.substring(8);

        StorageReference ref = storageRef.child(className+"/"+bookName+"/"+type+" ("+chapterNo+").pdf");

        try {
            progressBar.setVisibility(View.VISIBLE);
            final File localFile = File.createTempFile("type", "pdf");
            ref.getFile(localFile).addOnSuccessListener(taskSnapshot -> {
                System.out.println("Done");
                pdfView.fromFile(localFile).swipeHorizontal(true).enableSwipe(true).onLoad(nbPages -> {
                    progressBar.setVisibility(View.GONE);
                    textView.setVisibility(View.GONE);
                }).load();
            }).addOnFailureListener(exception -> {
                // Handle any errors
            }).addOnProgressListener(taskSnapshot -> {
                double percent = (taskSnapshot.getBytesTransferred()*1.0/taskSnapshot.getTotalByteCount())*100;
                long roundOf = (long) percent;
                textView.setText(roundOf+" %");
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return view;
    }



}
