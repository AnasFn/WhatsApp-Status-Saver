package statussaver.statusdownloader.storysaver.Fragments;

import android.content.UriPermission;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.documentfile.provider.DocumentFile;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import statussaver.statusdownloader.storysaver.R;
import statussaver.statusdownloader.storysaver.Adapter.VideoAdapter;
import statussaver.statusdownloader.storysaver.Models.Status;
import statussaver.statusdownloader.storysaver.Utils.Common;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;

public class VideoFragment extends Fragment {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private final List<Status> videoList = new ArrayList<>();
    private VideoAdapter videoAdapter;
    private RelativeLayout container;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView messageTextView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_videos, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        recyclerView = view.findViewById(R.id.recyclerViewVideo);
        progressBar = view.findViewById(R.id.prgressBarVideo);
        container = view.findViewById(R.id.videos_container);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        messageTextView = view.findViewById(R.id.messageTextVideo);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(requireActivity(), android.R.color.holo_orange_dark)
                , ContextCompat.getColor(requireActivity(), android.R.color.holo_green_dark),
                ContextCompat.getColor(requireActivity(), R.color.colorPrimary),
                ContextCompat.getColor(requireActivity(), android.R.color.holo_blue_dark));

        swipeRefreshLayout.setOnRefreshListener(this::getStatus);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), Common.GRID_COUNT));

        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        getStatus();
    }

    private void getStatus() {
        videoList.clear(); // Clear the list before populating

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            executeNew();
        } else if (Common.STATUS_DIRECTORY.exists()) {
            executeOld();
        } else {
            messageTextView.setVisibility(View.VISIBLE);
            messageTextView.setText(R.string.cant_find_whatsapp_dir);
            Toast.makeText(getActivity(), getString(R.string.cant_find_whatsapp_dir), Toast.LENGTH_SHORT).show();
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    private void executeNew() {
        Executors.newSingleThreadExecutor().execute(() -> {
            Handler mainHandler = new Handler(Looper.getMainLooper());

            List<UriPermission> list = requireActivity().getContentResolver().getPersistedUriPermissions();

            DocumentFile file = DocumentFile.fromTreeUri(requireActivity(), list.get(0).getUri());

            if (file == null) {
                mainHandler.post(() -> {
                    progressBar.setVisibility(View.GONE);
                    messageTextView.setVisibility(View.VISIBLE);
                    messageTextView.setText(R.string.no_files_found);
                    Toast.makeText(getActivity(), getString(R.string.no_files_found), Toast.LENGTH_SHORT).show();
                    swipeRefreshLayout.setRefreshing(false);
                });
                return;
            }

            DocumentFile[] statusFiles = file.listFiles();

            if (statusFiles.length <= 0) {
                mainHandler.post(() -> {
                    progressBar.setVisibility(View.GONE);
                    messageTextView.setVisibility(View.VISIBLE);
                    messageTextView.setText(R.string.no_files_found);
                    Toast.makeText(getActivity(), getString(R.string.no_files_found), Toast.LENGTH_SHORT).show();

                    swipeRefreshLayout.setRefreshing(false);
                });
                return;
            }

            try {
                for (DocumentFile documentFile : statusFiles) {
                    Status status = new Status(documentFile);

                    if (status.isVideo()) {
                        videoList.add(status);
                    }
                }
            }catch (Exception e) {
                Log.d("Videofra 140", e.toString() );
            }


            mainHandler.post(() -> {
                if (videoList.size() <= 0) {
                    messageTextView.setVisibility(View.VISIBLE);
                    messageTextView.setText(R.string.no_files_found);
                } else {
                    messageTextView.setVisibility(View.GONE);
                    messageTextView.setText("");
                }

                videoAdapter = new VideoAdapter(videoList, container);
                recyclerView.setAdapter(videoAdapter);
                videoAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            });

            swipeRefreshLayout.setRefreshing(false);
        });
    }

    private void executeOld() {
        Executors.newSingleThreadExecutor().execute(() -> {
            Handler mainHandler = new Handler(Looper.getMainLooper());

            File[] statusFiles = Common.STATUS_DIRECTORY.listFiles();

            if (statusFiles != null && statusFiles.length > 0) {
                Arrays.sort(statusFiles);

                try {
                    for (File file : statusFiles) {
                        Status status = new Status(file, file.getName(), file.getAbsolutePath());

                        if (status.isVideo()) {
                            videoList.add(status);
                        }
                    }
                }catch (Exception e){
                    Log.d("Videofra 182", e.toString() );
                }


                mainHandler.post(() -> {
                    if (videoList.size() <= 0) {
                        messageTextView.setVisibility(View.VISIBLE);
                        messageTextView.setText(R.string.no_files_found);
                    } else {
                        messageTextView.setVisibility(View.GONE);
                        messageTextView.setText("");
                    }

                    videoAdapter = new VideoAdapter(videoList, container);
                    recyclerView.setAdapter(videoAdapter);
                    videoAdapter.notifyDataSetChanged();
                    progressBar.setVisibility(View.GONE);
                });
            } else {
                mainHandler.post(() -> {
                    progressBar.setVisibility(View.GONE);
                    messageTextView.setVisibility(View.VISIBLE);
                    messageTextView.setText(R.string.no_files_found);
                    Toast.makeText(getActivity(), getString(R.string.no_files_found), Toast.LENGTH_SHORT).show();
                });
            }

            swipeRefreshLayout.setRefreshing(false);
        });
    }
}